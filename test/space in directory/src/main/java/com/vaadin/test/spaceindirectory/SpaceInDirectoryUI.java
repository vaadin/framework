package com.vaadin.test.spaceindirectory;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;

@Widgetset("com.vaadin.test.spaceindirectory.SpaceInDirectory")
public class SpaceInDirectoryUI extends AbstractTestWidgetSetUI {

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = SpaceInDirectoryUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
