package com.vaadin.tests.components.customcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class CustomComponentHideContent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label content = new Label("This is the content");
        CustomComponent customComponent = new CustomComponent(content);
        addComponent(customComponent);

        addComponent(new Button("Toggle content visibility",
                event -> content.setVisible(!content.isVisible())));
    }

    @Override
    protected String getTestDescription() {
        return "Hiding the composition root of a CustomComponent should not cause client-side exceptions";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9895);
    }

}
