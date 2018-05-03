package com.vaadin.tests.design.designroot;

import com.vaadin.ui.TextField;

public class ExtendedDesignWithAnnotation extends DesignWithAnnotation {
    private TextField customField = new TextField();

    public ExtendedDesignWithAnnotation() {
        customField.setInputPrompt("Something");
        addComponent(customField);

    }
}
