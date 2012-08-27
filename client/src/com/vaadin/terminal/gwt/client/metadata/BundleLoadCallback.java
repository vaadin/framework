/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

public interface BundleLoadCallback {
    public void loaded();

    public void failed(Throwable reason);
}
