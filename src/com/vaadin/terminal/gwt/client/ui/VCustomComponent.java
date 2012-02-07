/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.Util;

public class VCustomComponent extends SimplePanel implements Container {

    private static final String CLASSNAME = "v-customcomponent";
    private String height;
    ApplicationConnection client;
    boolean rendering;
    private String width;
    RenderSpace renderSpace = new RenderSpace();

    public VCustomComponent() {
        super();
        setStyleName(CLASSNAME);
    }

    boolean updateDynamicSize() {
        boolean updated = false;
        if (isDynamicWidth()) {
            int childWidth = Util.getRequiredWidth(getWidget());
            getElement().getStyle().setPropertyPx("width", childWidth);
            updated = true;
        }
        if (isDynamicHeight()) {
            int childHeight = Util.getRequiredHeight(getWidget());
            getElement().getStyle().setPropertyPx("height", childHeight);
            updated = true;
        }

        return updated;
    }

    protected boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    protected boolean isDynamicHeight() {
        return height == null || height.equals("");
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

    public boolean requestLayout(Set<Widget> children) {
        // If a child grows in size, it will not necessarily be calculated
        // correctly unless we remove previous size definitions
        if (isDynamicWidth()) {
            getElement().getStyle().setProperty("width", "");
        }
        if (isDynamicHeight()) {
            getElement().getStyle().setProperty("height", "");
        }

        return !updateDynamicSize();
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        return renderSpace;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        renderSpace.setHeight(getElement().getOffsetHeight());

        if (!height.equals(this.height)) {
            this.height = height;
            if (!rendering) {
                client.handleComponentRelativeSize(getWidget());
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        renderSpace.setWidth(getElement().getOffsetWidth());

        if (!width.equals(this.width)) {
            this.width = width;
            if (!rendering) {
                client.handleComponentRelativeSize(getWidget());
            }
        }
    }

}
