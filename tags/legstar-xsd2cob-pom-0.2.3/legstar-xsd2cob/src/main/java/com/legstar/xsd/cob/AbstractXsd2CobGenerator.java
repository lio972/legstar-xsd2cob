package com.legstar.xsd.cob;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.legstar.coxb.CobolMarkup;
import com.legstar.xsd.XsdMappingException;
import com.legstar.xsd.XsdObjectProcessor;

/**
 * Some common code shared by COBOL generators
 */
public abstract class AbstractXsd2CobGenerator implements XsdObjectProcessor {

    /** All output goes to the writer. Use toString to get the COBOL */
    private StringWriter _writer = new StringWriter();

    /** {@inheritDoc} */
    public void processElement(final XmlSchema schema,
            final XmlSchemaElement xsdElement, final int level)
            throws XsdMappingException {

        XmlSchemaAnnotation annotation = xsdElement.getAnnotation();
        if (annotation != null && annotation.getItems().getCount() > 0) {
            XmlSchemaAppInfo appinfo = (XmlSchemaAppInfo) annotation.getItems()
                    .getItem(0);
            if (appinfo.getMarkup() != null) {
                for (int i = 0; i < appinfo.getMarkup().getLength(); i++) {
                    Node node = appinfo.getMarkup().item(i);
                    if (node instanceof Element
                            && node.getLocalName().equals(CobolMarkup.ELEMENT)
                            && node.getNamespaceURI().equals(CobolMarkup.NS)) {
                        writeElement(schema, xsdElement, _writer,
                                (Element) node, level);
                    }
                }
            }
        }

    }

    /**
     * Write a COBOL clause fo an XML schema element. .
     * 
     * @param schema the XML Schema being processed
     * @param xsdElement the XML Schema element to process
     * @param writer the writer to generate in
     * @param elc the COBOL annotations as a set of DOM attributes
     * @param level the depth in the hierarchy
     * @throws XsdMappingException if writing fails
     */
    public abstract void writeElement(final XmlSchema schema,
            XmlSchemaElement xsdElement, Writer writer, final Element elc,
            final int level) throws XsdMappingException;

    /**
     * Calculate an indentation factor based on the hierarchy level.
     * 
     * @param levelNumber the level number
     * @return an indentation string
     */
    public String indent(final int levelNumber) {
        StringBuffer sb = new StringBuffer();
        sb.append("      ");
        for (int i = 0; i < levelNumber; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    public void processComplexType(XmlSchema schema,
            XmlSchemaComplexType xsdComplexType, int level)
            throws XsdMappingException {

    }

    /** {@inheritDoc} */
    public void setUp() throws IOException {
    }

    /** {@inheritDoc} */
    public String toString() {
        return _writer.toString();
    }

}
