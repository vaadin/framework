/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class IUnknownComponent extends Composite implements Paintable {

    com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
    Tree uidlTree = new Tree();

    public IUnknownComponent() {
        final VerticalPanel panel = new VerticalPanel();
        panel.add(caption);
        panel.add(uidlTree);
        initWidget(panel);
        setStyleName("itmill-unknown");
        caption.setStyleName("itmill-unknown-caption");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        setCaption("Client faced an unknown component type. Unrendered UIDL:");
        uidlTree.clear();
        uidlTree.addItem(uidl.dir());
    }

    public void setCaption(String c) {
        caption.setText(c);
    }
}
