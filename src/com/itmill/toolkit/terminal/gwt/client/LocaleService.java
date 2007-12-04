/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Date / time etc. localisation service for all widgets. Caches all loaded
 * locales as JSONObjects.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class LocaleService {

    private static Map cache = new HashMap();
    private static String defaultLocale;

    public static void addLocale(JSONObject json) {
        final String key = ((JSONString) json.get("name")).stringValue();
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
        cache.put(key, json);
        if (cache.size() == 1) {
            setDefaultLocale(key);
        }
    }

    public static void setDefaultLocale(String locale) {
        defaultLocale = locale;
    }

    public static String getDefaultLocale() {
        return defaultLocale;
    }

    public static Set getAvailableLocales() {
        return cache.keySet();
    }

    public static String[] getMonthNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONArray mn = (JSONArray) l.get("mn");
            final String[] temp = new String[12];
            temp[0] = ((JSONString) mn.get(0)).stringValue();
            temp[1] = ((JSONString) mn.get(1)).stringValue();
            temp[2] = ((JSONString) mn.get(2)).stringValue();
            temp[3] = ((JSONString) mn.get(3)).stringValue();
            temp[4] = ((JSONString) mn.get(4)).stringValue();
            temp[5] = ((JSONString) mn.get(5)).stringValue();
            temp[6] = ((JSONString) mn.get(6)).stringValue();
            temp[7] = ((JSONString) mn.get(7)).stringValue();
            temp[8] = ((JSONString) mn.get(8)).stringValue();
            temp[9] = ((JSONString) mn.get(9)).stringValue();
            temp[10] = ((JSONString) mn.get(10)).stringValue();
            temp[11] = ((JSONString) mn.get(11)).stringValue();
            return temp;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getShortMonthNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONArray smn = (JSONArray) l.get("smn");
            final String[] temp = new String[12];
            temp[0] = ((JSONString) smn.get(0)).stringValue();
            temp[1] = ((JSONString) smn.get(1)).stringValue();
            temp[2] = ((JSONString) smn.get(2)).stringValue();
            temp[3] = ((JSONString) smn.get(3)).stringValue();
            temp[4] = ((JSONString) smn.get(4)).stringValue();
            temp[5] = ((JSONString) smn.get(5)).stringValue();
            temp[6] = ((JSONString) smn.get(6)).stringValue();
            temp[7] = ((JSONString) smn.get(7)).stringValue();
            temp[8] = ((JSONString) smn.get(8)).stringValue();
            temp[9] = ((JSONString) smn.get(9)).stringValue();
            temp[10] = ((JSONString) smn.get(10)).stringValue();
            temp[11] = ((JSONString) smn.get(11)).stringValue();
            return temp;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getDayNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONArray dn = (JSONArray) l.get("dn");
            final String[] temp = new String[7];
            temp[0] = ((JSONString) dn.get(0)).stringValue();
            temp[1] = ((JSONString) dn.get(1)).stringValue();
            temp[2] = ((JSONString) dn.get(2)).stringValue();
            temp[3] = ((JSONString) dn.get(3)).stringValue();
            temp[4] = ((JSONString) dn.get(4)).stringValue();
            temp[5] = ((JSONString) dn.get(5)).stringValue();
            temp[6] = ((JSONString) dn.get(6)).stringValue();
            return temp;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getShortDayNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONArray sdn = (JSONArray) l.get("sdn");
            final String[] temp = new String[7];
            temp[0] = ((JSONString) sdn.get(0)).stringValue();
            temp[1] = ((JSONString) sdn.get(1)).stringValue();
            temp[2] = ((JSONString) sdn.get(2)).stringValue();
            temp[3] = ((JSONString) sdn.get(3)).stringValue();
            temp[4] = ((JSONString) sdn.get(4)).stringValue();
            temp[5] = ((JSONString) sdn.get(5)).stringValue();
            temp[6] = ((JSONString) sdn.get(6)).stringValue();
            return temp;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static int getFirstDayOfWeek(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONNumber fdow = (JSONNumber) l.get("fdow");
            return (int) fdow.getValue();
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String getDateFormat(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONString df = (JSONString) l.get("df");
            return df.stringValue();
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static boolean isTwelveHourClock(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONBoolean thc = (JSONBoolean) l.get("thc");
            return thc.booleanValue();
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String getClockDelimiter(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONString hmd = (JSONString) l.get("hmd");
            return hmd.stringValue();
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getAmPmStrings(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final JSONObject l = (JSONObject) cache.get(locale);
            final JSONArray ampm = (JSONArray) l.get("ampm");
            final String[] temp = new String[2];
            temp[0] = ((JSONString) ampm.get(0)).stringValue();
            temp[1] = ((JSONString) ampm.get(1)).stringValue();
            return temp;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

}
