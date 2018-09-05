package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

@JavaScript("analytics_connector.js")
public class Analytics extends AbstractJavaScriptExtension {

    public Analytics(UI ui, String account) {
        extend(ui);
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
}
