/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public class And implements AcceptCriterion {
    private AcceptCriterion f1;
    private AcceptCriterion f2;

    public And(AcceptCriterion f1, AcceptCriterion f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public boolean isClientSideVerifiable() {
        boolean a1 = f1 instanceof AcceptCriterion ? (f1)
                .isClientSideVerifiable() : false;
        boolean a2 = f2 instanceof AcceptCriterion ? (f2)
                .isClientSideVerifiable() : false;
        return a1 && a2;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", "and");
        (f1).paint(target);
        (f2).paint(target);
        target.endTag("-ac");
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return f1.accepts(dragEvent) && f2.accepts(dragEvent);
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }
}