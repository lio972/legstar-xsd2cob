package com.legstar.xsd.def;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * Test the XsdToCobolStructure API.
 * 
 */
public class Xsd2CobTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    /**
     * Test MSNSearch generation.
     */
    public void testMSNSearch() throws Exception {
        Xsd2CobConfig config = new Xsd2CobConfig();

        Xsd2Cob api = new Xsd2Cob(config);

        XsdToCobolStringResult results = api.translate(FileUtils
                .readFileToString(new File(XSD_DIR, "MSNSearch.wsdl"),
                        "UTF-8"));
        check("MSNSearch.wsdl", "xsd", results.getCobolXsd());
        check("MSNSearch.wsdl", "cpy", results.getCobolStructure());
    }

}
