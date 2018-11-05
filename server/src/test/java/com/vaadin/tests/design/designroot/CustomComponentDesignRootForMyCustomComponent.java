package com.vaadin.tests.design.designroot;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.declarative.Design;

@DesignRoot("MyCustomComponent.html")
public class CustomComponentDesignRootForMyCustomComponent
        extends CustomComponent {

    public CustomComponentDesignRootForMyCustomComponent() {
        Design.read(this);
    }
}
