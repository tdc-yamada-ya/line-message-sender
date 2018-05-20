package jp.co.tdc.line_message_sender.service;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.tdc.line_message_sender.domain.LinePushMessage;
import jp.co.tdc.line_message_sender.domain.LinePushMessageRepository;
import jp.co.tdc.line_message_sender.domain.TargetType;

@Service
public class LoadLinePushMessageCommandService extends LoadCommandService {
	@Autowired
	private LinePushMessageRepository linePushMessageRepository;

	@Override
	protected void handleCSVRecord(CSVRecord record) {
		LinePushMessage linePushMessage = new LinePushMessage();

		String pushMessageId = record.get(Header.push_message_id);
		String channelId = record.get(Header.channel_id);
		String targetType = record.get(Header.target_type);
		String target = record.get(Header.target);
		String templateId = record.get(Header.template_id);
		String tag = record.get(Header.tag);

		linePushMessage.setPushMessageId(StringUtils.isEmpty(pushMessageId) ? UUID.randomUUID().toString() : pushMessageId);
		linePushMessage.setChannelId(channelId);
		linePushMessage.setTargetType(TargetType.valueOf(targetType));
		linePushMessage.setTarget(target);
		linePushMessage.setTemplateId(templateId);
		linePushMessage.setTag(tag);
		linePushMessage.setCreatedAt(new Date());
		linePushMessageRepository.save(linePushMessage);
	}

	@Override
	protected Class<? extends Enum<?>> getHeader() {
		return Header.class;
	}

	private enum Header {
		push_message_id,
		channel_id,
		target_type,
		target,
		template_id,
		tag;
	}
}
