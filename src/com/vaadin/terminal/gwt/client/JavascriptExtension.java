/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.AbstractJavascriptExtension;
import com.vaadin.terminal.gwt.client.communication.HasJavascriptConnectorHelper;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;

@Connect(AbstractJavascriptExtension.class)
public class JavascriptExtension extends AbstractConnector implements
        HasJavascriptConnectorHelper {
    private final JavascriptConnectorHelper helper = new JavascriptConnectorHelper(
            this);

    @Override
    protected void init() {
        helper.init();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        helper.fireNativeStateChange();
    }

    public JavascriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }
}
