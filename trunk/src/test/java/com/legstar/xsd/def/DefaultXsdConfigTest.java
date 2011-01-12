package com.legstar.xsd.def;

import junit.framework.TestCase;

public class DefaultXsdConfigTest extends TestCase {
	
	public void testLoading() throws Exception {
		DefaultXsdConfig xsdConfig = new DefaultXsdConfig();
		xsdConfig.load();
		assertEquals(32, xsdConfig.getAlphanumericLen());
		assertEquals(32, xsdConfig.getOctetStreamLen());
		assertEquals(9, xsdConfig.getIntTotalDigits());
		assertEquals(4, xsdConfig.getShortTotalDigits());
		assertEquals(1, xsdConfig.getBoolTotalDigits());
		assertEquals(18, xsdConfig.getLongTotalDigits());
		assertEquals(9, xsdConfig.getDecTotalDigits());
		assertEquals(2, xsdConfig.getDecFracDigits());
		assertEquals(4, xsdConfig.getSingleFloatByteLen());
		assertEquals(8, xsdConfig.getDoubleFloatByteLen());
		assertEquals(10, xsdConfig.getDefaultMaxOccurs());
	}

}
