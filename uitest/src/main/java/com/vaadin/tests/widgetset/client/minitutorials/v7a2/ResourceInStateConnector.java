package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.ResourceInStateComponent;

@Connect(ResourceInStateComponent.class)
public class ResourceInStateConnector extends AbstractComponentConnector {
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        String iconUrl = getResourceUrl("myIcon");

        if (iconUrl != null) {
            getWidget().setUrl(iconUrl);
        } else {
            getWidget().setUrl("");
        }

    }

    @Override
    public Image getWidget() {
        return (Image) super.getWidget();
    }
}
