/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.user.client.ui.Image;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.URLReference;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.ResourceInStateComponent;

@Connect(ResourceInStateComponent.class)
public class ResourceInStateConnector extends AbstractComponentConnector {
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        URLReference icon = getState().getMyIcon();
        if (icon != null) {
            getWidget().setUrl(icon.getURL());
        } else {
            getWidget().setUrl("");
        }

    }

    @Override
    public ResourceInStateState getState() {
        return (ResourceInStateState) super.getState();
    }

    @Override
    public Image getWidget() {
        return (Image) super.getWidget();
    }
}
