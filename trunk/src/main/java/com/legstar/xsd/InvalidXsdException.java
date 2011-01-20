package com.legstar.xsd;

/**
 * Something is wrong with the input document. It cannot be parsed
 * or does not contain a valid XML Schema.
 *
 */
public class InvalidXsdException extends Exception {

	/**
	 * A serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidXsdException(final String message) {
		super(message);
	}

	public InvalidXsdException(final String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidXsdException(Throwable cause) {
		super(cause);
	}
}
