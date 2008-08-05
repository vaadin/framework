/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomComponent extends SimplePanel implements Container {

    private static final String CLASSNAME = "i-customcomponent";

    public ICustomComponent() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        final UIDL child = uidl.getChildUIDL(0);
        if (child != null) {
            final Paintable p = client.getPaintable(child);
            if (p != getWidget()) {
                if (getWidget() != null) {
                    client.unregisterPaintable((Paintable) getWidget());
                    clear();
                }
                setWidget((Widget) p);
            }
            p.updateFromUIDL(child, client);
        }

    }

    public boolean hasChildComponent(Widget component) {
        if (getWidget() == component) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (hasChildComponent(oldComponent)) {
            clear();
            setWidget(newComponent);
        } else {
            throw new IllegalStateException();
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO custom component could handle its composition roots caption
    }

}
