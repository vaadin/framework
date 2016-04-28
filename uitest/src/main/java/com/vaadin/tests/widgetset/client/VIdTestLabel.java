package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ui.VLabel;

/**
 * Client-side implementation for IdTestLabel (#10179).
 * 
 */
public class VIdTestLabel extends VLabel {

    public VIdTestLabel() {
        super();
        getElement().setId("default10179");
    }
}
