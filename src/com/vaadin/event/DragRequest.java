package com.vaadin.event;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager.DragEventType;

public class DragRequest {

    private DragEventType dragEventType;
    private Transferable transferable;
    private Map<String, Object> responseData;
    private Object eventDetails;

    public DragRequest(DragEventType dragEventType, Transferable transferable) {
        this.dragEventType = dragEventType;
        this.transferable = transferable;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public DragEventType getType() {
        return dragEventType;
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }

    /**
     * DropHanler can pass simple parameters back to client side.
     * 
     * TODO define which types are supported (most likely the same as in UIDL)
     * 
     * @param key
     * @param value
     */
    public void setResponseParameter(String key, Object value) {
        if (responseData == null) {
            responseData = new HashMap<String, Object>();
        }
        responseData.put(key, value);
    }

    public void setEventDetails(Object eventDetails) {
        this.eventDetails = eventDetails;
    }

    public Object getEventDetails() {
        return eventDetails;
    }

}
