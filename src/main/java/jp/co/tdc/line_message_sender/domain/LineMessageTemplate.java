package jp.co.tdc.line_message_sender.domain;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LineMessageTemplate {
	@Id
	private String pushMessageId;
	@Convert(converter = PayloadTypeConverter.class)
	private PayloadType payloadType;
	private String payload;
	private Date createdAt;

	public String getPushMessageId() {
		return pushMessageId;
	}

	public void setPushMessageId(String pushMessageId) {
		this.pushMessageId = pushMessageId;
	}

	public PayloadType getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(PayloadType payloadType) {
		this.payloadType = payloadType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
