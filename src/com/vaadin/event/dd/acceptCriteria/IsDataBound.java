/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VDataBound;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VDataBound.class)
public final class IsDataBound extends ClientSideCriterion {
    private static IsDataBound singleton = new IsDataBound();

    private IsDataBound() {
    }

    public static IsDataBound get() {
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