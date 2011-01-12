package com.legstar.dom;

import java.io.File;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.legstar.xsd.XsdConstants;

public class DocumentFactoryTest extends TestCase {

    /** Test cases folder. */
    public static final File XSD_FOLDER = new File("src/test/resources/cases");

    public void testParseFailure() throws Exception {
        try {
            DocumentFactory.parse("<a>broken xml</b>");
            fail();
        } catch (Exception e) {
            assertEquals(
                    "The element type \"a\" must be terminated by the matching end-tag \"</a>\".",
                    e.getMessage());
        }

    }

    public void testParseSuccess() throws Exception {
        Document doc = DocumentFactory.parse(new File(XSD_FOLDER,
                "MSNSearch.wsdl"));
        assertNotNull(doc);

        NodeList nodes = doc.getElementsByTagNameNS(
                XsdConstants.WSDL_NS, "definitions");
        assertNotNull(nodes);
        assertEquals(1, nodes.getLength());

        assertTrue(nodes.item(0) instanceof Element);
        nodes.item(0).getAttributes();

    }

}
