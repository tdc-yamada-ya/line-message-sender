package jp.co.tdc.line_message_sender.service;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.tdc.line_message_sender.domain.LineMessageTemplate;
import jp.co.tdc.line_message_sender.domain.LineMessageTemplateRepository;
import jp.co.tdc.line_message_sender.domain.PayloadType;

@Service
public class LoadLineMessageTemplateCommandService extends LoadCommandService {
	@Autowired
	private LineMessageTemplateRepository lineMessageTemplateRepository;

	@Override
	protected void handleCSVRecord(CSVRecord record) {
		LineMessageTemplate lineMessageTemplate = new LineMessageTemplate();

		String templateId = record.get(Header.template_id);
		String payloadType = record.get(Header.payload_type);
		String payload = record.get(Header.payload);

		lineMessageTemplate.setTemplateId(StringUtils.isEmpty(templateId) ? UUID.randomUUID().toString() : templateId);
		lineMessageTemplate.setPayloadType(PayloadType.valueOf(payloadType));
		lineMessageTemplate.setPayload(payload);
		lineMessageTemplate.setCreatedAt(new Date());
		lineMessageTemplateRepository.save(lineMessageTemplate);
	}

	@Override
	protected Class<? extends Enum<?>> getHeader() {
		return Header.class;
	}

	private enum Header {
		template_id,
		payload_type,
		payload;
	}
}
