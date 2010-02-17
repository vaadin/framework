/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VDropDetailEquals;

@ClientCriterion(VDropDetailEquals.class)
public final class DropDetailEquals extends ClientSideCriterion {

    private String propertyName;
    private String value;

    /**
     * TODO should support basic UIDL data types
     * 
     * @param propertyName
     * @param value
     */
    public DropDetailEquals(String propertyName, String value) {
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
        Object data = dragEvent.getDropTargetData().getData(propertyName);
        return value.equals(data);
    }
}