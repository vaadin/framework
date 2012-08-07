/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class ComplexTypesRoot extends Root {

    @Override
    protected void init(WrappedRequest request) {
        ComplexTypesComponent complexTypesComponent = new ComplexTypesComponent();
        complexTypesComponent.sendComplexTypes();
        addComponent(complexTypesComponent);
    }

}
