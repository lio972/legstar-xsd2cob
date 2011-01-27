package com.legstar.xsd.java;

import java.util.Arrays;
import java.util.List;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * Test the Java2Cob API.
 * 
 */
public class Java2CobTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    public void testCultureinfo() throws Exception {

        Java2Cob java2cob = new Java2Cob();

        List < String > classNames = Arrays.asList(new String[] {
                "com.legstar.xsdc.test.cases.cultureinfo.CultureInfoRequest",
                "com.legstar.xsdc.test.cases.cultureinfo.CultureInfoReply" });
        XsdToCobolStringResult results = java2cob.translate(classNames);

        check("cultureinfo", "xsd", results.getCobolXsd());
        check("cultureinfo", "cpy", results.getCobolStructure());

    }

}
