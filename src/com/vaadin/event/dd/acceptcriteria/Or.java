/*
@VaadinApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptcriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * A compound criterion that accepts the drag if any of its criterion accepts
 * it.
 * 
 * @see And
 * 
 * @since 6.3
 * 
 */
public class Or extends ClientSideCriterion {
    private static final long serialVersionUID = 1L;
    private AcceptCriterion criteria[];

    /**
     * @param criteria
     *            the criteria of which the Or criteria will be composed
     */
    public Or(ClientSideCriterion... criteria) {
        this.criteria = criteria;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (AcceptCriterion crit : criteria) {
            crit.paint(target);
        }
    }

    @Override
    public boolean accept(DragAndDropEvent dragEvent) {
        for (AcceptCriterion crit : criteria) {
            if (crit.accept(dragEvent)) {
                return true;
            }
        }
        return false;
    }

}