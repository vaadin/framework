package com.vaadin.event.dd;

import java.io.Serializable;
import java.util.Map;

import com.vaadin.event.Transferable;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
public interface DragSource extends Serializable {

    /**
     * DragSource may convert client side variables to meaningful values on
     * server side. For example Tree converts item identifiers to generated
     * string keys for the client side. Translators in Selects should convert
     * them back to item identifiers.
     * 
     * <p>
     * Translator should remove variables it handled from rawVariables. All non
     * handled variables are added to Transferable automatically by terminal.
     * </p>
     * 
     * <p>
     * 
     * @since 6.3
     * @param rawVariables
     * @return the drag source related transferable
     */
    public Transferable getTransferable(Map<String, Object> rawVariables);

}