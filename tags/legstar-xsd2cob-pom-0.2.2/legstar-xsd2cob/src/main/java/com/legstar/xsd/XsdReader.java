package com.legstar.xsd;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.legstar.coxb.CobolMarkup;

/**
 * Reads XML Schema from an input stream producing an Apache XML Schema object;
 * 
 */
public class XsdReader {

    /**
     * Reads an XML schema from a DOM document.
     * <p/>
     * The DOM document can hold a pure schema or a WSDL.
     * 
     * @param doc the DOM document
     * @param rootElements additional root elements to add to the XML schema
     * @return an XML Schema
     * @throws InvalidXsdException if there is something wrong in the document
     */
    public static XmlSchema read(final Document doc) throws InvalidXsdException {

        Element definitionsElement = null;
        Element schemaElement = null;

        NodeList nodes = doc.getElementsByTagNameNS(XsdConstants.WSDL_NS,
                "definitions");
        if (nodes.getLength() > 0) {
            definitionsElement = (Element) nodes.item(0);
        }

        nodes = doc.getElementsByTagNameNS(XsdConstants.XSD_NS, "schema");
        if (nodes.getLength() > 0) {
            schemaElement = (Element) nodes.item(0);
        }

        if (schemaElement == null) {
            throw new InvalidXsdException(
                    "Unable to locate an XML Schema in input document");
        }

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        XmlSchema schema = schemaCol.read(schemaElement);

        /* Add WSDL targetnamespace when needed */
        if (definitionsElement != null) {
            String targetNamespace = definitionsElement
                    .getAttribute("targetNamespace");
            if (schema.getTargetNamespace() == null) {
                schema.setTargetNamespace(targetNamespace);
            }
        }

        /* Includes need to be annotated so we pull them inside the main XSD */
        schema = pullIncludes(schema);

        /* Add the COXB namespace */
        addNamespace(XsdConstants.DEFAULT_COXB_NS_PFX, CobolMarkup.NS, schema);

        /* Add WSDL parts as elements when needed */
        if (definitionsElement != null) {
            addWsdlPartsAsRootElements(doc, schema);
        }

        /* Make sure the generated schema has an XML declaration */
        schema.setInputEncoding("UTF-8");

        return schema;
    }

    /**
     * Merge includes and imports (with identical target namespace) into the XML
     * Schema and then remove the included/imported items.
     * <p/>
     * 
     * @return a new XmlSchema where all includes and imports are expanded
     */
    public static XmlSchema pullIncludes(final XmlSchema xmlSchema) {
        Document[] docs = xmlSchema.getAllSchemas();
        if (docs.length < 2) {
            return xmlSchema;
        }

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        Document merged = mergeIncludes(docs, xmlSchema.getTargetNamespace());
        XmlSchema mergedSchema = schemaCol.read(merged, null);

        return mergedSchema;
    }

    /**
     * Imports and includes are in separate DOM Documents that we merge here.
     * <p/>
     * Merging is done only for schema elements which targetNamespace
     * corresponds to the container schema. This means that most of the time,
     * imports will not actually be merged.
     * <p/>
     * The XmlSchema getAllSchemas returns the main schema as the last document
     * but we want to process it first hence the reverse order processing.
     * 
     * @param docs a set of more than one schema documents
     * @param targetNamespace the main schema target namespace
     * @return a merge schema document
     */
    protected static Document mergeIncludes(final Document[] docs,
            final String targetNamespace) {

        Document merged = null;
        Element mergedSchemaElement = null;

        for (int i = docs.length - 1; i > -1; i--) {
            NodeList nodes = docs[i].getElementsByTagNameNS(
                    XsdConstants.XSD_NS, "schema");
            if (nodes.getLength() == 0) {
                continue;
            }
            Element schemaElement = (Element) nodes.item(0);
            if (hasCompatibleAttr(schemaElement, "targetNamespace",
                    targetNamespace)) {
                if (merged == null) {
                    merged = docs[i];
                    mergedSchemaElement = schemaElement;
                } else {
                    while (schemaElement.hasChildNodes()) {
                        Node kid = schemaElement.getFirstChild();
                        schemaElement.removeChild(kid);
                        kid = merged.importNode(kid, true);
                        mergedSchemaElement.appendChild(kid);
                    }
                }
            }

        }
        cleanIncludes(merged, targetNamespace);
        return merged;
    }

