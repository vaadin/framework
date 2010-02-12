/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Component;

public class ComponentFilter implements AcceptCriterion {
    private Component component;

    public ComponentFilter(Component component) {
        this.component = component;
    }

    public boolean isClientSideVerifiable() {
        return true;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", "component");
        target.addAttribute("component", component);
        target.endTag("-ac");
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof ComponentTransferable) {
            return ((ComponentTransferable) dragEvent.getTransferable())
                    .getSourceComponent() == component;
        } else {
            return false;
        }
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }
}