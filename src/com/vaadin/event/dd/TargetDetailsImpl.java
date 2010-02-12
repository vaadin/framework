package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.server.DragAndDropService;

public class TargetDetailsImpl implements TargetDetails {

    private HashMap<String, Object> data = new HashMap<String, Object>();

    public TargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public Object setData(String key, Object value) {
        return data.put(key, value);
    }

    public DropTarget getTarget() {
        return (DropTarget) data.get(DragAndDropService.DROPTARGET_KEY);
    }
}