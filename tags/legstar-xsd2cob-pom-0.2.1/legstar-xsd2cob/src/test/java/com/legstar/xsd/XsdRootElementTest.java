package com.legstar.xsd;

import junit.framework.TestCase;

/**
 * Test the XsdRootElement.
 * 
 */
public class XsdRootElementTest extends TestCase {

    public void testToString() {

        XsdRootElement xsdRootElement = new XsdRootElement();
        assertEquals("null:null", xsdRootElement.toString());
        xsdRootElement.setElementName("elementName");
        assertEquals("elementName:null", xsdRootElement.toString());
        xsdRootElement.setTypeName("typeName");
        assertEquals("elementName:typeName", xsdRootElement.toString());

    }

    public void testFromString() {

        XsdRootElement xsdRootElement = new XsdRootElement(
                "elementName:typeName");
        assertEquals("elementName", xsdRootElement.getElementName());
        assertEquals("typeName", xsdRootElement.getTypeName());

        xsdRootElement = new XsdRootElement("elementName:");
        assertEquals("elementName", xsdRootElement.getElementName());
        assertEquals(null, xsdRootElement.getTypeName());

        xsdRootElement = new XsdRootElement("elementName");
        assertEquals("elementName", xsdRootElement.getElementName());
        assertEquals(null, xsdRootElement.getTypeName());

        xsdRootElement = new XsdRootElement(":");
        assertEquals(null, xsdRootElement.getElementName());
        assertEquals(null, xsdRootElement.getTypeName());
    }
}
