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
package com.legstar.xsdc.test.cases.cultureinfo;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

/**
 * The request data object.
 *
 */
@XmlType(name = "cultureInfoParameters")
public class CultureInfoRequest {

    /** A decimal number to be formatted. */
    private BigDecimal mDecimalNumber;
    
    /** The locale requested. */
    private String mCultureCode;
    /**
     * @return the Culture Code
     */
    public String getCultureCode() {
        return mCultureCode;
    }
    /**
     * @param culturecode the Culture Code to set
     */
    public void setCultureCode(final String culturecode) {
        mCultureCode = culturecode;
    }
    /**
     * @return the DecimalNumber
     */
    public BigDecimal getDecimalNumber() {
        return mDecimalNumber;
    }
    /**
     * @param decimalNumber the DecimalNumber to set
     */
    public void setDecimalNumber(final BigDecimal decimalNumber) {
        mDecimalNumber = decimalNumber;
    }

}
