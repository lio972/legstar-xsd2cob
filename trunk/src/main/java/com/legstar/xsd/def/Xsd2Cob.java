package com.legstar.xsd.def;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Document;

import com.legstar.dom.DocumentFactory;
import com.legstar.dom.InvalidDocumentException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdMappingException;
import com.legstar.xsd.XsdNavigator;
import com.legstar.xsd.XsdReader;
import com.legstar.xsd.XsdRootElement;
import com.legstar.xsd.XsdToCobolStringResult;
import com.legstar.xsd.cob.Xsd2CobGenerator;

/**
 * XSD to COBOL Translator API.
 * <p/>
 * Takes raw XML Schema of XML Schema imbedded in a WSDL and turns the content
 * into 2 outputs:
 * <ul>
 * <li>An XML Schema with COBOL annotations</li>
 * <li>COBOL structures descriptions mapping the XML schema elements</li>
 * </ul>
 * 
 */
public class Xsd2Cob {

    /** Configuration data. */
    private Xsd2CobConfig _xsdConfig;

    /** New root elements to add to the generated XML schema. */
    private List < XsdRootElement > _newRootElements;

    /** An optional XSLT transform for XML schema customization. */
    private String _customXsltFileName;

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * Construct the translator.
     */
    public Xsd2Cob() {
        this(null, null, null);
    }

    /**
     * Construct the translator.
     * 
     * @param xsdConfig the configuration data
     */
    public Xsd2Cob(final Xsd2CobConfig xsdConfig) {
        this(xsdConfig, null, null);
    }

    /**
     * Construct the translator.
     * 
     * @param xsdConfig the configuration data
     * @param newRootElements additional XML schema root elements
     */
    public Xsd2Cob(final Xsd2CobConfig xsdConfig,
            final List < XsdRootElement > newRootElements) {
        this(xsdConfig, newRootElements, null);
    }

    /**
     * Construct the translator.
     * 
     * @param xsdConfig the configuration data
     * @param newRootElements additional XML schema root elements
     * @param customXsltFileName XSLT transform for XML schema customization
     */
    public Xsd2Cob(final Xsd2CobConfig xsdConfig,
            final List < XsdRootElement > newRootElements,
            final String customXsltFileName) {
        if (xsdConfig == null) {
            _xsdConfig = new Xsd2CobConfig();
        }
        _xsdConfig = xsdConfig;
        _newRootElements = newRootElements;
        _customXsltFileName = customXsltFileName;
    }

    /**
     * Execute the translation from XML schema to COBOL-annotated XML Schema and
     * COBOL structure.
     * 
     * @param xml the XML schema source
     * @return the XML Schema and the COBOL structure as strings
     */
    public XsdToCobolStringResult translate(final String xml)
            throws InvalidXsdException {
        return translate(parse(xml));
    }

    /**
     * Execute the translation from XML schema to COBOL-annotated XML Schema and
     * COBOL structure.
     * 
     * @param uri the XML schema URI
     * @return the XML Schema and the COBOL structure as strings
     */
    public XsdToCobolStringResult translate(final URI uri)
            throws InvalidXsdException {
        return translate(parse(uri));
    }

    /**
     * Execute the translation from XML schema to COBOL-annotated XML Schema and
     * COBOL structure.
     * 
     * @param schema the XML schema
     * @return the XML Schema and the COBOL structure as strings
     */
    public XsdToCobolStringResult translate(final XmlSchema schema)
            throws InvalidXsdException {

        if (_log.isDebugEnabled()) {
            _log.debug("Translating with options:" + getConfig().toString());
        }
        try {
            if (getNewRootElements() != null) {
                XsdReader.addRootElements(getNewRootElements(), schema);
            }

            Xsd2CobAnnotator annotator = new Xsd2CobAnnotator(getConfig());
            annotator.setUp();

            XsdNavigator visitor = new XsdNavigator(schema, annotator);
            visitor.visit();

            XmlSchema resultSchema = schema;
            if (getCustomXsltFileName() != null) {
                resultSchema = customize(schema, getCustomXsltFileName());
            }

            Xsd2CobGenerator generator = new Xsd2CobGenerator();
            visitor = new XsdNavigator(resultSchema, generator);
            visitor.visit();

            return new XsdToCobolStringResult(toString(resultSchema),
                    generator.toString());

        } catch (IOException e) {
            throw new InvalidXsdException(e);
        } catch (XsdMappingException e) {
            throw new InvalidXsdException(e);
        } catch (TransformerException e) {
            throw new InvalidXsdException(e);
        }
    }

