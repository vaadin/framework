/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.VPaintableWidget;

/**
 * Used to detect Widget from widget tree that has {@link #getDropHandler()}
 * 
 * Decide whether to get rid of this class. If so, {@link VAbstractDropHandler}
 * must extend {@link VPaintableWidget}.
 * 
 */
public interface VHasDropHandler {
    public VDropHandler getDropHandler();
}
