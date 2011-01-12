package com.legstar.xsd.def;


import com.legstar.xsd.AbstractXsdConfig;

/**
 * Properties used to provide COBOL specific attributes when they
 * cannot be inferred from the XML Schema.
 *
 */
public class DefaultXsdConfig extends AbstractXsdConfig {
	
    /** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** The cobol reserved words substitution file name. */
    public static final String OPTIONS_FILE_NAME = "options.properties";

	@Override
	public String getLocation() {
		return OPTIONS_FILE_NAME;
	}
    
	/**
	 * Default cobol alphanumeric length.
	 * 
	 * @return default cobol alphanumeric length
	 */
	public int getAlphanumericLen() {
		return Integer.parseInt(getProperty("default.alphanumeric.len", "32"));
	}

	/**
	 * Default cobol octet stream length.
	 * 
	 * @return default cobol octet stream length
	 */
	public int getOctetStreamLen() {
		return Integer.parseInt(getProperty("default.octet.stream.len", "32"));
	}

	/**
	 * Default cobol binary number of digits for integers.
	 * 
	 * @return default cobol binary number of digits for integers
	 */
	public int getIntTotalDigits() {
		return Integer.parseInt(getProperty("default.int.total.digits", "9"));
	}

	/**
	 * Default cobol binary number of digits for shorts.
	 * 
	 * @return default cobol binary number of digits for shorts
	 */
	public int getShortTotalDigits() {
		return Integer.parseInt(getProperty("default.short.total.digits", "4"));
	}

	/**
	 * Default cobol binary number of digits for a boolean.
	 * 
	 * @return default cobol binary number of digits for a boolean
	 */
	public int getBoolTotalDigits() {
		return Integer.parseInt(getProperty("default.bool.total.digits", "1"));
	}

	/**
	 * Default cobol binary number of digits for longs.
	 * 
	 * @return default cobol binary number of digits for longs
	 */
	public int getLongTotalDigits() {
		return Integer.parseInt(getProperty("default.long.total.digits", "18"));
	}

	/**
	 * Default cobol binary number of digits for decimals.
	 * 
	 * @return default cobol binary number of digits for decimals
	 */
	public int getDecTotalDigits() {
		return Integer.parseInt(getProperty("default.dec.total.digits", "9"));
	}

	/**
	 * Default cobol fraction number of digits for decimals.
	 * 
	 * @return default cobol fraction number of digits for decimals
	 */
	public int getDecFracDigits() {
		return Integer.parseInt(getProperty("default.dec.frac.digits", "2"));
	}

	/**
	 * Byte length of Cobol single float items.
	 * 
	 * @return byte length of Cobol single float items
	 */
	public int getSingleFloatByteLen() {
		return Integer.parseInt(getProperty("single.float.byte.len", "4"));
	}

	/**
	 * Byte length of Cobol single double float items.
	 * 
	 * @return byte length of Cobol single double float items
	 */
	public int getDoubleFloatByteLen() {
		return Integer.parseInt(getProperty("double.float.byte.len", "8"));
	}

	/**
	 * Default maximum number of items for arrays that mapped to unbounded.
	 * 
	 * @return default maximum number of items for arrays that mapped to unbounded
	 */
	public int getDefaultMaxOccurs() {
		return Integer.parseInt(getProperty("default.max.occurs", "10"));
	}

	

}
