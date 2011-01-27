package com.legstar.xsd.def;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdToCobolStringResult;
import com.legstar.xsd.XsdWriter;

/**
 * XML schema to COBOL translation using file system.
 * <p/>
 * An extension of the core API that deals with reading/ writing to the file
 * system.
 * 
 */
public class Xsd2CobIO extends Xsd2Cob {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    public Xsd2CobIO(final Xsd2CobModel model) {
        super(model);
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
         * If the URI is relative, assume it is a file URI relative to the
         * current directory.
         */
        if (!inputXsdUri.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            URI userDirURI = (new File(userDir)).toURI();
            inputXsdUri = userDirURI.resolve(inputXsdUri);
        }
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

        XsdWriter.writeResults(getDefaultName(uri), getModel()
                .getTargetXsdFile(), getModel().getTargetCobolFile(),
                getModel().getTargetCobolEncoding(), results, _log);
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

        XsdWriter.check(getModel().getTargetXsdFile(), getModel()
                .getTargetCobolFile());

    }

    /**
     * Retrieves the last segment from a URI (without suffixes).
     * 
     * @param uri the uri to process
     * @return the last segment of the path
     */
    protected String getDefaultName(final URI uri) {
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

}