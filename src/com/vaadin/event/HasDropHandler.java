package com.vaadin.event;

import java.util.Map;

import com.vaadin.ui.Component;

/**
 * Implementing component most commonly has also setDropHandler method, but not
 * polluting interface here as component might also have internal
 * AbstractDropHandler implementation.
 * 
 */
public interface HasDropHandler extends Component {
    public DropHandler getDropHandler();

    /**
     * TODO Consider using map-like type as return type that would be auto
     * created by terminal in case this implementation would return null. Would
     * enable using simple event details without server side class. Should at
     * least include the component that {@link HasDropHandler} -> would make it
     * possible for one drophandler to be used on multiple components.
     * 
     * @param rawVariables
     * @return
     */
    public Object getDragEventDetails(Map<String, Object> rawVariables);
}