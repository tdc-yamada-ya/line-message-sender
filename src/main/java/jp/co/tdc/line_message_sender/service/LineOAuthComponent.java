package jp.co.tdc.line_message_sender.service;

import jp.co.tdc.line_message_sender.line.bot.client.LineOAuthClient;
import jp.co.tdc.line_message_sender.line.bot.model.AccessToken;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
class LineOAuthComponent {
	@Retryable(value = {HttpServerErrorException.class, ResourceAccessException.class},
			maxAttempts = 3,
			backoff = @Backoff)
	AccessToken getAccessToken(LineOAuthClient client) {
		return client.getAccessToken();
	}
}
