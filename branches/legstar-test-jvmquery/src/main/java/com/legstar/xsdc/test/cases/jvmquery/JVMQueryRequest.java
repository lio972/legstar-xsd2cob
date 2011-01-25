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
 * Caller sends this type of request by passing a list of environment variable
 * names which values must be retrieved.
 */
public class JVMQueryRequest {

    /** The list of environment variable names.*/
    private List < String > mEnvVarNames = new ArrayList < String >();

    /**
     * @return the environment variable names to get
     */
    public List < String > getEnvVarNames() {
        return mEnvVarNames;
    }

    /**
     * @param envVarNames the the environment variable names to set
     */
    public void setEnvVarNames(final List < String > envVarNames) {
        mEnvVarNames = envVarNames;
    }

}
