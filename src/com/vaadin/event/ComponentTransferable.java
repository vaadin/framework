package com.vaadin.event;

import com.vaadin.ui.Component;

public interface ComponentTransferable extends Transferable {

    /**
     * @return the component that started the drag operation
     */
    public Component getSourceComponent();

}
