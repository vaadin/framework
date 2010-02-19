package com.vaadin.event;

import java.util.Collection;

import com.vaadin.ui.Component;

public interface Transferable {

    public Object getData(String dataFlawor);

    public void setData(String dataFlawor, Object value);

    public Collection<String> getDataFlawors();

    /**
     * @return the component that created the Transferable
     */
    public Component getSourceComponent();

}
