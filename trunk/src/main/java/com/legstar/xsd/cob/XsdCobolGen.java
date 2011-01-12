package com.legstar.xsd.cob;

import java.io.IOException;
import java.io.StringWriter;

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
 * Produces a COBOL copybook using a COBOL-annotated XML schema.
 * <p/>
 * Meant to work with an <code>XsdNavigator</code> which guarantees that items
 * are visited in the correct order.
 * <p/>
 * Extracts the COBOL annotations and restore the COBOL statement. TODO partial
 * implementation for now
 */
public class XsdCobolGen implements XsdObjectProcessor {

    /** All output goes to the writer. Use toString to get the COBOL */
    StringWriter _writer = new StringWriter();

    /** {@inheritDoc} */
    public void processElement(
            final XmlSchema schema,
            final XmlSchemaElement xsdElement,
            final int level) throws XsdMappingException {

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
                        writeElement((Element) node, level);
                    }
                }
            }
        }

    }

    /**
     * Recreates a COBOL data item declaration statement using COBOL
     * annotations.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @param level the depth in the hierarchy
     */
    protected void writeElement(final Element elc, final int level) {
        String cobolName = elc.getAttribute(CobolMarkup.COBOL_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append(indent(level));
        sb.append(String.format("%02d  %s", level, cobolName));
        sb.append(picture(elc));
        sb.append(usage(elc));
        sb.append(occurs(elc));
        sb.append(".");

        writeIn72cols(sb);
    }

    /**
     * Writes a statement, splitting it if it goes beyond column 72.
     * FIXME will incorrectly split VALUES
     * 
     * @param sb the current statement
     */
    protected void writeIn72cols(final StringBuilder sb) {
        if (sb.length() > 72) {
            for (int i = 71; i > -1; i--) {
                if (sb.charAt(i) == ' ') {
                    _writer.write(sb.substring(0, i) + "\n");
                    StringBuilder rest = new StringBuilder(
                            "            " + sb.substring(i));
                    writeIn72cols(rest);
                }
            }
        } else {
            _writer.write(sb.toString() + "\n");
        }

    }

    /**
     * Recreate an OCCURS clause.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @return an OCCURS clause or empty string
     */
    protected String occurs(final Element elc) {
        if (elc.getAttribute(CobolMarkup.MIN_OCCURS).length() == 0) {
            return "";
        }
        int minOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MIN_OCCURS));
        int maxOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MAX_OCCURS));
        String depending = elc.getAttribute(CobolMarkup.DEPENDING_ON);
        if (maxOccurs > minOccurs || minOccurs > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(" OCCURS ");
            if (maxOccurs == minOccurs) {
                sb.append(String.format("%d TIMES", maxOccurs));
            } else {
                sb.append(String.format("%d TO %d", minOccurs, maxOccurs));
                if (depending != null && depending.length() > 0) {
                    sb.append(String.format(" DEPENDING ON %s", depending));
                }
            }
            return sb.toString();
        } else {
            return "";
        }

    }

    /**
     * Recreate an PICTURE clause.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @return an PICTURE clause or empty string
     */
    protected String picture(final Element elc) {
        String picture = elc.getAttribute(CobolMarkup.PICTURE);
        String isSigned = elc.getAttribute(CobolMarkup.IS_SIGNED);

        if (picture.length() == 0) {
            return picture;
        }
        return " PIC " + ((isSigned.equals("true") ? "S" : "")) + picture;
    }

    /**
     * Recreate an USAGE clause when different from default.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @return an USAGE clause or empty string
     */
    protected String usage(final Element elc) {
        String usage = elc.getAttribute(CobolMarkup.USAGE);
        if (usage.length() == 0 || usage.equals("DISPLAY")) {
            return "";
        }
        return " " + usage;

    }

    /**
     * Calculate an indentation factor based on the hierarchy level.
     * 
     * @param levelNumber the level number
     * @return an indentation string
     */
    protected String indent(final int levelNumber) {
        StringBuffer sb = new StringBuffer();
        sb.append("      ");
        for (int i = 0; i < levelNumber; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    public void processComplexType(
            XmlSchema schema,
            XmlSchemaComplexType xsdComplexType,
            int level)
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
