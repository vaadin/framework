/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VAcceptAll extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        return true;
    }
}