package jp.co.tdc.line_message_sender.line.bot.client;

import java.net.URI;

import jp.co.tdc.line_message_sender.line.bot.model.AccessToken;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class LineOAuthClient {
	private String channelId;
	private String channelSecret;

	public LineOAuthClient(String channelId, String channelSecret) {
		this.channelId = channelId;
		this.channelSecret = channelSecret;
	}

	public AccessToken getAccessToken() throws RestClientException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

		body.add("grant_type", "client_credentials");
		body.add("client_id", channelId);
		body.add("client_secret", channelSecret);

		URI uri = UriComponentsBuilder.fromUriString("https://api.line.me/v2/oauth/accessToken").build().toUri();
		RequestEntity<?> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
		RestTemplate client = new RestTemplate();

		try {
			ResponseEntity<AccessToken> responseEntity = client.exchange(requestEntity, AccessToken.class);

			return responseEntity.getBody();
		} catch (RestClientException e) {
			LineClientUtils.handleRestClientException(e);
			throw e;
		}
	}
}
