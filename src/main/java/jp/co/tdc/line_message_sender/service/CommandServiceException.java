package jp.co.tdc.line_message_sender.service;

public class CommandServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommandServiceException(String message) {
		super(message);
	}

	public CommandServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
