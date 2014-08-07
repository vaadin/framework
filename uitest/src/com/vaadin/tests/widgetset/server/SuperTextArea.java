package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.superText.SuperTextAreaState;
import com.vaadin.ui.TextArea;

/**
 * @author artamonov
 * @version $Id$
 */
public class SuperTextArea extends TextArea {

    @Override
    public SuperTextAreaState getState() {
        return (SuperTextAreaState) super.getState();
    }
}