package com.vaadin.tests.layouts.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;

@SuppressWarnings("serial")
public class DataLocationInCustomLayout extends AbstractReindeerTestUI {

    protected static final String BUTTON_ID = "DataLocationInCustomLayoutTestButtonId";

    @Override
    protected Integer getTicketNumber() {
        return 8416;
    }

    @Override
    protected String getTestDescription() {
        return "A test for adding a component with the data-location attribute in a "
                + "CustomLayout: a button should be visible.";
    }

    @Override
    protected void setup(VaadinRequest request) {
        setTheme("tests-tickets");
        CustomLayout customLayout = new CustomLayout("Github8416");
        final Button button = new Button("Button");
        button.setId(BUTTON_ID);
        customLayout.addComponent(button, "dataloc");
        addComponent(customLayout);
    }

}
