package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;

public interface ActionOwner {

    /**
     * @return Array of IActions
     */
    public Action[] getActions();

    public ApplicationConnection getClient();

    public String getPaintableId();

}
