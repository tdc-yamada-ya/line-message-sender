package jp.co.tdc.line_message_sender.line.bot.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

public class LineClientUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineClientUtils.class);

	static void handleRestClientException(RestClientException e) {
		if (e instanceof HttpStatusCodeException) {
			HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException)e;

			LOGGER.warn("HTTP client error - statusCode={} responseBody=\"{}\"",
					httpStatusCodeException.getRawStatusCode(), httpStatusCodeException.getResponseBodyAsString());
		}
	}
}
