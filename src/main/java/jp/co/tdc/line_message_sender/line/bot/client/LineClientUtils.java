package jp.co.tdc.line_message_sender.line.bot.client;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public class LineClientUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineClientUtils.class);

	public static void handleRestClientException(RestClientException e) {
		if (e instanceof HttpClientErrorException) {
			HttpClientErrorException httpClientErrorException = (HttpClientErrorException)e;

			LOGGER.warn("HTTP client error - message={} responseBody={}", e.getMessage(), httpClientErrorException.getResponseBodyAsString());
		}
	}
}
