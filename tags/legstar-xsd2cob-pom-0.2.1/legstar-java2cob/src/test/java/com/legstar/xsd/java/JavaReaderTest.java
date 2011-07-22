package com.legstar.xsd.java;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;

import org.apache.commons.io.FileUtils;
import org.apache.ws.commons.schema.XmlSchema;

import com.legstar.xsd.AbstractTest;

/**
 * Test the JavaReader class.
 * 
 */
public class JavaReaderTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    /**
     * Test the schemagen method.
     * 
     * @throws Exception if generation fails
     */
    public void testSchemagen() throws Exception {
        Map < String, String > complexTypeToJavaClassMap = new HashMap < String, String >();
        List < Class < ? >> classes = new ArrayList < Class < ? >>();
        classes.add(simple.class);
        File resultFile = JavaReader.schemagen(classes,
                complexTypeToJavaClassMap, null);
        assertEquals("com.legstar.xsd.java.JavaReaderTest$simple",
                complexTypeToJavaClassMap.get("simple"));
        check("simpleSchemagen", "xsd",
                FileUtils.readFileToString(resultFile, "UTF-8"));
    }

    /**
     * Test the read method.
     * 
     * @throws Exception if generation fails
     */
    public void testRead() throws Exception {
        Map < String, String > complexTypeToJavaClassMap = new HashMap < String, String >();
        List < Class < ? >> classes = new ArrayList < Class < ? >>();
        classes.add(simple.class);
        XmlSchema schema = JavaReader.read(classes, complexTypeToJavaClassMap,
                null);
        assertEquals("com.legstar.xsd.java.JavaReaderTest$simple",
                complexTypeToJavaClassMap.get("simple"));

        StringWriter writer = new StringWriter();
        Map < String, String > options = new HashMap < String, String >();
        options.put(OutputKeys.ENCODING, "UTF-8");
        options.put(OutputKeys.INDENT, "yes");
        options.put(OutputKeys.OMIT_XML_DECLARATION, "no");
        options.put(OutputKeys.STANDALONE, "no");

        schema.write(writer, options);
        writer.flush();

        check("simpleRead", "xsd", writer.toString());
    }

    public static class simple {
        private String aField;

        /**
         * @return the aField
         */
        public String getaField() {
            return aField;
        }

        /**
         * @param aField the aField to set
         */
        public void setaField(String aField) {
            this.aField = aField;
        }
    }

}
