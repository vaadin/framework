package com.vaadin.ui;

import com.vaadin.shared.ui.embeddedbrowser.EmbeddedBrowserState;

/**
 * Component for embedding browser "iframe".
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
public class EmbeddedBrowser extends AbstractEmbedded {

    @Override
    public EmbeddedBrowserState getState() {
        return (EmbeddedBrowserState) super.getState();
    }
}
