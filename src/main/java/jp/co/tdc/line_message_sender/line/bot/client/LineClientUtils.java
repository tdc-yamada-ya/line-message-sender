package jp.co.tdc.line_message_sender.line.bot.client;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;

public class LineClientUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineClientUtils.class);

	public static void handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
		LOGGER.error("HTTP client error - message={} responseBody={}", httpClientErrorException.getMessage(), httpClientErrorException.getResponseBodyAsString());
	}
}
