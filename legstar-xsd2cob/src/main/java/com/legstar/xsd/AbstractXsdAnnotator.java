package com.legstar.xsd;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.legstar.cobol.gen.CobolNameResolver;
import com.legstar.coxb.CobolMarkup;
import com.legstar.coxb.CobolType;
import com.legstar.dom.ElementFactory;

/**
 * Common methods and properties for all XML schema annotators.
 * <p/>
 * An annotator must be setup before it is used (using the setUp method) this
 * will give it a chance to load configuration data.
 * 
 */
public abstract class AbstractXsdAnnotator implements XsdObjectProcessor {

    /** Associates a cobol type to an XSD primitive type. */
    private XsdCobolTypeMap _typeMap = new XsdCobolTypeMap();

    /** Used to build valid cobol names from java names. */
    private CobolNameResolver _nameResolver = new CobolNameResolver();

    /** Maps a complexType to a Java qualified class name. */
    private Map < String, String > _complexTypeToJavaClassMap;

    /** Logging. */
    private Log _log = LogFactory.getLog(getClass());

    /**
     * No arg constructor.
     */
    public AbstractXsdAnnotator() {

    }

    /**
     * Construct using a complex type to java class map.
     * 
     * @param complexTypeToJavaClassMap complex type to java class map
     */
    public AbstractXsdAnnotator(Map < String, String > complexTypeToJavaClassMap) {
        _complexTypeToJavaClassMap = complexTypeToJavaClassMap;
    }

    public void setUp() throws IOException {
    }

    /**
     * Add annotations to an XML schema object (Element or Type).
     * 
     * @param schema the XML schema
     * @param schemaObject the XML schema object to annotate
     * @param elc a DOM element holding the annotations
     */
    @SuppressWarnings("unchecked")
    public void annotate(final XmlSchema schema,
            final XmlSchemaAnnotated schemaObject, final Element elc) {

        /* Schema object might already be annotated */
        XmlSchemaAnnotation annotation = null;
        if (schemaObject.getAnnotation() == null) {
            annotation = new XmlSchemaAnnotation();
        } else {
            annotation = schemaObject.getAnnotation();
        }

        /* Annotation might already contain an appinfo */
        XmlSchemaAppInfo appInfo = null;
        /* Look for an existing appinfo in annotation */
        for (Iterator < XmlSchemaObject > i = annotation.getItems()
                .getIterator(); i.hasNext();) {
            XmlSchemaObject subAnnotation = i.next();
            if (subAnnotation instanceof XmlSchemaAppInfo) {
                appInfo = (XmlSchemaAppInfo) subAnnotation;
                break;
            }
        }
        if (appInfo == null) {
            appInfo = new XmlSchemaAppInfo();
            annotation.getItems().add(appInfo);
        }

        /* Preserve any previous markup from appinfo */
        NodeList markup = appInfo.getMarkup();
        if (markup == null || markup.getLength() == 0) {
            /* Create a DOM node to act as a dummy parent to the annotation node */
            String coxbPrefix = schema.getNamespaceContext().getPrefix(
                    CobolMarkup.NS);
            Element el = ElementFactory.createElement(CobolMarkup.NS,
                    coxbPrefix + ":dummy");
            /* Now add the COBOL markup */
            el.appendChild(elc);
            markup = el.getChildNodes();
        } else {
            /*
             * Reuse an existing DOM node and append our annotations. If there
             * was a COBOL annotation already, we replace it.
             */
            Document parentdoc = markup.item(0).getOwnerDocument();
            DocumentFragment fragment = parentdoc.createDocumentFragment();
            for (int i = 0; i < markup.getLength(); i++) {
                Node node = markup.item(i);
                if (node instanceof Element
                        && node.getLocalName().equals(CobolMarkup.ELEMENT)
                        && node.getNamespaceURI().equals(CobolMarkup.NS)) {
                    _log.warn("Replacing previous COBOL annotation for "
                            + ((Element) node)
                                    .getAttribute(CobolMarkup.COBOL_NAME));
                } else {
                    fragment.appendChild(node.cloneNode(true));
                }
            }
            fragment.appendChild(parentdoc.importNode(elc, true));
            markup = fragment.getChildNodes();
        }

        appInfo.setMarkup(markup);
        schemaObject.setAnnotation(annotation);
    }

    /**
     * Annotated complex types as group items.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdComplexType the XML schema type
     * @param elc the DOM Element representing the Cobol annotation
     * @param level the current level in the type hierarchy
     * @throws XsdMappingException if annotation fails
     */
    public void setComplexTypeAttributes(final XmlSchema schema,
            final XmlSchemaComplexType xsdComplexType, final Element elc,
            final int level) throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setComplexTypeAttributes started for type = "
                    + xsdComplexType.getName());
            _log.debug("   XmlSchemaType QName                    = "
                    + xsdComplexType.getQName());
        }

        elc.setAttribute(CobolMarkup.TYPE, CobolType.GROUP_ITEM.name());
        if (_log.isDebugEnabled()) {
            _log.debug("   Cobol type           = "
                    + elc.getAttribute(CobolMarkup.TYPE));
        }

        if (_log.isDebugEnabled()) {
            _log.debug("setComplexTypeAttributes ended for type = "
                    + xsdComplexType.getQName());
        }
    }

    /**
     * Method to infer a Cobol name from an XML schema type name.
     * 
     * @param xsdName the XSD type name
     * @return the proposed cobol name
     * */
    public String getCobolName(final String xsdName) {
        return getCobolNameResolver().getName(xsdName);
    }

    /**
     * @return the map that associates a cobol type to an XSD primitive type
     */
    public XsdCobolTypeMap getXsdCobolTypeMap() {
        return _typeMap;
    }

    /**
     * @return the COBOL name resolver
     */
    public CobolNameResolver getCobolNameResolver() {
        return _nameResolver;
    }

    /**
     * @return the complexType to Java qualified class name map
     */
    public Map < String, String > getComplexTypeToJavaClassMap() {
        return _complexTypeToJavaClassMap;
    }

}
