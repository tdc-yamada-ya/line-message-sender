package jp.co.tdc.line_message_sender.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LineChannelCredential {
	@Id
	private String channelCredentialId;
	private String channelId;
	private String channelSecret;
	private Date createdAt;
	private Date revokedAt;

	public String getChannelCredentialId() {
		return channelCredentialId;
	}

	public void setChannelCredentialId(String channelCredentialId) {
		this.channelCredentialId = channelCredentialId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelSecret() {
		return channelSecret;
	}

	public void setChannelSecret(String channelSecret) {
		this.channelSecret = channelSecret;
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
