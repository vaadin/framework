/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptAll;

@ClientCriterion(VAcceptAll.class)
public final class AcceptAll extends ClientSideCriterion {

    private static AcceptCriterion singleton = new AcceptAll();

    private AcceptAll() {
    }

    public static AcceptCriterion get() {
        return singleton;
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return true;
    }
}