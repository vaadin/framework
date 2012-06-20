/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class JavaScriptWidget extends Widget {
    public JavaScriptWidget() {
        setElement(Document.get().createDivElement());
    }

    public void showNoInitFound(ArrayList<String> attemptedNames) {
        String message = "Could not initialize JavaScriptConnector because no JavaScript init function was found. Make sure one of these functions are defined: <ul>";
        for (String name : attemptedNames) {
            message += "<li>" + name + "</li>";
        }
        message += "</ul>";

        getElement().setInnerHTML(message);
    }
}
