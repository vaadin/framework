package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.TransferTranslator;
import com.vaadin.terminal.gwt.client.Paintable;

/**
 * Client side counterpart for Transferable in com.vaadin.event.Transferable
 * 
 */
public class Transferable {

    /**
     * @return the component
     */
    public Paintable getComponent() {
        return component;
    }

    /**
     * @param component
     *            the component to set
     */
    public void setComponent(Paintable component) {
        this.component = component;
    }

    /**
     * This is commonly actually a key to property id on client side than the
     * actual propertyId.
     * 
     * Translated by terminal and {@link TransferTranslator}
     * 
     * @return the propertyId
     */
    public String getPropertyId() {
        return (String) variables.get("propertyId");
    }

    /**
     * This is commonly actually a key to property id on client side than the
     * actual propertyId.
     * 
     * Translated by terminal and {@link TransferTranslator}
     * 
     * @param propertyId
     *            the propertyId to set
     */
    public void setPropertyId(String propertyId) {
        variables.put("propertyId", propertyId);
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return (String) variables.get("itemId");
    }

    /**
     * This is commonly actually a key to item id on client side than the actual
     * itemId.
     * 
     * Translated by terminal and {@link TransferTranslator}
     * 
     * @param itemId
     *            the itemId to set
     */
    public void setItemId(String itemId) {
        variables.put("itemId", itemId);
    }

    private Paintable component;

    public Object getData(String dataFlawor) {
        return variables.get(dataFlawor);
    }

    public void setData(String dataFlawor, Object value) {
        variables.put(dataFlawor, value);
    }

    public Collection<String> getDataFlawors() {
        return variables.keySet();
    }

    private final Map<String, Object> variables = new HashMap<String, Object>();

    /**
     * This method should only be called by {@link DragAndDropManager}.
     * 
     * @return data in this Transferable that needs to be moved to server.
     */
    public Map<String, Object> getVariableMap() {
        return variables;
    }

}
