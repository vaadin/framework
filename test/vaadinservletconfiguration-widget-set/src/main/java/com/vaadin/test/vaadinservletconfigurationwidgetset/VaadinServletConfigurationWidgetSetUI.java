package com.vaadin.test.vaadinservletconfigurationwidgetset;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;

public class VaadinServletConfigurationWidgetSetUI
        extends AbstractTestWidgetSetUI {

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = VaadinServletConfigurationWidgetSetUI.class, productionMode = false, widgetset = "com.vaadin.test.vaadinservletconfigurationwidgetset.VaadinServletConfigurationWidgetSet")
    public static class MyUIServlet extends VaadinServlet {
    }
}