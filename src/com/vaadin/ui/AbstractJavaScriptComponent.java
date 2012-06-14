/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.JavaScriptCallbackHelper;
import com.vaadin.terminal.gwt.client.ui.JavaScriptComponentState;

public class AbstractJavaScriptComponent extends AbstractComponent {
    private JavaScriptCallbackHelper callbackHelper = new JavaScriptCallbackHelper(
            this);

    protected void registerCallback(String functionName,
            JavaScriptCallback javascriptCallback) {
        callbackHelper.registerCallback(functionName, javascriptCallback);
    }

    protected void invokeCallback(String name, Object... arguments) {
        callbackHelper.invokeCallback(name, arguments);
    }

    @Override
    public JavaScriptComponentState getState() {
        return (JavaScriptComponentState) super.getState();
    }
}
