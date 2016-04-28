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
package com.vaadin.tests.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.tests.design.DesignFormatterTest;

/**
 * Tests various things about shortcut actions.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ShortcutActionTest {

    private static final String[] KEYS = "a b c d e f g h i j k l m n o p q r s t u v w x y z"
            .split("\\s+");

    @Test
    public void testHashCodeUniqueness() {
        HashSet<ShortcutAction> set = new HashSet<ShortcutAction>();
        for (String modifier : new String[] { "^", "&", "_", "&^", "&_", "_^",
                "&^_" }) {
            for (String key : KEYS) {
                ShortcutAction action = new ShortcutAction(modifier + key);
                for (ShortcutAction other : set) {
                    assertFalse(equals(action, other));
                }
                set.add(action);
            }
        }
    }

    @Test
    public void testModifierOrderIrrelevant() {
        for (String key : KEYS) {
            // two modifiers
            for (String modifier : new String[] { "&^", "&_", "_^" }) {
                ShortcutAction action1 = new ShortcutAction(modifier + key);
                ShortcutAction action2 = new ShortcutAction(
                        modifier.substring(1) + modifier.substring(0, 1) + key);
                assertTrue(modifier + key, equals(action1, action2));
            }
            // three modifiers
            ShortcutAction action1 = new ShortcutAction("&^_" + key);
            for (String modifier : new String[] { "&_^", "^&_", "^_&", "_^&",
                    "_&^" }) {
                ShortcutAction action2 = new ShortcutAction(modifier + key);
                assertTrue(modifier + key, equals(action1, action2));

            }
        }
    }

    @Test
    public void testSameKeycodeDifferentCaptions() {
        ShortcutAction act1 = new ShortcutAction("E&xit");
        ShortcutAction act2 = new ShortcutAction("Lu&xtorpeda - Autystyczny");
        assertFalse(equals(act1, act2));
    }

    /**
     * A static method to allow comparison two different actions.
     * 
     * @see DesignFormatterTest
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
