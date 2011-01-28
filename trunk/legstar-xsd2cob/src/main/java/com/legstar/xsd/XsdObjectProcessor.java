package com.legstar.xsd;

import java.io.IOException;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 * Class which implement this interface are capable of performing
 * some processing on XML Schema objects.
 *
 */
public interface XsdObjectProcessor {
	
	
    /**
     * Initialize whatever needs to be initialized.
     * @throws IOException if initialization fails
     */
    public void setUp() throws IOException;

	/**
	 * Process an XML schema element.
	 * 
	 * @param schema  the XML Schema being navigated
	 * @param xsdElement the XML Schema element to process
	 * @param level the current level in the elements hierarchy.
	 * @throws XsdMappingException if processing fails
	 */
    void processElement(
            final XmlSchema schema,
            final XmlSchemaElement xsdElement,
            final int level) throws XsdMappingException;


    /**
	 * Process an XML schema complex type element.
	 * 
	 * @param schema  the XML Schema being navigated
	 * @param xsdComplexType the XML Schema complex type to process
	 * @param level the current level in the elements hierarchy.
	 * @throws XsdMappingException if processing fails
	 */
    void processComplexType(
            final XmlSchema schema,
            final XmlSchemaComplexType xsdComplexType,
            final int level) throws XsdMappingException;
    
}
