/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.RunAsyncCallback;

/** A helper class used by WidgetMap implementation. Used by the generated code. */
abstract class WidgetLoader implements RunAsyncCallback {

    public void onFailure(Throwable reason) {
        ApplicationConfiguration.endWidgetLoading();
    }

    public void onSuccess() {
        addInstantiator();
        ApplicationConfiguration.endWidgetLoading();
    }

    abstract void addInstantiator();
}
