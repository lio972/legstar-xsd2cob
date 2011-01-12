/*******************************************************************************
 * Copyright (c) 2010 LegSem.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     LegSem - initial API and implementation
 ******************************************************************************/
package com.legstar.xsd;

/**
 * This class collects all facet values of interest in an XML schema type.
 */
public class XsdFacets {

    /** Size of string elements. */
    private int _length = -1;

    /** Regular expression pattern for elements values. */
    private String _pattern = null;

    /** Total number of digits for numeric elements. */
    private int _totalDigits = -1;

    /** Number of fractional digits for numeric elements. */
    private int _fractionDigits = -1;

    /**
     * @return the fractional digits
     */
    public int getFractionDigits() {
        return _fractionDigits;
    }

    /**
     * @param fractionalDigits the fractional digits to set
     */
    public void setFractionDigits(
            final int fractionalDigits) {
        _fractionDigits = fractionalDigits;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return _length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(final int length) {
        _length = length;
    }

    /**
     * @return the regular expression pattern
     */
    public String getPattern() {
        return _pattern;
    }

    /**
     * @param pattern the regular expression pattern to set
     */
    public void setPattern(final String pattern) {
        _pattern = pattern;
    }

    /**
     * @return the total number of digits
     */
    public int getTotalDigits() {
        return _totalDigits;
    }

    /**
     * @param totalDigits the total number of digits to set
     */
    public void setTotalDigits(final int totalDigits) {
        _totalDigits = totalDigits;
    }
}
