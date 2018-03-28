package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20URI%20or%20
 * parameters%20or%20screen%20size%20when%20initializing%20an%20application
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UsingXyzWhenInitializing extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        String name = request.getParameter("name");
        if (name == null) {
            name = "Unknown";
        }

        layout.addComponent(new Label("Hello " + name));

        String pathInfo = request.getPathInfo();
        if ("/viewSource".equals(pathInfo)) {
            layout.addComponent(new Label("This is the source"));
        } else {
            layout.addComponent(new Label("Welcome to my application"));
        }

        WebBrowser browser = getPage().getWebBrowser();
        String resolution = "Your browser window on startup was "
                + browser.getScreenWidth() + "x" + browser.getScreenHeight();
        if (browser.getScreenWidth() > 1024) {
            layout.addComponent(
                    new Label("The is the large version of the application. "
                            + resolution));
        } else {
            layout.addComponent(
                    new Label("This is the small version of the application. "
                            + resolution));
        }
    }

}
