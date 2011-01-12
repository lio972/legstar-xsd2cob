package com.legstar.xsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A simple configuration model based on a properties file loaded
 * from classpath.
 *
 */
public abstract class AbstractXsdConfig extends Properties {
	
    /** Serial ID. */
	private static final long serialVersionUID = 1L;

    public void load() throws IOException {
    	loadProperties(this, getLocation());
    }
    
    public abstract String getLocation();
    

	/**
	 * Loads a properties file from a location.
	 * <p/>
	 * The location could be within the classpath. A first attempt using the
	 * properties class loader is made. If that fails, the context class
	 * loader is tried. Finally if all that fails, a local file is tried.
	 * 
	 * @param location the properties content location
	 * @param props the Properties class to load
	 * @throws IOException if content cannot be located
	 */
	public static void loadProperties(final Properties props, final String location)
			throws IOException {
		InputStream stream = null;
		try {
			stream = props.getClass().getResourceAsStream(location);
			if (stream == null) {
				stream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(location);
			}
			if (stream == null) {
				stream = new FileInputStream(new File(location));
			}
			props.load(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

}
