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
package com.legstar.xsdc.test.cases.jvmquery;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO replies by providing values for requested environment variables
 * as well as other JVM parameters.
 */
public class JVMQueryReply {
    
    /** A list of values for each requested environment variable. */
    private List < String > mEnvVarValues = new ArrayList < String >();
    /** Requested date formatted using the current Locale. */
    private String mFormattedDate;
    /** Country from current Locale.*/
    private String mCountry;
    /** language from current Locale. */
    private String mLanguage;
    /** Currency symbol from current Locale.*/
    private String mCurrencySymbol;

    /**
     * @return the environment variable values to get
     */
    public List < String > getEnvVarValues() {
        return mEnvVarValues;
    }

    /**
     * @param envVarValues the the environment variable values to set
     */
    public void setEnvVarValues(final List < String > envVarValues) {
        mEnvVarValues = envVarValues;
    }

    /**
     * @return the Formatted Date
     */
    public String getFormattedDate() {
        return mFormattedDate;
    }

    /**
     * @param formattedDate the Formatted Date to set
     */
    public void setFormattedDate(final String formattedDate) {
        mFormattedDate = formattedDate;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return mCountry;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(final String country) {
        mCountry = country;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * @param mlanguage the language to set
     */
    public void setLanguage(final String mlanguage) {
        this.mLanguage = mlanguage;
    }

    /**
     * @return the Currency Symbol
     */
    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    /**
     * @param currencySymbol the Currency Symbol to set
     */
    public void setCurrencySymbol(final String currencySymbol) {
        mCurrencySymbol = currencySymbol;
    }

}
