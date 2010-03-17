/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VContainsDataFlavor;

/**
 * A Criterion that checks whether {@link Transferable} contains given data
 * flavor. The developer might for example accept the incoming data only if it
 * contains "Url" or "Text".
 * 
 * @since 6.3
 */
@ClientCriterion(VContainsDataFlavor.class)
public class ContainsDataFlavor extends ClientSideCriterion {

    private String dataFlavorId;

    /**
     * Constructs a new instance of {@link ContainsDataFlavor}.
     * 
     * @param dataFlawor
     *            the type of data that will be checked from
     *            {@link Transferable}
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

    @Override
    protected String getIdentifier() {
        // extending classes use client side implementation from this class
        return ContainsDataFlavor.class.getCanonicalName();
    }
}