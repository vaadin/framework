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
        String t = configuration.hasAttribute("t") ? configuration
                .getStringAttribute("t").intern() : "s";
        Object value = null;
        if (t == "s") {
            value = configuration.getStringAttribute("v");
        } else if (t == "b") {
            value = configuration.getBooleanAttribute("v");
        }
        if (value != null) {
            Object object = drag.getDropDetails().get(name);
            return value.equals(object);
        } else {
            return false;
        }

    }
}