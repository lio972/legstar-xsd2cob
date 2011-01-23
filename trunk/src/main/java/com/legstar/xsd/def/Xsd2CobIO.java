package com.legstar.xsd.def;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdToCobolStringResult;

/**
 * XML schema to COBOL translation using file system.
 * <p/>
 * An extension of the core API that deals with reading/ writing to the file
 * system.
 * 
 */
public class Xsd2CobIO extends Xsd2Cob {

    /** Extension to add on generated XML schema file names. */
    public static final String XSD_FILE_EXTENSION = "xsd";

    /** Extension to add on generated COBOL copybooks. */
    public static final String COBOL_FILE_EXTENSION = "cpy";

    /** Holds all options. */
    private Xsd2CobModel _model;

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    public Xsd2CobIO(final Xsd2CobModel model) {
        super(model.getXsdConfig());
        _model = model;
    }

    /**
     * Generates a new annotated schema.
     * 
     * @throws IOException if basic read/write operation fails
     * @throws InvalidXsdException if XML schema read is invalid
     * @throws InvalidParameterException if one of the parameters is invalid
     */
    @SuppressWarnings("unchecked")
    public void execute() throws IOException, InvalidXsdException,
            InvalidParameterException {
        if (_log.isDebugEnabled()) {
            _log.debug("XML Schema to COBOL translator started");
        }
        checkParameters();

        URI inputXsdUri = getModel().getInputXsdUri();
        /*
         * If URI is a folder on local file system, process all XML schema and
         * WSDL files in there.
         */
        if (inputXsdUri.getScheme().equals("file")) {
            File inputFile = new File(inputXsdUri);
            if (inputFile.isDirectory()) {
                Collection < File > xsdFiles = FileUtils.listFiles(inputFile,
                        new String[] { "xsd", "wsdl" }, true);
                for (File file : xsdFiles) {
                    execute(file.toURI());
                }
            } else {
                execute(getModel().getInputXsdUri());
            }
        } else {
            execute(getModel().getInputXsdUri());
        }

    }

    /**
     * Generates a new annotated schema.
     * 
     * @param uri the input URI
     * 
     * @throws IOException if basic read/write operation fails
     * @throws InvalidXsdException if XML schema read is invalid
     */
    protected void execute(final URI uri) throws InvalidXsdException,
            IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("Processing URI " + uri.toString());
        }
        XsdToCobolStringResult results = translate(uri);
        writeResults(uri, results);
    }

    /**
     * Sanity checks this model.
     * 
     * @throws InvalidParameterException if a parameter is invalid
     */
    public void checkParameters() throws InvalidParameterException {

        if (getModel().getInputXsdUri() == null) {
            throw new InvalidParameterException("No input URI specified");
        }

        if (getModel().getTargetXsdFile() == null) {
            throw new InvalidParameterException(
                    "No target folder or file was specified for COBOL-annotated XML schema");
        }

        if (getModel().getTargetCobolFile() == null) {
            throw new InvalidParameterException(
                    "No target folder or file was specified for COBOL copybook");
        }

        /* Make sure the API knows about additional root elements */
        setNewRootElements(getModel().getNewRootElements());
    }

    /**
     * Write the results to file system.
     * 
     * @param uri the input URI
     * @param results the translation results
     * @throws IOException if writing fails
     */
    protected void writeResults(final URI uri,
            final XsdToCobolStringResult results) throws IOException {

        /* Use the last segment of the URI as a default output name. */
        String defaultName = getLastSegment(uri);

        File xsdFile = getFile(getModel().getTargetXsdFile(), defaultName + "."
                + XSD_FILE_EXTENSION);
        File cobolFile = getFile(getModel().getTargetCobolFile(), defaultName
                + "." + COBOL_FILE_EXTENSION);
        results.toFileSystem(xsdFile, cobolFile, getModel()
                .getTargetCobolEncoding());

        if (_log.isDebugEnabled()) {
            _log.debug("Result COBOL-annotated XML Schema " + xsdFile);
            _log.debug(results.getCobolXsd());
            _log.debug("Result COBOL copybook " + cobolFile);
            _log.debug(results.getCobolStructure());
        }

    }

    /**
     * If the file has no extension, it is considered a folder.
     * <p/>
     * The folder is created and new file, using the proposed fineName is
     * created in there.
     * <p/>
     * If the file has an extension, then the containing folder is created and
     * the fileName is ignored.
     * 
     * @param file a folder or file
     * @param fileName a file name to use if file is a folder
     * @return a file in an existing folder
     * @throws IOException if folders cannot be created
     */
    protected File getFile(final File file, final String fileName)
            throws IOException {
        String filePath = file.getAbsolutePath();
        String ext = FilenameUtils.getExtension(filePath);
        if (ext == null || ext.length() == 0) {
            if (fileName == null || fileName.length() == 0) {
                throw new IOException("No default file name was provided");
            }
            FileUtils.forceMkdir(file);
            return new File(file, fileName);
        } else {
            String folderPath = FilenameUtils
                    .getFullPathNoEndSeparator(filePath);
            FileUtils.forceMkdir(new File(folderPath));
            return file;
        }
    }

    /**
     * Retrieves the last segment from a URI (without suffixes).
     * 
     * @param uri the uri to process
     * @return the last segment of the path
     */
    protected String getLastSegment(final URI uri) {
        String path = uri.getPath();
        if (path == null || path.length() < 2) {
            return null;
        }
        if ((path.charAt(path.length() - 1)) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        int pos = path.lastIndexOf('/');
        if (pos < 0) {
            return null;
        }
        path = path.substring(++pos, path.length());
        pos = path.lastIndexOf('.');
        if (pos > 0) {
            return path.substring(0, pos);
        } else {
            return path;
        }
    }

    /**
     * @return the options model
     */
    public Xsd2CobModel getModel() {
        return _model;
    }

}