package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ui.label.LabelConnector;
import com.vaadin.shared.ui.Connect;

/**
 * Connects server-side <code>IdTestLabel</code> component to client-side
 * {@link VIdTestLabel} component (#10179).
 * 
 */
@Connect(com.vaadin.tests.widgetset.server.IdTestLabel.class)
public class IdTestLabelConnector extends LabelConnector {

    @Override
    public VIdTestLabel getWidget() {
        return (VIdTestLabel) super.getWidget();
    }
}
