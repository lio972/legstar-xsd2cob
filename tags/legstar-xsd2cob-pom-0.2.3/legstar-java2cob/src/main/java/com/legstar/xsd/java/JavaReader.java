package com.legstar.xsd.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.xml.sax.SAXParseException;

import com.legstar.coxb.CobolMarkup;
import com.legstar.xsd.XsdConstants;
import com.legstar.xsd.XsdReader;
import com.sun.xml.bind.api.CompositeStructure;
import com.sun.xml.bind.api.ErrorListener;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.model.core.Ref;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationsException;
import com.sun.xml.bind.v2.schemagen.XmlSchemaGenerator;

/**
 * Uses JAXB XJC schemagen utility to turn a set of java classes to XML schema.
 * 
 */
public final class JavaReader {

    /**
     * Utility class.
     */
    private JavaReader() {

    }

    /**
     * Reads a set of java classes and produce an Apache XML schema.
     * 
     * @param classes a set of java classes
     * @param complexTypeToJavaClassMap an output map between complex types and
     *            java class names
     * @param targetNamespace the target namespace to use
     * @return an XML Schema
     * @throws InvalidJavaException if reading java classes fails
     */
    public static XmlSchema read(final List < Class < ? >> classes,
            final Map < String, String > complexTypeToJavaClassMap,
            final String targetNamespace) throws InvalidJavaException {
        try {
            File tempXsdFile = schemagen(classes, complexTypeToJavaClassMap,
                    targetNamespace);
            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(tempXsdFile), "UTF-8");
            XmlSchema schema = schemaCol.read(reader, null);

            /* Add the COXB namespace */
            XsdReader.addNamespace(XsdConstants.DEFAULT_COXB_NS_PFX,
                    CobolMarkup.NS, schema);

            /* Make sure the generated schema has an XML declaration */
            schema.setInputEncoding("UTF-8");

            return schema;
        } catch (UnsupportedEncodingException e) {
            throw new InvalidJavaException(e);
        } catch (FileNotFoundException e) {
            throw new InvalidJavaException(e);
        }

    }

    /**
     * This is basically the same code as
     * <code>JAXBContextImpl.createSchemaGenerator</code>. The reason it is
     * duplicated here is because JAXBContextImpl is declared final and cannot
     * be derived from and all useful methods are private. What we need is to
     * grab the relationship between Class names and XSD complex type names.
     * JAXB does its own name conversion and the relationship between the
     * original class name and the new complex type name is lost with the
     * standard JAXB schemagen ant Task.
     * 
     * @param classes a set of java classes
     * @param complexTypeToJavaClassMap an output map between complex types and
     *            java class names
     * @param targetNamespace the target namespace to use
     * @return a temporary file holding the result of JAXB schemagen
     * @throws InvalidJavaException is something goes wrong
     */
    @SuppressWarnings({ "rawtypes" })
    protected static File schemagen(final List < Class < ? >> classes,
            final Map < String, String > complexTypeToJavaClassMap,
            final String targetNamespace) throws InvalidJavaException {
        try {
            final SAXParseException[] exceptions = new SAXParseException[1];
            final Map < Class, Class > subclassReplacements = Collections
                    .emptyMap();

            final ModelBuilder < Type, Class, Field, Method > builder = new ModelBuilder < Type, Class, Field, Method >(
                    new RuntimeInlineAnnotationReader(), Navigator.REFLECTION,
                    subclassReplacements, targetNamespace);

            IllegalAnnotationsException.Builder errorHandler = new IllegalAnnotationsException.Builder();
            builder.setErrorHandler(errorHandler);

            for (Class c : classes) {
                if (c == CompositeStructure.class) {
                    // CompositeStructure doesn't have TypeInfo, so skip it.
                    // We'll add JaxBeanInfo for this later automatically
                    continue;
                }
                builder.getTypeInfo(new Ref < Type, Class >(c));
            }

            TypeInfoSet < Type, Class, Field, Method > infoSet = builder.link();

            errorHandler.check();
            assert infoSet != null : "if no error was reported, the link must be a success";

            /* Store the name mapping (Java Class to XSD Complex Type) */
            for (ClassInfo < Type, Class > ci : infoSet.beans().values()) {
                complexTypeToJavaClassMap.put(ci.getTypeName().getLocalPart(),
                        ci.getClazz().getName());
            }

            XmlSchemaGenerator < Type, Class, Field, Method > xsdgen = new XmlSchemaGenerator < Type, Class, Field, Method >(
                    infoSet.getNavigator(), infoSet);

            File tempXsdFile = File.createTempFile("legstar-xsd2cob", ".tmp");
            tempXsdFile.deleteOnExit();

            xsdgen.write(new MySchemaOutputResolver(tempXsdFile),
                    new ErrorListener() {
                        public void error(final SAXParseException e) {
                            exceptions[0] = e;
                        }

                        public void fatalError(final SAXParseException e) {
                            exceptions[0] = e;
                        }

                        public void warning(final SAXParseException e) {
                        }

                        public void info(final SAXParseException e) {
                        }
                    });
            if (exceptions[0] != null) {
                throw new InvalidJavaException(exceptions[0]);
            } else {
                return tempXsdFile;
            }

        } catch (IllegalAnnotationsException e) {
            throw new InvalidJavaException(e);
        } catch (IOException e) {
            throw new InvalidJavaException(e);
        }

    }

    /**
     * Implementation of JAXB output resolver.
     */
    public static class MySchemaOutputResolver extends SchemaOutputResolver {
        /** The schema file. */
        private File mSchemaFile;

        /**
         * Constructor for a schema file.
         * 
         * @param schemaFile the schema file
         */
        public MySchemaOutputResolver(final File schemaFile) {
            mSchemaFile = schemaFile;
        }

        /** {@inheritDoc} */
        public Result createOutput(final String namespaceUri,
                final String suggestedFileName) throws IOException {
            return new StreamResult(mSchemaFile);
        }
    }

}
