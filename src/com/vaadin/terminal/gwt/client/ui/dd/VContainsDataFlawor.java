/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

final public class VContainsDataFlawor extends VAcceptCriterion {

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        String name = configuration.getStringAttribute("p");
        return drag.getTransferable().getDataFlawors().contains(name);
    }
}