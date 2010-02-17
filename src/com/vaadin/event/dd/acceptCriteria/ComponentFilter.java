/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.ComponentTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VComponentFilter;
import com.vaadin.ui.Component;

@ClientCriterion(VComponentFilter.class)
public class ComponentFilter extends ClientSideCriterion {
    private Component[] component;

    public ComponentFilter(Component... component) {
        this.component = component;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("c", component.length);
        for (int i = 0; i < component.length; i++) {
            target.addAttribute("component" + i, component[i]);
        }
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof ComponentTransferable) {
            Component sourceComponent = ((ComponentTransferable) dragEvent
                    .getTransferable()).getSourceComponent();
            for (Component c : component) {
                if (c == sourceComponent) {
                    return true;
                }
            }
        }

        return false;
    }

}