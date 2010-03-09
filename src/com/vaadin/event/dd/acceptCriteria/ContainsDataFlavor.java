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
import com.vaadin.terminal.gwt.client.ui.dd.VContainsDataFlavor;

/**
 * TODO Javadoc!
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VContainsDataFlavor.class)
public final class ContainsDataFlavor extends ClientSideCriterion {

    private String dataFlavorId;

    /**
     * TODO should support basic UIDL data types
     * 
     * @param dataFlawor
     * @param value
     */
    public ContainsDataFlavor(String dataFlawor) {
        dataFlavorId = dataFlawor;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("p", dataFlavorId);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return dragEvent.getTransferable().getDataFlavors().contains(
                dataFlavorId);
    }
}