/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.ui.JavascriptCallback;

public class AbstractJavascriptExtension extends AbstractExtension {
    private JavascriptRpcHelper rpcHelper = new JavascriptRpcHelper(this);

    protected void registerCallback(String functionName,
            JavascriptCallback javascriptCallback) {
        rpcHelper.registerCallback(functionName, javascriptCallback);
    }

    protected void invokeCallback(String name, Object... arguments) {
        rpcHelper.invokeCallback(name, arguments);
    }
}
