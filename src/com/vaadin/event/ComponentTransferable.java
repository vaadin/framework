package com.vaadin.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Component;

public class ComponentTransferable implements Transferable {
    private Map<String, Object> rawVariables = new HashMap<String, Object>();
    private Component sourceComponent;

    public ComponentTransferable(Component sourceComponent,
            Map<String, Object> rawVariables) {
        this.sourceComponent = sourceComponent;
        this.rawVariables = rawVariables;
    }

    public Component getSourceComponent() {
        return sourceComponent;
    }

    public Object getData(String dataFlawor) {
        return rawVariables.get(dataFlawor);
    }

    public void setData(String dataFlawor, Object value) {
        rawVariables.put(dataFlawor, value);
    }

    public Collection<String> getDataFlawors() {
        return rawVariables.keySet();
    }

}
