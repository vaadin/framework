/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * Criterion type that joins two or more {@link ClientSideCriterion} together
 * and validates that all sub criterion validates.
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(com.vaadin.terminal.gwt.client.ui.dd.VAnd.class)
public class And extends ClientSideCriterion {

    private ClientSideCriterion[] f1;

    /**
     * 
     * @param f1
     *            ClientSideCriterion that must match
     */
    public And(ClientSideCriterion... f1) {
        this.f1 = f1;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (ClientSideCriterion crit : f1) {
            crit.paint(target);
        }
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        for (ClientSideCriterion crit : f1) {
            if (!crit.accepts(dragEvent)) {
                return false;
            }
        }
        return true;
    }

}