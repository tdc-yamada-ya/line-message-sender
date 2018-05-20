package jp.co.tdc.line_message_sender.domain;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LinePushMessage {
	@Id
	private String pushMessageId;
	private String channelId;
	private String tag;
	@Convert(converter = TargetTypeConverter.class)
	private TargetType targetType;
	private String target;
	private String templateId;
	private Date createdAt;
	private Date sentAt;
	private Date errorAt;

	public String getPushMessageId() {
		return pushMessageId;
	}

	public void setPushMessageId(String pushMessageId) {
		this.pushMessageId = pushMessageId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getSentAt() {
		return sentAt;
	}

	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	public Date getErrorAt() {
		return errorAt;
	}

	public void setErrorAt(Date errorAt) {
		this.errorAt = errorAt;
	}
}
