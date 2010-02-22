package com.vaadin.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Component;

/**
 * TODO Javadoc!
 * 
 * @since 6.3
 */
public class TransferableImpl implements Transferable {
    private Map<String, Object> rawVariables = new HashMap<String, Object>();
    private Component sourceComponent;

    public TransferableImpl(Component sourceComponent,
            Map<String, Object> rawVariables) {
        this.sourceComponent = sourceComponent;
        this.rawVariables = rawVariables;
    }

    public Component getSourceComponent() {
        return sourceComponent;
    }

    public Object getData(String dataFlavor) {
        return rawVariables.get(dataFlavor);
    }

    public void setData(String dataFlavor, Object value) {
        rawVariables.put(dataFlavor, value);
    }

    public Collection<String> getDataFlavors() {
        return rawVariables.keySet();
    }

}
