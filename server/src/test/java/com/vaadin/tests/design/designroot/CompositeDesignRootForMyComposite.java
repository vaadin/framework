package com.vaadin.tests.design.designroot;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Composite;
import com.vaadin.ui.declarative.Design;

@DesignRoot("MyComposite.html")
public class CompositeDesignRootForMyComposite extends Composite {

    public CompositeDesignRootForMyComposite() {
        Design.read(this);
    }
}
