package jp.co.tdc.line_message_sender.service;

import jp.co.tdc.line_message_sender.line.bot.client.LineMessagingClient;
import jp.co.tdc.line_message_sender.line.bot.model.PushMessage;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
class LineMessagingComponent {
	@Retryable(value = {HttpServerErrorException.class, ResourceAccessException.class},
			maxAttempts = 3,
			backoff = @Backoff)
	void pushMessage(LineMessagingClient client, PushMessage pushMessage) {
		client.pushMessage(pushMessage);
	}
}
