package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.TextField;

/**
 * Test UI for default contrast color value.
 *
 * @author Vaadin Ltd
 */
@Theme("tests-valo-contrast")
public class ContrastFontColor extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextField field = new TextField();
        addComponent(field);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14793;
    }

    @Override
    protected String getTestDescription() {
        return "Provide a variable for default contrast value in valo-font-color function.";
    }

}
