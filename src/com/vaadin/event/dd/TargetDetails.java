/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.ui.Tree.TreeTargetDetails;

/**
 * TargetDetails wraps drop target related information about
 * {@link DragAndDropEvent}.
 * <p>
 * When a TargetDetails object is used in {@link DropHandler} it is often
 * preferable to cast the TargetDetails to an implementation provided by
 * DropTarget like {@link TreeTargetDetails}. They often provide a better typed,
 * drop target specific API.
 * 
 * @since 6.3
 * 
 */
public interface TargetDetails extends Serializable {

    /**
     * Gets target data associated with the given string key
     * 
     * @param key
     * @return The data associated with the key
     */
    public Object getData(String key);

    /**
     * @return the drop target on which the {@link DragAndDropEvent} happened.
     */
    public DropTarget getTarget();

}
