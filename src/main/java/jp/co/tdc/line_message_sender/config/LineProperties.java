package jp.co.tdc.line_message_sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "line")
public class LineProperties {
	private String channelId;
	private int pushRatePerMinute = 1;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public int getPushRatePerMinute() {
		return pushRatePerMinute;
	}

	public void setPushRatePerMinute(int pushRatePerMinute) {
		this.pushRatePerMinute = pushRatePerMinute;
	}
}
