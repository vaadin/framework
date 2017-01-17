package com.vaadin.test.addonusingownwidgetset;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.test.widgetset.AbstractTestWidgetSetUI;
import com.vaadin.ui.AbstractComponent;

@Widgetset("com.vaadin.test.addonusingownwidgetset.AddonUsingOwnWidgetSet")
public class AddonUsingOwnWidgetSetUI extends AbstractTestWidgetSetUI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        super.init(vaadinRequest);
        new ContextMenu((AbstractComponent) getContent(), true);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AddonUsingOwnWidgetSetUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}