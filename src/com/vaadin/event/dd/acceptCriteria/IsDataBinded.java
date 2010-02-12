/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.DataBindedTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public final class IsDataBinded implements AcceptCriterion {
    private static IsDataBinded singleton = new IsDataBinded();

    private IsDataBinded() {
    }

    public boolean isClientSideVerifiable() {
        return true;
    }

    public void paint(PaintTarget target) throws PaintException {
        target.startTag("-ac");
        target.addAttribute("name", "needsItemId");
        target.endTag("-ac");
    }

    public static IsDataBinded get() {
        return singleton;
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof DataBindedTransferable) {
            return ((DataBindedTransferable) dragEvent.getTransferable())
                    .getItemId() != null;
        }
        return false;
    }

    public void paintResponse(PaintTarget target) throws PaintException {
        // TODO Auto-generated method stub

    }
}