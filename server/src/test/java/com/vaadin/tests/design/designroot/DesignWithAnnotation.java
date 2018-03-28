package com.vaadin.tests.design.designroot;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot("DesignWithEmptyAnnotation.html")
public class DesignWithAnnotation extends VerticalLayout {

    public Button ok;
    public Button cancel;
    public Label preInitializedField = new Label("original");

    public DesignWithAnnotation() {
        Design.read(this);
    }
}
