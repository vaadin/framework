package com.vaadin.test.osgi.myapplication1;

import com.vaadin.osgi.resources.OsgiVaadinTheme;
import com.vaadin.osgi.resources.OsgiVaadinWidgetset;
import com.vaadin.ui.themes.ValoTheme;
import org.osgi.service.component.annotations.Component;

@Component
public class CustomWidgetSet implements OsgiVaadinWidgetset {
    @Override
    public String getName() {
        return "com.vaadin.test.osgi.widgetset.CustomWidgetSet";
    }
}
