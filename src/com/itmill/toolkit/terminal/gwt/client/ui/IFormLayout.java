/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * Two col Layout that places caption on left col and field on right col
 */
public class IFormLayout extends FlexTable implements Container {

    HashMap componentToCaption = new HashMap();
    private ApplicationConnection client;

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        int i = 0;
        for (final Iterator it = uidl.getChildIterator(); it.hasNext(); i++) {
            prepareCell(i, 1);
            final UIDL childUidl = (UIDL) it.next();
            final Paintable p = (Paintable) client.getWidget(childUidl);
            Caption c = (Caption) componentToCaption.get(p);
            if (c == null) {
                c = new Caption(p, client);
                componentToCaption.put(p, c);
            }
            final Paintable oldComponent = (Paintable) getWidget(i, 1);
            if (oldComponent == null) {
                setWidget(i, 1, (Widget) p);
            } else if (oldComponent != p) {
                client.unregisterPaintable(oldComponent);
                setWidget(i, 1, (Widget) p);
            }
            setWidget(i, 0, c);
            p.updateFromUIDL(childUidl, client);
        }
        i++;
        while (getRowCount() > i) {
            final Paintable p = (Paintable) getWidget(i, 1);
            client.unregisterPaintable(p);
            componentToCaption.remove(p);
            removeRow(i);
        }
    }

    public boolean hasChildComponent(Widget component) {
        return componentToCaption.containsKey(component);
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        int i;
        for (i = 0; i < getRowCount(); i++) {
            if (oldComponent == getWidget(i, 1)) {
                final Caption newCap = new Caption((Paintable) newComponent,
                        client);
                setWidget(i, 0, newCap);
                setWidget(i, 1, newComponent);
                client.unregisterPaintable((Paintable) oldComponent);
                break;
            }
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        final Caption c = (Caption) componentToCaption.get(component);
        if (c != null) {
            c.updateCaption(uidl);
        }
    }
}
