package com.vaadin.tests.components.label;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

public class LabelPropertySourceValue extends AbstractTestUI {
    private Label label;

    @Override
    public void setup(VaadinRequest request) {
        label = new Label("Hello Vaadin user");
        addComponent(label);
        Button button = new Button("Give label a new property data source...");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ObjectProperty<String> p = new ObjectProperty<String>(
                        "This text should appear on the label after clicking the button.");

                label.setPropertyDataSource(p);
            }
        });
        addComponent(button);
        button = new Button("Remove data source", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                label.setPropertyDataSource(null);
            }
        });
        addComponent(button);

        button = new Button("Set label value to 'foo'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                label.setValue("foo");
            }
        });
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
