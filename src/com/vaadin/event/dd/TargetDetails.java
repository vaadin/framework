package com.vaadin.event.dd;

import java.io.Serializable;

public interface TargetDetails extends Serializable {

    public Object getData(String key);

    public Object setData(String key, Object value);

    public DropTarget getTarget();

}
