/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;

public final class IsDatabound extends ClientSideCriterion {
    private static IsDatabound singleton = new IsDatabound();

    private IsDatabound() {
    }

    public static IsDatabound get() {
        return singleton;
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof DataBoundTransferable) {
            return ((DataBoundTransferable) dragEvent.getTransferable())
                    .getItemId() != null;
        }
        return false;
    }
}