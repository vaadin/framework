/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.server.ResourceReference;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.ResourceInStateState;
import com.vaadin.ui.AbstractComponent;

public class ResourceInStateComponent extends AbstractComponent {
    @Override
    public ResourceInStateState getState() {
        return (ResourceInStateState) super.getState();
    }

    public void setMyIcon(Resource icon) {
        getState().setMyIcon(new ResourceReference(icon));
    }

    public Resource getMyIcon() {
        return ResourceReference.getResource(getState().getMyIcon());
    }
}
