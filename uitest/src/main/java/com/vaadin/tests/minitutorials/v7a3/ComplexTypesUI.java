package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class ComplexTypesUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        ComplexTypesComponent complexTypesComponent = new ComplexTypesComponent();
        complexTypesComponent.sendComplexTypes();
        setContent(complexTypesComponent);
    }

}
