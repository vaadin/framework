/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;

public class ComponentInStateState extends ComponentState {
    private Connector otherComponent;

    public Connector getOtherComponent() {
        return otherComponent;
    }

    public void setOtherComponent(Connector otherComponent) {
        this.otherComponent = otherComponent;
    }
}
