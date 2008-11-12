package com.itmill.toolkit.terminal.gwt.client.ui.layout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.MarginInfo;

public abstract class CellBasedLayout extends ComplexPanel implements Container {

    protected Map<Widget, ChildComponentContainer> widgetToComponentContainer = new HashMap<Widget, ChildComponentContainer>();

    protected ApplicationConnection client = null;

    private Element root;

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    protected final Margins marginsFromCSS = new Margins(10, 10, 10, 10);
    protected final Margins activeMargins = new Margins(0, 0, 0, 0);
    protected MarginInfo activeMarginsInfo = new MarginInfo(false, false,
            false, false);

    protected boolean spacingEnabled = false;
    protected final Spacing spacingFromCSS = new Spacing(12, 12);
    protected final Spacing activeSpacing = new Spacing(0, 0);

    private boolean dynamicWidth;

    private boolean dynamicHeight;

    private Element clearElement;

    public static class Spacing {

        public int hSpacing = 0;
        public int vSpacing = 0;

        public Spacing(int hSpacing, int vSpacing) {
            this.hSpacing = hSpacing;
            this.vSpacing = vSpacing;
        }

        @Override
        public String toString() {
            return "Spacing [hSpacing=" + hSpacing + ",vSpacing=" + vSpacing
                    + "]";
        }

    }

    public CellBasedLayout() {
        super();

        setElement(DOM.createDiv());
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");

        root = DOM.createDiv();
        DOM.setStyleAttribute(root, "width", "500%");
        getElement().appendChild(root);

        clearElement = DOM.createDiv();
        DOM.setStyleAttribute(clearElement, "width", "0px");
        DOM.setStyleAttribute(clearElement, "height", "0px");
        DOM.setStyleAttribute(clearElement, "clear", "both");
        DOM.setStyleAttribute(clearElement, "overflow", "hidden");

        DOM.appendChild(root, clearElement);

    }

    public boolean hasChildComponent(Widget component) {
        return widgetToComponentContainer.containsKey(component);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;

        // Only non-cached UIDL:s can introduce changes
        if (uidl.getBooleanAttribute("cached")) {
            return;
        }

        /*
         * This must be called before size so that setWidth/setHeight is aware
         * of the margins in use.
         */
        handleMarginsAndSpacing(uidl);

        /*
         * This call should be made first. Ensure correct implementation, handle
         * size etc.
         */
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        handleDynamicDimensions(uidl);

    }

    private void handleDynamicDimensions(UIDL uidl) {
        String w = uidl.hasAttribute("width") ? uidl
                .getStringAttribute("width") : "";

        String h = uidl.hasAttribute("height") ? uidl
                .getStringAttribute("height") : "";

        if (w.equals("")) {
            dynamicWidth = true;
        } else {
            dynamicWidth = false;
        }

        if (h.equals("")) {
            dynamicHeight = true;
        } else {
            dynamicHeight = false;
        }

    }

    protected void addOrMoveChild(ChildComponentContainer childComponent,
            int position) {
        widgetToComponentContainer.put(childComponent.getWidget(),
                childComponent);
        super.insert(childComponent, root, position, true);

    }

    protected ChildComponentContainer getComponentContainer(Widget child) {
        return widgetToComponentContainer.get(child);
    }

    protected boolean isDynamicWidth() {
        return dynamicWidth;
    }

    protected boolean isDynamicHeight() {
        return dynamicHeight;
    }

    private void handleMarginsAndSpacing(UIDL uidl) {
        MarginInfo newMargins = new MarginInfo(uidl.getIntAttribute("margins"));
        updateMargins(newMargins);
        boolean spacing = uidl.getBooleanAttribute("spacing");
        updateSpacing(spacing);
    }

    private void updateSpacing(boolean spacing) {
        if (spacing != spacingEnabled) {
            spacingEnabled = spacing;
            if (spacing) {
                activeSpacing.hSpacing = spacingFromCSS.hSpacing;
                activeSpacing.vSpacing = spacingFromCSS.vSpacing;
            } else {
                activeSpacing.hSpacing = 0;
                activeSpacing.vSpacing = 0;
            }
        }

    }

