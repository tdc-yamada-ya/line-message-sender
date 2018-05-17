package jp.co.tdc.line_message_sender.service;

import java.util.Date;
import java.util.UUID;

import jp.co.tdc.line_message_sender.config.LineProperties;
import jp.co.tdc.line_message_sender.domain.LineChannelCredential;
import jp.co.tdc.line_message_sender.domain.LineChannelCredentialRepository;
import jp.co.tdc.line_message_sender.domain.LineChannelToken;
import jp.co.tdc.line_message_sender.domain.LineChannelTokenRepository;
import jp.co.tdc.line_message_sender.line.bot.client.LineOAuthClient;
import jp.co.tdc.line_message_sender.line.bot.model.AccessToken;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCommandHandleService implements CommandHandleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenCommandHandleService.class);

	@Autowired
	private LineProperties lineProperties;

	@Autowired
	private LineChannelCredentialRepository lineChannelCredentialRepository;

	@Autowired
	private LineChannelTokenRepository lineChannelTokenRepository;

	@Override
	public void run(ApplicationArguments args) {
		LineChannelCredential credential = lineChannelCredentialRepository.findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAt(lineProperties.getChannelId());

		if (credential == null) {
			throw new CommandHandleServiceException("Credential not found - channelId=" + lineProperties.getChannelId());
		}

		LOGGER.info("channelCredentialId={}", credential.getChannelCredentialId());

		LineOAuthClient client = new LineOAuthClient(lineProperties.getChannelId(), credential.getChannelSecret());

		LOGGER.info("Get access token - channelId={}", lineProperties.getChannelId());

		AccessToken response = client.getAccessToken();

		LineChannelToken token = new LineChannelToken();

		token.setChannelTokenId(UUID.randomUUID().toString());
		token.setChannelId(lineProperties.getChannelId());
		token.setToken(response.getAccessToken());

		Date currentDate = new Date();

		token.setCreatedAt(currentDate);
		token.setExpiresAt(DateUtils.addSeconds(currentDate, response.getExpiresIn()));

		lineChannelTokenRepository.save(token);

		LOGGER.info("Access token saved - channelTokenId={}", token.getChannelTokenId());
	}
}
