/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public class Or implements AcceptCriterion {
    private AcceptCriterion f1;
    private AcceptCriterion f2;

    Or(AcceptCriterion f1, AcceptCriterion f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public boolean isClientSideVerifiable() {
        // TODO Auto-generated method stub
        return false;
    }

    public void paint(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return f1.accepts(dragEvent) || f2.accepts(dragEvent);
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }
}