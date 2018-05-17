package jp.co.tdc.line_message_sender.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinePushMessageRepository extends JpaRepository<LineMessageTemplate, String> {

}
