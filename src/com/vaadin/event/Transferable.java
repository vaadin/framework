package com.vaadin.event;

import java.util.Collection;

public interface Transferable {

    public Object getData(String dataFlawor);

    public void setData(String dataFlawor, Object value);

    public Collection<String> getDataFlawors();

}
