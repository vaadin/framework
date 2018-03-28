package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public class ResponsiveStylesDesign extends VerticalLayout {

    protected Component collapsed;
    protected Component narrow;
    protected Component wide;

    public ResponsiveStylesDesign() {
        Design.read(this);
    }
}
