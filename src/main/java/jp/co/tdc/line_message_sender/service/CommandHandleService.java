package jp.co.tdc.line_message_sender.service;

import org.springframework.boot.ApplicationArguments;

public interface CommandHandleService {
	void run(ApplicationArguments args);
}
