package com.vaadin.event.dd;

import java.util.Map;

import com.vaadin.ui.Component;

/**
 * DropTarget is a marker interface for components supporting drop operations. A
 * component that wants to receive drop events should implement this interface
 * and provide a DropHandler which will handle the actual drop event.
 * 
 */
public interface DropTarget extends Component {

    public DropHandler getDropHandler();

    /**
     * Called before a drop operation to translate the drop data provided by the
     * client widget. Should return a DropData implementation with the new
     * values. If null is returned the terminal implementation will
     * automatically create a {@link DropTargetDetails} with all the client
     * variables.
     * <p>
     * If this method returns null the data from client side will be passed
     * through as-is without conversion.
     * 
     * @param rawVariables
     *            Parameters passed from the client side widget.
     * @return A DropData object with the translated data or null.
     */
    public DropTargetDetails translateDragDropDetails(
            Map<String, Object> clientVariables);

}