package jp.co.tdc.line_message_sender.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import jp.co.tdc.line_message_sender.config.LineProperties;
import jp.co.tdc.line_message_sender.domain.LineChannelToken;
import jp.co.tdc.line_message_sender.domain.LineChannelTokenRepository;
import jp.co.tdc.line_message_sender.domain.PayloadType;
import jp.co.tdc.line_message_sender.domain.TargetType;
import jp.co.tdc.line_message_sender.line.bot.client.LineMessagingClient;
import jp.co.tdc.line_message_sender.line.bot.model.Message;
import jp.co.tdc.line_message_sender.line.bot.model.PushMessage;
import jp.co.tdc.line_message_sender.line.bot.model.TextMessage;

@Service
public class PushMessagesCommandService implements CommandService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushMessagesCommandService.class);

	private static final String TAG_OPTION_NAME = "tag";
	private static final String FIND_PUSH_MESSAGES_SQL = "SELECT push_message_id, target_type, target, template_id FROM line_push_message WHERE channel_id = ? AND tag = ? AND sent_at IS NULL AND error_at IS NULL ORDER BY created_at ASC";
	private static final String FIND_MESSAGE_TEMPLATE_SQL = "SELECT payload_type, payload FROM line_message_template WHERE template_id = ?";
	private static final String PATCH_PUSH_MESSAGE_SQL = "UPDATE line_push_message SET sent_at = ?, error_at = ? WHERE push_message_id = ?";

	@Autowired
	private LineProperties lineProperties;

	@Autowired
	private LineChannelTokenRepository lineChannelTokenRepository;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private LineMessagingComponent lineMessagingComponent;

	@Override
	public void run(ApplicationArguments args) {
		LOGGER.info("Push messages start");

		// 実行時引数から必要なパラメータを抽出
		List<String> tagOptionValues = args.getOptionValues(TAG_OPTION_NAME);

		if (tagOptionValues == null || tagOptionValues.isEmpty()) {
			throw new CommandServiceException("\"--tag=<tag>\" is not specified");
		}

		String tag = tagOptionValues.get(0);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("channelId={} tag=\"{}\"", lineProperties.getChannelId(), tag);
		}

		// 最新のアクセストークンをデータベースから取得
		LineChannelToken token = lineChannelTokenRepository.findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAtDesc(lineProperties.getChannelId());

		if (token == null) {
			throw new CommandServiceException("Token not found - channelId=" + lineProperties.getChannelId());
		}

		LOGGER.info("Found latest channel token - channelTokenId={}", token.getChannelTokenId());

		LineMessagingClient client = new LineMessagingClient(token.getToken());

		// Push APIのコール回数をカウント開始した時刻 (エポックミリ秒)
		long apiCallCountBaseTimeInMillis;

		// コマンド中のPush APIコール回数
		int apiCallCountPerMinute;

		int sentCount = 0;
		int errorCount = 0;

		try {
			try(Connection connection = dataSource.getConnection()) {
				connection.setAutoCommit(true);

				try (
					PreparedStatement findPushMessagesStatement = connection.prepareStatement(FIND_PUSH_MESSAGES_SQL);
					PreparedStatement findMessageTemplateStatement = connection.prepareStatement(FIND_MESSAGE_TEMPLATE_SQL);
					PreparedStatement patchPushMessageStatement = connection.prepareStatement(PATCH_PUSH_MESSAGE_SQL)
				) {
					// Push 対象のメッセージを検索
					findPushMessagesStatement.setString(1, lineProperties.getChannelId());
					findPushMessagesStatement.setString(2, tag);

					try (ResultSet pushMessageResultSet = findPushMessagesStatement.executeQuery()) {
						apiCallCountBaseTimeInMillis = System.currentTimeMillis();
						apiCallCountPerMinute = 0;

						while(pushMessageResultSet.next()) {
							// Push APIのコール回数が設定値を超えた場合、1分間 - APIコールにかかった時間だけスリープする
							if (apiCallCountPerMinute >= lineProperties.getPushRatePerMinute()) {
								long span = System.currentTimeMillis() - apiCallCountBaseTimeInMillis;
								long delay = 60000 - span;

								if (delay > 0) {
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("API rate over - delay={}", delay);
									}

									try {
										Thread.sleep(delay);
									} catch(InterruptedException e) {
										LOGGER.warn("Thread sleep interrupted");
									}
								}

								apiCallCountBaseTimeInMillis = System.currentTimeMillis();
								apiCallCountPerMinute = 0;
							}

							String pushMessageId = pushMessageResultSet.getString(1);
							String targetType = pushMessageResultSet.getString(2);
							String target = pushMessageResultSet.getString(3);
							String templateId = pushMessageResultSet.getString(4);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("pushMessageId={} targetType={} target={} templateId={}", pushMessageId, targetType, target, templateId);
							}

							try {
								String payloadType = null;
								String payload = null;

								// 送信対象のメッセージテンプレートを検索
								findMessageTemplateStatement.setString(1, templateId);

								try (ResultSet messageTemplateResultSet = findMessageTemplateStatement.executeQuery()) {
									if (messageTemplateResultSet.next()) {
										payloadType = messageTemplateResultSet.getString(1);
										payload = messageTemplateResultSet.getString(2);
									}
								}

								if (payloadType == null || payload == null) {
									LOGGER.warn("Message template \"" + templateId + "\" not found - pushMessageId={}", pushMessageId);

									throw new PushMessageException("Message template \"" + templateId + "\" not found");
								}

								// 送信するメッセージを組み立て
								Message message = null;

								if (StringUtils.equals(payloadType, PayloadType.text.name())) {
									message = new TextMessage(payload);
								}

								if (StringUtils.equals(targetType, TargetType.to.name())) {
									// ユーザID, グループID, ルームIDのいずれかを対象に送信
									PushMessage pushMessage = new PushMessage(target, message);

									try {
										lineMessagingComponent.pushMessage(client, pushMessage);
										apiCallCountPerMinute++;
									} catch (RestClientException e) {
										LOGGER.warn("Push API error - pushMessageId={} message=\"{}\"", pushMessageId, e.getMessage());

										throw new PushMessageException("Push API error");
									}
								} else {
									LOGGER.warn("Target type \"" + targetType + "\" not defined - pushMessageId={}", pushMessageId);

									throw new PushMessageException("Target type \"" + targetType + "\" not defined");
								}

								// メッセージの送信時刻を更新
								patchPushMessageStatement.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
								patchPushMessageStatement.setDate(2, null);
								patchPushMessageStatement.setString(3, pushMessageId);
								patchPushMessageStatement.executeUpdate();

								sentCount++;
							} catch (PushMessageException e) {
								// メッセージのエラー発生時刻を更新
								patchPushMessageStatement.setDate(1, null);
								patchPushMessageStatement.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
								patchPushMessageStatement.setString(3, pushMessageId);
								patchPushMessageStatement.executeUpdate();

								errorCount++;
							}
						}
					}
				}
			} catch(SQLException e) {
				throw new CommandServiceException("Database error", e);
			}
		} finally {
			LOGGER.info("sentCount={}, errorCount={}", sentCount, errorCount);
		}

		LOGGER.info("Push messages finished");
	}

	private static class PushMessageException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public PushMessageException(String message) {
			super(message);
		}
	}
}
