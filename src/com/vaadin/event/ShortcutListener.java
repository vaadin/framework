/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.event;

import com.vaadin.event.Action.Listener;
import com.vaadin.terminal.Resource;

public abstract class ShortcutListener extends ShortcutAction implements
        Listener {

    private static final long serialVersionUID = 1L;

    public ShortcutListener(String caption, int keyCode, int... modifierKeys) {
        super(caption, keyCode, modifierKeys);
    }

    public ShortcutListener(String shorthandCaption, int... modifierKeys) {
        super(shorthandCaption, modifierKeys);
    }

    public ShortcutListener(String caption, Resource icon, int keyCode,
            int... modifierKeys) {
        super(caption, icon, keyCode, modifierKeys);
    }

    public ShortcutListener(String shorthandCaption) {
        super(shorthandCaption);
    }

    abstract public void handleAction(Object sender, Object target);
}
