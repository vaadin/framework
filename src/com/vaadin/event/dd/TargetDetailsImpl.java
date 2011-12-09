/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.util.HashMap;
import java.util.Map;

/**
 * A HashMap backed implementation of {@link TargetDetails} for terminal
 * implementation and for extension.
 * 
 * @since 6.3
 * 
 */
@SuppressWarnings("serial")
public class TargetDetailsImpl implements TargetDetails {

    private HashMap<String, Object> data = new HashMap<String, Object>();
    private DropTarget dropTarget;

    protected TargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public TargetDetailsImpl(Map<String, Object> rawDropData,
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