package com.legstar.xsd.cob;

import java.io.File;

import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;

import com.legstar.dom.DocumentFactory;
import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdNavigator;
import com.legstar.xsd.XsdReader;

/**
 * Test the COBOL generator.
 * 
 */
public class XsdCobolGeneratorTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    /**
     * Go though a complete generation.
     * 
     * @throws Exception if generation fails
     */
    public void testGen() throws Exception {
        XsdCobolGenerator gen = new XsdCobolGenerator();
        Document doc = DocumentFactory.parse(new File(
                XSD_REF_FOLDER, "DefaultXsdAnnotatorTest/MSNSearch.wsdl.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        gen.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, gen);
        navigator.visit();

        check("MSNSearch.wsdl.xsd", "cpy", gen.toString());
    }

}
