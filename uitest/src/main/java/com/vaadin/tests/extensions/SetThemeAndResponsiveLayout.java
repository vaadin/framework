package com.vaadin.tests.extensions;

import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class SetThemeAndResponsiveLayout extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSizeFull();
        CssLayout responsiveLayout = new CssLayout();
        responsiveLayout.addStyleName("width-and-height");
        responsiveLayout.setSizeFull();
        setContent(responsiveLayout);
        responsiveLayout.addComponent(new Label(
                "First set the theme using the button and then resize the browser window in both dimensions to see the background color change."));
        Button setThemeButton = new Button("Set theme");
        setThemeButton.addClickListener(event -> setTheme("tests-responsive"));
        responsiveLayout.addComponent(setThemeButton);
        Responsive.makeResponsive(responsiveLayout);
    }

    @Override
    protected String getTestDescription() {
        return "This test verifies that responsive works also when theme is set using setTheme method";
    };

    @Override
    protected Integer getTicketNumber() {
        return 15281;
    };

}
