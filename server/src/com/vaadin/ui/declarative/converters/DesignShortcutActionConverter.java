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

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ShortcutAction;

/**
 * Converter for {@link ShortcutActions}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignShortcutActionConverter implements
        Converter<String, ShortcutAction> {

    /**
     * Default instance of the shortcut key mapper.
     */
    private final ShortcutKeyMapper keyMapper;

    /**
     * Constructs the converter with given key mapper.
     * 
     * @param mapper
     *            Key mapper to use.
     */
    public DesignShortcutActionConverter(ShortcutKeyMapper mapper) {
        keyMapper = mapper;
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
        // handle keycode
        String keyCodePart = parts[parts.length - 1];
        int keyCode = getKeyMapper().getKeycodeForString(keyCodePart);
        if (keyCode < 0) {
            throw new IllegalArgumentException("Invalid shortcut definition "
                    + value);
        }
        // handle modifiers
        int[] modifiers = null;
        if (parts.length > 1) {
            modifiers = new int[parts.length - 1];
        }
        for (int i = 0; i < parts.length - 1; i++) {
            int modifier = getKeyMapper().getKeycodeForString(parts[i]);
            if (modifier > 0) {
                modifiers[i] = modifier;
            } else {
                throw new IllegalArgumentException(
                        "Invalid shortcut definition " + value);
            }
        }
        return new ShortcutAction(data.length == 2 ? data[1] : null, keyCode,
                modifiers);
    }

    @Override
    public String convertToPresentation(ShortcutAction value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        StringBuilder sb = new StringBuilder();
        // handle modifiers
        if (value.getModifiers() != null) {
            for (int modifier : value.getModifiers()) {
                sb.append(getKeyMapper().getStringForKeycode(modifier)).append(
                        "-");
            }
        }
        // handle keycode
        sb.append(getKeyMapper().getStringForKeycode(value.getKeyCode()));
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

    /**
     * Returns the currently used key mapper.
     * 
     * @return Key mapper.
     */
    public ShortcutKeyMapper getKeyMapper() {
        return keyMapper;
    }

}
