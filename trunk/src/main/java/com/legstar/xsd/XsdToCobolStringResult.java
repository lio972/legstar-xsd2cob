package com.legstar.xsd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Results from translation are a COBOL annotated XML schema and COBOL source
 * code that describes the COBOL structures.
 * 
 */
public class XsdToCobolStringResult {

    /** A COBOL annotated XML schema. */
    private String cobolXsd;

    /** COBOL source code describing structures. */
    private String cobolStructure;

    /**
     * @param cobolXsd COBOL annotated XML schema
     * @param cobolStructure COBOL source code describing structures
     */
    public XsdToCobolStringResult(final String cobolXsd,
            final String cobolStructure) {
        this.cobolXsd = cobolXsd;
        this.cobolStructure = cobolStructure;
    }

    /**
     * Saves the results to the file system.
     * 
     * @param cobolXsdFile the file to use for the COBOL-annotate XML schema
     * @param cobolStructureFile the file to use for the COBOL copybook
     * @param cobolStructureEncoding the COBOL copybook character encoding
     *            to use
     * @throws IOException if writing to file system fails
     */
    public void toFileSystem(final File cobolXsdFile,
            final File cobolStructureFile,
            final String cobolStructureEncoding) throws IOException {
        FileUtils.writeStringToFile(cobolXsdFile, getCobolXsd(), "UTF-8");
        FileUtils.writeStringToFile(cobolStructureFile, getCobolStructure(),
                cobolStructureEncoding);
    }

    /**
     * @return COBOL annotated XML schema
     */
    public String getCobolXsd() {
        return cobolXsd;
    }

    /**
     * @return COBOL source code describing structures
     */
    public String getCobolStructure() {
        return cobolStructure;
    }
}
