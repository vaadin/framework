package com.vaadin.tests.design.designroot;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class ExtendedDesignWithEmptyAnnotation
        extends DesignWithEmptyAnnotation {

    private TextField customField = new TextField();

    public ExtendedDesignWithEmptyAnnotation() {
        super();
        customField.setInputPrompt("Something");
        addComponent(customField);

        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("OK");
            }
        });

        CaNCEL.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("cancel");
            }
        });
    }
}
