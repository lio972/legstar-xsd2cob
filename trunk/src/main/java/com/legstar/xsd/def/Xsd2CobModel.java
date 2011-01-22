package com.legstar.xsd.def;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.legstar.codegen.CodeGenMakeException;
import com.legstar.codegen.models.AbstractAntBuildModel;
import com.legstar.xsd.XsdRootElement;

/**
 * This class gathers execution parameters for the XML Schema to COBOL utility.
 * <p/>
 * The class is also capable of generating a fully configured ANT script to run
 * the ant version of the utility with the current parameter set.
 * 
 */
public class Xsd2CobModel extends AbstractAntBuildModel {

    /** This generator name. */
    public static final String S2C_GENERATOR_NAME = "LegStar XML Schema to COBOL generator";

    /** This velocity template. */
    public static final String S2C_VELOCITY_MACRO_NAME = "vlc/build-xsd2cob-xml.vm";

    /* ====================================================================== */
    /* Following are key identifiers for this model persistence. = */
    /* ====================================================================== */

    /** URI where the XSD is available. */
    public static final String INPUT_XSD_URI = "inputXsdUri";

    /** XSD target folder or file. */
    public static final String TARGET_XSD_FILE = "targetXsdFile";

    /** COBOL target folder or file. */
    public static final String TARGET_COBOL_FILE = "targetCobolFile";

    /** COBOL source target encoding. */
    public static final String TARGET_COBOL_ENCODING = "targetCobolEncoding";

    /** New root elements to add. */
    public static final String NEW_ROOT_ELEMENTS = "newRootElements";

    /* ====================================================================== */
    /* Following are this class fields that are persistent. = */
    /* ====================================================================== */

    /** A URI where the XSD is available. */
    private URI _inputXsdUri;

    /** The target folder or file where annotated XSD will be created. */
    private File _targetXsdFile;

    /** The target folder or file where COBOL copybook will be created. */
    private File _targetCobolFile;

    /** The target COBOL source encoding. */
    private String _targetCobolEncoding;

    /** Translator configuration parameters. */
    private Xsd2CobConfig _xsdConfig;

    /**
     * New root elements to add to XML schema.
     * <p/>
     * These elements will be added at the XML Schema root elements.
     * <p/>
     * Each element must map to an existing XML schema complex type.
     */
    private List < XsdRootElement > _newRootElements;

    /**
     * A no-Arg constructor.
     */
    public Xsd2CobModel() {
        super();
        _xsdConfig = new Xsd2CobConfig();
    }

    /**
     * Construct from a properties file.
     * 
     * @param props the property file
     */
    public Xsd2CobModel(final Properties props) {
        super(props);
        setInputXsdUri(getURI(props, INPUT_XSD_URI, null));
        setTargetXsdFile(getFile(props, TARGET_XSD_FILE, null));
        setTargetCobolFile(getFile(props, TARGET_COBOL_FILE, null));
        setTargetCobolEncoding(getString(props, TARGET_COBOL_ENCODING, null));
        setNewRootElements(toRootElements(getStringList(props,
                NEW_ROOT_ELEMENTS, null)));
        _xsdConfig = new Xsd2CobConfig(props);
    }

    public void generateBuild(File scriptFile) throws CodeGenMakeException {
        // TODO Auto-generated method stub

    }

    /**
     * The input XML schema uri.
     * 
     * @return the input XML schema uri
     */
    public URI getInputXsdUri() {
        return _inputXsdUri;
    }

    /**
     * The input XML schema uri.
     * 
     * @param xsdUri the input XML schema uri to set
     */
    public void setInputXsdUri(final URI xsdUri) {
        _inputXsdUri = xsdUri;
    }

    /**
     * @return the XML schema target folder or file
     */
    public File getTargetXsdFile() {
        return _targetXsdFile;
    }

    /**
     * @param targetXsdDir the target XML schema folder or file to set
     */
    public void setTargetXsdFile(final File targetXsdDir) {
        _targetXsdFile = targetXsdDir;
    }

    /**
     * @return the COBOL copybook target folder or file
     */
    public File getTargetCobolFile() {
        return _targetCobolFile;
    }

    /**
     * @param targetCobolDir the target COBOL copybook folder or file to set
     */
    public void setTargetCobolFile(final File targetCobolDir) {
        _targetCobolFile = targetCobolDir;
    }

    /**
     * @return the target COBOL copybook encoding
     */
    public String getTargetCobolEncoding() {
        return _targetCobolEncoding;
    }

    /**
     * @param targetCobolEncoding the target COBOL copybook encoding to set
     */
    public void setTargetCobolEncoding(final String targetCobolEncoding) {
        _targetCobolEncoding = targetCobolEncoding;
    }

    /**
     * Translator configuration parameters.
     * 
     * @return the translator configuration parameters
     */
    public Xsd2CobConfig getXsdConfig() {
        return _xsdConfig;
    }

    /**
     * Translator configuration parameters.
     * 
     * @param xsdConfig the translator configuration parameters to set
     */
    public void setXsdConfig(final Xsd2CobConfig xsdConfig) {
        _xsdConfig = xsdConfig;
    }

    /**
     * Used by ANT to create a new instance of the embedded xsdConfig element to
     * receive parameters set from the ant script.
     * 
     * @return a new instance of the translator configuration parameters to set
     */
    public Xsd2CobConfig createXsdConfig() {
        _xsdConfig = new Xsd2CobConfig();
        return _xsdConfig;
    }

    /**
     * @return the list of root elements to be added.
     */
    public List < XsdRootElement > getNewRootElements() {
        return _newRootElements;
    }

    /**
     * @param newRootElements a list of root elements to add
     */
    public void setNewRootElements(final List < XsdRootElement > newRootElements) {
        _newRootElements = newRootElements;
    }

    /**
     * @param xsdRootElement a root element to add to the XML Schema
     */
    public void addNewRootElement(final XsdRootElement xsdRootElement) {
        if (_newRootElements == null) {
            _newRootElements = new ArrayList < XsdRootElement >();
        }
        _newRootElements.add(xsdRootElement);
    }

    /**
     * @return a properties file holding the values of this object fields
     */
    public Properties toProperties() {
        Properties props = super.toProperties();
        putURI(props, INPUT_XSD_URI, getInputXsdUri());
        putFile(props, TARGET_XSD_FILE, getTargetXsdFile());
        putFile(props, TARGET_COBOL_FILE, getTargetCobolFile());
        putString(props, TARGET_COBOL_ENCODING, getTargetCobolEncoding());
        putStringList(props, NEW_ROOT_ELEMENTS,
                toStringList(getNewRootElements()));
        _xsdConfig.toProperties(props);
        return props;
    }

    /**
     * Helper to serialize the list of root elements to a properties file.
     * 
     * @param rootElements the list of root elements
     * @return a list of strings
     */
    protected List < String > toStringList(List < XsdRootElement > rootElements) {
        List < String > stringList = new ArrayList < String >();
        if (rootElements != null) {
            for (XsdRootElement rootElement : rootElements) {
                stringList.add(rootElement.toString());
            }
        }
        return stringList;
    }

    /**
     * Helper to deserialize a list of root elements from a properties file.
     * 
     * @param stringList a list of strings
     * @return a list of root elements
     */
    protected List < XsdRootElement > toRootElements(List < String > stringList) {
        if (stringList != null) {
            List < XsdRootElement > rootElements = new ArrayList < XsdRootElement >();
            for (String string : stringList) {
                rootElements.add(new XsdRootElement(string));
            }
            return rootElements;
        } else {
            return null;
        }
    }
}
