package com.legstar.xsd.cob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.legstar.cobol.model.CobolDataItem;
import com.legstar.coxb.CobolMarkup;
import com.legstar.coxb.CobolType;
import com.legstar.coxb.CobolUsage;
import com.legstar.xsd.XsdConstants;
import com.legstar.xsd.XsdMappingException;
import com.legstar.xsd.XsdObjectProcessor;

/**
 * Map an XML schema with COBOL annotations to a series of COBOL data items
 * organized in hierarchies.
 * 
 */
public class Xsd2CobMapper implements XsdObjectProcessor {

    /**
     * List of root COBOL data items.
     */
    private List < CobolDataItem > rootDataItems = new ArrayList < CobolDataItem >();

    /**
     * Group items hierarchy.
     */
    private Stack < CobolDataItem > groupStack = new Stack < CobolDataItem >();

    private Log logger = LogFactory.getLog(getClass());

    public void setUp() throws IOException {

    }

    public void processElement(XmlSchema schema, XmlSchemaElement xsdElement,
            int level) throws XsdMappingException {

        DecoratedCobolDataItem cobolDataItem = toCobol(level, xsdElement);
        if (cobolDataItem == null) {
            logger.warn("XSD element: " + xsdElement.getQName().toString()
                    + " does not have COBOL annotations");
            return;
        }

        if (level == XsdConstants.DEFAULT_ROOT_LEVEL) {
            rootDataItems.add(cobolDataItem);
        }
        // Unstack all lower level items
        while (!groupStack.isEmpty()
                && groupStack.peek().getLevelNumber() >= level) {
            groupStack.pop();
        }
        if (!groupStack.isEmpty()) {
            CobolDataItem parentDataItem = groupStack.peek();
            parentDataItem.getChildren().add(cobolDataItem);
        }
        if (cobolDataItem.getCobolType() == CobolType.GROUP_ITEM) {
            groupStack.add(cobolDataItem);
        }
    }

    public void processComplexType(XmlSchema schema,
            XmlSchemaComplexType xsdComplexType, int level)
            throws XsdMappingException {

    }

    /**
     * Produce a Cobol data item from an XML schema COBOL annotations.
     * 
     * @param level the COBOL level this data item should be assigned
     * @param xsdElement the XML schema element
     * @return a COBOL data item or null if this element has no COBOL
     *         annotations
     */
    protected DecoratedCobolDataItem toCobol(int level,
            XmlSchemaElement xsdElement) {

        Element elc = getCobolAnnotation(xsdElement);
        if (elc != null) {

            String cobolName = elc.getAttribute(CobolMarkup.COBOL_NAME);
            String cobolType = elc.getAttribute(CobolMarkup.TYPE);
            DecoratedCobolDataItem cobolDataItem = new DecoratedCobolDataItem(
                    level, cobolName, CobolType.valueOf(cobolType));

            String picture = elc.getAttribute(CobolMarkup.PICTURE);
            if (StringUtils.isNotBlank(picture)) {
                cobolDataItem.setPicture(picture);
            }
            String cobolUsage = elc.getAttribute(CobolMarkup.USAGE);
            if (StringUtils.isNotBlank(cobolUsage)
                    && !cobolUsage.equals("DISPLAY")) {
                cobolDataItem.setUsage(CobolUsage.getUsage(cobolUsage));
            }
            String cobolMinOccurs = elc.getAttribute(CobolMarkup.MIN_OCCURS);
            if (StringUtils.isNotBlank(cobolMinOccurs)) {
                mapOccursAttributes(xsdElement, elc, cobolDataItem);
            }

            return cobolDataItem;
        } else {
            return null;
        }

    }

