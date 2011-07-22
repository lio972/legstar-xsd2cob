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
public class Xsd2CobGeneratorTest extends AbstractTest {

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
        Xsd2CobGenerator gen = new Xsd2CobGenerator();
        Document doc = DocumentFactory.parse(new File(XSD_REF_DIR,
                "Xsd2CobAnnotatorTest/stockquote.wsdl.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        gen.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, gen);
        navigator.visit();

        check("stockquote.wsdl.xsd", "cpy", gen.toString());
    }

    /**
     * Showcase Issue 2.
     * 
     * @throws Exception if generation fails
     */
    public void testGenWithSingleDependingOn() throws Exception {
        Xsd2CobGenerator gen = new Xsd2CobGenerator();
        Document doc = DocumentFactory.parse(new File(XSD_REF_DIR,
                "Xsd2CobAnnotatorTest/listssdo.xsd.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        gen.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, gen);
        navigator.visit();

        check("listssdo.xsd.xsd", "cpy", gen.toString());
    }

    /**
     * Showcase Issue 2.
     * 
     * @throws Exception if generation fails
     */
    public void testGenWithMultipleDependingOn() throws Exception {
        Xsd2CobGenerator gen = new Xsd2CobGenerator();
        Document doc = DocumentFactory.parse(new File(XSD_REF_DIR,
                "Xsd2CobAnnotatorTest/MSNSearch.wsdl.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        gen.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, gen);
        navigator.visit();

        check("MSNSearch.wsdl.xsd", "cpy", gen.toString());
    }

    /**
     * Should not generate dynamic counters.
     * 
     * @throws Exception if generation fails
     */
    public void testGenWithFixedSizeArray() throws Exception {
        Xsd2CobGenerator gen = new Xsd2CobGenerator();
        Document doc = DocumentFactory.parse(new File(XSD_REF_DIR,
                "Xsd2CobAnnotatorTest/listssdofixed.xsd.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        gen.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, gen);
        navigator.visit();

        check("listssdofixed.xsd.xsd", "cpy", gen.toString());
    }
}
