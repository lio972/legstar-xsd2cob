package com.legstar.xsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple configuration model based on a properties file loaded
 * from classpath.
 *
 */
public abstract class AbstractXsdConfig extends Properties {
	
    /** Serial ID. */
	private static final long serialVersionUID = 1L;

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * At construction time, we attempt to load from file system but
     * just ignore any failures (User did not provide a
     * configuration on file and wants to use defaults).
     */
    public AbstractXsdConfig() {
        try {
            load();
        } catch (IOException e) {
            _log.info("File " + getLocation() + " was not found. Using defaults.");
            return;
        }
    }
    
    /**
     * Create a configuration from a previous set of properties.
     * @param props the previous property set
     */
    public AbstractXsdConfig(final Properties props) {
        putAll(props);
    }
    
    

    /**
     * Load the properties from file system.
     * @throws IOException if file not readable
     */
    public void load() throws IOException {
        loadProperties(this, getLocation());
    }
    
    /**
     * @return the location of the configuration file on file system
     */
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
	
	/**
	 * Return a property value.
	 * @param propertyName the property name
	 * @param defaultValue the default value
	 * @return the property value or default if there are no values
	 */
	public int getIntProperty(final String propertyName, final int defaultValue) {
		String value = getProperty(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

    /**
     * Set a property value.
     * @param propertyName the property name
     * @param value the value
     */
    public void setIntProperty(final String propertyName, final int value) {
        setProperty(propertyName, Integer.toString(value));
    }
    
    /**
     * Serialize to another set of properties.
     * @param props the other property set
     */
    public void toProperties(final Properties props) {
        props.putAll(this);
    }

}
