package com.vaadin.tests.widgetset.client.superText;

import com.vaadin.client.ui.textarea.TextAreaConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.SuperTextArea;

/**
 * @author artamonov
 * @version $Id$
 */
@Connect(SuperTextArea.class)
public class SuperTextAreaConnector extends TextAreaConnector {

    // @DelegateToWidget will not work with overridden state
    @Override
    public SuperTextAreaState getState() {
        return (SuperTextAreaState) super.getState();
    }
}