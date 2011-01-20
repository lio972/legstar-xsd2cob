package com.legstar.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Builds a DOM document from an input stream.
 * <p/>
 * Uses a new document builder with any useful attributes.
 * <p/>
 * This is not thread safe.
 */
public final class DocumentFactory {

    /** This builder is used for annotation markup elements. */
    private static DocumentBuilder _docBuilder;

    private DocumentFactory() {

    }

    /**
     * Creates a new, empty, DOM document.
     * 
     * @return a DOM document
     * @throws Exception if creation fails
     */
    public static synchronized Document create() throws Exception {
        ParsingErrorHandler eh = initBuilder();
        Document doc = _docBuilder.newDocument();
        if (eh.getException() != null) {
            throw eh.getException();
        }
        return doc;
    }

    /**
     * Parse an input stream and return a new DOM document.
     * 
     * @param in the input stream
     * @return the DOM document
     * @throws InvalidDocumentException if parsing failed
     */
    public static synchronized Document parse(final InputStream in)
            throws InvalidDocumentException {
        try {
            ParsingErrorHandler eh = initBuilder();
            Document doc = _docBuilder.parse(in);
            if (eh.getException() != null) {
                throw eh.getException();
            }
            return doc;
        } catch (SAXException e) {
            throw new InvalidDocumentException(e);
        } catch (IOException e) {
            throw new InvalidDocumentException(e);
        }
    }
    
    /**
     * Parse an input stream and return a new DOM document.
     * 
     * @param uri the input URI
     * @return the DOM document
     * @throws InvalidDocumentException if parsing failed
     */
    public static synchronized Document parse(final URI uri)
            throws InvalidDocumentException {
        try {
            ParsingErrorHandler eh = initBuilder();
            Document doc = _docBuilder.parse(uri.toString());
            if (eh.getException() != null) {
                throw eh.getException();
            }
            return doc;
        } catch (SAXException e) {
            throw new InvalidDocumentException(e);
        } catch (IOException e) {
            throw new InvalidDocumentException(e);
        }
    }
    /**
     * Creates a builder if it is not there and associate an error handler.
     * <p/>
     * This is not thread safe.
     * 
     * @return and error handler associated with the builder
     * @throws InvalidDocumentException if initialization fails
     */
    protected static ParsingErrorHandler initBuilder()
            throws InvalidDocumentException {
        try {
            if (_docBuilder == null) {
                _docBuilder = newBuilder();
            }
            ParsingErrorHandler eh = new ParsingErrorHandler();
            _docBuilder.setErrorHandler(eh);
            return eh;
        } catch (ParserConfigurationException e) {
            throw new InvalidDocumentException(e);
        }
    }    

    /**
     * Setup a new document builder.
     * 
     * @return a new document builder
     * @throws ParserConfigurationException if builder cannot be constructed
     */
    private static DocumentBuilder newBuilder()
            throws ParserConfigurationException {
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        docFac.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        return docBuilder;
    }

    /**
     * Parse an input file and return a new DOM document.
     * 
     * @param file the input file
     * @return the DOM document
     * @throws InvalidDocumentException if parsing failed
     */
    public static Document parse(File file) throws InvalidDocumentException {
        try {
            return parse(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new InvalidDocumentException(e);
        }
    }

    /**
     * Parse an input string and return a new DOM document.
     * 
     * @param file the input file
     * @return the DOM document
     * @throws InvalidDocumentException if parsing failed
     */
    public static Document parse(String xml) throws InvalidDocumentException {
        return parse(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Log any exception and store it for later reporting.
     * 
     */
    private static class ParsingErrorHandler implements ErrorHandler {

        private Log _log = LogFactory.getLog(ParsingErrorHandler.class);

        private InvalidDocumentException _e;

        public void error(SAXParseException e) {
            _log.error("Parsing error:", e);
            _e = new InvalidDocumentException(e);
        }

        public void fatalError(SAXParseException e) {
            _log.error("Parsing error:", e);
            _e = new InvalidDocumentException(e);
        }

        public void warning(SAXParseException e) {
            _log.warn("Parsing error:", e);
            _e = new InvalidDocumentException(e);
        }

        public InvalidDocumentException getException() {
            return _e;
        }

    }

}
