/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.ui.JavascriptCallback;

public class AbstractJavascriptExtension extends AbstractExtension {
    private JavascriptRpcHelper rpcHelper = new JavascriptRpcHelper(this);

    protected void registerRpc(JavascriptCallback javascriptCallback,
            String functionName) {
        rpcHelper.registerRpc(javascriptCallback, functionName);
    }

    protected void callRpcFunction(String name, Object... arguments) {
        rpcHelper.callRpcFunction(name, arguments);
    }
}
