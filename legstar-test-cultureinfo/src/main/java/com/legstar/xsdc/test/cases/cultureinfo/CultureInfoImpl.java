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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.jws.WebService;

/**
 * A sample POJO with JSR 181 annotations. It a locale from the caller as well
 * as a number to be formatted as a decimal number in the requested locale
 * and returns that formatted data as well as some other locale-related infos.
 *
 */
@WebService
public class CultureInfoImpl {

    /**
     * Gets info from the JVM.
     * @param request the request data object
     * @return the reply data object
     * @throws CultureInfoException if method fails
     */
    public CultureInfoReply getInfo(
            final CultureInfoRequest request) throws CultureInfoException {

        /* Validate request */
        if (request.getCultureCode() == null
                || request.getCultureCode().length() == 0) {
            throw new CultureInfoException("You must provide a CultureInfo");
        }
        /* Format of CultureInfo is expected to be xx-yy*/
        if ((request.getCultureCode().length() != 5)
                || (request.getCultureCode().charAt(2) != '-')) {
            throw new CultureInfoException("CultureInfo " + request.getCultureCode()
                    + " does not conform to xx-yy format");
        }
        CultureInfoReply reply = new CultureInfoReply();

        Locale locale = new Locale(
                request.getCultureCode().substring(0, 2),
                request.getCultureCode().substring(3, 5));

        /* return corresponding language and country */
        reply.setDisplayLanguage(locale.getDisplayLanguage());
        reply.setDisplayCountry(locale.getDisplayCountry());

        /* Format date and time  */
        Date date = new Date();
        reply.setFormattedDate(
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,
                        locale).format(date));

        /* Get the currency code */
        reply.setCurrencySymbol(Currency.getInstance(locale).getSymbol());

        /* Format a decimal number */
        NumberFormat nf = NumberFormat.getInstance(locale);
        reply.setFormattedDecimalNumber(nf.format(request.getDecimalNumber()));

        /* Extract server locale and culture info */
        Locale serverLocale = Locale.getDefault();
        ServerCultureInfo serverCultureInfo = new ServerCultureInfo();
        serverCultureInfo.setDisplayCountry(serverLocale.getDisplayCountry());
        serverCultureInfo.setDisplayLanguage(serverLocale.getDisplayLanguage());
        serverCultureInfo.setCultureCode(
                serverLocale.getLanguage() + '-' + serverLocale.getCountry());
        reply.setServerCultureInfo(serverCultureInfo);

        return reply;
    }
}
