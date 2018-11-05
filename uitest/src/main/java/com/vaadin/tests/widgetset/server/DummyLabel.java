package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.LabelState;
import com.vaadin.ui.AbstractComponent;

/**
 * Dummy component to cause {@link LabelState} to be used to test #8683
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class DummyLabel extends AbstractComponent {
    public DummyLabel(String text) {
        getState().setText(text);
    }

    @Override
    public LabelState getState() {
        return (LabelState) super.getState();
    }
}
