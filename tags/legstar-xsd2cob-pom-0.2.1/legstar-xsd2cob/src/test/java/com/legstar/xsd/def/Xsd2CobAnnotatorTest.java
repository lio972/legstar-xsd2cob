package com.legstar.xsd.def;

import com.legstar.xsd.AbstractTest;

public class Xsd2CobAnnotatorTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    public void testSimple() throws Exception {
        visitAndCheck("simple.xsd", new Xsd2CobAnnotator());
    }

    public void testCustomer() throws Exception {
        visitAndCheck("customertype.xsd", new Xsd2CobAnnotator());
    }

    public void testWsdlWithTypeParts() throws Exception {
        visitAndCheck("stockquote.wsdl", new Xsd2CobAnnotator());
    }

    public void testXsdIncludes() throws Exception {
        visitAndCheck("include.xsd", new Xsd2CobAnnotator());
    }

    public void testWsdlMSNSearch() throws Exception {
        visitAndCheck("MSNSearch.wsdl", new Xsd2CobAnnotator());
    }

    public void testListssdo() throws Exception {
        visitAndCheck("listssdo.xsd", new Xsd2CobAnnotator());
    }

    public void testListssdofixed() throws Exception {
        visitAndCheck("listssdofixed.xsd", new Xsd2CobAnnotator());
    }
}
