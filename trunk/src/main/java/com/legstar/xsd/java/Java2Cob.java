package com.legstar.xsd.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ws.commons.schema.XmlSchema;

import com.legstar.coxb.util.ClassUtil;
import com.legstar.coxb.util.NameUtil;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdRootElement;
import com.legstar.xsd.XsdToCobolStringResult;
import com.legstar.xsd.def.Xsd2Cob;

public class Java2Cob extends Xsd2Cob {

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
     * We hope to load the classes from the classpath.
     * 
     * @param javaClassNames the list of fully qualified java class names
     * @return an XML schema
     * @throws InvalidJavaException if something fails
     */
    protected XmlSchema parse(final List < String > javaClassNames,
            final Map < String, String > complexTypeToJavaClassMap)
            throws InvalidJavaException {
        try {

            List < Class < ? > > classTypes = new ArrayList < Class < ? > >();
            for (String className : javaClassNames) {
                classTypes.add(ClassUtil.loadClass(className));
            }

            return JavaReader.read(classTypes, complexTypeToJavaClassMap,
                    getModel().getNewTargetNamespace());

        } catch (ClassNotFoundException e) {
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
