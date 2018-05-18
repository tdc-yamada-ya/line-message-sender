package jp.co.tdc.line_message_sender.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineChannelCredentialRepository extends JpaRepository<LineChannelCredential, String> {
	/**
	 * 指定したチャンネルIDにおける最新のクレデンシャルを取得
	 *
	 * @param channelId チャンネルID
	 * @return 最新のクレデンシャル
	 */
	public LineChannelCredential findTopByChannelIdAndRevokedAtIsNullOrderByCreatedAtDesc(String channelId);
}
