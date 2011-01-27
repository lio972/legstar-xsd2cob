package com.legstar.xsd.def;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.XsdRootElement;

/**
 * Test the Xsd2CobIO classs.
 * 
 */
public class Xsd2CobIOTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    private Xsd2CobIO _xsd2cob;

    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
        _xsd2cob = new Xsd2CobIO(new Xsd2CobModel());
    }

    /**
     * Test parameters checking.
     * 
     * @throws Exception if testing fails
     */
    public void testCheckParameters() throws Exception {
        try {
            _xsd2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals("No input URI specified", e.getMessage());
        }

        _xsd2cob.getModel().setInputXsdUri(
                (new File(XSD_DIR, "customertype.xsd")).toURI());
        try {
            _xsd2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals(
                    "No target folder or file was specified for COBOL-annotated XML schema",
                    e.getMessage());
        }

        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_DIR);
        try {
            _xsd2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals(
                    "No target folder or file was specified for COBOL copybook",
                    e.getMessage());
        }

        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_DIR);
        try {
            _xsd2cob.checkParameters();
        } catch (InvalidParameterException e) {
            fail();
        }
    }

    /**
     * Test the getDefaultName method.
     * 
     * @throws URISyntaxException if URI is invalid
     */
    public void testgetDefaultName() throws URISyntaxException {
        assertNull(_xsd2cob.getDefaultName(new URI("http://localhost")));
        assertNull(_xsd2cob.getDefaultName(new URI("uri:localhost")));
        assertEquals("toto",
                _xsd2cob.getDefaultName(new URI("http://localhost/toto")));
        assertEquals("toto",
                _xsd2cob.getDefaultName(new URI("http://localhost/toto/")));
        assertEquals("tata",
                _xsd2cob.getDefaultName(new URI("http://localhost/toto/tata")));
        assertEquals("tata", _xsd2cob.getDefaultName(new URI(
                "http://localhost/toto/tata.xsd")));
    }

    /**
     * Test a complete translation for an XSD without root.
     * 
     * @throws Exception if test fails
     */
    public void testXsdNoRootTranslation() throws Exception {
        _xsd2cob.getModel().setInputXsdUri(
                (new File(XSD_DIR, "customertype.xsd")).toURI());
        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_DIR);
        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_DIR);
        _xsd2cob.getModel().addNewRootElement(
                new XsdRootElement("customer", "CustomerType"));

        _xsd2cob.execute();

        check("customertype", "xsd", FileUtils.readFileToString(new File(
                GEN_XSD_DIR, "customertype.xsd"), "UTF-8"));
        check("customertype", "cpy", FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "customertype.cpy"), "UTF-8"));
    }

    /**
     * Test a complete translation for an XSD with root.
     * 
     * @throws Exception if test fails
     */
    public void testXsdRootTranslation() throws Exception {
        _xsd2cob.getModel().setInputXsdUri(
                (new File(XSD_DIR, "customer.xsd")).toURI());
        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_DIR);
        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_DIR);

        _xsd2cob.execute();

        check("customer", "xsd", FileUtils.readFileToString(new File(
                GEN_XSD_DIR, "customer.xsd"), "UTF-8"));
        check("customer", "cpy", FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "customer.cpy"), "UTF-8"));
    }

    /**
     * Test a complete translation for a WSDL.
     * 
     * @throws Exception if test fails
     */
    public void testWsdlTranslation() throws Exception {
        _xsd2cob.getModel().setInputXsdUri(
                (new File(XSD_DIR, "stockquote.wsdl")).toURI());
        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_DIR);
        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_DIR);

        _xsd2cob.execute();

        check("stockquote", "xsd", FileUtils.readFileToString(new File(
                GEN_XSD_DIR, "stockquote.xsd"), "UTF-8"));
        check("stockquote", "cpy", FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "stockquote.cpy"), "UTF-8"));
    }
}
