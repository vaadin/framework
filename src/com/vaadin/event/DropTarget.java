package com.vaadin.event;

import com.vaadin.ui.Component;

/**
 * DropTarget is a marker interface for components supporting drop operations. A
 * component that wants to receive drop events should implement this interface
 * and provide a DropHandler which will handle the actual drop event.
 * 
 */
public interface DropTarget extends Component {

    public DropHandler getDropHandler();

}