    /**
     * Removes includes and imports which have been merged into the main schema.
     * <p/>
     * Note that includes have the same target namespace as the main schema
     * while imports generally have a different one (in which case they were not
     * merged and should not be removed).
     * 
     * @param merged the main schema after a merge
     * @param targetNamespace the main schema target namespace
     */
    protected static void cleanIncludes(Document merged,
            final String targetNamespace) {
        NodeList nodes = merged.getElementsByTagNameNS(XsdConstants.XSD_NS,
                "include");
        while (nodes.getLength() > 0) {
            Element incElement = (Element) nodes.item(0);
            ((Element) incElement.getParentNode()).removeChild(incElement);
        }
        nodes = merged.getElementsByTagNameNS(XsdConstants.XSD_NS, "import");
        List < Element > impElements = new ArrayList < Element >();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element impElement = (Element) nodes.item(i);
            if (hasCompatibleAttr(impElement, "namespace", targetNamespace)) {
                impElements.add(impElement);
            }
        }
        for (Element impElement : impElements) {
            ((Element) impElement.getParentNode()).removeChild(impElement);
        }
    }

    /**
     * Check if an element has a compatible attribute.
     * 
     * @param element the element to check
     * @param attribute the attribute to check
     * @param value the attribute value to check
     * @return true if element does not have the attribute or have it with an
     *         identical value
     */
    protected static boolean hasCompatibleAttr(final Element element,
            final String attribute, final String value) {
        String incNs = element.getAttribute(attribute);
        if (incNs == null || incNs.length() == 0 || incNs.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * Get name for item from another schema.
     * 
     * @param table the other schema table
     * @param xsdObject the object for which we seek a name
     * @return the name
     */
    protected static QName getName(final XmlSchemaObjectTable table,
            final XmlSchemaObject xsdObject) {
        for (Iterator < ? > it = table.getNames(); it.hasNext();) {
            QName name = (QName) it.next();
            if (xsdObject == table.getItem(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * WSDL part elements which are bound to schema types (rather than
     * elements), do not have an element counterpart in the wsdl schema.
     * <p/>
     * What we do here, is that we store them in the XML Schema as additional
     * root elements which will be later added to the schema.
     * 
     * @param doc the XML document containing the WSDL
     * @param schema the XML schema
     * @throws InvalidXsdException if root elements cannot be added
     */
    public static void addWsdlPartsAsRootElements(final Document doc,
            final XmlSchema schema) throws InvalidXsdException {

        List < XsdRootElement > rootElements = new ArrayList < XsdRootElement >();

        NodeList nodes = doc.getElementsByTagNameNS(XsdConstants.WSDL_NS,
                "part");

        /* First */
        for (int i = 0; i < nodes.getLength(); i++) {
            Element partElement = (Element) nodes.item(i);
            String name = partElement.getAttribute("name");
            String type = partElement.getAttribute("type");
            if (type != null && type.length() > 0) {
                if (type.indexOf(':') > 0) {
                    type = type.substring(type.indexOf(':') + 1);
                }
                rootElements.add(new XsdRootElement(name, type));
            }
        }
        addRootElements(rootElements, schema);
    }

    /**
     * As an option, caller might want to add root elements to the schema before
     * it is processed. To be valid these elements must map to an existing type
     * and should not conflict with existing elements.
     * <p/>
     * In case of name conflict we suffix the element name with the type name in
     * hope to disambiguate the name.
     * 
     * @param rootElements the root elements to add to the schema
     * @param schema the schema to be annotated
     * @throws InvalidXsdException if root elements cannot be added
     */
    public static void addRootElements(
            final List < XsdRootElement > rootElements, final XmlSchema schema)
            throws InvalidXsdException {
        if (rootElements == null) {
            return;
        }
        for (XsdRootElement xsdRootElement : rootElements) {

            QName typeQName = new QName(schema.getTargetNamespace(),
                    xsdRootElement.getTypeName());
            QName elementQName = new QName(schema.getTargetNamespace(),
                    xsdRootElement.getElementName());

            if (schema.getTypeByName(typeQName) == null) {
                throw (new InvalidXsdException("Root element "
                        + xsdRootElement.getElementName()
                        + " has unknown type " + typeQName));
            }

            XmlSchemaElement el = schema.getElementByName(elementQName);
            if (el == null) {
                addXmlSchemaElement(typeQName, elementQName, schema);
            } else {
                QName newElementQName = new QName(
                        elementQName.getNamespaceURI(),
                        elementQName.getLocalPart() + typeQName.getLocalPart());
                addXmlSchemaElement(typeQName, newElementQName, schema);
            }
        }
    }

    /**
     * Create a nex XML schema element.
     * <p/>
     * We use the source URI to mark these injected elements.
     * 
     * @param typeQName the element type
     * @param elementQName the element name
     * @param schema the XML schema
     */
    public static void addXmlSchemaElement(final QName typeQName,
            final QName elementQName, final XmlSchema schema) {
        XmlSchemaElement el = new XmlSchemaElement();
        el.setQName(elementQName);
        el.setName(elementQName.getLocalPart());
        el.setSchemaTypeName(typeQName);
        el.setSchemaType(schema.getTypeByName(typeQName));
        el.setSourceURI(XsdConstants.INJECTED_ELEMENT);
        schema.getElements().add(elementQName, el);
        schema.getItems().add(el);
    }

    /**
     * Add a new namespace to the XML Schema.
     * <p/>
     * If the namespace is already there, returns the corresponding prefix.
     * <p/>
     * If the default prefix is already taken, add a suffix and try again.
     * 
     * @param defaultPrefix the default namespace prefix
     * @param namespaceUri the namespace to add
     * @param schema the XML schema
     * @return the prefix used for the namespace
     */
    public static String addNamespace(final String defaultPrefix,
            final String namespaceUri, final XmlSchema schema) {

        NamespaceMap prefixmap = new NamespaceMap();
        NamespacePrefixList npl = schema.getNamespaceContext();
        for (int i = 0; i < npl.getDeclaredPrefixes().length; i++) {
            String uri = npl.getNamespaceURI(npl.getDeclaredPrefixes()[i]);
            if (namespaceUri.equals(uri)) {
                return npl.getDeclaredPrefixes()[i];
            }
            prefixmap.add(npl.getDeclaredPrefixes()[i], uri);
        }
        String coxbPrefix = defaultPrefix;
        int i = 0;
        while (prefixmap.containsKey(coxbPrefix)) {
            coxbPrefix = coxbPrefix + Integer.toString(i);
            i++;
        }
        prefixmap.add(coxbPrefix, namespaceUri);
        schema.setNamespaceContext(prefixmap);
        return coxbPrefix;

    }

    /**
     * Changes the target namespace of an XML schema.
     * <p/>
     * When the target schema needs to be changed it is much easier to work on
     * the serialized schema that switching all elements one by one.
     * 
     * @param schema the XML schema
     * @param newTargetNamespace the new target namespace
     * @return a new XML schema with new target namespace
     */
    public static XmlSchema switchTargetNamespace(final XmlSchema schema,
            final String newTargetNamespace) {
        String oldTargetNamespace = schema.getTargetNamespace();
        StringWriter writer = new StringWriter();
        schema.write(writer);
        writer.flush();

        String newXsdContent = writer.toString().replace(oldTargetNamespace,
                newTargetNamespace);
        StringReader reader = new StringReader(newXsdContent);

        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        return schemaCol.read(reader, null);

    }

}
