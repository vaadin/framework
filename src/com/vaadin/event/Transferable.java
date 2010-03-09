/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event;

import java.util.Collection;

import com.vaadin.ui.Component;

/**
 * TODO Javadoc!
 * 
 * @since 6.3
 */
public interface Transferable {

    public Object getData(String dataFlavor);

    public void setData(String dataFlavor, Object value);

    public Collection<String> getDataFlavors();

    /**
     * @return the component that created the Transferable
     */
    public Component getSourceComponent();

}
