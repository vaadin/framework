/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public final class AcceptAll implements AcceptCriterion {

    private static AcceptCriterion singleton = new AcceptAll();

    private AcceptAll() {
    }

    public boolean isClientSideVerifiable() {
        return true;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", getClass().getCanonicalName());
        target.endTag("-ac");
    }

    public static AcceptCriterion get() {
        return singleton;
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return true;
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }
}