package com.legstar.xsd.def;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.legstar.codegen.CodeGenUtil;
import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdRootElement;

/**
 * Test the Xsd2CobModel class.
 * 
 */
public class Xsd2CobModelTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
        FileUtils.forceMkdir(GEN_ANT_DIR);
    }

    public void testSerialize() {
        Xsd2CobModel model = new Xsd2CobModel();
        Properties props = model.toProperties();
        assertEquals("4", props.getProperty(Xsd2CobConfig.SHORT_TOTAL_DIGITS));

        model.setInputXsdUri((new File(XSD_DIR, "customertype.xsd")).toURI());
        props = model.toProperties();
        assertTrue(props.getProperty(Xsd2CobModel.INPUT_XSD_URI).contains(
                "src/test/resources/cases/customertype.xsd"));

        List < XsdRootElement > newRootElements = new ArrayList < XsdRootElement >();
        newRootElements.add(new XsdRootElement("el1:Type1"));
        newRootElements.add(new XsdRootElement("el2:Type2"));
        model.setNewRootElements(newRootElements);
        props = model.toProperties();
        assertEquals("el1:Type1", props.getProperty("newRootElements_0"));
        assertEquals("el2:Type2", props.getProperty("newRootElements_1"));

    }

    public void testDeserialize() {

        Properties props = new Properties();
        Xsd2CobModel model = new Xsd2CobModel(props);
        assertEquals(4, model.getXsdConfig().getShortTotalDigits());

        props.setProperty(Xsd2CobConfig.SHORT_TOTAL_DIGITS, "7");
        model = new Xsd2CobModel(props);
        assertEquals(7, model.getXsdConfig().getShortTotalDigits());

        assertNull(model.getNewRootElements());
        props.put("newRootElements_0", "el1:Type1");
        props.put("newRootElements_1", "el2:Type2");
        model = new Xsd2CobModel(props);
        assertNotNull(model.getNewRootElements());
        assertEquals("el1:Type1", model.getNewRootElements().get(0).toString());
        assertEquals("el2:Type2", model.getNewRootElements().get(1).toString());

    }

    /**
     * Use the model capability to generate an ANT script. Then submit to script
     * and check the output.
     * 
     * @throws Exception if something goes wrong
     */
    public void testAntScriptGeneration() throws Exception {
        Xsd2CobModel model = new Xsd2CobModel();
        model.setProductLocation("../../../..");
        model.setProbeFile(new File("probe.file.tmp"));
        model.setInputXsdUri((new File(XSD_DIR, "customertype.xsd")).toURI());
        model.setTargetXsdFile(GEN_XSD_DIR);
        model.setTargetCobolFile(GEN_COBOL_DIR);
        model.setTargetCobolEncoding("ISO-8859-1");
        model.addNewRootElement(new XsdRootElement("customer", "CustomerType"));

        File resultFile = genAntScriptAsFile(model);
        check("build", "xml", FileUtils.readFileToString(resultFile, "UTF-8"));
        runAnt(resultFile);

        String xsdContent = FileUtils.readFileToString(new File(GEN_XSD_DIR,
                "customertype.xsd"));
        assertTrue(xsdContent
                .contains("<cb:cobolElement cobolName=\"customer\" levelNumber=\"1\" type=\"GROUP_ITEM\"/>"));
        String cobolContent = FileUtils.readFileToString(new File(
                GEN_COBOL_DIR, "customertype.cpy"));
        assertTrue(cobolContent.contains("         03  name PIC X(32)."));
    }

    /**
     * Generates an ant script from a VLC template.
     * 
     * @param model the generation model
     * @return the script as a string
     * @throws Exception if generation fails
     */
    protected File genAntScriptAsFile(final Xsd2CobModel model)
            throws Exception {
        File resultFile = CodeGenUtil.getFile(GEN_ANT_DIR, "build.xml");
        model.generateBuild(resultFile);
        return resultFile;
    }

    /**
     * Execute an ant script.
     * 
     * @param buildFile the ant script
     * @throws Exception if ant script execution fails
     */
    protected void runAnt(final File buildFile) throws Exception {
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