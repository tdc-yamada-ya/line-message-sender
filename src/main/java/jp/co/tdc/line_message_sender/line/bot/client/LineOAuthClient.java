package jp.co.tdc.line_message_sender.line.bot.client;

import java.net.URI;

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

import jp.co.tdc.line_message_sender.line.bot.model.AccessToken;

public class LineOAuthClient extends LineClient {
	private static final String ENDPOINT = "https://api.line.me/v2/oauth";
	private static final String ACCESS_TOKEN_RESOURCE_NAME = "/accessToken";
	private static final String GRANT_TYPE_PARAM_NAME = "grant_type";
	private static final String CLIENT_CREDENTIALS_PARAM_VALUE = "client_credentials";
	private static final String CLIENT_ID_PARAM_NAME = "client_id";
	private static final String CLIENT_SECRET_PARAM_NAME = "client_secret";

	private String channelId;
	private String channelSecret;

	/**
	 * @param channelId LINE Developersサイトから確認できるLINEチャンネルID
	 * @param channelSecret LINE Developersサイトから確認できるLINEチャンネルシークレット
	 */
	public LineOAuthClient(String channelId, String channelSecret) {
		super(ENDPOINT);
		this.channelId = channelId;
		this.channelSecret = channelSecret;
	}

	public AccessToken getAccessToken() throws RestClientException {
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

		body.add(GRANT_TYPE_PARAM_NAME, CLIENT_CREDENTIALS_PARAM_VALUE);
		body.add(CLIENT_ID_PARAM_NAME, channelId);
		body.add(CLIENT_SECRET_PARAM_NAME, channelSecret);

		URI uri = UriComponentsBuilder.fromUriString(getEndpoint() + ACCESS_TOKEN_RESOURCE_NAME).build().toUri();
		RequestEntity<?> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
		RestTemplate client = createClient();

		try {
			ResponseEntity<AccessToken> responseEntity = client.exchange(requestEntity, AccessToken.class);

			return responseEntity.getBody();
		} catch (RestClientException e) {
			LineClientUtils.handleRestClientException(e);
			throw e;
		}
	}
}
