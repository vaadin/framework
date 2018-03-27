package com.vaadin.tests.design.nested;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Root design component
 *
 * @author Vaadin Ltd
 */
@DesignRoot("mydesignroot.html")
public class MyDesignRoot extends VerticalLayout {
    // should be assigned automatically
    public MyExtendedChildDesign childDesign;

    public MyDesignRoot() {
        Design.read(this);
    }
}
