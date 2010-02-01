package com.vaadin.event;

import com.vaadin.ui.Component;

public interface ComponentTransferable extends Transferable {

    /**
     * @return the component where the drag operation started
     */
    public Component getSourceComponent();

}
