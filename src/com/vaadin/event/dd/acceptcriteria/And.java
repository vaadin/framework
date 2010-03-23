/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptcriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * A compound criterion that accepts the drag if all of its criteria accepts the
 * drag.
 * 
 * @see Or
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(com.vaadin.terminal.gwt.client.ui.dd.VAnd.class)
public class And extends ClientSideCriterion {

    private static final long serialVersionUID = -5242574480825471748L;
    protected ClientSideCriterion[] criteria;

    /**
     * 
     * @param criteria
     *            criteria of which the And criterion will be composed
     */
    public And(ClientSideCriterion... criteria) {
        this.criteria = criteria;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (ClientSideCriterion crit : criteria) {
            crit.paint(target);
        }
    }

    public boolean accept(DragAndDropEvent dragEvent) {
        for (ClientSideCriterion crit : criteria) {
            if (!crit.accept(dragEvent)) {
                return false;
            }
        }
        return true;
    }

}