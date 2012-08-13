/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.AbstractJavaScriptExtension;
import com.vaadin.terminal.gwt.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.terminal.gwt.client.extensions.AbstractExtensionConnector;

@Connect(AbstractJavaScriptExtension.class)
public final class JavaScriptExtension extends AbstractExtensionConnector
        implements HasJavaScriptConnectorHelper {
    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this);

    @Override
    protected void init() {
        super.init();
        helper.init();
    }

    @Override
    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }

    @Override
    public JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }
}
