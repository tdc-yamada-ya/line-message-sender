package jp.co.tdc.line_message_sender.domain;

import javax.persistence.AttributeConverter;

public class TargetTypeConverter implements AttributeConverter<TargetType, String>{
	@Override
	public String convertToDatabaseColumn(TargetType attribute) {
		return attribute == null ? null : attribute.name();
	}

	@Override
	public TargetType convertToEntityAttribute(String dbData) {
		return dbData == null ? null : TargetType.valueOf(dbData);
	}
}
