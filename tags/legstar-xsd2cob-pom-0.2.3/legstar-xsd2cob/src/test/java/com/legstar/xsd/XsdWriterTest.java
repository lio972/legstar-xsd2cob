package com.legstar.xsd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test the XsdWriter class.
 * 
 */
public class XsdWriterTest extends AbstractTest {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * Test the GetFile method.
     * 
     * @throws IOException if tests fails
     */
    public void testGetFile() throws IOException {
        try {
            XsdWriter.getFile(GEN_XSD_DIR, null);
        } catch (IOException e) {
            assertEquals("No default file name was provided", e.getMessage());
        }
        XsdWriter.getFile(GEN_XSD_DIR, "toto");
        assertTrue(GEN_XSD_DIR.exists());

        XsdWriter.getFile(new File(GEN_COBOL_DIR, "copyb.cpy"), null);
        assertTrue(GEN_COBOL_DIR.exists());

    }

    /**
     * Test the writeResults method.
     * 
     * @throws IOException if test fails
     */
    public void testWriteResults() throws IOException {

        XsdToCobolStringResult results = new XsdToCobolStringResult("<a></a>",
                "     01 A PIC X.");
        XsdWriter.writeResults("customertype", GEN_XSD_DIR, GEN_COBOL_DIR,
                "ISO-8859-1", results, _log);

        String xsdContent = FileUtils.readFileToString(new File(GEN_XSD_DIR,
                "customertype.xsd"));
        assertEquals("<a></a>", xsdContent);
        String cobolContent = FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "customertype.cpy"));
        assertEquals("     01 A PIC X.", cobolContent);
    }

}
