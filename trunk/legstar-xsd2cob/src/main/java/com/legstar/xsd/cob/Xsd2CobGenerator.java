package com.legstar.xsd.cob;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Element;

import com.legstar.coxb.CobolMarkup;
import com.legstar.xsd.XsdMappingException;
import com.legstar.xsd.XsdNavigator;

/**
 * Produces a COBOL copybook using a COBOL-annotated XML schema.
 * <p/>
 * Meant to work with an <code>XsdNavigator</code> which guarantees that items
 * are visited in the correct order.
 * <p/>
 * Extracts the COBOL annotations and restore the COBOL statement. TODO partial
 * implementation for now
 */
public class Xsd2CobGenerator extends AbstractXsd2CobGenerator {

    /** Will contain the root level. */
    private int rootLevel = 100;

    /** These needs to be included before the first child of root items. */
    private String dynamicCounters;

    /**
     * Recreates a COBOL data item declaration statement using COBOL
     * annotations.
     * <p/>
     * Dynamic counters are generated for each root element but they are written
     * on the first child of that root (because we need the child level).
     * 
     * @param schema the XML Schema being processed
     * @param xsdElement the XML Schema element to process
     * @param writer the writer to generate in
     * @param elc the COBOL annotations as a set of DOM attributes
     * @param level the depth in the hierarchy
     * @throws XsdMappingException if writing fails
     */
    public void writeElement(XmlSchema schema, XmlSchemaElement xsdElement,
            Writer writer, final Element elc, final int level)
            throws XsdMappingException {

        if (level > rootLevel && dynamicCounters != null
                && dynamicCounters.length() > 0) {
            writeDynamicCounters(writer, level);
            dynamicCounters = null;
        }

        String cobolName = elc.getAttribute(CobolMarkup.COBOL_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append(indent(level));
        sb.append(String.format("%02d  %s", level, cobolName));
        sb.append(picture(elc));
        sb.append(usage(elc));
        sb.append(occurs(elc));
        sb.append(".");

        writeIn72cols(writer, sb.toString());

        if (level <= rootLevel) {
            rootLevel = level;
            StringWriter sw = new StringWriter();
            fetchDynamicCounters(sw, schema, xsdElement, level);
            dynamicCounters = sw.toString();
        }
    }

    /**
     * Writes a statement, splitting it if it goes beyond column 72. FIXME will
     * incorrectly split VALUES
     * 
     * @param writer the writer to generate in
     * @param s the current statement
     * @throws IOException if writing fails
     */
    protected void writeIn72cols(final Writer writer, final String s)
            throws XsdMappingException {
        try {
            if (s.length() > 72) {
                for (int i = 71; i > -1; i--) {
                    if (s.charAt(i) == ' ') {
                        writer.write(s.substring(0, i) + "\n");
                        String rest = "            " + s.substring(i);
                        writeIn72cols(writer, rest);
                        break;
                    }
                }
            } else {
                writer.write(s.toString() + "\n");
            }
        } catch (IOException e) {
            throw new XsdMappingException(e);
        }

    }

    /**
     * Produces dynamic counters for variable size arrays without explicit
     * DEPENDING ON clause.
     * 
     * @param writer the output writer
     * @param schema the XML schema
     * @param xsdElement the XML Schema element to process
     * @param level the depth in the hierarchy
     * @throws XsdMappingException if writing fails
     */
    protected void fetchDynamicCounters(Writer writer, XmlSchema schema,
            XmlSchemaElement xsdElement, final int level)
            throws XsdMappingException {
        try {
            Xsd2CobCounters countersGen = new Xsd2CobCounters();
            XsdNavigator navigator = new XsdNavigator(schema, countersGen);
            navigator.processElement(xsdElement, level);
            writer.write(countersGen.toString());
        } catch (IOException e) {
            throw new XsdMappingException(e);
        }
    }

    /**
     * Outputs the dynamic counters at the same level as the first child.
     * 
     * @param writer the output writer
     * @param level the depth in the hierarchy
     * @throws XsdMappingException if writing fails
     */
    protected void writeDynamicCounters(Writer writer, final int level)
            throws XsdMappingException {
        try {
            String[] counters = dynamicCounters.split(",");
            for (String counter : counters) {
                writer.write(String.format("%s %02d  %s PIC 9(9) COMP.\n",
                        indent(level), level, counter));
            }
        } catch (IOException e) {
            throw new XsdMappingException(e);
        }
    }

    /**
     * Recreate an OCCURS clause.
     * <p/>
     * If size is variable and there are no depending ons, generate one
     * referring to a dynamic counter.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @return an OCCURS clause or empty string
     */
    protected String occurs(final Element elc) {
        if (elc.getAttribute(CobolMarkup.MIN_OCCURS).length() == 0) {
            return "";
        }
        String cobolName = elc.getAttribute(CobolMarkup.COBOL_NAME);
        int minOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MIN_OCCURS));
        int maxOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MAX_OCCURS));
        String depending = elc.getAttribute(CobolMarkup.DEPENDING_ON);
        if (depending == null || depending.length() == 0) {
            depending = Xsd2CobCounters.getCounterCobolName(cobolName);
        }
        if (maxOccurs > minOccurs || minOccurs > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(" OCCURS ");
            if (maxOccurs == minOccurs) {
                sb.append(String.format("%d TIMES", maxOccurs));
            } else {
                sb.append(String.format("%d TO %d", minOccurs, maxOccurs));
                sb.append(String.format(" DEPENDING ON %s", depending));
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

        if (picture.length() == 0) {
            return picture;
        }
        return " PIC " + picture;
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

}
