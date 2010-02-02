package com.vaadin.event;

import com.vaadin.ui.Component;

public interface DragDropDetails {

    public Object get(String key);

    public Object put(String key, Object value);

    public Component getTarget();
}
