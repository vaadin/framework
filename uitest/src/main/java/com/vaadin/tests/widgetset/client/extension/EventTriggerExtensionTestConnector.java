package com.vaadin.tests.widgetset.client.extension;

import java.util.logging.Logger;

import com.vaadin.client.extensions.AbstractEventTriggerExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.EventTriggerExtensionTest;

@Connect(EventTriggerExtensionTest.class)
public class EventTriggerExtensionTestConnector extends AbstractEventTriggerExtensionConnector{
    Logger logger = Logger
            .getLogger(EventTriggerExtensionTestConnector.class.getSimpleName());
    @Override
    protected void trigger() {
        logger.warning("TRIGGERED!");
    }
}
