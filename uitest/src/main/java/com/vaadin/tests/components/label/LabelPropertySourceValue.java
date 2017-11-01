package com.vaadin.tests.components.label;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.Label;

public class LabelPropertySourceValue extends AbstractReindeerTestUI {
    private Label label;

    @Override
    public void setup(VaadinRequest request) {
        label = new Label("Hello Vaadin user");
        addComponent(label);
        Button button = new Button("Give label a new property data source...");
        button.addClickListener(event -> {
            ObjectProperty<String> p = new ObjectProperty<>(
                    "This text should appear on the label after clicking the button.");

            label.setPropertyDataSource(p);
        });
        addComponent(button);
        button = new Button("Remove data source",
                event -> label.setPropertyDataSource(null));
        addComponent(button);

        button = new Button("Set label value to 'foo'",
                event -> label.setValue("foo"));
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "The value should change by clicking the button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9618;
    }

}
