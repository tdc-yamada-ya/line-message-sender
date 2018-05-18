package jp.co.tdc.line_message_sender.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineChannelTokenRepository extends JpaRepository<LineChannelToken, String> {
	/**
	 * 指定したチャンネルIDにおける最新のトークンを取得
	 *
	 * @param channelId チャンネルID
	 * @return 最新のトークン
	 */
	public LineChannelToken findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAtDesc(String channelId);
}
