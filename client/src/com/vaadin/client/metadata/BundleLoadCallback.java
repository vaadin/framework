/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.client.metadata;

public interface BundleLoadCallback {
    public void loaded();

    public void failed(Throwable reason);
}
