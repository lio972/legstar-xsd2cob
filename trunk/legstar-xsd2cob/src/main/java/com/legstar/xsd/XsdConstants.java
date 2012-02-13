package com.legstar.xsd;

import com.legstar.coxb.CobolUsage.Usage;

/**
 * A set of constant values.
 * 
 */
public final class XsdConstants {

    /** XML Schema namespace. */
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    /** WSDL namespace. */
    public static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    /** Default COBOL annotations prefix. */
    public static final String DEFAULT_COXB_NS_PFX = "cb";

    /** Mark an element that was artifically injected in the XML schema. */
    public static final String INJECTED_ELEMENT = "uri:injected";

    /** How much to increment the level number when going down a hierarchy. */
    public static final int DEFAULT_LEVEL_INCREMENT = 2;

    /** The default COBOL level for root items. */
    public static final int DEFAULT_ROOT_LEVEL = 1;

    /**
     * The set of counters associated with variable size arrays are added to the
     * final COBOL structure under this group name
     */
    public static final String OCCURS_COUNTERS_GROUP_NAME = "OCCURS-COUNTERS--C";

    /**
     * Dynamic counters COBOL picture clause.
     */
    public static final String COUNTER_COBOL_PICTURE = "9(9)";

    /**
     * Dynamic counters COBOL usage clause.
     */
    public static final Usage COUNTER_COBOL_USAGE = Usage.NATIVEBINARY;

    /**
     * Dynamic counters also need a cobol name which is built from the
     * corresponding list or array cobol name plus this suffix.
     */
    public static final String COUNTER_COBOL_SUFFIX = "--C";

}
