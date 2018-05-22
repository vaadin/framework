package com.vaadin.tests.design;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.declarative.Design;

public class DesignReadInConstructor extends CssLayout {

    public DesignReadInConstructor() {
        Design.read(
                getClass().getResourceAsStream("DesignReadInConstructor.html"),
                this);
    }
}
