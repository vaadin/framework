/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.JavascriptRpcHelper;

public class AbstractJavascriptComponent extends AbstractComponent {
    private JavascriptRpcHelper rpcHelper = new JavascriptRpcHelper(this);

    protected void registerCallback(String functionName,
            JavascriptCallback javascriptCallback) {
        rpcHelper.registerCallback(functionName, javascriptCallback);
    }

    protected void invokeCallback(String name, Object... arguments) {
        rpcHelper.invokeCallback(name, arguments);
    }
}
