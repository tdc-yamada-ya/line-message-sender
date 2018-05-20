package jp.co.tdc.line_message_sender.line.bot.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public abstract class LineClient {
	private String endpoint;
	private int connectTimeout = 10000;
	private int readTimeout = 10000;

	public LineClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	protected RestTemplate createClient() {
		return new RestTemplateBuilder()
			.setConnectTimeout(connectTimeout)
			.setReadTimeout(readTimeout)
			.build();
	}
}
