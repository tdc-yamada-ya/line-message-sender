package jp.co.tdc.line_message_sender.line.bot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("text")
public class TextMessage implements Message {
	private final String text;

	@JsonCreator
	public TextMessage(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
