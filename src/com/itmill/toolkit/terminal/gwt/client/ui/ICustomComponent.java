/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomComponent extends SimplePanel implements Container {

    private static final String CLASSNAME = "i-customcomponent";
    private String height;
    private ApplicationConnection client;
    private Paintable component;
    private boolean rendering;
    private String width;

    public ICustomComponent() {
        super();
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;
        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }
        this.client = client;

        final UIDL child = uidl.getChildUIDL(0);
        if (child != null) {
            final Paintable p = client.getPaintable(child);
            if (p != getWidget()) {
                if (getWidget() != null) {
                    client.unregisterPaintable((Paintable) getWidget());
                    clear();
                }
                setWidget((Widget) p);
                component = p;
            }
            p.updateFromUIDL(child, client);
        }
        rendering = false;
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
        // NOP, custom component dont render composition roots caption
    }

    public boolean requestLayout(Set<Paintable> child) {
        // TODO Auto-generated method stub
        return false;
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        return new RenderSpace(getElement().getOffsetWidth(), getElement()
                .getOffsetHeight());
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (!height.equals(this.height)) {
            this.height = height;
            if (!rendering) {
                client.runDescendentsLayout(this);
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (!width.equals(this.width)) {
            this.width = width;
            if (!rendering) {
                client.runDescendentsLayout(this);
            }
        }
    }

}
