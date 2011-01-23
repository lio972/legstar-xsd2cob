package com.legstar.xsd.def;

import java.util.Properties;

import com.legstar.xsd.AbstractXsdConfig;

/**
 * Properties used to provide COBOL specific attributes when they cannot be
 * inferred from the XML Schema.
 * 
 */
public class Xsd2CobConfig extends AbstractXsdConfig {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Properties file name. */
    public static final String OPTIONS_FILE_NAME = "options.properties";

    /* ====================================================================== */
    /* Following are default field values. = */
    /* ====================================================================== */

    /** Size for alphanumerics. */
    public static final String ALPHANUMERIC_LEN = "alphanumeric.len";

    /** Size for octet streams. */
    public static final String OCTET_STREAM_LEN = "octet.stream.len";

    /** Integers total digits. */
    public static final String INT_TOTAL_DIGITS = "int.total.digits";

    /** Shorts total digits. */
    public static final String SHORT_TOTAL_DIGITS = "short.total.digits";

    /** Booleans total digits. */
    public static final String BOOL_TOTAL_DIGITS = "bool.total.digits";

    /** Longs total digits. */
    public static final String LONG_TOTAL_DIGITS = "long.total.digits";

    /** Decimals total digits. */
    public static final String DEC_TOTAL_DIGITS = "dec.total.digits";

    /** Decimals fractional digits. */
    public static final String DEC_FRAC_DIGITS = "dec.frac.digits";

    /** Single floats byte length. */
    public static final String SINGLE_FLOAT_BYTE_LEN = "single.float.byte.len";

    /** Double floats byte length. */
    public static final String DOUBLE_FLOAT_BYTE_LEN = "double.float.byte.len";

    /** Arrays maximum occurences. */
    public static final String MAX_OCCURS = "max.occurs";

    /* ====================================================================== */
    /* Following are key identifiers for this model persistence. = */
    /* ====================================================================== */

    /** Default size for alphanumerics. */
    public static final int DEFAULT_ALPHANUMERIC_LEN = 32;

    /** Default size for octet streams. */
    public static final int DEFAULT_OCTET_STREAM_LEN = 32;

    /** Default integers total digits. */
    public static final int DEFAULT_INT_TOTAL_DIGITS = 9;

    /** Default shorts total digits. */
    public static final int DEFAULT_SHORT_TOTAL_DIGITS = 4;

    /** Default booleans total digits. */
    public static final int DEFAULT_BOOL_TOTAL_DIGITS = 1;

    /** Default longs total digits. */
    public static final int DEFAULT_LONG_TOTAL_DIGITS = 18;

    /** Default decimals total digits. */
    public static final int DEFAULT_DEC_TOTAL_DIGITS = 9;

    /** Default decimals fractional digits. */
    public static final int DEFAULT_DEC_FRAC_DIGITS = 2;

    /** Default single floats byte length. */
    public static final int DEFAULT_SINGLE_FLOAT_BYTE_LEN = 4;

    /** Default double floats byte length. */
    public static final int DEFAULT_DOUBLE_FLOAT_BYTE_LEN = 8;

    /** Default arrays maximum occurences. */
    public static final int DEFAULT_MAX_OCCURS = 10;

    /**
     * At construction time, we attempt to load from file system but
     * just ignore any failures (User did not provide a
     * configuration on file and wants to use defaults).
     */
    public Xsd2CobConfig() {
        super();
    }
    
