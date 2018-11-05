package com.vaadin.tests.components.customcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class CustomComponentChildVisibility extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("In panel");
        label.setId("label");
        final CustomComponent cc = new CustomComponent(
                new Panel("In CustomComponent", label));

        Button hideButton = new Button("Hide CustomComponent");
        hideButton.addClickListener(event -> cc.setVisible(false));

        addComponent(cc);
        addComponent(hideButton);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
