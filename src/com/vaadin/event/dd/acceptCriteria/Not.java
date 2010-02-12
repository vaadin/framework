/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public class Not implements AcceptCriterion {
    private AcceptCriterion acceptCriterion;

    public Not(AcceptCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

    public boolean isClientSideVerifiable() {
        // TODO Auto-generated method stub
        return false;
    }

    public void paint(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return !acceptCriterion.accepts(dragEvent);
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }

}