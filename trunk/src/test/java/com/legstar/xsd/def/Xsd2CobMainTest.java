/*******************************************************************************
 * Copyright (c) 2010 LegSem.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     LegSem - initial API and implementation
 ******************************************************************************/
package com.legstar.xsd.def;

import java.io.File;

import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import com.legstar.xsd.AbstractTest;

/**
 * Test the executable jar.
 * 
 */
public class Xsd2CobMainTest extends AbstractTest {

    /** True when references should be created. */
    private static final boolean CREATE_REFERENCES = false;

    private Xsd2CobMain _main;

    /** @{inheritDoc */
    public void setUp() throws Exception {
        super.setUp();
        setCreateReferences(CREATE_REFERENCES);
        _main = new Xsd2CobMain(new Xsd2CobModel());
    }

    /**
     * Test without arguments.
     */
    public void testNoArgument() {
        try {
            Options options = _main.createOptions();
            assertTrue(_main.collectOptions(options, null));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test with help argument.
     */
    public void testHelpArgument() {
        try {
            Options options = _main.createOptions();
            assertFalse(_main.collectOptions(options, new String[] { "-h" }));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test with unsupported argument.
     */
    public void testUnsupportedArgument() {
        try {
            Options options = _main.createOptions();
            _main.collectOptions(options, new String[] { "- #" });
        } catch (Exception e) {
            assertEquals(
                    "org.apache.commons.cli.UnrecognizedOptionException: Unrecognized option: - #",
                    e.toString());
        }
    }

    /**
     * Test with targetCobolEncoding argument.
     */
    public void testTargetCobolEncodingArgument() {
        try {
            Options options = _main.createOptions();
            assertEquals(null, _main.getModel().getTargetCobolEncoding());
            assertTrue(_main.collectOptions(options,
                    new String[] { "-e ISO-8859-1" }));
            assertEquals("ISO-8859-1", _main.getModel()
                    .getTargetCobolEncoding());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test with bad input URI.
     */
    public void testWrongInputArgument() {
        try {
            _main.execute(new String[] { "" });
            fail();
        } catch (Exception e) {
            assertEquals("No input URI specified", e.getCause().getMessage());
        }
        try {
            _main.execute(new String[] { "-i", "c:\\noaURI" });
            fail();
        } catch (Exception e) {
            assertEquals(
                    "Illegal character in opaque part at index 2: c:\\noaURI",
                    e.getCause().getMessage());
        }
    }

    /**
     * Test a complete generation.
     */
    public void testConfigurationArgument() {
        try {
            _main.execute(new String[] { "-i",
                    (new File(XSD_DIR, "customertype.xsd")).toURI().toString(),
                    "-ox", GEN_XSD_DIR.toString(), "-oc",
                    GEN_COBOL_DIR.toString(), "-r", "customer:CustomerType" });
            check("customertype", "xsd", FileUtils.readFileToString(new File(
                    GEN_XSD_DIR, "customertype.xsd"), "UTF-8"));
            check("customertype", "cpy", FileUtils.readFileToString(new File(
                    GEN_COBOL_DIR, "customertype.cpy"), "UTF-8"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
