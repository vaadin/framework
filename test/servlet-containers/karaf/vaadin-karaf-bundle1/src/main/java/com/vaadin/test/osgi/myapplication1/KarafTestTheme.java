package com.vaadin.test.osgi.myapplication1;

import com.vaadin.osgi.resources.OsgiVaadinTheme;
import com.vaadin.ui.themes.ValoTheme;
import org.osgi.service.component.annotations.Component;

@Component
public class KarafTestTheme extends ValoTheme implements OsgiVaadinTheme {
    @Override
    public String getName() {
        return "karaftesttheme";
    }
}
