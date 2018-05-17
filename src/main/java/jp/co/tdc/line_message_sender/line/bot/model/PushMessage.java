package jp.co.tdc.line_message_sender.line.bot.model;

import java.util.Collections;
import java.util.List;

public class PushMessage {
	private String to;
	private List<Message> messages;

	public PushMessage(String to, Message message) {
		this.to = to;
		this.messages = Collections.singletonList(message);
	}

	public String getTo() {
		return to;
	}

	public List<Message> getMessages() {
		return messages;
	}
}
