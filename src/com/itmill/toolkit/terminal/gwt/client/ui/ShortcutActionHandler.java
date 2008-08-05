/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * A helper class to implement keyboard shorcut handling. Keeps a list of owners
 * actions and fires actions to server. User class needs to delegate keyboard
 * events to handleKeyboardEvents function.
 * 
 * @author IT Mill ltd
 */
public class ShortcutActionHandler {
    private final ArrayList actions = new ArrayList();
    private ApplicationConnection client;
    private String paintableId;

    /**
     * 
     * @param pid
     *                Paintable id
     * @param c
     *                reference to application connections
     */
    public ShortcutActionHandler(String pid, ApplicationConnection c) {
        paintableId = pid;
        client = c;
    }

    /**
     * Updates list of actions this handler listens to.
     * 
     * @param c
     *                UIDL snippet containing actions
     */
    public void updateActionMap(UIDL c) {
        actions.clear();
        final Iterator it = c.getChildIterator();
        while (it.hasNext()) {
            final UIDL action = (UIDL) it.next();

            int[] modifiers = null;
            if (action.hasAttribute("mk")) {
                modifiers = action.getIntArrayAttribute("mk");
            }

            final ShortcutKeyCombination kc = new ShortcutKeyCombination(action
                    .getIntAttribute("kc"), modifiers);
            final String key = action.getStringAttribute("key");
            final String caption = action.getStringAttribute("caption");
            actions.add(new ShortcutAction(key, kc, caption));
        }
    }

    public void handleKeyboardEvent(final Event event) {
        final int modifiers = KeyboardListenerCollection
                .getKeyboardModifiers(event);
        final char keyCode = (char) DOM.eventGetKeyCode(event);
        final ShortcutKeyCombination kc = new ShortcutKeyCombination(keyCode,
                modifiers);
        final Iterator it = actions.iterator();
        while (it.hasNext()) {
            final ShortcutAction a = (ShortcutAction) it.next();
            if (a.getShortcutCombination().equals(kc)) {
                DOM.eventPreventDefault(event);
                shakeTarget(DOM.eventGetTarget(event));
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        client.updateVariable(paintableId, "action",
                                a.getKey(), true);
                    }
                });
                break;
            }
        }
    }

    public static native void shakeTarget(Element e)
    /*-{
            if(e.blur) {
                e.blur();
                e.focus();
       }
    }-*/;

}

class ShortcutKeyCombination {

    public static final int SHIFT = 16;
    public static final int CTRL = 17;
    public static final int ALT = 18;

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
        keyCode = Character.toUpperCase(keyCode);

        modifiersMask = 0;
        if (modifiers != null) {
            for (int i = 0; i < modifiers.length; i++) {
                switch (modifiers[i]) {
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

    public ShortcutAction(String key, ShortcutKeyCombination sc, String caption) {
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
