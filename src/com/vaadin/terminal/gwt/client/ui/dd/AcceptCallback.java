package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ValueMap;

public interface AcceptCallback {

    /**
     * This method is called by {@link DragAndDropManager} if the
     * {@link DragEvent} is still active. Developer can update for example drag
     * icon or target emphasis based on the information returned from server
     * side. If the drag and drop operation ends or the
     * {@link AbstractDropHandler} has changed before response arrives, the
     * method is never called.
     * 
     * @param responseData
     */
    public void handleResponse(ValueMap responseData);

}
