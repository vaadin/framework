package com.vaadin.event;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.server.DragAndDropService;
import com.vaadin.ui.Component;

public class DragDropDetailsImpl implements DragDropDetails {

    private HashMap<String, Object> data = new HashMap<String, Object>();

    public DragDropDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    public Component getTarget() {
        return (Component) data.get(DragAndDropService.DROPTARGET_KEY);
    }
}
