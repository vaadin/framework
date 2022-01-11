/*
 * Copyright 2000-2022 Vaadin Ltd.
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

package com.vaadin.client.ui;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;

/**
 * A helper class to implement keyboard shortcut handling. Keeps a list of
 * owners actions and fires actions to server. User class needs to delegate
 * keyboard events to handleKeyboardEvents function.
 *
 * @author Vaadin Ltd
 */
public class ShortcutActionHandler {

    /**
     * An interface implemented by those users of this helper class that want to
     * support special components like {@code VRichTextArea} that don't properly
     * propagate key down events. Those components can build support for
     * shortcut actions by traversing the closest
     * {@link ShortcutActionHandlerOwner} from the component hierarchy an
     * passing keydown events to {@link ShortcutActionHandler}.
     */
    public interface ShortcutActionHandlerOwner extends HasWidgets {

        /**
         * Returns the ShortCutActionHandler currently used or null if there is
         * currently no shortcutactionhandler.
         */
        ShortcutActionHandler getShortcutActionHandler();
    }

    private final ArrayList<ShortcutAction> actions = new ArrayList<>();
    private ApplicationConnection client;
    private String paintableId;

    /**
     *
     * @param pid
     *            Paintable id
     * @param c
     *            reference to application connections
     */
    public ShortcutActionHandler(String pid, ApplicationConnection c) {
        paintableId = pid;
        client = c;
    }

    /**
     * Updates list of actions this handler listens to.
     *
     * @param c
     *            UIDL snippet containing actions
     */
    public void updateActionMap(UIDL c) {
        actions.clear();
        for (final Object child : c) {
            final UIDL action = (UIDL) child;

            int[] modifiers = null;
            if (action.hasAttribute("mk")) {
                modifiers = action.getIntArrayAttribute("mk");
            }

            final ShortcutKeyCombination kc = new ShortcutKeyCombination(
                    action.getIntAttribute("kc"), modifiers);
            final String key = action.getStringAttribute("key");
            final String caption = action.getStringAttribute("caption");
            actions.add(new ShortcutAction(key, kc, caption));
        }
    }

    public void handleKeyboardEvent(final Event event,
            ComponentConnector target) {
        final char keyCode = (char) DOM.eventGetKeyCode(event);
        if (keyCode == 0) {
            return;
        }
        final int modifiers = KeyboardListenerCollection
                .getKeyboardModifiers(event);
        final ShortcutKeyCombination kc = new ShortcutKeyCombination(keyCode,
                modifiers);
        for (final ShortcutAction a : actions) {
            if (a.getShortcutCombination().equals(kc)) {
                fireAction(event, a, target);
                break;
            }
        }

    }

    public void handleKeyboardEvent(final Event event) {
        handleKeyboardEvent(event, null);
    }

    private void fireAction(final Event event, final ShortcutAction a,
            ComponentConnector target) {
        final Element et = DOM.eventGetTarget(event);
        if (target == null) {
            target = Util.findPaintable(client, et);
        }
        final ComponentConnector finalTarget = target;
        event.preventDefault();

        /*
         * The focused component might have unpublished changes, try to
         * synchronize them before firing shortcut action.
         */
        boolean flushed = false;
        ComponentConnector activeConnector = getActiveConnector(client);

        if (activeConnector != null) {
            Class<?> clz = activeConnector.getClass();
            while (clz != null) {
                if (clz.getName().equals(
                        "com.vaadin.v7.client.ui.textfield.TextFieldConnector")) {
                    /*
                     * Legacy textfields support modern flushing.
                     */
                    client.flushActiveConnector();
                    flushed = true;
                    break;
                }
                if (clz.getName().equals(
                        "com.vaadin.v7.client.ui.AbstractLegacyComponentConnector")) {
                    /*
                     * Most of the legacy components don't have built-in logic
                     * for flushing, they need a workaround with blur and focus
                     * to trigger the value change.
                     */
                    shakeTarget(et);
                    Scheduler.get().scheduleDeferred(() -> {
                        shakeTarget(et);
                    });
                    flushed = true;
                    break;
                }
                clz = clz.getSuperclass();
            }
        }
        if (!flushed) {
            // Use V8 style flushing for the rest.
            client.flushActiveConnector();
        }

        Scheduler.get().scheduleDeferred(() -> {
            if (finalTarget != null) {
                client.updateVariable(paintableId, "actiontarget", finalTarget,
                        false);
            }
            client.updateVariable(paintableId, "action", a.getKey(), true);
        });
    }

    /**
     * We try to fire value change in the component the key combination was
     * typed. E.g. TextField may contain newly typed text that is expected to be
     * sent to server before the shortcut action is triggered. This is done by
     * removing focus and then returning it immediately back to target element.
     * <p>
     * This is a hack copied over from V7 in order to keep the compatibility
     * classes working. Main V8 classes don't require shaking.
     */
    private static void shakeTarget(final Element e) {
        blur(e);
        focus(e);
    }

    private static native ComponentConnector getActiveConnector(
            ApplicationConnection ac)
    /*-{
        return ac.@com.vaadin.client.ApplicationConnection::getActiveConnector()();
    }-*/;

    private static native void blur(Element e)
    /*-{
        if (e.blur) {
            e.blur();
       }
    }-*/;

    private static native void focus(Element e)
    /*-{
        if (e.blur) {
            e.focus();
       }
    }-*/;

}

class ShortcutKeyCombination {

    public static final int SHIFT = 16;
    public static final int CTRL = 17;
    public static final int ALT = 18;
    public static final int META = 91;

    char keyCode = 0;
    private int modifiersMask;

    public ShortcutKeyCombination() {
    }

    ShortcutKeyCombination(char kc, int modifierMask) {
        keyCode = kc;
        modifiersMask = modifierMask;
    }

    ShortcutKeyCombination(int kc, int[] modifiers) {
        keyCode = (char) kc;

        modifiersMask = 0;
        if (modifiers != null) {
            for (int modifier : modifiers) {
                switch (modifier) {
                case ALT:
                    modifiersMask = modifiersMask
                            | KeyboardListener.MODIFIER_ALT;
                    break;
                case CTRL:
                    modifiersMask = modifiersMask
                            | KeyboardListener.MODIFIER_CTRL;
                    break;
                case SHIFT:
                    modifiersMask = modifiersMask
                            | KeyboardListener.MODIFIER_SHIFT;
                    break;
                case META:
                    modifiersMask = modifiersMask
                            | KeyboardListener.MODIFIER_META;
                    break;
                default:
                    break;
                }
            }
        }
    }

    public boolean equals(ShortcutKeyCombination other) {
        if (keyCode == other.keyCode && modifiersMask == other.modifiersMask) {
            return true;
        }
        return false;
    }
}

class ShortcutAction {

    private final ShortcutKeyCombination sc;
    private final String caption;
    private final String key;

    public ShortcutAction(String key, ShortcutKeyCombination sc,
            String caption) {
        this.sc = sc;
        this.key = key;
        this.caption = caption;
    }

    public ShortcutKeyCombination getShortcutCombination() {
        return sc;
    }

    public String getCaption() {
        return caption;
    }

    public String getKey() {
        return key;
    }

}
