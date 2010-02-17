/**
 * 
 */
package com.vaadin.event.dd.acceptCriteria;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VContainsDataFlawor;

@ClientCriterion(VContainsDataFlawor.class)
public final class ContainsDataFlawor extends ClientSideCriterion {

    private String dataFlaworId;

    /**
     * TODO should support basic UIDL data types
     * 
     * @param dataFlawor
     * @param value
     */
    public ContainsDataFlawor(String dataFlawor) {
        dataFlaworId = dataFlawor;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("p", dataFlaworId);
    }

    public boolean accepts(DragAndDropEvent dragEvent) {
        return dragEvent.getTransferable().getDataFlawors().contains(
                dataFlaworId);
    }
}