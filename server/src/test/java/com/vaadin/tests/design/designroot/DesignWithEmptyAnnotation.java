package com.vaadin.tests.design.designroot;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
public class DesignWithEmptyAnnotation extends VerticalLayout {

    protected Button ok;
    protected Button CaNCEL;
    protected Label preInitializedField = new Label("original");

    public DesignWithEmptyAnnotation() {
        Design.read(this);
    }
}
