package com.vaadin.tests.design.designroot;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

@DesignRoot("DesignWithEmptyAnnotation.html")
public class CompositeDesignRootForVerticalLayout extends Composite {

    public Button ok;
    public Button cancel;
    public Label preInitializedField = new Label("original");

    public CompositeDesignRootForVerticalLayout() {
        Design.read(this);
    }
}
