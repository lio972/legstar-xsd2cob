package com.legstar.xsd.java;

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;

import com.legstar.coxb.util.ClassUtil;
import com.legstar.coxb.util.NameUtil;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdRootElement;
import com.legstar.xsd.XsdToCobolStringResult;
import com.legstar.xsd.def.Xsd2Cob;

public class Java2Cob extends Xsd2Cob {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * Construct the translator.
     */
    public Java2Cob() {
        this(null);
    }

    /**
     * Construct the translator.
     * 
     * @param model the configuration data
     */
    public Java2Cob(final Java2CobModel model) {
        super((model == null) ? new Java2CobModel() : model);
    }

    /**
     * Execute the translation from Java classes to COBOL-annotated XML Schema
     * and COBOL structure.
     * 
     * @return the XML Schema and the COBOL structure as strings
     */
    public XsdToCobolStringResult translate() throws InvalidJavaException {
        return translate(getModel().getClassNames());
    }

    /**
     * Execute the translation from Java classes to COBOL-annotated XML Schema
     * and COBOL structure.
     * 
     * @param javaClassNames the list of fully qualified java class names
     * @return the XML Schema and the COBOL structure as strings
     */
    public XsdToCobolStringResult translate(final List < String > javaClassNames)
            throws InvalidJavaException {
        try {
            Map < String, String > complexTypeToJavaClassMap = new HashMap < String, String >();
            XmlSchema schema = parse(javaClassNames, complexTypeToJavaClassMap);

            if (complexTypeToJavaClassMap.size() == 0) {
                throw new InvalidJavaException(
                        "Schemagen did not find any java class");
            }

            /*
             * Schema produced by schemagen only contains complex types so we
             * need to inject root elements to seed the annotation process.
             */
            List < XsdRootElement > newRootElements = null;
            if (getModel().getNewRootElements() == null) {
                newRootElements = new ArrayList < XsdRootElement >();
            } else {
                newRootElements = getModel().getNewRootElements();
            }
            for (String javaClassName : javaClassNames) {
                newRootElements.add(getXsdRootElement(javaClassName,
                        complexTypeToJavaClassMap));
            }

            return translate(schema, newRootElements, complexTypeToJavaClassMap);
        } catch (InvalidXsdException e) {
            throw new InvalidJavaException(e);
        }
    }

    /**
     * Converts a fully qualified java class name to an element/complexType map
     * entry. The complex type is retrieved from a map of complex types to java
     * classes. The element name is simply created from the complex type name.
     * 
     * @param complexTypeToJavaClassMap maps a complex type to a fully qualified
     *            java class name
     * @param javaClassName a fully qualified java class name
     * @return a root element entry
     */
    protected XsdRootElement getXsdRootElement(final String javaClassName,
            Map < String, String > complexTypeToJavaClassMap) {
        String typeName = null;
        for (Entry < String, String > entry : complexTypeToJavaClassMap
                .entrySet()) {
            if (entry.getValue().equals(javaClassName)) {
                typeName = entry.getKey();
                break;
            }

        }
        return new XsdRootElement(NameUtil.toVariableName(typeName), typeName);
    }

    /**
     * Parse a list of java classes and generate an XML Schema.
     * <p/>
     * If pathElements were passed to us we switch the context class loader to
     * make sure to contain these elements.
     * <p/>
     * From there on, we hope to load the classes from the classpath.
     * 
     * @param javaClassNames the list of fully qualified java class names
     * @return an XML schema
     * @throws InvalidJavaException if something fails
     */
    protected XmlSchema parse(final List < String > javaClassNames,
            final Map < String, String > complexTypeToJavaClassMap)
            throws InvalidJavaException {
        ClassLoader parentCl = Thread.currentThread().getContextClassLoader();
        if (parentCl == null) {
            parentCl = this.getClass().getClassLoader();
        }
        try {

            if (_log.isDebugEnabled()) {
                _log.debug("about to execute schemagen on " + javaClassNames);
            }

            Thread.currentThread().setContextClassLoader(
                    extendedClasspath(parentCl));

            List < Class < ? > > classTypes = new ArrayList < Class < ? > >();
            for (String className : javaClassNames) {
                classTypes.add(ClassUtil.loadClass(className));
            }

            XmlSchema schema = JavaReader.read(classTypes,
                    complexTypeToJavaClassMap, getModel()
                            .getNewTargetNamespace());

            if (_log.isDebugEnabled()) {
                _log.debug("returned from schemagen");
                _log.debug("complex types to java class map is "
                        + complexTypeToJavaClassMap);
                _log.debug("xml schema is ");
                StringWriter writer = new StringWriter();
                schema.write(writer);
                _log.debug(writer.toString());
            }

            return schema;

        } catch (ClassNotFoundException e) {
            throw new InvalidJavaException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(parentCl);
        }
    }

    /**
     * Uses the path element locations passed as parameters to create a new
     * class loader capable of finding the java classes to be parsed.
     * 
     * @param parentCl the parent class loader
     * @return a new class loader which will search all path element locations
     * @throws InvalidJavaException if class loader cannot be built
     */
    protected ClassLoader extendedClasspath(final ClassLoader parentCl)
            throws InvalidJavaException {
        try {
            if (getModel().getPathElementLocations() != null
                    && getModel().getPathElementLocations().size() > 0) {
                List < URL > urls = new ArrayList < URL >();
                for (String pathElementLocation : getModel()
                        .getPathElementLocations()) {
                    URI uri = new URI(pathElementLocation);
                    /*
                     * If the URI is relative, assume it is a file URI relative
                     * to the current directory.
                     */
                    if (!uri.isAbsolute()) {
                        String userDir = System.getProperty("user.dir");
                        URI userDirURI = (new File(userDir)).toURI();
                        uri = userDirURI.resolve(uri);
                    }
                    urls.add(uri.toURL());
                }

                return new URLClassLoader(urls.toArray(new URL[] {}), parentCl);
            } else {
                return parentCl;
            }
        } catch (MalformedURLException e) {
            throw new InvalidJavaException(e);
        } catch (URISyntaxException e) {
            throw new InvalidJavaException(e);
        }
    }

    /**
     * @return the configuration data
     */
    public Java2CobModel getModel() {
        return (Java2CobModel) super.getModel();
    }
}
