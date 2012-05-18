/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.Connector;

public abstract class ConnectorClassBasedFactory<T> {
    public interface Creator<T> {
        public T create();
    }

    private Map<Class<? extends Connector>, Creator<? extends T>> creators = new HashMap<Class<? extends Connector>, Creator<? extends T>>();

    protected void addCreator(Class<? extends Connector> cls,
            Creator<? extends T> creator) {
        creators.put(cls, creator);
    }

    /**
     * Creates a widget using GWT.create for the given connector, based on its
     * {@link AbstractComponentConnector#getWidget()} return type.
     * 
     * @param connector
     * @return
     */
    public T create(Class<? extends Connector> connector) {
        Creator<? extends T> foo = creators.get(connector);
        if (foo == null) {
            throw new RuntimeException(getClass().getName()
                    + " could not find a creator for connector of type "
                    + connector.getName());
        }
        return foo.create();
    }

}
