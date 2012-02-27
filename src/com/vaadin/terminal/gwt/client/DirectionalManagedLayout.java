/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.ui.ManagedLayout;

public interface DirectionalManagedLayout extends ManagedLayout {
    public void layoutVertically();

    public void layoutHorizontally();
}