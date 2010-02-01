package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ValueMap;

public interface VAcceptCallback {

    /**
     * This method is called by {@link VDragAndDropManager} if the
     * {@link VDragEvent} is still active. Developer can update for example drag
     * icon or target emphasis based on the information returned from server
     * side. If the drag and drop operation ends or the
     * {@link VAbstractDropHandler} has changed before response arrives, the
     * method is never called.
     * 
     * @param responseData
     */
    public void handleResponse(ValueMap responseData);

}
