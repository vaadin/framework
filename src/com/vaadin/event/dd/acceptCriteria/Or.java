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
import com.vaadin.terminal.gwt.client.ui.dd.VOr;

/**
 * A compound criterion that accepts the drag if any of its criterion accepts
 * it.
 * 
 * @see And
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VOr.class)
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

    public boolean accepts(DragAndDropEvent dragEvent) {
        for (AcceptCriterion crit : criteria) {
            if (crit.accepts(dragEvent)) {
                return true;
            }
        }
        return false;
    }

}