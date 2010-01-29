package com.vaadin.event;

import com.vaadin.ui.Component;

public interface ComponentTransferrable extends Transferable {

    /**
     * @return the component where the drag operation started
     */
    public Component getSourceComponent();

}