    /**
     * Create a configuration from a previous set of properties.
     * @param props the previous property set
     */
    public Xsd2CobConfig(final Properties props) {
        super(props);
    }

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
        return getIntProperty(ALPHANUMERIC_LEN, DEFAULT_ALPHANUMERIC_LEN);
    }

    /**
     * Default cobol octet stream length.
     * 
     * @return default cobol octet stream length
     */
    public int getOctetStreamLen() {
        return getIntProperty(OCTET_STREAM_LEN, DEFAULT_OCTET_STREAM_LEN);
    }

    /**
     * Default cobol binary number of digits for integers.
     * 
     * @return default cobol binary number of digits for integers
     */
    public int getIntTotalDigits() {
        return getIntProperty(INT_TOTAL_DIGITS, DEFAULT_INT_TOTAL_DIGITS);
    }

    /**
     * Default cobol binary number of digits for shorts.
     * 
     * @return default cobol binary number of digits for shorts
     */
    public int getShortTotalDigits() {
        return getIntProperty(SHORT_TOTAL_DIGITS, DEFAULT_SHORT_TOTAL_DIGITS);
    }

    /**
     * Default cobol binary number of digits for a boolean.
     * 
     * @return default cobol binary number of digits for a boolean
     */
    public int getBoolTotalDigits() {
        return getIntProperty(BOOL_TOTAL_DIGITS, DEFAULT_BOOL_TOTAL_DIGITS);
    }

    /**
     * Default cobol binary number of digits for longs.
     * 
     * @return default cobol binary number of digits for longs
     */
    public int getLongTotalDigits() {
        return getIntProperty(LONG_TOTAL_DIGITS, DEFAULT_LONG_TOTAL_DIGITS);
    }

    /**
     * Default cobol binary number of digits for decimals.
     * 
     * @return default cobol binary number of digits for decimals
     */
    public int getDecTotalDigits() {
        return getIntProperty(DEC_TOTAL_DIGITS, DEFAULT_DEC_TOTAL_DIGITS);
    }

    /**
     * Default cobol fraction number of digits for decimals.
     * 
     * @return default cobol fraction number of digits for decimals
     */
    public int getDecFracDigits() {
        return getIntProperty(DEC_FRAC_DIGITS, DEFAULT_DEC_FRAC_DIGITS);
    }

    /**
     * Byte length of Cobol single float items.
     * 
     * @return byte length of Cobol single float items
     */
    public int getSingleFloatByteLen() {
        return getIntProperty(SINGLE_FLOAT_BYTE_LEN,
                DEFAULT_SINGLE_FLOAT_BYTE_LEN);
    }

    /**
     * Byte length of Cobol single double float items.
     * 
     * @return byte length of Cobol single double float items
     */
    public int getDoubleFloatByteLen() {
        return getIntProperty(DOUBLE_FLOAT_BYTE_LEN,
                DEFAULT_DOUBLE_FLOAT_BYTE_LEN);
    }

    /**
     * Default maximum number of items for arrays that mapped to unbounded.
     * 
     * @return default maximum number of items for arrays that mapped to
     *         unbounded
     */
    public int getMaxOccurs() {
        return getIntProperty(MAX_OCCURS, DEFAULT_MAX_OCCURS);
    }

    /**
     * Default cobol alphanumeric length.
     * 
     * @param value default cobol alphanumeric length
     */
    public void setAlphanumericLen(final int value) {
        setIntProperty(ALPHANUMERIC_LEN, value);
    }

    /**
     * Default cobol octet stream length.
     * 
     * @param value default cobol octet stream length
     */
    public void setOctetStreamLen(final int value) {
        setIntProperty(OCTET_STREAM_LEN, value);
    }

    /**
     * Default cobol binary number of digits for integers.
     * 
     * @param value default cobol binary number of digits for integers
     */
    public void setIntTotalDigits(final int value) {
        setIntProperty(INT_TOTAL_DIGITS, value);
    }

    /**
     * Default cobol binary number of digits for shorts.
     * 
     * @param value default cobol binary number of digits for shorts
     */
    public void setShortTotalDigits(final int value) {
        setIntProperty(SHORT_TOTAL_DIGITS, value);
    }

    /**
     * Default cobol binary number of digits for a boolean.
     * 
     * @param value default cobol binary number of digits for a boolean
     */
    public void setBoolTotalDigits(final int value) {
        setIntProperty(BOOL_TOTAL_DIGITS, value);
    }

    /**
     * Default cobol binary number of digits for longs.
     * 
     * @param value default cobol binary number of digits for longs
     */
    public void setLongTotalDigits(final int value) {
        setIntProperty(LONG_TOTAL_DIGITS, value);
    }

    /**
     * Default cobol binary number of digits for decimals.
     * 
     * @param value default cobol binary number of digits for decimals
     */
    public void setDecTotalDigits(final int value) {
        setIntProperty(DEC_TOTAL_DIGITS, value);
    }

    /**
     * Default cobol fraction number of digits for decimals.
     * 
     * @param value default cobol fraction number of digits for decimals
     */
    public void setDecFracDigits(final int value) {
        setIntProperty(DEC_FRAC_DIGITS, value);
    }

    /**
     * Byte length of Cobol single float items.
     * 
     * @param value byte length of Cobol single float items
     */
    public void setSingleFloatByteLen(final int value) {
        setIntProperty(SINGLE_FLOAT_BYTE_LEN, value);
    }

    /**
     * Byte length of Cobol single double float items.
     * 
     * @param value byte length of Cobol single double float items
     */
    public void setDoubleFloatByteLen(final int value) {
        setIntProperty(DOUBLE_FLOAT_BYTE_LEN, value);
    }

    /**
     * Default maximum number of items for arrays that mapped to unbounded.
     * 
     * @param value default maximum number of items for arrays that mapped to
     *         unbounded
     */
    public void setMaxOccurs(final int value) {
        setIntProperty(MAX_OCCURS, value);
    }
}
