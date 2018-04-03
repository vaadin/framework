package com.vaadin.tests.themes;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

public class ThemeChangeFavicon extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        for (final String theme : Arrays.asList("valo", "reindeer")) {
            addComponent(new Button(theme, event -> setTheme(theme)));
        }
    }

    @Override
    protected String getTestDescription() {
        return "UI for testing that the favicon changes when changing themes";
    }
}
