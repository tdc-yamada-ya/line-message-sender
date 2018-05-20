package jp.co.tdc.line_message_sender.service;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import jp.co.tdc.line_message_sender.config.LineProperties;
import jp.co.tdc.line_message_sender.domain.LineChannelCredential;
import jp.co.tdc.line_message_sender.domain.LineChannelCredentialRepository;
import jp.co.tdc.line_message_sender.domain.LineChannelToken;
import jp.co.tdc.line_message_sender.domain.LineChannelTokenRepository;
import jp.co.tdc.line_message_sender.line.bot.client.LineOAuthClient;
import jp.co.tdc.line_message_sender.line.bot.model.AccessToken;

@Service
public class RefreshTokenCommandService implements CommandService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenCommandService.class);

	@Autowired
	private LineProperties lineProperties;

	@Autowired
	private LineChannelCredentialRepository lineChannelCredentialRepository;

	@Autowired
	private LineChannelTokenRepository lineChannelTokenRepository;

	@Autowired
	private LineOAuthComponent lineOAuthComponent;

	@Override
	public void run(ApplicationArguments args) {
		LOGGER.info("Refresh token start");

		LineChannelCredential credential = lineChannelCredentialRepository.findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAtDesc(lineProperties.getChannelId());

		if (credential == null) {
			throw new CommandServiceException("Credential not found - channelId=" + lineProperties.getChannelId());
		}

		LOGGER.info("Found latest channel credential - channelCredentialId={}", credential.getChannelCredentialId());

		LineOAuthClient client = new LineOAuthClient(lineProperties.getChannelId(), credential.getChannelSecret());

		LOGGER.info("Get access token - channelId={}", lineProperties.getChannelId());

		// LINE APIを呼び出して短期アクセストークンを取得
		AccessToken response = lineOAuthComponent.getAccessToken(client);

		// 短期アクセストークンをデータベースに登録
		LineChannelToken token = new LineChannelToken();

		token.setChannelTokenId(UUID.randomUUID().toString());
		token.setChannelId(lineProperties.getChannelId());
		token.setToken(response.getAccessToken());

		Date currentDate = new Date();

		token.setCreatedAt(currentDate);
		token.setExpiresAt(DateUtils.addSeconds(currentDate, response.getExpiresIn()));

		lineChannelTokenRepository.save(token);

		LOGGER.info("Access token saved - channelTokenId={}", token.getChannelTokenId());
		LOGGER.info("Refresh token finished");
	}
}
