/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

/**
 * A HashMap backed implementation of {@link DropTargetDetails} for terminal
 * implementation and for extension.
 * 
 * @since 6.3
 * 
 */
public class DropTargetDetailsImpl implements DropTargetDetails {

    private static final long serialVersionUID = -5099462771593036776L;
    private HashMap<String, Object> data = new HashMap<String, Object>();
    private DropTarget dropTarget;

    protected DropTargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public DropTargetDetailsImpl(Map<String, Object> rawDropData,
            DropTarget dropTarget) {
        this(rawDropData);
        this.dropTarget = dropTarget;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public Object setData(String key, Object value) {
        return data.put(key, value);
    }

    public DropTarget getTarget() {
        return dropTarget;
    }

}