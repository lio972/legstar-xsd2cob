package com.legstar.xsd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;

/**
 * Writes results to the file system.
 * 
 */
public final class XsdWriter {

    /** Extension to add on generated XML schema file names. */
    public static final String XSD_FILE_EXTENSION = "xsd";

    /** Extension to add on generated COBOL copybooks. */
    public static final String COBOL_FILE_EXTENSION = "cpy";

    /**
     * Utility class.
     */
    private XsdWriter() {

    }

    /**
     * Check the output files in which we are supposed to write.
     * 
     * @param targetXsdFile the output XML schema file or folder
     * @param targetCobolFile the output COBOL copybook file or folder
     * @throws InvalidParameterException if the file or folders are invalid
     */
    public static void check(final File targetXsdFile,
            final File targetCobolFile) throws InvalidParameterException {

        if (targetXsdFile == null) {
            throw new InvalidParameterException(
                    "No target folder or file was specified for COBOL-annotated XML schema");
        }

        if (targetCobolFile == null) {
            throw new InvalidParameterException(
                    "No target folder or file was specified for COBOL copybook");
        }
    }

    /**
     * Write the results to file system.
     * 
     * @param defaultName the base name to use if output files are folders
     * @param targetXsdFile the XSD file or folder
     * @param targetCobolFile the COBOL file or folder
     * @param targetCobolEncoding the target COBOL file content encoding
     * @param results the translation results
     * @param log a logger to print out the results
     * @throws IOException if writing fails
     */
    public static void writeResults(final String defaultName,
            final File targetXsdFile, final File targetCobolFile,
            final String targetCobolEncoding,
            final XsdToCobolStringResult results, final Log log)
            throws IOException {

        File xsdFile = getFile(targetXsdFile, defaultName + "."
                + XSD_FILE_EXTENSION);
        File cobolFile = getFile(targetCobolFile, defaultName + "."
                + COBOL_FILE_EXTENSION);
        results.toFileSystem(xsdFile, cobolFile, targetCobolEncoding);

        if (log.isDebugEnabled()) {
            log.debug("Result COBOL-annotated XML Schema " + xsdFile);
            log.debug(results.getCobolXsd());
            log.debug("Result COBOL copybook " + cobolFile);
            log.debug(results.getCobolStructure());
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
    public static File getFile(final File file, final String fileName)
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

}
