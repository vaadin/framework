package com.vaadin.tests.design.designroot;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class ExtendedDesignWithEmptyAnnotation
        extends DesignWithEmptyAnnotation {

    private final TextField customField = new TextField();

    public ExtendedDesignWithEmptyAnnotation() {
        super();
        customField.setPlaceholder("Something");
        addComponent(customField);

        ok.addClickListener(event -> Notification.show("OK"));

        CaNCEL.addClickListener(event -> Notification.show("cancel"));
    }
}
