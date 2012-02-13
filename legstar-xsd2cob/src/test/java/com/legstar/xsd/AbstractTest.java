package com.legstar.xsd;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;

import com.legstar.dom.DocumentFactory;

/**
 * Some helpful shared test methods.
 * 
 */
public abstract class AbstractTest extends TestCase {

    /** Test cases folder. */
    public static final File XSD_DIR = new File("src/test/resources/cases");

    /** Reference folder. */
    public static final File REF_DIR = new File("src/test/resources/reference");

    /** Extension added to xsd reference files. */
    public static final String XSD_REF_FILE_EXT = "xsd";

    /** Extension added to copybook reference files. */
    public static final String CPY_REF_FILE_EXT = "cpy";

    /** Extension added to other reference files. */
    public static final String OTHER_REF_FILE_EXT = "txt";

    /** Generated annotated classes folder. */
    public static final File GEN_XSD_DIR = new File("target/gen/schema");

    /** Generated COBOL copybook folder. */
    public static final File GEN_COBOL_DIR = new File("target/gen/cobol");

    /** Generated ANT script folder. */
    public static final File GEN_ANT_DIR = new File("target/gen/ant");

    /** This means references should be created instead of compared to results. */
    private boolean _createReferences = false;

    private Log logger = LogFactory.getLog(getClass());

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        if (isCreateReferences()) {
            cleanOldReferences();
        }
        if (GEN_XSD_DIR.exists()) {
            FileUtils.forceDelete(GEN_XSD_DIR);
        }
        if (GEN_COBOL_DIR.exists()) {
            FileUtils.forceDelete(GEN_COBOL_DIR);
        }
        if (GEN_ANT_DIR.exists()) {
            FileUtils.forceDelete(GEN_ANT_DIR);
        }
    }

    /**
     * This is our chance to remove reference files that are no longer used by a
     * test case. This happens when test cases are renamed or removed.
     */
    protected void cleanOldReferences() {
        if (!getReferenceFolder().exists()) {
            return;
        }
        Method[] methods = getClass().getDeclaredMethods();

        for (File refFile : FileUtils.listFiles(getReferenceFolder(),
                new String[] { XSD_REF_FILE_EXT, CPY_REF_FILE_EXT,
                        OTHER_REF_FILE_EXT }, false)) {
            boolean found = false;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(
                        FilenameUtils.getBaseName(refFile.getName()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                refFile.delete();
            }
        }
    }

    /**
     * Location where where reference files are stored for this test case.
     * 
     * @return the test case reference files folder
     */
    public File getReferenceFolder() {
        return new File(REF_DIR, getClass().getSimpleName());
    }

    /**
     * Parse a file and generate an XML Schema.
     * 
     * @param fileName the input file (an XSD or WSDL)
     * @return an XML schema
     * @throws Exception if something fails
     */
    public XmlSchema parse(final String fileName) throws Exception {
        Document doc = DocumentFactory.parse(new File(XSD_DIR, fileName));
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
        if (logger.isDebugEnabled()) {
            logger.debug(result);
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
     * @param result the test results
     * 
     * @throws IOException if something fails
     */
    protected void check(final String fileName, final String extension,
            final String result) {
        try {
            logger.debug(getClass().getSimpleName() + "-" + getName() + ":\n"
                    + result);
            File referenceFile = new File(getReferenceFolder(), fileName + "."
                    + extension);

            if (isCreateReferences()) {
                FileUtils.writeStringToFile(referenceFile, result, "UTF-8");
            } else {
                String expected = FileUtils.readFileToString(referenceFile,
                        "UTF-8");
                assertEquals(expected, result);
            }
        } catch (IOException e) {
            logger.error("Test " + getName() + " failed", e);
            fail(e.getMessage());
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
    public static String getUnqualName(final Class < ? > clazz) {
        String unqname = clazz.getName();
        if (unqname.lastIndexOf('.') > 0) {
            unqname = unqname.substring(unqname.lastIndexOf('.') + 1);
        }
        return unqname;
    }

    /**
     * Execute an ant script.
     * 
     * @param buildFile the ant script
     * @throws Exception if ant script execution fails
     */
    public void runAnt(final File buildFile) throws Exception {
        final Project project = new Project();
        project.setCoreLoader(this.getClass().getClassLoader());
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);
        helper.parse(project, buildFile);
        Vector < String > targets = new Vector < String >();
        targets.addElement(project.getDefaultTarget());
        project.setBaseDir(new File("."));
        project.executeTargets(targets);
    }

}
