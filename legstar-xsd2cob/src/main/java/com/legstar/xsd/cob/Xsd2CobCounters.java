package com.legstar.xsd.cob;

import java.io.IOException;
import java.io.Writer;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Element;

import com.legstar.coxb.CobolMarkup;
import com.legstar.xsd.XsdMappingException;

/**
 * Produces a list (comma-separated) of COBOL counters for variable size arrays.
 * <p/>
 * All variable size arrays without an explicit DEPENDING ON clause must be
 * assigned a dynamically built counter.
 * <p/>
 * These counters must be included at the the beginning of the generated
 * copybook.
 */
public class Xsd2CobCounters extends AbstractXsd2CobGenerator {

    /** Needed to comma-separate counters */
    private boolean firstCounter = true;

    /**
     * Dynamic counters also need a cobol name which is built from the
     * corresponding list or array cobol name plus this suffix.
     */
    private static final String COUNTER_COBOL_SUFFIX = "--C";

    /**
     * Writes dynamic counters COBOL names separated by commas.
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
        try {
            String counter = getDynamicCounter(elc);
            if (counter != null) {
                if (firstCounter) {
                    firstCounter = false;
                } else {
                    writer.write(",");
                }
                writer.write(counter);
            }
        } catch (IOException e) {
            throw new XsdMappingException(e);
        }
    }

    /**
     * Lookup for an OCCURS clause with variable size and no DEPENDING ON. If
     * this is a variable size array with no explicit DEPENDING ON clause,
     * generate a dynamic counter.
     * 
     * @param elc the COBOL annotations as a set of DOM attributes
     * @return an OCCURS clause or empty string
     */
    protected String getDynamicCounter(final Element elc) {
        if (elc.getAttribute(CobolMarkup.MIN_OCCURS).length() == 0) {
            return null;
        }
        String cobolName = elc.getAttribute(CobolMarkup.COBOL_NAME);
        int minOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MIN_OCCURS));
        int maxOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MAX_OCCURS));
        String depending = elc.getAttribute(CobolMarkup.DEPENDING_ON);
        if (maxOccurs > minOccurs
                && (depending == null || depending.length() == 0)) {
            return getCounterCobolName(cobolName);
        }
        return null;
    }

    /**
     * Dynamic counters need a unique Cobol name. This method determines such a
     * name based on the related array or list cobol name. This method does not
     * guarantee unicity. TODO reuse logic in CobolGen for unique Cobol name
     * generation
     * 
     * @param cobolName cobol name of corresponding list or array
     * @return the proposed counter cobol name
     */
    public static String getCounterCobolName(final String cobolName) {
        if (cobolName.length() < 31 - COUNTER_COBOL_SUFFIX.length()) {
            return cobolName + COUNTER_COBOL_SUFFIX;
        } else {
            return cobolName.substring(0, 30 - COUNTER_COBOL_SUFFIX.length())
                    + COUNTER_COBOL_SUFFIX;
        }
    }

}
