package com.vaadin.tests.components.label;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.WrappedRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class LabelPropertySourceValue extends AbstractTestUI implements
        Button.ClickListener {
    private Label label;

    @Override
    public void setup(WrappedRequest request) {
        label = new Label("Hello Vaadin user");
        Button button = new Button("Give label a new property data source...");
        button.addClickListener(this);

        addComponent(label);
        addComponent(button);
    }

    public void buttonClick(ClickEvent event) {
        ObjectProperty<String> p = new ObjectProperty<String>(
                "This text should appear on the label after clicking the button.");

        label.setPropertyDataSource(p);
        //
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