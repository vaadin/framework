/* 
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
 * @author Vaadin Ltd.
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

    public static Set<String> getAvailableLocales() {
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
