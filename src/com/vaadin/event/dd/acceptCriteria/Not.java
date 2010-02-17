/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VNot;

@ClientCriterion(VNot.class)
public class Not extends ClientSideCriterion {

    private AcceptCriterion acceptCriterion;

    public Not(ClientSideCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return !acceptCriterion.accepts(dragEvent);
    }

}