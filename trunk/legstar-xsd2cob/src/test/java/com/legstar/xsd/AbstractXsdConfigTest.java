package com.legstar.xsd;

import java.util.Properties;

import junit.framework.TestCase;

public class AbstractXsdConfigTest extends TestCase {

	public void testLoadProperties() throws Exception {
		
		/* Uses the current class loader */
		Properties currentprops = new Properties();
		AbstractXsdConfig.loadProperties(currentprops, "/log4j.properties");
		assertEquals("org.apache.log4j.ConsoleAppender",
				currentprops.getProperty("log4j.appender.stdout"));

		/* Uses the thread class loader */
		Properties contextprops = new Properties();
		AbstractXsdConfig.loadProperties(contextprops, "log4j.properties");
		assertEquals("org.apache.log4j.ConsoleAppender",
				contextprops.getProperty("log4j.appender.stdout"));
		
		/* Uses a local file */
		Properties fileprops = new Properties();
		AbstractXsdConfig.loadProperties(fileprops, "src/test/resources/log4j.properties");
		assertEquals("org.apache.log4j.ConsoleAppender",
				fileprops.getProperty("log4j.appender.stdout"));
	}
	
}
