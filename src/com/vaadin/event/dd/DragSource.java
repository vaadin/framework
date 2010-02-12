package com.vaadin.event.dd;

import java.util.Map;

import com.vaadin.event.Transferable;

public interface DragSource {

    /**
     * DragSource may convert client side variables to meaningful values on
     * server side. For example in Selects we convert item identifiers to
     * generated string keys for the client side. Translators in Selects should
     * convert them back to item identifiers.
     * <p>
     * Translator should remove variables it handled from rawVariables. All non
     * handled variables are added to Transferable automatically by terminal.
     * 
     * <p>
     * 
     * @param transferable
     *            the Transferable object if one has been created for this drag
     *            and drop operation, null if Transferable is not yet
     *            instantiated
     * @param rawVariables
     * @return
     */
    public Transferable getTransferable(Transferable transferable,
            Map<String, Object> rawVariables);

}