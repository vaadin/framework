package com.vaadin.tests.design.designroot;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class ExtendedDesignWithEmptyAnnotationUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(new ExtendedDesignWithEmptyAnnotation());

    }

}
