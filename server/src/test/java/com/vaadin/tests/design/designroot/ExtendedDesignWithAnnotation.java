package com.vaadin.tests.design.designroot;

import com.vaadin.ui.TextField;

public class ExtendedDesignWithAnnotation extends DesignWithAnnotation {
    private final TextField customField = new TextField();

    public ExtendedDesignWithAnnotation() {
        customField.setPlaceholder("Something");
        addComponent(customField);

    }
}
