package com.vaadin.tests.design.nested;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

/**
 * Child design component
 *
 * @author Vaadin Ltd
 */
@DesignRoot("mychilddesign.html")
public class MyChildDesign extends HorizontalLayout {
    public Label childLabel;
    public MyChildDesignCustomComponent childCustomComponent;

    public MyChildDesign() {
        Design.read(this);
        childLabel.setDescription("added in constructor");
    }
}
