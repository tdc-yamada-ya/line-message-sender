package jp.co.tdc.line_message_sender.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import jp.co.tdc.line_message_sender.config.LineProperties;
import jp.co.tdc.line_message_sender.domain.LineChannelToken;
import jp.co.tdc.line_message_sender.domain.LineChannelTokenRepository;
import jp.co.tdc.line_message_sender.domain.PayloadType;
import jp.co.tdc.line_message_sender.domain.TargetType;
import jp.co.tdc.line_message_sender.line.bot.client.LineMessagingClient;
import jp.co.tdc.line_message_sender.line.bot.model.Message;
import jp.co.tdc.line_message_sender.line.bot.model.PushMessage;
import jp.co.tdc.line_message_sender.line.bot.model.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

@Service
public class SendCommandHandleService implements CommandHandleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendCommandHandleService.class);

	@Autowired
	private LineProperties lineProperties;

	@Autowired
	private LineChannelTokenRepository lineChannelTokenRepository;

	@Autowired
	private DataSource dataSource;

	@Override
	public void run(ApplicationArguments args) {
		LineChannelToken token = lineChannelTokenRepository.findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAt(lineProperties.getChannelId());

		if (token == null) {
			throw new CommandHandleServiceException("Token not found - channelId=" + lineProperties.getChannelId());
		}

		LineMessagingClient lineMessagingClient = new LineMessagingClient(token.getToken());
		long apiCallCountBaseTimeInMillis;
		int apiCallCountPerMinute;
		int sentCount = 0;
		int errorCount = 0;

		try {
			try(Connection connection = dataSource.getConnection()) {
				connection.setAutoCommit(true);

				try (
					PreparedStatement selectPushMessageStatement = connection.prepareStatement(
							"SELECT push_message_id, target_type, target, template_id FROM line_push_message WHERE channel_id = ? AND sent_at IS NULL AND error_at IS NULL");
					PreparedStatement selectMessageTemplateStatement = connection.prepareStatement(
							"SELECT payload_type, payload FROM line_message_template WHERE template_id = ?");
					PreparedStatement updatePushMessageStatement = connection.prepareStatement(
							"UPDATE line_push_message SET sent_at = ?, error_at = ? WHERE push_message_id = ?")
				) {
					selectPushMessageStatement.setString(1, lineProperties.getChannelId());

					try (ResultSet pushMessageResultSet = selectPushMessageStatement.executeQuery()) {
						apiCallCountBaseTimeInMillis = System.currentTimeMillis();
						apiCallCountPerMinute = 0;

						while(pushMessageResultSet.next()) {
							String pushMessageId = pushMessageResultSet.getString(1);
							String targetType = pushMessageResultSet.getString(2);
							String target = pushMessageResultSet.getString(3);
							String templateId = pushMessageResultSet.getString(4);

							try {
								String payloadType = null;
								String payload = null;

								selectMessageTemplateStatement.setString(1, templateId);

								try (ResultSet messageTemplateResultSet = selectMessageTemplateStatement.executeQuery()) {
									if (messageTemplateResultSet.next()) {
										payloadType = messageTemplateResultSet.getString(1);
										payload = messageTemplateResultSet.getString(2);
									}
								}

								if (payloadType == null || payload == null) {
									throw new PushMessageException("Message template not found - templateId=" + templateId);
								}

								Message message = null;

								if (StringUtils.equals(payloadType, PayloadType.text.name())) {
									message = new TextMessage(payload);
								}

								if (StringUtils.equals(targetType, TargetType.to.name())) {
									PushMessage pushMessage = new PushMessage(target, message);

									apiCallCountPerMinute++;
									lineMessagingClient.push(pushMessage);
								} else {
									throw new PushMessageException("TargetType not defined - targetType=" + targetType);
								}

								updatePushMessageStatement.setDate(1, new java.sql.Date(new Date().getTime()));
								updatePushMessageStatement.setDate(2, null);
								updatePushMessageStatement.setString(3, pushMessageId);
								updatePushMessageStatement.executeUpdate();

								sentCount++;
							} catch (PushMessageException e) {
								LOGGER.warn("Push message failed - pushMessageId={} message={}", pushMessageId, e.getMessage());

								updatePushMessageStatement.setDate(1, null);
								updatePushMessageStatement.setDate(2, new java.sql.Date(new Date().getTime()));
								updatePushMessageStatement.setString(3, pushMessageId);
								updatePushMessageStatement.executeUpdate();

								errorCount++;
							}

							if (apiCallCountPerMinute >= lineProperties.getPushRatePerMinute()) {
								long span = System.currentTimeMillis() - apiCallCountBaseTimeInMillis;
								long delay = 60000 - span;

								if (delay > 0) {
									try {
										Thread.sleep(delay);
									} catch(InterruptedException e) {
										LOGGER.warn("Thread sleep interrupted");
									}
								}

								apiCallCountBaseTimeInMillis = System.currentTimeMillis();
								apiCallCountPerMinute = 0;
							}
						}
					}
				}
			} catch(SQLException e) {
				throw new CommandHandleServiceException("Database error", e);
			}
		} finally {
			LOGGER.info("sentCount={}, errorCount={}", sentCount, errorCount);
		}
	}

	private class PushMessageException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public PushMessageException(String message) {
			super(message);
		}
	}
}
