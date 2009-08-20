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
import com.vaadin.terminal.gwt.client.VUIDLBrowser;

public class VUnknownComponent extends Composite implements Paintable {

    com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
    Tree uidlTree;
    private VerticalPanel panel;

    public VUnknownComponent() {
        panel = new VerticalPanel();
        panel.add(caption);
        initWidget(panel);
        setStyleName("vaadin-unknown");
        caption.setStyleName("vaadin-unknown-caption");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        setCaption("Client faced an unknown component type. Unrendered UIDL:");
        if (uidlTree != null) {
            uidlTree.removeFromParent();
        }

        uidlTree = new VUIDLBrowser(uidl);
        panel.add(uidlTree);
    }

    public void setCaption(String c) {
        caption.setText(c);
    }
}
