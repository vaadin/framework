package com.vaadin.test.osgi.myapplication1;

import com.vaadin.osgi.resources.OsgiVaadinWidgetset;
import org.osgi.service.component.annotations.Component;

@Component
public class CustomWidgetSet implements OsgiVaadinWidgetset {
    @Override
    public String getName() {
        return "com.vaadin.test.osgi.widgetset.CustomWidgetSet";
    }
}
