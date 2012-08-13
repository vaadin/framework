/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.annotations.JavaScript;
import com.vaadin.terminal.AbstractJavaScriptExtension;
import com.vaadin.terminal.gwt.server.ClientConnector;
import com.vaadin.ui.Root;

@JavaScript("analytics_connector.js")
public class Analytics extends AbstractJavaScriptExtension {

    public Analytics(String account) {
        pushCommand("_setAccount", account);
    }

    public void trackPageview(String name) {
        pushCommand("_trackPageview", name);
    }

    private void pushCommand(Object... commandAndArguments) {
        // Cast to Object to use Object[] commandAndArguments as the first
        // varargs argument instead of as the full varargs argument array.
        callFunction("pushCommand", (Object) commandAndArguments);
    }

    protected void extend(Root root) {
        super.extend(root);
    }

    @Override
    protected Class<? extends ClientConnector> getSupportedParentType() {
        return Root.class;
    }
}
