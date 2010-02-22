package com.vaadin.event.dd;

import java.io.Serializable;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
public interface DropTargetDetails extends Serializable {

    public Object getData(String key);

    public Object setData(String key, Object value);

    public DropTarget getTarget();

}
