package com.vaadin.event.dd;

public interface TargetDetails {
    public Object getData(String key);

    public Object setData(String key, Object value);

    public DropTarget getTarget();

}
