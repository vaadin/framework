package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

public interface VAcceptCriteria {

    public boolean accept(VDragEvent drag, UIDL configuration);

}
