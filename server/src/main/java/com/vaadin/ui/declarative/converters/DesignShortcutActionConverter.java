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

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;

/**
 * Converter for {@link ShortcutActions}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignShortcutActionConverter implements
        Converter<String, ShortcutAction> {

    private final Map<Integer, String> keyCodeMap;
    private final Map<String, Integer> presentationMap;

    public DesignShortcutActionConverter() {
        HashMap<Integer, String> codes = new HashMap<Integer, String>();
        // map modifiers
        codes.put(ModifierKey.ALT, "alt");
        codes.put(ModifierKey.CTRL, "ctrl");
        codes.put(ModifierKey.META, "meta");
        codes.put(ModifierKey.SHIFT, "shift");
        // map keys
        codes.put(KeyCode.ENTER, "enter");
        codes.put(KeyCode.ESCAPE, "escape");
        codes.put(KeyCode.PAGE_UP, "pageup");
        codes.put(KeyCode.PAGE_DOWN, "pagedown");
        codes.put(KeyCode.TAB, "tab");
        codes.put(KeyCode.ARROW_LEFT, "left");
        codes.put(KeyCode.ARROW_UP, "up");
        codes.put(KeyCode.ARROW_RIGHT, "right");
        codes.put(KeyCode.ARROW_DOWN, "down");
        codes.put(KeyCode.BACKSPACE, "backspace");
        codes.put(KeyCode.DELETE, "delete");
        codes.put(KeyCode.INSERT, "insert");
        codes.put(KeyCode.END, "end");
        codes.put(KeyCode.HOME, "home");
        codes.put(KeyCode.F1, "f1");
        codes.put(KeyCode.F2, "f2");
        codes.put(KeyCode.F3, "f3");
        codes.put(KeyCode.F4, "f4");
        codes.put(KeyCode.F5, "f5");
        codes.put(KeyCode.F6, "f6");
        codes.put(KeyCode.F7, "f7");
        codes.put(KeyCode.F8, "f8");
        codes.put(KeyCode.F9, "f9");
        codes.put(KeyCode.F10, "f10");
        codes.put(KeyCode.F11, "f11");
        codes.put(KeyCode.F12, "f12");
        codes.put(KeyCode.NUM0, "0");
        codes.put(KeyCode.NUM1, "1");
        codes.put(KeyCode.NUM2, "2");
        codes.put(KeyCode.NUM3, "3");
        codes.put(KeyCode.NUM4, "4");
        codes.put(KeyCode.NUM5, "5");
        codes.put(KeyCode.NUM6, "6");
        codes.put(KeyCode.NUM7, "7");
        codes.put(KeyCode.NUM8, "8");
        codes.put(KeyCode.NUM9, "9");
        codes.put(KeyCode.SPACEBAR, "spacebar");
        codes.put(KeyCode.A, "a");
        codes.put(KeyCode.B, "b");
        codes.put(KeyCode.C, "c");
        codes.put(KeyCode.D, "d");
        codes.put(KeyCode.E, "e");
        codes.put(KeyCode.F, "f");
        codes.put(KeyCode.G, "g");
        codes.put(KeyCode.H, "h");
        codes.put(KeyCode.I, "i");
        codes.put(KeyCode.J, "j");
        codes.put(KeyCode.K, "k");
        codes.put(KeyCode.L, "l");
        codes.put(KeyCode.M, "m");
        codes.put(KeyCode.N, "n");
        codes.put(KeyCode.O, "o");
        codes.put(KeyCode.P, "p");
        codes.put(KeyCode.Q, "q");
        codes.put(KeyCode.R, "r");
        codes.put(KeyCode.S, "s");
        codes.put(KeyCode.T, "t");
        codes.put(KeyCode.U, "u");
        codes.put(KeyCode.V, "v");
        codes.put(KeyCode.X, "x");
        codes.put(KeyCode.Y, "y");
        codes.put(KeyCode.Z, "z");

        keyCodeMap = Collections.unmodifiableMap(codes);

        HashMap<String, Integer> presentations = new HashMap<String, Integer>();
        for (Entry<Integer, String> entry : keyCodeMap.entrySet()) {
            presentations.put(entry.getValue(), entry.getKey());
        }

        presentationMap = Collections.unmodifiableMap(presentations);
    }

    @Override
    public ShortcutAction convertToModel(String value,
            Class<? extends ShortcutAction> targetType, Locale locale)
            throws Converter.ConversionException {
        if (value.length() == 0) {
            return null;
        }

        String[] data = value.split(" ", 2);
        String[] parts = data[0].split("-");

        try {
            // handle keycode
            String keyCodePart = parts[parts.length - 1];
            int keyCode = getKeycodeForString(keyCodePart);
            if (keyCode < 0) {
                throw new IllegalArgumentException("Invalid key '"
                        + keyCodePart + "'");
            }
            // handle modifiers
            int[] modifiers = null;
            if (parts.length > 1) {
                modifiers = new int[parts.length - 1];
            }
            for (int i = 0; i < parts.length - 1; i++) {
                int modifier = getKeycodeForString(parts[i]);
                if (modifier > 0) {
                    modifiers[i] = modifier;
                } else {
                    throw new IllegalArgumentException("Invalid modifier '"
                            + parts[i] + "'");
                }
            }
            return new ShortcutAction(data.length == 2 ? data[1] : null,
                    keyCode, modifiers);
        } catch (Exception e) {
            throw new ConversionException("Invalid shortcut '" + value + "'", e);
        }
    }

    @Override
    public String convertToPresentation(ShortcutAction value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        StringBuilder sb = new StringBuilder();
        // handle modifiers
        if (value.getModifiers() != null) {
            for (int modifier : value.getModifiers()) {
                sb.append(getStringForKeycode(modifier)).append("-");
            }
        }
        // handle keycode
        sb.append(getStringForKeycode(value.getKeyCode()));
        if (value.getCaption() != null) {
            sb.append(" ").append(value.getCaption());
        }
        return sb.toString();
    }

    @Override
    public Class<ShortcutAction> getModelType() {
        return ShortcutAction.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    public int getKeycodeForString(String attributePresentation) {
        Integer code = presentationMap.get(attributePresentation);
        return code != null ? code.intValue() : -1;
    }

    public String getStringForKeycode(int keyCode) {
        return keyCodeMap.get(keyCode);
    }

}
