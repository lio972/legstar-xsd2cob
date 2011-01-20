package com.legstar.dom;

/**
 * Something is wrong with the input document. It cannot be parsed
 * or does not contain a valid XML Schema.
 *
 */
public class InvalidDocumentException extends Exception {

	/**
	 * A serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidDocumentException(final String message) {
		super(message);
	}

	public InvalidDocumentException(final String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDocumentException(Throwable cause) {
		super(cause);
	}
}
