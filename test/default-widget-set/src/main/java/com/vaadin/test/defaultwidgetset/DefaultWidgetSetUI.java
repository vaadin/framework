package com.vaadin.test.defaultwidgetset;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;

public class DefaultWidgetSetUI extends AbstractTestWidgetSetUI {

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = DefaultWidgetSetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}