    private void updateMargins(MarginInfo newMarginInfo) {
        // Update active margins
        activeMarginsInfo = newMarginInfo;
        if (newMarginInfo.hasTop()) {
            activeMargins.setMarginTop(marginsFromCSS.getMarginTop());
        } else {
            activeMargins.setMarginTop(0);
        }
        if (newMarginInfo.hasBottom()) {
            activeMargins.setMarginBottom(marginsFromCSS.getMarginBottom());
        } else {
            activeMargins.setMarginBottom(0);
        }
        if (newMarginInfo.hasLeft()) {
            activeMargins.setMarginLeft(marginsFromCSS.getMarginLeft());
        } else {
            activeMargins.setMarginLeft(0);
        }
        if (newMarginInfo.hasRight()) {
            activeMargins.setMarginRight(marginsFromCSS.getMarginRight());
        } else {
            activeMargins.setMarginRight(0);
        }

        DOM.setStyleAttribute(root, "marginLeft", activeMargins.getMarginLeft()
                + "px");
        DOM.setStyleAttribute(root, "marginRight", activeMargins
                .getMarginRight()
                + "px");
        DOM.setStyleAttribute(root, "marginTop", activeMargins.getMarginTop()
                + "px");
        DOM.setStyleAttribute(root, "marginBottom", activeMargins
                .getMarginBottom()
                + "px");

    }

    protected boolean measureMarginsAndSpacing(String styleName,
            String marginTopLeftStyleNames, String marginBottomRightStyleNames,
            String spacingStyleNames) {
        if (!isAttached()) {
            return false;
        }

        Element measurement = DOM.createDiv();
        DOM.setStyleAttribute(measurement, "position", "absolute");
        DOM.setStyleAttribute(measurement, "width", "1px");
        DOM.setStyleAttribute(measurement, "height", "1px");
        DOM.setStyleAttribute(measurement, "visibility", "hidden");

        root.appendChild(measurement);

        // Measure top and left margins (actually CSS padding)
        measurement.setClassName(marginTopLeftStyleNames);

        marginsFromCSS.setMarginTop(measurement.getOffsetHeight() - 1);
        marginsFromCSS.setMarginLeft(measurement.getOffsetWidth() - 1);

        // Measure bottom and right margins (actually CSS padding)
        measurement.setClassName(marginBottomRightStyleNames);

        marginsFromCSS.setMarginBottom(measurement.getOffsetHeight() - 1);
        marginsFromCSS.setMarginRight(measurement.getOffsetWidth() - 1);

        // Measure spacing (actually CSS padding)
        measurement.setClassName(spacingStyleNames);

        spacingFromCSS.vSpacing = measurement.getOffsetHeight() - 1;
        spacingFromCSS.hSpacing = measurement.getOffsetWidth() - 1;

        // ApplicationConnection.getConsole().log("Margins: " + marginsFromCSS);
        // ApplicationConnection.getConsole().log("Spacing: " + spacingFromCSS);

        root.removeChild(measurement);

        updateMargins(activeMarginsInfo);
        return true;
    }

    protected ChildComponentContainer getFirstChildComponentContainer() {
        int size = getChildren().size();
        if (size < 2) {
            return null;
        }

        return (ChildComponentContainer) getChildren().get(0);
    }

    protected void removeChildrenAfter(int pos) {
        // Remove all children after position "pos" but leave the clear element
        // in place

        int toRemove = getChildren().size() - pos;
        while (toRemove-- > 0) {
            ChildComponentContainer child = (ChildComponentContainer) getChildren()
                    .get(pos);
            widgetToComponentContainer.remove(child.getWidget());
            remove(child);
        }

    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        ChildComponentContainer componentContainer = widgetToComponentContainer
                .remove(oldComponent);
        if (componentContainer == null) {
            return;
        }

        componentContainer.setWidget(newComponent);
        client.unregisterPaintable((Paintable) oldComponent);
        widgetToComponentContainer.put(newComponent, componentContainer);
    }

}
