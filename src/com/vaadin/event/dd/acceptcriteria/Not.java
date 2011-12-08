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
import com.vaadin.terminal.gwt.client.ui.dd.VNot;

/**
 * Criterion that wraps another criterion and inverts its return value.
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VNot.class)
public class Not extends ClientSideCriterion {

    private static final long serialVersionUID = 1131422338558613244L;
    private AcceptCriterion acceptCriterion;

    public Not(ClientSideCriterion acceptCriterion) {
        this.acceptCriterion = acceptCriterion;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        acceptCriterion.paint(target);
    }

    public boolean accept(DragAndDropEvent dragEvent) {
        return !acceptCriterion.accept(dragEvent);
    }

}