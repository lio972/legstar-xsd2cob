package com.legstar.xsd;

import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;

import com.legstar.coxb.CobolMarkup;
import com.legstar.dom.DocumentFactory;

/**
 * Test the XsdReader.
 * 
 */
public class XsdReaderTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    public void testNoXsd() {
        try {
            Document doc = DocumentFactory.parse("<a>no xsd</a>");
            XsdReader.read(doc);
            fail();
        } catch (InvalidXsdException e) {
            assertEquals("Unable to locate an XML Schema in input document",
                    e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testXsd() throws Exception {
        parseAndCheck("simple.xsd");
    }

    public void testWsdl() throws Exception {
        parseAndCheck("simple.wsdl");
    }

    public void testWsdlWithTypeParts() throws Exception {
        parseAndCheck("stockquote.wsdl");
    }

    public void testXsdIncludes() throws Exception {
        parseAndCheck("include.xsd");
    }

    public void testAddNamespace() throws Exception {
        XmlSchema schema = parse("simple.xsd");
        assertTrue(toString(schema).contains(
                "xmlns:cb=\"" + CobolMarkup.NS + "\""));

        String prefix = null;
        /* Try again with same namespace */
        prefix = XsdReader.addNamespace("cb2", CobolMarkup.NS, schema);
        assertFalse(toString(schema).contains("xmlns:cb2="));
        assertEquals("cb", prefix);

        /* Try with same prefix and a different namespace */
        prefix = XsdReader.addNamespace("cb", "test2", schema);
        assertTrue(toString(schema).contains("xmlns:cb0=\"test2\""));
        assertEquals("cb0", prefix);
    }

}
