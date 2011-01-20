package com.legstar.xsd.def;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;

import com.legstar.dom.DocumentFactory;
import com.legstar.dom.InvalidDocumentException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdMappingException;
import com.legstar.xsd.XsdNavigator;
import com.legstar.xsd.XsdReader;
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

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * Construct the translator.
     */
    public Xsd2Cob() {
        this(new Xsd2CobConfig());
    }

    /**
     * Construct the translator.
     * 
     * @param xsdConfig the configuration data
     */
    public Xsd2Cob(final Xsd2CobConfig xsdConfig) {
        _xsdConfig = xsdConfig;
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

            Xsd2CobAnnotator annotator = new Xsd2CobAnnotator(getConfig());
            annotator.setUp();

            XsdNavigator visitor = new XsdNavigator(schema, annotator);
            visitor.visit();

            Xsd2CobGenerator generator = new Xsd2CobGenerator();
            visitor = new XsdNavigator(schema, generator);
            visitor.visit();

            return new XsdToCobolStringResult(toString(schema),
                    generator.toString());

        } catch (IOException e) {
            throw new InvalidXsdException(e);
        } catch (XsdMappingException e) {
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
     */
    protected String toString(final XmlSchema schema) {
        StringWriter writer = new StringWriter();
        schema.write(writer);
        String result = writer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(result);
        }
        return result;
    }

    /**
     * @return the configuration data
     */
    public Xsd2CobConfig getConfig() {
        return _xsdConfig;
    }

}
