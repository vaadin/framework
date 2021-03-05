/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.event;

import com.vaadin.ui.Component.Focusable;

/**
 * A ready-made {@link ShortcutListener} that focuses the given
 * {@link Focusable} (usually a {@link Field}) when the keyboard shortcut is
 * invoked.
 *
 * @author Vaadin Ltd
 * @since 8.7
 */
public class FocusShortcut extends ShortcutListener {
    protected Focusable focusable;

    /**
     * Creates a keyboard shortcut for focusing the given {@link Focusable}
     * using the shorthand notation defined in {@link ShortcutAction}.
     *
     * @param focusable
     *            to focused when the shortcut is invoked
     * @param shorthandCaption
     *            caption with keycode and modifiers indicated
     */
    public FocusShortcut(Focusable focusable, String shorthandCaption) {
        super(shorthandCaption);
        this.focusable = focusable;
    }

    /**
     * Creates a keyboard shortcut for focusing the given {@link Focusable}.
     *
     * @param focusable
     *            to focused when the shortcut is invoked
     * @param keyCode
     *            keycode that invokes the shortcut
     * @param modifiers
     *            modifiers required to invoke the shortcut
     */
    public FocusShortcut(Focusable focusable, int keyCode, int... modifiers) {
        super(null, keyCode, modifiers);
        this.focusable = focusable;
    }

    /**
     * Creates a keyboard shortcut for focusing the given {@link Focusable}.
     *
     * @param focusable
     *            to focused when the shortcut is invoked
     * @param keyCode
     *            keycode that invokes the shortcut
     */
    public FocusShortcut(Focusable focusable, int keyCode) {
        this(focusable, keyCode, null);
    }

    @Override
    public void handleAction(Object sender, Object target) {
        focusable.focus();
    }
}
