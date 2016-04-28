package com.vaadin.tests.components.button;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

public class ButtonsWaiAria extends ComponentTestCase<Button> {

    @Override
    protected Class<Button> getTestClass() {
        return Button.class;
    }

    @Override
    protected void initializeComponents() {

        Button l;
        boolean nat = false;

        l = createButton("Default Button", nat);
        addTestComponent(l);

        l = createButton("Icon Button, empty alt", nat);
        l.setIcon(ICON_16_USER_PNG_CACHEABLE);
        l.setDescription("Empty alt text");
        addTestComponent(l);

        l = createButton("Icon Button with alt", nat);
        l.setIcon(ICON_16_USER_PNG_CACHEABLE, "user icon");
        addTestComponent(l);

        l = createButton("Tooltip Button", nat);
        l.setDescription("Tooltip");
        addTestComponent(l);

        l = createButton("Another tooltip", nat);
        l.setDescription("Another");
        addTestComponent(l);
    }

    private Button createButton(String text, boolean nativeButton) {
        Button b;
        if (nativeButton) {
            b = new NativeButton(text);
        } else {
            b = new Button(text);
        }

        return b;
    }

    @Override
    protected String getDescription() {
        return "A generic test for Buttons in different configurations";
    }
}
