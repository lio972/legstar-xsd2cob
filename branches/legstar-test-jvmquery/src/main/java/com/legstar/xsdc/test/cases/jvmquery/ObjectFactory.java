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

/**
 * A simple helper class that creates instances of data objects.
 *
 */
public class ObjectFactory {
    /**
     * Create an instance of {@link JVMQueryRequest }.
     * @return a request data object 
     */
    public JVMQueryRequest createJVMQueryRequest() {
        return new JVMQueryRequest();
    }

    /**
     * Create an instance of {@link JVMQueryReply }.
     * @return a reply data object 
     */
    public JVMQueryReply createJVMQueryReply() {
        return new JVMQueryReply();
    }


}
