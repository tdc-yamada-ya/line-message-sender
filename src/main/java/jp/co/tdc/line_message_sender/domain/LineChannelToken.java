package jp.co.tdc.line_message_sender.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LineChannelToken {
	@Id
	private String channelTokenId;
	private String channelId;
	private String token;
	private Date expiresAt;
	private Date createdAt;
	private Date revokedAt;

	public String getChannelTokenId() {
		return channelTokenId;
	}

	public void setChannelTokenId(String channelTokenId) {
		this.channelTokenId = channelTokenId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getRevokedAt() {
		return revokedAt;
	}

	public void setRevokedAt(Date revokedAt) {
		this.revokedAt = revokedAt;
	}
}
