package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;

public interface ActionOwner {

    /**
     * @return Array of IActions
     */
    public Action[] getActions();

    public ApplicationConnection getClient();

    public String getPaintableId();

}
