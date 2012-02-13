package com.legstar.xsd;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;

/**
 * Navigates an XML Schema starting from root elements.
 * <p/>
 * For elements which are complex types, the visitor recursively visits all
 * children elements. It is important that at least one root element exists in
 * the XML schema to seed the process, otherwise nothing is visited.
 * <p/>
 * In particular, an XML schema only made of complex types will not be
 * navigated. Because of this particular way of navigating the XML schema, there
 * is no guarantee that all complex types will be visited.
 * <p/>
 * What is guaranteed though, is that starting from a root element, the entire
 * hierarchy will be visited in the child-first order.
 * <p/>
 * The navigator does not perform any processing on the XML Schema objects
 * visited it delegates instead to a schema object processor.
 * 
 */
public class XsdNavigator {

    /** Logging. */
    private Log _log = LogFactory.getLog(getClass());

    /** The XML Schema to visit. */
    private XmlSchema _schema;

    /** The annotator to use. */
    private XsdObjectProcessor _processor;

    /**
     * @param schema the XML Schema
     * @param processor a specialized XML schema objects processor
     */
    public XsdNavigator(final XmlSchema schema,
            final XsdObjectProcessor processor) {
        _schema = schema;
        _processor = processor;
    }

    /**
     * Process each element in the input Schema.
     * <p/>
     * Use root elements (as opposed to named complex types) to drive the
     * process because we need to reconstruct a hierarchy of complex types.
     * Elements give us the roots to start from.
     * 
     */
    public void visit() throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("visit XML Schema started");
        }

        processCollectionElements(_schema.getItems(), 1);

        if (_log.isDebugEnabled()) {
            _log.debug("visit XML Schema ended");
        }
    }

    /**
     * Take all elements from a collection and process them.
     * 
     * @param items the parent collection
     * @param level the current level in the elements hierarchy.
     * @throws XsdMappingException if processing fails
     */
    protected void processCollectionElements(
            final XmlSchemaObjectCollection items, final int level)
            throws XsdMappingException {

        /* Process each element in the collection */
        for (int i = 0; i < items.getCount(); i++) {
            XmlSchemaObject element = items.getItem(i);
            if (element instanceof XmlSchemaElement) {
                processElement((XmlSchemaElement) element, level);
            } else if (element instanceof XmlSchemaGroupRef) {
                /* This is a reference to a group so we fetch the group */
                XmlSchemaGroupRef groupRef = (XmlSchemaGroupRef) element;
                XmlSchemaGroup group = (XmlSchemaGroup) _schema.getGroups()
                        .getItem(groupRef.getRefName());
                processParticle(group.getName(), group.getParticle(), level);
            }

        }
    }

    /**
     * Process an XML schema element.
     * 
     * @param xsdElement the XML Schema element to process
     * @param level the current level in the elements hierarchy.
     * @throws XsdMappingException if processing fails
     */
    public void processElement(final XmlSchemaElement xsdElement,
            final int level) throws XsdMappingException {
        /*
         * If this element is referencing another, it might not be useful to
         * process it.
         */
        if (xsdElement.getRefName() != null) {
            return;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("process started for element = " + xsdElement.getName());
        }

        /* Delegate processing of XML schema element */
        _processor.processElement(_schema, xsdElement, level);

        /*
         * If this element is of a complex type, we process its children.
         */
        if (xsdElement.getSchemaType() instanceof XmlSchemaComplexType) {
            XmlSchemaComplexType xsdComplexType = (XmlSchemaComplexType) xsdElement
                    .getSchemaType();
            processComplexType(xsdComplexType, level);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("process ended for element = " + xsdElement.getName());
        }
    }

    /**
     * Process an XML schema complex type.
     * 
     * @param xsdComplexType the XML Schema type to process
     * @param level the current level in the elements hierarchy.
     * @throws XsdMappingException if processing fails
     */
    public void processComplexType(final XmlSchemaComplexType xsdComplexType,
            final int level) throws XsdMappingException {
        if (_log.isDebugEnabled()) {
            _log.debug("process started for complex type = "
                    + xsdComplexType.getName());
        }

        /* Delegate processing of XML schema complex type */
        _processor.processComplexType(_schema, xsdComplexType, level);

        /* Complex types might particles such as sequence and all */
        XmlSchemaParticle particle = xsdComplexType.getParticle();
        processParticle(xsdComplexType.getQName(), particle, level
                + XsdConstants.DEFAULT_LEVEL_INCREMENT);

        if (_log.isDebugEnabled()) {
            _log.debug("process ended for complex type = "
                    + xsdComplexType.getName());
        }
    }

    /**
     * A particle is usually all or sequence. It contains a collection of other
     * schema objects that need to be processed.
     * 
     * @param parentName the name of the parent schema object for logging
     * @param particle the particle schema object
     * @param level the current level in the elements hierarchy.
     * @throws XsdMappingException if processing fails
     */
    public void processParticle(final QName parentName,
            final XmlSchemaParticle particle, final int level)
            throws XsdMappingException {

        if (particle == null) {
            _log.warn("Schema object " + parentName
                    + " does not contain a particle");
            return;
        }

        if (particle.getMaxOccurs() > 1) {
            /* TODO find a way to handle occuring sequences and alls */
            _log.warn("Schema object " + parentName
                    + " contains a multi-occurence particle that is ignored");
        }

        if (particle instanceof XmlSchemaSequence) {
            XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
            processCollectionElements(sequence.getItems(), level);

        } else if (particle instanceof XmlSchemaAll) {
            XmlSchemaAll all = (XmlSchemaAll) particle;
            processCollectionElements(all.getItems(), level);
        } else {
            /* TODO process other particle types of interest */
            /* TODO find a way to handle xsd:attribute */
            _log.warn("Schema object " + parentName
                    + " does not contain a sequence or all element");
        }

    }

}
