package com.legstar.xsd.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdRootElement;

/**
 * Java to COBOL translation standalone executable.
 * <p/>
 * This is the main class for the executable jar. It takes options from the
 * command line and calls the {@link Java2Cob} API.
 * <p/>
 * Usage: <code>
 * java -jar legstar-java2cob-x.y.z-exe.jar
 *      -i&lt;input comma-separated list of fully qualified java class names&gt;
 *      -ox&lt;output COBOL-annotated XML schema folder or file&gt;
 *      -oc&lt;output COBOL copybook folder or file&gt;
 * </code>
 * 
 */
public class Java2CobMain {

    /** The version properties file name. */
    private static final String VERSION_FILE_NAME = "/com/legstar/xsd/def/version.properties";

    /** The default COBOL-annotated XML schema output folder. */
    private static final File DEFAULT_XSD_OUTPUT_FILE = new File("cobolschema");

    /** The default COBOL copybook output folder. */
    private static final File DEFAULT_COBOL_OUTPUT_FILE = new File("cobol");

    /** Set of generation options to use. */
    private Java2CobModel _model;

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    public Java2CobMain(final Java2CobModel model) {
        _model = model;
    }

    /**
     * @param args translator options. Provides help if no arguments passed.
     */
    public static void main(final String[] args) {
        Java2CobMain main = new Java2CobMain(new Java2CobModel());
        main.execute(args);
    }

    /**
     * Process command line options and run generator.
     * <p/>
     * If no options are passed, prints the help. Help is also printed if the
     * command line options are invalid.
     * 
     * @param args generator options
     */
    public void execute(final String[] args) {
        try {
            Options options = createOptions();
            if (collectOptions(options, args)) {
                setDefaults();
                execute();
            }
        } catch (Exception e) {
            _log.error("Java to COBOL translation failure", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the command line options
     */
    protected Options createOptions() {
        Options options = new Options();

        Option version = new Option("v", "version", false,
                "print the version information and exit");
        options.addOption(version);

        Option help = new Option("h", "help", false,
                "print the options available");
        options.addOption(help);

        Option inputClasses = new Option("i", "inputClasses", true,
                "Comma-separated list of fully qualified java class names");
        inputClasses.setRequired(true);
        options.addOption(inputClasses);

        Option targetXsdFile = new Option("ox", "outputXsd", true,
                "Output COBOL-annotated XML schema folder or file");
        options.addOption(targetXsdFile);

        Option targetTargetNamespace = new Option("ns", "targetNamespace",
                true,
                "The target namspace for the output COBOL-annotated XML schema");
        options.addOption(targetTargetNamespace);

        Option targetCobolFile = new Option("oc", "outputCobol", true,
                "output COBOL copybook folder or file");
        options.addOption(targetCobolFile);

        Option targetCobolEncoding = new Option("e", "outputCobolEncoding",
                true, "Character set used for COBOL source files encoding");
        options.addOption(targetCobolEncoding);

        Option newRootElements = new Option(
                "r",
                "newRootElements",
                true,
                "Comma-separated list of root elements to add."
                        + " Each item in the list must match the elementName:elementType pattern");
        options.addOption(newRootElements);

        return options;
    }

    /**
     * Take arguments received on the command line and setup corresponding
     * options.
     * <p/>
     * No arguments is valid. It means use the defaults.
     * 
     * @param options the expected options
     * @param args the actual arguments received on the command line
     * @return true if arguments were valid
     * @throws Exception if something goes wrong while parsing arguments
     */
    protected boolean collectOptions(final Options options, final String[] args)
            throws Exception {
        if (args != null && args.length > 0) {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);
            return processLine(line, options);
        }
        return true;
    }

    /**
     * Process the command line options selected.
     * 
     * @param line the parsed command line
     * @param options available
     * @return false if processing needs to stop, true if its ok to continue
     * @throws Exception if line cannot be processed
     */
    protected boolean processLine(final CommandLine line, final Options options)
            throws Exception {
        if (line.hasOption("version")) {
            System.out.println("version " + getVersion());
            return false;
        }
        if (line.hasOption("help")) {
            produceHelp(options);
            return false;
        }
        if (line.hasOption("inputClasses")) {
            String[] classNames = line.getOptionValue("inputClasses").trim()
                    .split(",");
            getModel().setClassNames(Arrays.asList(classNames));
        }
        if (line.hasOption("outputXsd")) {
            getModel().setTargetXsdFile(
                    new File((line.getOptionValue("outputXsd").trim())));
        }
        if (line.hasOption("targetNamespace")) {
            getModel().setNewTargetNamespace(
                    (line.getOptionValue("targetNamespace").trim()));
        }
        if (line.hasOption("outputCobol")) {
            getModel().setTargetCobolFile(
                    new File((line.getOptionValue("outputCobol").trim())));
        }
        if (line.hasOption("outputCobolEncoding")) {
            getModel().setTargetCobolEncoding(
                    (line.getOptionValue("outputCobolEncoding").trim()));
        }
        if (line.hasOption("newRootElements")) {
            String[] elements = line.getOptionValue("newRootElements").trim()
                    .split(",");
            for (String element : elements) {
                getModel().addNewRootElement(new XsdRootElement(element));
            }
        }

        return true;
    }

    /**
     * Make sure mandatory parameters have default values.
     */
    protected void setDefaults() {
        if (getModel().getTargetXsdFile() == null) {
            getModel().setTargetXsdFile(DEFAULT_XSD_OUTPUT_FILE);
        }
        if (getModel().getTargetCobolFile() == null) {
            getModel().setTargetCobolFile(DEFAULT_COBOL_OUTPUT_FILE);
        }

    }

    /**
     * @param options options available
     * @throws Exception if help cannot be produced
     */
    protected void produceHelp(final Options options) throws Exception {
        HelpFormatter formatter = new HelpFormatter();
        String version = getVersion();
        formatter.printHelp(
                "java -jar legstar-java2cob-"
                        + version.substring(0, version.indexOf(' '))
                        + "-exe.jar followed by:", options);
    }

    /**
     * Translate the input URI to COBOL. Place results in the output folders or
     * files.
     * 
     * @throws IOException if basic read/write operation fails
     * @throws InvalidXsdException if XML schema read is invalid
     * @throws InvalidParameterException if one of the parameters is invalid
     * 
     */
    protected void execute() throws IOException, InvalidXsdException,
            InvalidParameterException {

        Java2CobIO java2cob = new Java2CobIO(getModel());
        java2cob.execute();

    }

    /**
     * Pick up the version from the properties file.
     * 
     * @return the product version
     * @throws IOException if version cannot be identified
     */
    protected String getVersion() throws IOException {
        InputStream stream = null;
        try {
            Properties version = new Properties();
            stream = Java2CobMain.class.getResourceAsStream(VERSION_FILE_NAME);
            version.load(stream);
            return version.getProperty("version");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Gather all parameters into a context object.
     * 
     * @return a parameter context to be used throughout all code
     */
    public Java2CobModel getModel() {
        return _model;
    }

}