    /**
     * Parse a file and generate an XML Schema.
     * 
     * @param xml the input XML content (an XSD or WSDL)
     * @return an XML schema
     * @throws InvalidXsdException if something fails
     */
    protected XmlSchema parse(final String xml) throws InvalidXsdException {
        try {
            Document doc = DocumentFactory.parse(xml);
            return XsdReader.read(doc);
        } catch (InvalidDocumentException e) {
            throw new InvalidXsdException(e);
        }
    }

    /**
     * Parse a file and generate an XML Schema.
     * 
     * @param uri the input XML URI
     * @return an XML schema
     * @throws InvalidXsdException if something fails
     */
    protected XmlSchema parse(final URI uri) throws InvalidXsdException {
        try {
            Document doc = DocumentFactory.parse(uri);
            return XsdReader.read(doc);
        } catch (InvalidDocumentException e) {
            throw new InvalidXsdException(e);
        }
    }

    /**
     * Prints an XML schema in a String.
     * 
     * @param schema the XML schema
     * @return the content as a string
     * @throws TransformerException if formatting of output fails
     */
    protected String toString(final XmlSchema schema)
            throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        try {
            tFactory.setAttribute("indent-number", "4");
        } catch (IllegalArgumentException e) {
            _log.warn("Unable to set indent-number on transfomer factory", e);
        }
        StringWriter writer = new StringWriter();
        Source source = new DOMSource(schema.getAllSchemas()[0]);
        Result result = new StreamResult(writer);
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        transformer.transform(source, result);
        writer.flush();
        return writer.toString();
    }

    /**
     * Customize the XML schema by applying an XSLT stylesheet.
     * 
     * @param schema the XML schema
     * @param xsltFileName the XSLT stylesheet
     * @return the transformed XML schema as a string
     * @throws TransformerException if XSLT transform fails
     */
    protected XmlSchema customize(final XmlSchema schema,
            final String xsltFileName) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StringWriter writer = new StringWriter();
        Source source = new DOMSource(schema.getAllSchemas()[0]);
        Result result = new StreamResult(writer);
        Source xsltSource = new StreamSource(new File(xsltFileName));
        Transformer transformer = tFactory.newTransformer(xsltSource);
        transformer.transform(source, result);
        writer.flush();
        StringReader reader = new StringReader(writer.toString());
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        return schemaCol.read(reader, null);
    }

    /**
     * @return the configuration data
     */
    public Xsd2CobConfig getConfig() {
        return _xsdConfig;
    }

    /**
     * @param newRootElements a list of root elements to add
     */
    public void setNewRootElements(final List < XsdRootElement > newRootElements) {
        _newRootElements = newRootElements;
    }

    /**
     * @param xsdRootElement a root element to add to the XML Schema
     */
    public void addNewRootElement(final XsdRootElement xsdRootElement) {
        if (_newRootElements == null) {
            _newRootElements = new ArrayList < XsdRootElement >();
        }
        _newRootElements.add(xsdRootElement);
    }

    /**
     * @return the new root elements to add to the generated XML schema
     */
    public List < XsdRootElement > getNewRootElements() {
        return _newRootElements;
    }

    /**
     * An optional XSLT transform for XML schema customization.
     * 
     * @return an optional XSLT transform for XML schema customization
     */
    public String getCustomXsltFileName() {
        return _customXsltFileName;
    }

    /**
     * @param customXsltFileName an optional XSLT transform for XML schema
     *            customization
     */
    public void setCustomXsltFileName(final String customXsltFileName) {
        _customXsltFileName = customXsltFileName;
    }

}
