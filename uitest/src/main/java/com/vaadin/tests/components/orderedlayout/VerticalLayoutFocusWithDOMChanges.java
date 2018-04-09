package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.TextField;

public class VerticalLayoutFocusWithDOMChanges extends AbstractReindeerTestUI
        implements ValueChangeListener {

    Button dummyButton = new Button("Just a button");
    TextField listenedTextField = new TextField();
    TextField changingTextField = new TextField();

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        setSizeFull();
        listenedTextField.addValueChangeListener(this);
        listenedTextField.setImmediate(true);
        changingTextField.setImmediate(true);
        content.addComponent(dummyButton);
        content.addComponent(listenedTextField);
        content.addComponent(changingTextField);
        content.setMargin(true);
        content.setSpacing(true);
        setContent(content);
    }

    @Override
    protected String getTestDescription() {
        return "Check that creating or removing caption wrap doesn't lose focus";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12967;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        changingTextField.setRequired(!changingTextField.isRequired());
    }

}
