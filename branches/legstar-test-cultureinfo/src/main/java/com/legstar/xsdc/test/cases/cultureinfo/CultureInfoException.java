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

/**
 * Exception while executing the cultureinfo main method.
 *
 */
public class CultureInfoException extends Exception {

    /** serialVersionUID. */
    private static final long serialVersionUID = 5200497911841705180L;

    /**
     * @param string the exception description
     */
    public CultureInfoException(final String string) {
        super(string);
    }

}
