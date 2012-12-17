package com.vaadin.client.ui;

import com.vaadin.client.ComponentConnector;

/**
 * A focusable {@link ComponentConnector} implementing this interface will be
 * notified of change in browser history.
 * 
 * @author Vaadin Ltd
 */
public interface HistoryChangeActionListener extends ComponentConnector {

    /**
     * This method is called by UIConnector for the Paintable is currently
     * focused when browser history changes.
     * 
     * Eg. a field can update its possibly changed value to the server.
     */
    public void onHistoryChangeAction();
}
