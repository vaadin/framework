/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.terminal.JavascriptRpcHelper;

public class AbstractJavascriptComponent extends AbstractComponent {
    private JavascriptRpcHelper rpcHelper = new JavascriptRpcHelper(this);

    protected void registerRpc(JavascriptCallback javascriptCallback,
            String functionName) {
        rpcHelper.registerRpc(javascriptCallback, functionName);
    }

    protected void callRpcFunction(String name, Object... arguments) {
        rpcHelper.callRpcFunction(name, arguments);
    }
}
