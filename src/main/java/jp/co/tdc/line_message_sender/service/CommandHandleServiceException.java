package jp.co.tdc.line_message_sender.service;

public class CommandHandleServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommandHandleServiceException(String message) {
		super(message);
	}

	public CommandHandleServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
