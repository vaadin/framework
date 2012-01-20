/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.PaintableMap;
import com.vaadin.terminal.gwt.client.UIDL;

final public class VIsOverId extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {

            String pid = configuration.getStringAttribute("s");
            VDropHandler currentDropHandler = VDragAndDropManager.get()
                    .getCurrentDropHandler();
            Paintable paintable = currentDropHandler.getPaintable();
            PaintableMap paintableMap = PaintableMap.get(currentDropHandler
                    .getApplicationConnection());

            String pid2 = paintableMap.getPid(paintable);
            if (pid2.equals(pid)) {
                Object searchedId = drag.getDropDetails().get("itemIdOver");
                String[] stringArrayAttribute = configuration
                        .getStringArrayAttribute("keys");
                for (String string : stringArrayAttribute) {
                    if (string.equals(searchedId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}