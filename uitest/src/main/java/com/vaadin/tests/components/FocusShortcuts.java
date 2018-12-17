package com.vaadin.tests.components;

import com.vaadin.event.FocusShortcut;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class FocusShortcuts extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        TextField name = new TextField("Name (Alt+N)");
        name.addShortcutListener(
                new FocusShortcut(name, KeyCode.N, ModifierKey.ALT));
        name.addFocusListener(event -> log("Alt+N"));

        CheckBox checkBox = new CheckBox("hello");
        checkBox.add

        TextField address = new TextField("Address (Alt+A)");
        address.addShortcutListener(new FocusShortcut(address, "&Address"));
        address.addFocusListener(event -> log("Alt+A"));

        TextField name2 = new TextField("Name (Ctrl+Shift+D)");
        name2.addShortcutListener(new FocusShortcut(name2, KeyCode.D,
                ModifierKey.CTRL, ModifierKey.SHIFT));
        name2.addFocusListener(event -> log("Ctrl+Shift+D"));

        addComponents(name, address, name2);
    }

}
