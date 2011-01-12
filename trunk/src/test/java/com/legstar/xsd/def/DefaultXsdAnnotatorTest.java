package com.legstar.xsd.def;

import com.legstar.xsd.AbstractTest;

public class DefaultXsdAnnotatorTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
    }

    public void testSimple() throws Exception {
        visitAndCheck("simple.xsd", new DefaultXsdAnnotator());
    }

    public void testCustomer() throws Exception {
        visitAndCheck("customertype.xsd", new DefaultXsdAnnotator());
    }

    public void testWsdlWithTypeParts() throws Exception {
        visitAndCheck("withtypeparts.wsdl", new DefaultXsdAnnotator());
    }

    public void testXsdIncludes() throws Exception {
        visitAndCheck("include.xsd", new DefaultXsdAnnotator());
    }

    public void testWsdlMSNSearch() throws Exception {
        visitAndCheck("MSNSearch.wsdl", new DefaultXsdAnnotator());
    }
}
