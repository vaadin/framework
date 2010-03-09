/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.util.Map;

import com.vaadin.ui.Component;

/**
 * DropTarget is an interface for components supporting drop operations. A
 * component that wants to receive drop events should implement this interface
 * and provide a DropHandler which will handle the actual drop event.
 * 
 * @since 6.3
 */
public interface DropTarget extends Component {

    public DropHandler getDropHandler();

    /**
     * Called before a drop operation to translate the drop target details
     * provided by the client widget (drop target). Should return a DropData
     * implementation with the new values. If null is returned the terminal
     * implementation will automatically create a {@link DropTargetDetails} with
     * all the client variables.
     * 
     * @param rawVariables
     *            Parameters passed from the client side widget.
     * @return A DropTargetDetails object with the translated data or null to
     *         use a default implementation.
     */
    public DropTargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables);

}