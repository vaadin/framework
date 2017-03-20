/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.util;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import elemental.json.impl.JsonUtil;

/**
 * Utilities related to {@link com.google.gwt.i18n.client.TimeZone}.
 *
 * @author Vaadin Ltd
 * @since 8.0.4
 */
public final class TimeZoneUtil implements Serializable {

    private TimeZoneUtil() {
        // Static utils only
    }

    /**
     * Returns a JSON string of the specified {@code zoneId} and {@link Locale},
     * which is used in 
     * {@link com.google.gwt.i18n.client.TimeZone#createTimeZone(String)}.
     *
     * @param zoneId
     *            the {@link ZoneId} to get the daylight transitions from
     * @param locale
     *            the locale the locale to translate the name
     *
     * @return the encoded string
     */
    public static String toJSON(ZoneId zoneId, Locale locale) {
        if (zoneId == null || locale == null) {
            return null;
        }
        ZoneRules rules = zoneId.getRules();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        List<Long> transtionsList = new ArrayList<>();
        
        TimeZoneInfo info = new TimeZoneInfo();
        
        int endYear = LocalDate.now().getYear() + 20;
        if (timeZone.useDaylightTime()) {
            for (int year = 1980; year <= endYear; year++) {
                ZonedDateTime i = LocalDateTime.of(year, 1, 1, 0, 0).atZone(zoneId);
                while (true) {
                    ZoneOffsetTransition t = rules.nextTransition(i.toInstant());
                    i = t.getInstant().atZone(zoneId);
                    if (i.toLocalDate().getYear() != year) {
                        break;
                    }
                    long epocHours = Duration.ofSeconds(t.getInstant().getEpochSecond()).toHours();
                    long duration = Math.max(t.getDuration().toMinutes(), 0);
                    transtionsList.add(epocHours);
                    transtionsList.add(duration);
                };
            }
        }
        info.id = zoneId.getId();
        info.transitions = transtionsList.stream().mapToLong(l -> l).toArray();
        info.std_offset = (int) Duration.ofMillis(timeZone.getRawOffset()).toMinutes();
        info.names = new String[] {
                timeZone.getDisplayName(false, TimeZone.SHORT, locale),
                timeZone.getDisplayName(false, TimeZone.LONG, locale),
                timeZone.getDisplayName(true, TimeZone.SHORT, locale),
                timeZone.getDisplayName(true, TimeZone.LONG, locale)
        };

        return stringify(info);
    }

    private static String stringify(TimeZoneInfo info) {
        JreJsonFactory factory = new JreJsonFactory();
        JsonObject object = factory.createObject();
        object.put("id", info.id);
        object.put("std_offset", info.std_offset);
        object.put("names", getArray(factory, info.names));
        object.put("transitions", getArray(factory, info.transitions));
        return JsonUtil.stringify(object);
    }

    private static JsonArray getArray(JreJsonFactory factory, long[] array) {
        JsonArray jsonArray = factory.createArray();
        for (int i = 0; i < array.length; i++) {
            jsonArray.set(i, array[i]);
        }
        return jsonArray;
    }

    private static JsonArray getArray(JreJsonFactory factory, String[] array) {
        JsonArray jsonArray = factory.createArray();
        for (int i = 0; i < array.length; i++) {
            jsonArray.set(i, array[i]);
        }
        return jsonArray;
    }

    private static class TimeZoneInfo implements Serializable {
        String id;
        int std_offset;
        String[] names;
        long[] transitions;
    }
}
