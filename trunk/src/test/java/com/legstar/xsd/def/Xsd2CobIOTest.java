package com.legstar.xsd.def;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * Test the Xsd2CobIO classs.
 * 
 */
public class Xsd2CobIOTest extends AbstractTest {

    private Xsd2CobIO _xsd2cob;

    public void setUp() throws Exception {
        _xsd2cob = new Xsd2CobIO(new Xsd2CobModel());
        if (GEN_XSD_FOLDER.exists()) {
            FileUtils.forceDelete(GEN_XSD_FOLDER);
        }
        if (GEN_COBOL_FOLDER.exists()) {
            FileUtils.forceDelete(GEN_COBOL_FOLDER);
        }
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
                (new File(XSD_FOLDER, "customertype.xsd")).toURI());
        try {
            _xsd2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals(
                    "No target folder or file was specified for COBOL-annotated XML schema",
                    e.getMessage());
        }

        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_FOLDER);
        try {
            _xsd2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals(
                    "No target folder or file was specified for COBOL copybook",
                    e.getMessage());
        }

        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_FOLDER);
        try {
            _xsd2cob.checkParameters();
        } catch (InvalidParameterException e) {
            fail();
        }
    }

    /**
     * Test the getLastSegment method.
     * 
     * @throws URISyntaxException if URI is invalid
     */
    public void testgetLastSegment() throws URISyntaxException {
        assertNull(_xsd2cob.getLastSegment(new URI("http://localhost")));
        assertNull(_xsd2cob.getLastSegment(new URI("uri:localhost")));
        assertEquals("toto",
                _xsd2cob.getLastSegment(new URI("http://localhost/toto")));
        assertEquals("toto",
                _xsd2cob.getLastSegment(new URI("http://localhost/toto/")));
        assertEquals("tata",
                _xsd2cob.getLastSegment(new URI("http://localhost/toto/tata")));
        assertEquals("tata", _xsd2cob.getLastSegment(new URI(
                "http://localhost/toto/tata.xsd")));
    }

    /**
     * Test the GetFile method.
     * 
     * @throws IOException if tests fails
     */
    public void testGetFile() throws IOException {
        try {
            _xsd2cob.getFile(GEN_XSD_FOLDER, null);
        } catch (IOException e) {
            assertEquals("No default file name was provided", e.getMessage());
        }
        _xsd2cob.getFile(GEN_XSD_FOLDER, "toto");
        assertTrue(GEN_XSD_FOLDER.exists());

        _xsd2cob.getFile(new File(GEN_COBOL_FOLDER, "copyb.cpy"), null);
        assertTrue(GEN_COBOL_FOLDER.exists());

    }

    /**
     * Test the writeResults method.
     * 
     * @throws IOException if test fails
     */
    public void testWriteResults() throws IOException {
        _xsd2cob.getModel().setInputXsdUri(
                (new File(XSD_FOLDER, "customertype.xsd")).toURI());
        _xsd2cob.getModel().setTargetXsdFile(GEN_XSD_FOLDER);
        _xsd2cob.getModel().setTargetCobolFile(GEN_COBOL_FOLDER);

        XsdToCobolStringResult results = new XsdToCobolStringResult("<a></a>",
                "     01 A PIC X.");
        _xsd2cob.writeResults(results);

        String xsdContent = FileUtils.readFileToString(new File(GEN_XSD_FOLDER,
                "customertype.xsd"));
        assertEquals("<a></a>", xsdContent);
        String cobolContent = FileUtils.readFileToString(new File(
                GEN_COBOL_FOLDER, "customertype.cpy"));
        assertEquals("     01 A PIC X.", cobolContent);
    }

    public void testTranslation() {

    }

}
