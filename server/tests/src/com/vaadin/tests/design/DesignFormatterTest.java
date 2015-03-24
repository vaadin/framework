/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * Various tests related to formatter.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignFormatterTest {

    private DesignFormatter formatter;

    @Before
    public void setUp() {
        // initialise with default classes
        formatter = new DesignFormatter();
    }

    @Test
    public void testSupportedClasses() {

        for (Class<?> type : new Class<?>[] { String.class, Boolean.class,
                Integer.class, Float.class, Byte.class, Short.class,
                Double.class, ShortcutAction.class, Date.class,
                FileResource.class, ExternalResource.class,
                ThemeResource.class, Resource.class, TimeZone.class }) {
            assertTrue("not supported " + type.getSimpleName(),
                    formatter.canConvert(type));
        }
    }

    @Test
    public void testDate() throws Exception {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-17");
        String formatted = formatter.format(date);
        Date result = formatter.parse(formatted, Date.class);

        // writing will always give full date string
        assertEquals("2012-02-17 00:00:00+0200", formatted);
        assertEquals(date, result);

        // try short date as well
        result = formatter.parse("2012-02-17", Date.class);
        assertEquals(date, result);
    }

    @Test
    public void testShortcutActions() {
        ShortcutAction action = new ShortcutAction("&^d");
        String formatted = formatter.format(action);
        // note the space here - it separates key combination from caption
        assertEquals("alt-ctrl-d d", formatted);

        ShortcutAction result = formatter
                .parse(formatted, ShortcutAction.class);
        assertTrue(equals(action, result));
    }

    @Test
    public void testShortcutActionNoCaption() {
        ShortcutAction action = new ShortcutAction(null,
                ShortcutAction.KeyCode.D, new int[] {
                        ShortcutAction.ModifierKey.ALT,
                        ShortcutAction.ModifierKey.CTRL });
        String formatted = formatter.format(action);
        assertEquals("alt-ctrl-d", formatted);

        ShortcutAction result = formatter
                .parse(formatted, ShortcutAction.class);
        assertTrue(equals(action, result));
    }

    @Test
    public void testTimeZone() {
        TimeZone zone = TimeZone.getTimeZone("GMT+2");
        String formatted = formatter.format(zone);
        assertEquals("GMT+02:00", formatted);
        TimeZone result = formatter.parse(formatted, TimeZone.class);
        assertEquals(zone, result);
        // try shorthand notation as well
        result = formatter.parse("GMT+2", TimeZone.class);
        assertEquals(zone, result);
    }

    /**
     * A static method to allow comparison two different actions.
     * 
     * @param act
     *            One action to compare.
     * @param other
     *            Second action to compare.
     * @return <b>true</b> when both actions are the same (caption, icon, and
     *         key combination).
     */
    public static final boolean equals(ShortcutAction act, ShortcutAction other) {
        if (SharedUtil.equals(other.getCaption(), act.getCaption())
                && SharedUtil.equals(other.getIcon(), act.getIcon())
                && act.getKeyCode() == other.getKeyCode()
                && act.getModifiers().length == other.getModifiers().length) {
            HashSet<Integer> thisSet = new HashSet<Integer>(
                    act.getModifiers().length);
            // this is a bit tricky comparison, but there is no nice way of
            // making int[] into a Set
            for (int mod : act.getModifiers()) {
                thisSet.add(mod);
            }
            for (int mod : other.getModifiers()) {
                thisSet.remove(mod);
            }
            return thisSet.isEmpty();
        }
        return false;
    }

}
