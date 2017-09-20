package com.vaadin.test.vaadin7widgetset;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;

public class Vaadin7WidgetSetUI extends AbstractTestWidgetSetUI {

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Vaadin7WidgetSetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
