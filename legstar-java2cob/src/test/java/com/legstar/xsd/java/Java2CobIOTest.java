package com.legstar.xsd.java;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.InvalidParameterException;

public class Java2CobIOTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    private Java2CobIO _java2cob;

    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
        _java2cob = new Java2CobIO(new Java2CobModel());
    }

    /**
     * Test parameters checking.
     * 
     * @throws Exception if testing fails
     */
    public void testCheckParameters() throws Exception {
        try {
            _java2cob.checkParameters();
            fail();
        } catch (InvalidParameterException e) {
            assertEquals("No java classes were specified", e.getMessage());
        }
    }

    /**
     * Test the getDefaultName method.
     * 
     * @throws URISyntaxException if URI is invalid
     */
    public void testgetDefaultName() throws URISyntaxException {
        assertNull(_java2cob.getDefaultName());

        _java2cob.getModel().setClassNames(
                Arrays.asList(new String[] { "Abcd" }));
        assertEquals("abcd", _java2cob.getDefaultName());

        _java2cob.getModel().setClassNames(
                Arrays.asList(new String[] { "efgf.Abcd" }));
        assertEquals("abcd", _java2cob.getDefaultName());

        _java2cob.getModel().setClassNames(
                Arrays.asList(new String[] { "efgf.Abcd", "efgf.1234" }));
        assertEquals("efgf", _java2cob.getDefaultName());

        _java2cob.getModel()
                .setClassNames(
                        Arrays.asList(new String[] { "ijkl.efgf.Abcd",
                                "ijkl.efgf.1234" }));
        assertEquals("efgf", _java2cob.getDefaultName());

        _java2cob.getModel().setClassNames(
                Arrays.asList(new String[] { "ijkl.efgf.Abcd$789",
                        "ijkl.efgf.1234" }));
        assertEquals("efgf", _java2cob.getDefaultName());
    }

    /**
     * Test a complete translation for a java class.
     * 
     * @throws Exception if test fails
     */
    public void testJavaTranslation() throws Exception {

        _java2cob
                .getModel()
                .setClassNames(
                        Arrays.asList(new String[] { "com.legstar.xsd.java.JavaReaderTest$simple" }));
        _java2cob.getModel().setTargetXsdFile(GEN_XSD_DIR);
        _java2cob.getModel().setTargetCobolFile(GEN_COBOL_DIR);

        _java2cob.execute();

        check("simple", "xsd", FileUtils.readFileToString(new File(GEN_XSD_DIR,
                "javareadertest$simple.xsd"), "UTF-8"));
        check("simple", "cpy", FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "javareadertest$simple.cpy"), "UTF-8"));
    }

}
