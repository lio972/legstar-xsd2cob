package com.legstar.xsd;

/**
 * Unable to map an XML Schema element or attribute to
 * a COBOL data item or attribute.
 *
 */
public class XsdMappingException extends Exception {

	/**
	 * A serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	public XsdMappingException(final String message) {
		super(message);
	}

	public XsdMappingException(final String message, Throwable cause) {
		super(message, cause);
	}

	public XsdMappingException(Throwable cause) {
		super(cause);
	}
}
