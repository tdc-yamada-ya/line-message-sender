package jp.co.tdc.line_message_sender.service;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.tdc.line_message_sender.domain.LineChannelCredential;
import jp.co.tdc.line_message_sender.domain.LineChannelCredentialRepository;

@Service
public class LoadLineChannelCredentialCommandService extends LoadCommandService {
	@Autowired
	private LineChannelCredentialRepository lineChannelCredentialRepository;

	@Override
	protected void handleCSVRecord(CSVRecord record) {
		LineChannelCredential loadChannelCredential = new LineChannelCredential();

		String channelCredentialId = record.get(Header.channel_credential_id);
		String channelId = record.get(Header.channel_id);
		String channelSecret = record.get(Header.channel_secret);

		loadChannelCredential.setChannelCredentialId(StringUtils.isEmpty(channelCredentialId) ? UUID.randomUUID().toString() : channelCredentialId);
		loadChannelCredential.setChannelId(channelId);
		loadChannelCredential.setChannelSecret(channelSecret);
		loadChannelCredential.setCreatedAt(new Date());
		lineChannelCredentialRepository.save(loadChannelCredential);
	}

	@Override
	protected Class<? extends Enum<?>> getHeader() {
		return Header.class;
	}

	private enum Header {
		channel_credential_id,
		channel_id,
		channel_secret;
	}
}
