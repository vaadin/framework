/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * TODO consider replacing this with Union
 * 
 */
public class And extends ClientSideCriterion {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private AcceptCriterion f1;
    private AcceptCriterion f2;

    public And(ClientSideCriterion f1, ClientSideCriterion f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    // @Override
    // public boolean isClientSideVerifiable() {
    // boolean a1 = f1 instanceof AcceptCriterion ? (f1)
    // .isClientSideVerifiable() : false;
    // boolean a2 = f2 instanceof AcceptCriterion ? (f2)
    // .isClientSideVerifiable() : false;
    // return a1 && a2;
    // }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        (f1).paint(target);
        (f2).paint(target);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return f1.accepts(dragEvent) && f2.accepts(dragEvent);
    }

}