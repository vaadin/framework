/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.ui.Tree.TreeDropTargetDetails;

/**
 * DropTargetDetails wraps drop target related information about
 * {@link DragAndDropEvent}.
 * <p>
 * When a DropTargetDetails object is used in {@link DropHandler} it is often
 * preferable to cast the DropTargetDetail to an implementation provided by
 * DropTarget like {@link TreeDropTargetDetails}. They often provide better
 * typed, drop target specific API.
 * 
 * @since 6.3
 * 
 */
public interface DropTargetDetails extends Serializable {

    /**
     * Gets target data associated to given string key
     * 
     * @param key
     * @return
     */
    public Object getData(String key);

    /**
     * @return the drop target on which the {@link DragAndDropEvent} happened.
     */
    public DropTarget getTarget();

}
