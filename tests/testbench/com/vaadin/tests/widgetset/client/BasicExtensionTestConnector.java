/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.extensions.AbstractExtensionConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.tests.extensions.BasicExtension;

@Connect(BasicExtension.class)
public class BasicExtensionTestConnector extends AbstractExtensionConnector {
    @Override
    protected void extend(ServerConnector target) {
        String message = Util.getSimpleName(this) + " extending "
                + Util.getSimpleName(target);

        DivElement element = Document.get().createDivElement();
        element.setInnerText(message);

        Document.get().getBody().insertFirst(element);
    }
}
