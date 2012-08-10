/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.extensions.AbstractExtensionConnector;
import com.vaadin.tests.extensions.BasicExtension;

@Connect(BasicExtension.class)
public class BasicExtensionTestConnector extends AbstractExtensionConnector {
    private ServerConnector target;

    @Override
    protected void extend(ServerConnector target) {
        this.target = target;
        appendMessage(" extending ");
    }

    private void appendMessage(String action) {
        String message = Util.getSimpleName(this) + action
                + Util.getSimpleName(target);

        DivElement element = Document.get().createDivElement();
        element.setInnerText(message);

        Document.get().getBody().insertFirst(element);
    }

    @Override
    public void onUnregister() {
        appendMessage(" removed for ");
    }
}
