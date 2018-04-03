package com.vaadin.tests.widgetset.client.extension;

import com.vaadin.client.extensions.AbstractEventTriggerExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.EventTriggerExtension;

@Connect(EventTriggerExtension.class)
public class EventTriggerExtensionConnector extends AbstractEventTriggerExtensionConnector{
    @Override
    protected native void trigger() /*-{
        alert("Trigger");
    }-*/;
}
