package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextField;

public class PrimaryStyle extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Use a set of three common components as a test.
        final Label label = new Label("Test Label");
        label.setPrimaryStyleName("initial");
        label.setStyleName("state");
        addComponent(label);

        final Button button = new Button("Test Button");
        button.setPrimaryStyleName("initial");
        button.setStyleName("state");
        addComponent(button);

        final TextField tf = new TextField("Test TextField");
        tf.setPrimaryStyleName("initial");
        tf.setStyleName("state");
        addComponent(tf);

        Button updateButton = new Button("Update styles", event -> {
            label.setPrimaryStyleName("updated");
            label.setStyleName("correctly");

            button.setPrimaryStyleName("updated");
            button.setStyleName("correctly");

            tf.setPrimaryStyleName("updated");
            tf.setStyleName("correctly");
        });
        updateButton.setId("update-button");
        addComponent(updateButton);
    }

    @Override
    protected String getTestDescription() {
        return "Test that setPrimaryStyleName followed by setStyleName results in correct class names.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12190;
    }

}
