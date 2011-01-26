package com.legstar.xsd.def;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.tomcat.Tomcat6xInstalledLocalContainer;
import org.codehaus.cargo.container.tomcat.Tomcat6xStandaloneLocalConfiguration;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * Test the XsdToCobolStructure API.
 * 
 */
public class Xsd2CobTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    /**
     * Test MSNSearch generation.
     */
    public void testMSNSearch() throws Exception {

        Xsd2CobModel model = new Xsd2CobModel();

        /* Use custom XSLT to change some element sizes from defualts. */
        model.setCustomXsltFileName("src/test/resources/xslt/MSNSearch.xsl");

        Xsd2Cob api = new Xsd2Cob(model);

        XsdToCobolStringResult results = api
                .translate(FileUtils.readFileToString(new File(XSD_DIR,
                        "MSNSearch.wsdl"), "UTF-8"));
        check("MSNSearch", "xsd", results.getCobolXsd());
        check("MSNSearch", "cpy", results.getCobolStructure());
    }

    /**
     * Test cultureinfo generation.
     */
    public void testCultureinfo() throws Exception {

        InstalledLocalContainer webapp = getContainer("target/wars/legstar-test-cultureinfo.war");
        webapp.start();
        try {

            Xsd2Cob api = new Xsd2Cob();

            XsdToCobolStringResult results = api
                    .translate(new URI(
                            "http://localhost:8080/legstar-test-cultureinfo/getinfo?wsdl"));
            check("cultureinfo", "xsd", results.getCobolXsd());
            check("cultureinfo", "cpy", results.getCobolStructure());
        } finally {
            webapp.stop();
        }

    }

    /**
     * Test jvmquery generation.
     */
    public void testJVMQuery() throws Exception {

        InstalledLocalContainer webapp = getContainer("target/wars/legstar-test-jvmquery.war");
        webapp.start();
        try {
            Xsd2CobModel model = new Xsd2CobModel();
            /* We switch namespace to avoid conflict with jvmquery pojo. */
            model.setNewTargetNamespace("http://jvmquery.ws.cases.test.xsdc.legstar.com/");

            Xsd2Cob api = new Xsd2Cob(model);

            XsdToCobolStringResult results = api
                    .translate(new URI(
                            "http://localhost:8080/legstar-test-jvmquery/queryJvm?wsdl"));
            check("jvmquery-ws", "xsd", results.getCobolXsd());
            check("jvmquery-ws", "cpy", results.getCobolStructure());
        } finally {
            webapp.stop();
        }

    }

    /**
     * Create a Cargo web container deploying a war in it.
     * 
     * @param warLocation the war location
     * @return a container ready to start
     */
    protected InstalledLocalContainer getContainer(final String warLocation) {
        Deployable war = new WAR(warLocation);
        LocalConfiguration configuration = new Tomcat6xStandaloneLocalConfiguration(
                "target/tomcat6x");
        configuration.addDeployable(war);
        InstalledLocalContainer webapp = new Tomcat6xInstalledLocalContainer(
                configuration);
        webapp.setHome(System.getenv("CATALINA_HOME"));
        webapp.setOutput("target/cargo.log");
        return webapp;
    }

}
