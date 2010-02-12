package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.UIDL;

public interface VDragEventServerCallback {

    public void handleResponse(boolean accepted, UIDL response);

}
