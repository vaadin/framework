/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VDropDetailEquals;

/**
 * Criteria for checking if drop target details contain the specific property
 * with the specific value.
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VDropDetailEquals.class)
public final class DropTargetDetailEquals extends ClientSideCriterion {

    private String propertyName;
    private String value;

    /**
     * TODO should support basic UIDL data types
     * 
     * @param propertyName
     * @param value
     */
    public DropTargetDetailEquals(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("p", propertyName);
        target.addAttribute("v", value);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        Object data = dragEvent.getDropTargetDetails().getData(propertyName);
        return value.equals(data);
    }
}