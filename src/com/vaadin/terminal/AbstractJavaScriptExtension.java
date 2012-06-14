/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.client.JavaScriptExtensionState;
import com.vaadin.ui.JavaScriptCallback;

public class AbstractJavaScriptExtension extends AbstractExtension {
    private JavaScriptCallbackHelper callbackHelper = new JavaScriptCallbackHelper(
            this);

    protected void registerCallback(String functionName,
            JavaScriptCallback javaScriptCallback) {
        callbackHelper.registerCallback(functionName, javaScriptCallback);
    }

    protected void invokeCallback(String name, Object... arguments) {
        callbackHelper.invokeCallback(name, arguments);
    }

    @Override
    public JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }
}
