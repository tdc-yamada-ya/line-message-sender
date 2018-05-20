package jp.co.tdc.line_message_sender.line.bot.client;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jp.co.tdc.line_message_sender.line.bot.model.PushMessage;

public class LineMessagingClient extends LineClient {
	private static final String ENDPOINT = "https://api.line.me/v2/bot/message";
	private static final String PUSH_RESOURCE_NAME = "/push";

	private String channelToken;

	/**
	 * @param channelToken LINE Developersサイトから確認あるいはOAuth APIから取得できるLINEチャンネルトークン
	 */
	public LineMessagingClient(String channelToken) {
		super(ENDPOINT);
		this.channelToken = channelToken;
	}

	public void pushMessage(PushMessage pushMessage) throws RestClientException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + this.channelToken);

		URI uri = UriComponentsBuilder.fromUriString(getEndpoint() + PUSH_RESOURCE_NAME).build().toUri();
		RequestEntity<?> requestEntity = new RequestEntity<>(pushMessage, headers, HttpMethod.POST, uri);
		RestTemplate client = createClient();

		try {
			client.exchange(requestEntity, String.class);
		} catch (RestClientException e) {
			LineClientUtils.handleRestClientException(e);
			throw e;
		}
	}
}
