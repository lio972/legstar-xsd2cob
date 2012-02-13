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
public class Xsd2CobMapperTest extends AbstractTest {

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
    public void testMapSingleChild() throws Exception {
        Xsd2CobMapper mapper = new Xsd2CobMapper();
        Document doc = DocumentFactory.parse(new File(REF_DIR,
                "Xsd2CobAnnotatorTest/stockquote.wsdl.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        mapper.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, mapper);
        navigator.visit();

        check(getName(), "txt", mapper.getRootDataItems().toString());
    }

    /**
     * Showcase Issue 2.
     * 
     * @throws Exception if generation fails
     */
    public void testMapWithMultipleDependingOn() throws Exception {
        Xsd2CobMapper mapper = new Xsd2CobMapper();
        Document doc = DocumentFactory.parse(new File(REF_DIR,
                "Xsd2CobAnnotatorTest/MSNSearch.wsdl.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        mapper.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, mapper);
        navigator.visit();

        check(getName(), "txt", mapper.getRootDataItems().toString());
    }

    /**
     * Should not generate dynamic counters.
     * 
     * @throws Exception if generation fails
     */
    public void testMapWithFixedSizeArray() throws Exception {
        Xsd2CobMapper mapper = new Xsd2CobMapper();
        Document doc = DocumentFactory.parse(new File(REF_DIR,
                "Xsd2CobAnnotatorTest/listssdofixed.xsd.xsd"));
        XmlSchema schema = XsdReader.read(doc);
        mapper.setUp();

        XsdNavigator navigator = new XsdNavigator(schema, mapper);
        navigator.visit();

        check(getName(), "txt", mapper.getRootDataItems().toString());
    }
}
