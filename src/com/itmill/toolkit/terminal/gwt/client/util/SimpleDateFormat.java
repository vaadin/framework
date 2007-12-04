/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itmill.toolkit.terminal.gwt.client.util;

import java.util.Date;

/**
 * <dl>
 * <dt><b>Title: </b>
 * <dd>SimpleDateFormat</dd>
 * <p>
 * <dt><b>Description: </b>
 * <dd>GWT does not implement any of the java.text package, so this class tries
 * to fill the void of the missing java.text.SimpleDateFormat class. This
 * version however only supports a subset of the date and time patterns
 * supported by its java.text counterpart. The pattern symbols supported by this
 * class are:
 * <dl>
 * <dt><b>E</b></dt>
 * <dd>Day in a week</dd>
 * <dt><b>d</b></dt>
 * <dd>Day of the month</dd>
 * <dt><b>y</b></dt>
 * <dd>Year</dd>
 * <dt><b>M</b></dt>
 * <dd>Month January, Jan, 01, 1</dd>
 * <dt><b>H</b></dt>
 * <dd>Hour in 24 hour format (0-23)</dd>
 * <dt><b>h</b></dt>
 * <dd>Hour in 12 hour format (1-12)</dd>
 * <dt><b>m</b></dt>
 * <dd>Minute of the hour </dd>
 * <dt><b>s</b></dt>
 * <dd>Seconds of the minute</dd>
 * <dt><b>a</b></dt>
 * <dd>am/pm</dd>
 * </dl>
 * All characters that are not recognised as a date format character are
 * translated literally into the output string. <br/> </dd>
 * <p>
 * </dl>
 * <p>
 * A simple date parsing facility has also been implemented resembling the java
 * prototype. You can currently parse most numeric patterns but no temporal
 * literals (such as day or month names).
 * </p>
 * 
 * @author <a href="mailto:jasone@greenrivercomputing.com">Jason Essington</a>
 * @author <a href="mailto:g.georgovassilis@gmail.com">George Georgovassilis</a>
 * @version $Revision: 0.0 $
 */
public class SimpleDateFormat {
    private final String format;
    private DateLocale locale = new DateLocale();

    /**
     * Gets the support locale for formatting and parsing dates
     * 
     * @return
     */
    public DateLocale getLocale() {
        return locale;
    }

    public void setLocale(DateLocale locale) {
        this.locale = locale;
    }

    public SimpleDateFormat(String pattern) {
        format = pattern;
    }

    public String format(Date date) {
        String f = "";
        if (format != null && format.length() > 0) {
            String lastTokenType = null;
            String currentToken = "";
            for (int i = 0; i < format.length(); i++) {
                final String thisChar = format.substring(i, i + 1);
                final String currentTokenType = DateLocale.SUPPORTED_DF_TOKENS
                        .contains(thisChar) ? thisChar : "";
                if (currentTokenType.equals(lastTokenType) || i == 0) {
                    currentToken += thisChar;
                    lastTokenType = currentTokenType;
                } else {
                    if ("".equals(lastTokenType)) {
                        f += currentToken;
                    } else {
                        f += handleToken(currentToken, date);
                    }
                    currentToken = thisChar;
                    lastTokenType = currentTokenType;
                }
            }
            if ("".equals(lastTokenType)) {
                f += currentToken;
            } else {
                f += handleToken(currentToken, date);
            }
        }
        return f;
    }

    /**
     * takes a date format string and returns the formatted portion of the date.
     * For instance if the token is MMMM then the full month name is returned.
     * 
     * @param token
     *                date format token
     * @param date
     *                date to format
     * @return formatted portion of the date
     */
    private String handleToken(String token, Date date) {
        String response = token;
        final String tc = token.substring(0, 1);
        if (DateLocale.TOKEN_DAY_OF_WEEK.equals(tc)) {
            if (token.length() > 3) {
                response = locale.getWEEKDAY_LONG()[date.getDay()];
            } else {
                response = locale.getWEEKDAY_SHORT()[date.getDay()];
            }
        } else if (DateLocale.TOKEN_DAY_OF_MONTH.equals(tc)) {
            if (token.length() == 1) {
                response = Integer.toString(date.getDate());
            } else {
                response = twoCharDateField(date.getDate());
            }
        } else if (DateLocale.TOKEN_MONTH.equals(tc)) {
            switch (token.length()) {
            case 1:
                response = Integer.toString(date.getMonth() + 1);
                break;
            case 2:
                response = twoCharDateField(date.getMonth() + 1);
                break;
            case 3:
                response = locale.MONTH_SHORT[date.getMonth()];
                break;
            default:
                response = locale.MONTH_LONG[date.getMonth()];
                break;
            }
        } else if (DateLocale.TOKEN_YEAR.equals(tc)) {
            if (token.length() >= 2) {
                response = Integer.toString(date.getYear() + 1900);
            } else {
                response = twoCharDateField(date.getYear());
            }
        } else if (DateLocale.TOKEN_HOUR_12.equals(tc)) {
            int h = date.getHours();
            if (h == 0) {
                h = 12;
            } else if (h > 12) {
                h -= 12;
            }
            // if (token.length() > 1)
            response = twoCharDateField(h);
            // else
            // response = Integer.toString(h);
        } else if (DateLocale.TOKEN_HOUR_24.equals(tc)) {
            // if (token.length() > 1)
            response = twoCharDateField(date.getHours());
            // else
            // response = Integer.toString(date.getHours());
        } else if (DateLocale.TOKEN_MINUTE.equals(tc)) {
            // if (token.length() > 1)
            response = twoCharDateField(date.getMinutes());
            // else
            // response = Integer.toString(date.getMinutes());
        } else if (DateLocale.TOKEN_SECOND.equals(tc)) {
            // if (token.length() > 1)
            response = twoCharDateField(date.getSeconds());
            // else
            // response = Integer.toString(date.getSeconds());
        } else if (DateLocale.TOKEN_AM_PM.equals(tc)) {
            final int hour = date.getHours();
            if (hour > 11) {
                response = DateLocale.getPM();
            } else {
                response = DateLocale.getAM();
            }
        }
        return response;
    }

    /**
     * This is basically just a sneaky way to guarantee that our 1 or 2 digit
     * numbers come out as a 2 character string. we add an arbitrary number
     * larger than 100, convert this new number to a string, then take the right
     * most 2 characters.
     * 
     * @param num
     * @return
     */
    private String twoCharDateField(int num) {
        String res = Integer.toString(num + 1900);
        res = res.substring(res.length() - 2);
        return res;
    }

    private static Date newDate(long time) {
        return new Date(time);
    }

    /**
     * Parses text and returns the corresponding date object.
     * 
     * @param source
     * @return java.util.Date
     */
    public Date parse(String source) {
        return SimpleDateParser.parse(source, format);
    };

}
