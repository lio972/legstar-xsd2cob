package com.legstar.xsd.def;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import com.legstar.codegen.CodeGenMakeException;
import com.legstar.codegen.models.AbstractAntBuildModel;

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
     * @param _xsdConfig the translator configuration parameters to set
     */
    public void setXsdConfig(final Xsd2CobConfig xsdConfig) {
        _xsdConfig = xsdConfig;
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
        _xsdConfig.toProperties(props);
        return props;
    }
}
