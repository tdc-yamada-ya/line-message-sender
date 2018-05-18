package jp.co.tdc.line_message_sender;

public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
