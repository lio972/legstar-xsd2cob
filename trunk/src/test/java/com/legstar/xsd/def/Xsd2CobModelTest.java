package com.legstar.xsd.def;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.legstar.xsd.AbstractTest;
import com.legstar.xsd.XsdRootElement;

public class Xsd2CobModelTest extends AbstractTest {

    public void testSerialize() {
        Xsd2CobModel model = new Xsd2CobModel();
        Properties props = model.toProperties();
        assertEquals("4", props.getProperty(Xsd2CobConfig.SHORT_TOTAL_DIGITS));

        model.setInputXsdUri((new File(XSD_FOLDER, "customertype.xsd")).toURI());
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
}
