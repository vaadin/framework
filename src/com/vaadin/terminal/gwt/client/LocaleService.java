/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JsArray;

/**
 * Date / time etc. localisation service for all widgets. Caches all loaded
 * locales as JSONObjects.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class LocaleService {

    private static Map<String, ValueMap> cache = new HashMap<String, ValueMap>();
    private static String defaultLocale;

    public static void addLocale(ValueMap valueMap) {

        final String key = valueMap.getString("name");
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
        cache.put(key, valueMap);
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
            final ValueMap l = cache.get(locale);
            return l.getStringArray("mn");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getShortMonthNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getStringArray("smn");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getDayNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getStringArray("dn");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getShortDayNames(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getStringArray("sdn");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static int getFirstDayOfWeek(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getInt("fdow");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String getDateFormat(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getString("df");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static boolean isTwelveHourClock(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getBoolean("thc");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String getClockDelimiter(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getString("hmd");
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public static String[] getAmPmStrings(String locale)
            throws LocaleNotLoadedException {
        if (cache.containsKey(locale)) {
            final ValueMap l = cache.get(locale);
            return l.getStringArray("ampm");
        } else {
            throw new LocaleNotLoadedException(locale);
        }

    }

    public static void addLocales(JsArray<ValueMap> valueMapArray) {
        for (int i = 0; i < valueMapArray.length(); i++) {
            addLocale(valueMapArray.get(i));

        }

    }

}
