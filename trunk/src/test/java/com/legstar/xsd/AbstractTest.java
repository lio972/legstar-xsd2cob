package com.legstar.xsd;

import java.io.File;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;

import com.legstar.dom.DocumentFactory;

/**
 * Some helpful shared test methods.
 * 
 */
public abstract class AbstractTest extends TestCase {

    /** Test cases folder. */
    public static final File XSD_FOLDER = new File("src/test/resources/cases");

    /** Reference folder. */
    public static final File XSD_REF_FOLDER = new File(
            "src/test/resources/reference");

    /** Generated annotated classes folder. */
    public static final File GEN_XSD_FOLDER = new File("target/gen/schema");

    /** Generated COBOL copybook folder. */
    public static final File GEN_COBOL_FOLDER = new File("target/gen/cobol");

    /** This means references should be created instead of compared to results. */
    private boolean _createReferences = false;

    private Log _log = LogFactory.getLog(getClass());

    /**
     * Parse a file and generate an XML Schema.
     * 
     * @param fileName the input file (an XSD or WSDL)
     * @return an XML schema
     * @throws Exception if something fails
     */
    public XmlSchema parse(final String fileName) throws Exception {
        Document doc = DocumentFactory.parse(new File(XSD_FOLDER, fileName));
        return XsdReader.read(doc);
    }

    /**
     * Prints an XML schema in a String.
     * 
     * @param schema the XML schema
     * @return the content as a string
     */
    public String toString(final XmlSchema schema) {
        StringWriter writer = new StringWriter();
        schema.write(writer);
        String result = writer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(result);
        }
        return result;
    }

    /**
     * Parse a file and generate an XML Schema then check that schema against a
     * reference.
     * 
     * @param fileName the input file (an XSD or WSDL)
     * @throws Exception if something fails
     */
    protected void parseAndCheck(final String fileName) throws Exception {
        XmlSchema schema = parse(fileName);
        String result = toString(schema);
        check(fileName, "xsd", result);

    }

    /**
     * Visit and annotate a schema then check against a reference.
     * 
     * @param fileName the XML schema to start from
     * @param annotator the annotator to use
     * @throws Exception if something goes wrong
     */
    public void visitAndCheck(final String fileName,
            final XsdObjectProcessor annotator) throws Exception {
        XmlSchema schema = parse(fileName);
        annotator.setUp();

        XsdNavigator visitor = new XsdNavigator(schema, annotator);
        visitor.visit();
        check(fileName, "xsd", toString(schema));
    }

    /**
     * Check a result against a reference.
     * 
     * @param fileName the input file (an XSD or WSDL)
     * @param extension the reference file name extension to use
     * @throws Exception if something fails
     */
    protected void check(final String fileName, final String extension,
            final String result) throws Exception {
        File referenceFile = new File(XSD_REF_FOLDER, getUnqualName(getClass())
                + "/"
                + fileName
                + ((extension == null || extension.length() == 0) ? "" : "."
                        + extension));

        if (isCreateReferences()) {
            FileUtils.writeStringToFile(referenceFile, result, "UTF-8");
        } else {
            String expected = FileUtils
                    .readFileToString(referenceFile, "UTF-8");
            assertEquals(expected, result);
        }

    }

    /**
     * @return true if references should be created instead of compared to
     *         results
     */
    public boolean isCreateReferences() {
        return _createReferences;
    }

    /**
     * @param createReferences true if references should be created instead of
     *            compared to results
     */
    public void setCreateReferences(boolean createReferences) {
        _createReferences = createReferences;
    }

    /**
     * Return a class unqualified (no package prefix) name.
     * 
     * @param clazz the class
     * @return the unqualified name
     */
    public static String getUnqualName(final Class<?> clazz) {
        String unqname = clazz.getName();
        if (unqname.lastIndexOf('.') > 0) {
            unqname = unqname.substring(unqname.lastIndexOf('.') + 1);
        }
        return unqname;
    }

}
