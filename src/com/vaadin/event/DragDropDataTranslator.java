package com.vaadin.event;

import java.util.Map;

/**
 * DragDropDataTranslator provides a method for translating drop data from a
 * client side widget to server side values. This interface is optional for drop
 * operations and only need to be implemented if translation is necessary. If
 * this is not implemented the data will be passed through as-is without
 * conversion.
 */
public interface DragDropDataTranslator {
    /**
     * Called before a drop operation to translate the drop data provided by the
     * client widget. Should return a DropData implementation with the new
     * values. If null is returned the {@link DragDropHandler} will
     * automatically create a DropData with all the client variables.
     * 
     * @param rawVariables
     *            Parameters passed from the client side widget.
     * @return A DropData object with the translated data or null.
     */
    public DragDropDetails translateDragDropDetails(
            Map<String, Object> clientVariables);

}