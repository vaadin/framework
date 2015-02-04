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
package com.vaadin.ui.declarative.converters;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;

/**
 * Provides mappings between shortcut keycodes and their representation in
 * design attributes. Contains a default framework implementation as a field.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface ShortcutKeyMapper extends Serializable {

    /**
     * Gets the key code for a given string.
     * 
     * @param attributePresentation
     *            String
     * @return Key code.
     */
    public int getKeycodeForString(String attributePresentation);

    /**
     * Returns a string for a given key code.
     * 
     * @param keyCode
     *            Key code.
     * @return String.
     */
    public String getStringForKeycode(int keyCode);

    /**
     * An instance of a default keymapper.
     */
    public static final ShortcutKeyMapper DEFAULT = new ShortcutKeyMapper() {

        private final Map<Integer, String> keyCodeMap = new ConcurrentHashMap<Integer, String>();
        private final Map<String, Integer> presentationMap = new ConcurrentHashMap<String, Integer>();

        {
            // map modifiers
            mapKey(ModifierKey.ALT, "alt");
            mapKey(ModifierKey.CTRL, "ctrl");
            mapKey(ModifierKey.META, "meta");
            mapKey(ModifierKey.SHIFT, "shift");
            // map keys
            mapKey(KeyCode.ENTER, "enter");
            mapKey(KeyCode.ESCAPE, "escape");
            mapKey(KeyCode.PAGE_UP, "pageup");
            mapKey(KeyCode.PAGE_DOWN, "pagedown");
            mapKey(KeyCode.TAB, "tab");
            mapKey(KeyCode.ARROW_LEFT, "left");
            mapKey(KeyCode.ARROW_UP, "up");
            mapKey(KeyCode.ARROW_RIGHT, "right");
            mapKey(KeyCode.ARROW_DOWN, "down");
            mapKey(KeyCode.BACKSPACE, "backspace");
            mapKey(KeyCode.DELETE, "delete");
            mapKey(KeyCode.INSERT, "insert");
            mapKey(KeyCode.END, "end");
            mapKey(KeyCode.HOME, "home");
            mapKey(KeyCode.F1, "f1");
            mapKey(KeyCode.F2, "f2");
            mapKey(KeyCode.F3, "f3");
            mapKey(KeyCode.F4, "f4");
            mapKey(KeyCode.F5, "f5");
            mapKey(KeyCode.F6, "f6");
            mapKey(KeyCode.F7, "f7");
            mapKey(KeyCode.F8, "f8");
            mapKey(KeyCode.F9, "f9");
            mapKey(KeyCode.F10, "f10");
            mapKey(KeyCode.F11, "f11");
            mapKey(KeyCode.F12, "f12");
            mapKey(KeyCode.NUM0, "0");
            mapKey(KeyCode.NUM1, "1");
            mapKey(KeyCode.NUM2, "2");
            mapKey(KeyCode.NUM3, "3");
            mapKey(KeyCode.NUM4, "4");
            mapKey(KeyCode.NUM5, "5");
            mapKey(KeyCode.NUM6, "6");
            mapKey(KeyCode.NUM7, "7");
            mapKey(KeyCode.NUM8, "8");
            mapKey(KeyCode.NUM9, "9");
            mapKey(KeyCode.SPACEBAR, "spacebar");
            mapKey(KeyCode.A, "a");
            mapKey(KeyCode.B, "b");
            mapKey(KeyCode.C, "c");
            mapKey(KeyCode.D, "d");
            mapKey(KeyCode.E, "e");
            mapKey(KeyCode.F, "f");
            mapKey(KeyCode.G, "g");
            mapKey(KeyCode.H, "h");
            mapKey(KeyCode.I, "i");
            mapKey(KeyCode.J, "j");
            mapKey(KeyCode.K, "k");
            mapKey(KeyCode.L, "l");
            mapKey(KeyCode.M, "m");
            mapKey(KeyCode.N, "n");
            mapKey(KeyCode.O, "o");
            mapKey(KeyCode.P, "p");
            mapKey(KeyCode.Q, "q");
            mapKey(KeyCode.R, "r");
            mapKey(KeyCode.S, "s");
            mapKey(KeyCode.T, "t");
            mapKey(KeyCode.U, "u");
            mapKey(KeyCode.V, "v");
            mapKey(KeyCode.X, "x");
            mapKey(KeyCode.Y, "y");
            mapKey(KeyCode.Z, "z");
        }

        private void mapKey(int keyCode, String presentation) {
            keyCodeMap.put(keyCode, presentation);
            presentationMap.put(presentation, keyCode);
        }

        @Override
        public int getKeycodeForString(String attributePresentation) {
            Integer code = presentationMap.get(attributePresentation);
            return code != null ? code.intValue() : -1;
        }

        @Override
        public String getStringForKeycode(int keyCode) {
            return keyCodeMap.get(keyCode);
        }

    };
}