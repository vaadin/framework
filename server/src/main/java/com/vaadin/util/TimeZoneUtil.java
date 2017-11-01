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
 * @since 8.2
 */
public final class TimeZoneUtil implements Serializable {

    /**
     * The start year used to send the time zone transition dates.
     */
    private static final int STARTING_YEAR = 1980;

    /**
     * Till how many years from now, should we send the time zone transition
     * dates.
     */
    private static final int YEARS_FROM_NOW = 20;

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
     *            the locale used to determine the short name of the time zone
     *
     * @return the encoded string
     */
    public static String toJSON(ZoneId zoneId, Locale locale) {
        if (zoneId == null || locale == null) {
            return null;
        }
        ZoneRules rules = zoneId.getRules();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        List<Long> transitionsList = new ArrayList<>();

        TimeZoneInfo info = new TimeZoneInfo();

        int endYear = LocalDate.now().getYear() + YEARS_FROM_NOW;
        if (timeZone.useDaylightTime()) {
            for (int year = STARTING_YEAR; year <= endYear; year++) {
                ZonedDateTime i = LocalDateTime.of(year, 1, 1, 0, 0)
                        .atZone(zoneId);
                while (true) {
                    ZoneOffsetTransition t = rules
                            .nextTransition(i.toInstant());
                    if (t == null) {
                        break;
                    }
                    i = t.getInstant().atZone(zoneId);
                    if (i.toLocalDate().getYear() != year) {
                        break;
                    }
                    long epochHours = Duration
                            .ofSeconds(t.getInstant().getEpochSecond())
                            .toHours();
                    long duration = Math.max(t.getDuration().toMinutes(), 0);
                    transitionsList.add(epochHours);
                    transitionsList.add(duration);
                }
            }
        }
        info.id = zoneId.getId();
        info.transitions = transitionsList.stream().mapToLong(l -> l).toArray();
        info.stdOffset = (int) Duration.ofMillis(timeZone.getRawOffset())
                .toMinutes();
        info.names = new String[] {
                timeZone.getDisplayName(false, TimeZone.SHORT, locale),
                timeZone.getDisplayName(false, TimeZone.LONG, locale),
                timeZone.getDisplayName(true, TimeZone.SHORT, locale),
                timeZone.getDisplayName(true, TimeZone.LONG, locale) };

        return stringify(info);
    }

    private static String stringify(TimeZoneInfo info) {
        JreJsonFactory factory = new JreJsonFactory();
        JsonObject object = factory.createObject();
        object.put("id", info.id);
        object.put("std_offset", info.stdOffset);
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
        int stdOffset;
        String[] names;
        long[] transitions;
    }
}
