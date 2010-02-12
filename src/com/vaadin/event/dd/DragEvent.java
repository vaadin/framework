package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.Transferable;

public class DragEvent extends DragAndDropEvent {

    private HashMap<String, Object> responseData;

    public DragEvent(Transferable tr, TargetDetails details) {
        super(tr, details);
    }

    private static final long serialVersionUID = 7105802828455781246L;

    public void setResponseData(String key, Object value) {
        if (responseData != null) {
            responseData = new HashMap<String, Object>();
        }
        responseData.put(key, value);
    }

    /**
     * non-api, used by terminal
     * 
     * @return
     */
    public Map<String, Object> getResponseData() {
        return responseData;
    }

}
