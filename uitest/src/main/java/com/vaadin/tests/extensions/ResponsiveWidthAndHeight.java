package com.vaadin.tests.extensions;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

@Theme("tests-responsive")
public class ResponsiveWidthAndHeight extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout layout = new CssLayout();
        layout.addStyleName("width-and-height");
        layout.setSizeFull();
        setContent(layout);
        Responsive.makeResponsive(layout);

        layout.addComponent(new Label(
                "Resize the browser window in both dimensions to see the background color change."));
    }

    @Override
    protected String getTestDescription() {
        return "The CssLayout with both width-range and height-range defined";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13587;
    }
}