    /**
     * Arrays are mapped to COBOL arrays. Most of the XSD arrays are variable
     * size in which case we map them to COBOL arrays with a depending on clause
     * where the subject is a new counter variable that gets dynamically
     * generated.
     * 
     * @param xsdElement the XSD element that is a collection
     * @param elc the COBOL annotations
     * @param cobolDataItem the COBOL data item whose attributes are being
     *            populated
     */
    protected void mapOccursAttributes(XmlSchemaElement xsdElement,
            Element elc, CobolDataItem cobolDataItem) {
        int minOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MIN_OCCURS));
        int maxOccurs = Integer.parseInt(elc
                .getAttribute(CobolMarkup.MAX_OCCURS));
        if (maxOccurs > minOccurs || minOccurs > 1) {
            cobolDataItem.setMinOccurs(minOccurs);
            cobolDataItem.setMaxOccurs(maxOccurs);

            if (maxOccurs > minOccurs) {
                String dependingOn = elc.getAttribute(CobolMarkup.DEPENDING_ON);
                if (StringUtils.isBlank(dependingOn)) {
                    CobolDataItem occursCounters = getOccursCountersGroup();
                    if (occursCounters == null) {
                        logger.warn("XSD element: "
                                + xsdElement.getQName().toString()
                                + " is a variable size array but there are no root items to store counters");
                    } else {
                        CobolDataItem counter = addCounter(cobolDataItem,
                                occursCounters);
                        dependingOn = counter.getCobolName();
                    }
                }
                cobolDataItem.setDependingOn(dependingOn);
            }
        }
    }

    /**
     * Decorate the COBOL data item with its type.
     * <p/>
     * Since we collect children later, we need to know early if a data item is
     * a group item.
     * 
     */
    public class DecoratedCobolDataItem extends CobolDataItem {

        private CobolType cobolType;

        public DecoratedCobolDataItem(int levelNumber, String cobolName,
                CobolType cobolType) {
            super(levelNumber, cobolName);
            this.cobolType = cobolType;
        }

        /**
         * @return the cobolType
         */
        public CobolType getCobolType() {
            return cobolType;
        }

    }

    /**
     * Extracts the cobol annotation from an XML schema element.
     * 
     * @param xsdElement the XML schema element
     * @return the COBOL annotation or bull if none found
     */
    protected Element getCobolAnnotation(XmlSchemaElement xsdElement) {
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
                        return (Element) node;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return the root COBOL data items
     */
    public List < CobolDataItem > getRootDataItems() {
        return rootDataItems;
    }

    /**
     * Retrieve the current group item that holds counters for variable size
     * arrays. If none exist, this creates a new group to hold the counters.
     * 
     * @return the current group item that holds counters for variable size
     *         arrays
     */
    protected CobolDataItem getOccursCountersGroup() {
        if (rootDataItems.size() == 0) {
            return null;
        }
        CobolDataItem rootDataItem = rootDataItems
                .get(rootDataItems.size() - 1);
        if ((rootDataItem.getChildren().size() > 0)
                && (rootDataItem.getChildren().get(0).getCobolName()
                        .equals(XsdConstants.OCCURS_COUNTERS_GROUP_NAME))) {
            return rootDataItem.getChildren().get(0);
        } else {
            return createOccursCountersGroup();
        }
    }

    /**
     * Counters for variable size arrays are kept in a group item which is
     * stored as the first child of the root item.
     * 
     * @return a new group for counters stored as the first child of the current
     *         root item
     */
    protected CobolDataItem createOccursCountersGroup() {
        if (rootDataItems.size() == 0) {
            return null;
        }
        CobolDataItem rootDataItem = rootDataItems
                .get(rootDataItems.size() - 1);
        CobolDataItem occursCounters = new CobolDataItem(
                XsdConstants.DEFAULT_ROOT_LEVEL
                        + XsdConstants.DEFAULT_LEVEL_INCREMENT,
                XsdConstants.OCCURS_COUNTERS_GROUP_NAME);
        rootDataItem.getChildren().add(0, occursCounters);
        return occursCounters;
    }

    /**
     * Adds a new variable size array counter.
     * <p/>
     * A counter is a numeric COBOL data item that holds an array count.
     * 
     * @param cobolDataItem the COBOL array
     * @param occursCounters the group that holds all counters
     * @return the created counter
     */
    protected CobolDataItem addCounter(CobolDataItem cobolDataItem,
            CobolDataItem occursCounters) {
        CobolDataItem counterDataItem = new CobolDataItem(
                occursCounters.getLevelNumber()
                        + XsdConstants.DEFAULT_LEVEL_INCREMENT,
                getCounterCobolName(cobolDataItem.getCobolName()));
        counterDataItem.setPicture(XsdConstants.COUNTER_COBOL_PICTURE);
        counterDataItem.setUsage(XsdConstants.COUNTER_COBOL_USAGE);
        occursCounters.getChildren().add(counterDataItem);
        return counterDataItem;
    }

    /**
     * Dynamic counters need a unique Cobol name. This method determines such a
     * name based on the related array or list cobol name.
     * 
     * 
     * @param cobolName cobol name of corresponding list or array
     * @return the proposed counter cobol name
     */
    protected String getCounterCobolName(final String cobolName) {
        if (cobolName.length() < 31 - XsdConstants.COUNTER_COBOL_SUFFIX
                .length()) {
            return cobolName + XsdConstants.COUNTER_COBOL_SUFFIX;
        } else {
            return cobolName.substring(0,
                    30 - XsdConstants.COUNTER_COBOL_SUFFIX.length())
                    + XsdConstants.COUNTER_COBOL_SUFFIX;
        }
    }

}
