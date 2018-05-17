package jp.co.tdc.line_message_sender.domain;

import javax.persistence.AttributeConverter;

public class PayloadTypeConverter implements AttributeConverter<PayloadType, String>{
	@Override
	public String convertToDatabaseColumn(PayloadType attribute) {
		return attribute == null ? null : attribute.name();
	}

	@Override
	public PayloadType convertToEntityAttribute(String dbData) {
		return dbData == null ? null : PayloadType.valueOf(dbData);
	}
}
