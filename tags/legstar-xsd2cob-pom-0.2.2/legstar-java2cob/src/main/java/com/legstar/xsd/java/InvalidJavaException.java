package com.legstar.xsd.java;

import com.legstar.xsd.InvalidXsdException;

/**
 * Something is wrong with the input java classes.
 * 
 */
public class InvalidJavaException extends InvalidXsdException {

    /**
     * A serial ID.
     */
    private static final long serialVersionUID = 1L;

    public InvalidJavaException(final String message) {
        super(message);
    }

    public InvalidJavaException(final String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJavaException(Throwable cause) {
        super(cause);
    }
}
