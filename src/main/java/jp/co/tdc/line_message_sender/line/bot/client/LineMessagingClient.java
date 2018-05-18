package jp.co.tdc.line_message_sender.line.bot.client;

import java.net.URI;

import jp.co.tdc.line_message_sender.line.bot.model.PushMessage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class LineMessagingClient {
	private String channelToken;

	public LineMessagingClient(String channelToken) {
		this.channelToken = channelToken;
	}

	public void pushMessage(PushMessage pushMessage) throws RestClientException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + this.channelToken);

		URI uri = UriComponentsBuilder.fromUriString("https://api.line.me/v2/bot/message/push").build().toUri();
		RequestEntity<?> requestEntity = new RequestEntity<>(pushMessage, headers, HttpMethod.POST, uri);
		RestTemplate client = new RestTemplate();

		try {
			client.exchange(requestEntity, String.class);
		} catch (RestClientException e) {
			LineClientUtils.handleRestClientException(e);
			throw e;
		}
	}
}
