package com.legstar.xsd;

/**
 * Something is wrong with one of the parameters.
 * 
 */
public class InvalidParameterException extends Exception {

    /**
     * A serial ID.
     */
    private static final long serialVersionUID = 1L;

    public InvalidParameterException(final String message) {
        super(message);
    }

    public InvalidParameterException(final String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }
}
