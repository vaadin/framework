/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VDropDetailEquals extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        String name = configuration.getStringAttribute("p");
        String value = configuration.getStringAttribute("v");
        Object object = drag.getDropDetails().get(name);
        return value.equals(object);
    }
}