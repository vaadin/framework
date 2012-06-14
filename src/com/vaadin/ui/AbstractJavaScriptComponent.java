/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.JavaScriptCallbackHelper;

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
}
