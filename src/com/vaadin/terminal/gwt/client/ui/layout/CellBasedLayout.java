package com.vaadin.terminal.gwt.client.ui.layout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;

public abstract class CellBasedLayout extends ComplexPanel implements Container {

    protected Map<Widget, ChildComponentContainer> widgetToComponentContainer = new HashMap<Widget, ChildComponentContainer>();

    protected ApplicationConnection client = null;

    protected DivElement root;

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    protected Margins activeMargins = new Margins(0, 0, 0, 0);
    protected VMarginInfo activeMarginsInfo = new VMarginInfo(-1);

    protected boolean spacingEnabled = false;
    protected final Spacing spacingFromCSS = new Spacing(12, 12);
    protected final Spacing activeSpacing = new Spacing(0, 0);

    private boolean dynamicWidth;

    private boolean dynamicHeight;

    private final DivElement clearElement = Document.get().createDivElement();

    private String lastStyleName = "";

    private boolean marginsNeedsRecalculation = false;

    protected String STYLENAME_SPACING = "";
    protected String STYLENAME_MARGIN_TOP = "";
    protected String STYLENAME_MARGIN_RIGHT = "";
    protected String STYLENAME_MARGIN_BOTTOM = "";
    protected String STYLENAME_MARGIN_LEFT = "";

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

        setElement(Document.get().createDivElement());
        getElement().getStyle().setProperty("overflow", "hidden");
        if (BrowserInfo.get().isIE()) {
            getElement().getStyle().setProperty("position", "relative");
            getElement().getStyle().setProperty("zoom", "1");
        }

        root = Document.get().createDivElement();
        root.getStyle().setProperty("overflow", "hidden");
        if (BrowserInfo.get().isIE()) {
            root.getStyle().setProperty("position", "relative");
        }

        getElement().appendChild(root);

        Style style = clearElement.getStyle();
        style.setProperty("width", "0px");
        style.setProperty("height", "0px");
        style.setProperty("clear", "both");
        style.setProperty("overflow", "hidden");
        root.appendChild(clearElement);

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

        /**
         * Margin and spacind detection depends on classNames and must be set
         * before setting size. Here just update the details from UIDL and from
         * overridden setStyleName run actual margin detections.
         */
        updateMarginAndSpacingInfo(uidl);

        /*
         * This call should be made first. Ensure correct implementation, handle
         * size etc.
         */
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        handleDynamicDimensions(uidl);

    }

    @Override
    public void setStyleName(String styleName) {
        super.setStyleName(styleName);

        if (isAttached() && marginsNeedsRecalculation
                || !lastStyleName.equals(styleName)) {
            measureMarginsAndSpacing();
            lastStyleName = styleName;
            marginsNeedsRecalculation = false;
        }

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
        if (childComponent.getParent() == this) {
            if (getWidgetIndex(childComponent) != position) {
                // Detach from old position child.
                childComponent.removeFromParent();

                // Logical attach.
                getChildren().insert(childComponent, position);

                root.insertBefore(childComponent.getElement(), root
                        .getChildNodes().getItem(position));

                adopt(childComponent);
            }
        } else {
            widgetToComponentContainer.put(childComponent.getWidget(),
                    childComponent);

            // Logical attach.
            getChildren().insert(childComponent, position);

            // avoid inserts (they are slower than appends)
            boolean insert = true;
            if (widgetToComponentContainer.size() == position) {
                insert = false;
            }
            if (insert) {
                root.insertBefore(childComponent.getElement(), root
                        .getChildNodes().getItem(position));
            } else {
                root.insertBefore(childComponent.getElement(), clearElement);
            }
            // Adopt.
            adopt(childComponent);

        }

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

    private void updateMarginAndSpacingInfo(UIDL uidl) {
        int bitMask = uidl.getIntAttribute("margins");
        if (activeMarginsInfo.getBitMask() != bitMask) {
            activeMarginsInfo = new VMarginInfo(bitMask);
            marginsNeedsRecalculation = true;
        }
        boolean spacing = uidl.getBooleanAttribute("spacing");
        if (spacing != spacingEnabled) {
            marginsNeedsRecalculation = true;
            spacingEnabled = spacing;
        }
    }

    private static DivElement measurement;
    private static DivElement measurement2;
    private static DivElement measurement3;
    private static DivElement helper;

    static {
        helper = Document.get().createDivElement();
        helper
                .setInnerHTML("<div style=\"position:absolute;top:0;left:0;height:0;visibility:hidden;overflow:hidden;\">"
                        + "<div style=\"width:0;height:0;visibility:hidden;overflow;hidden;\"></div></div><div style=\"position:absolute;height:0;\"></div>");
        NodeList<Node> childNodes = helper.getChildNodes();
        measurement = (DivElement) childNodes.getItem(0);
        measurement2 = (DivElement) measurement.getFirstChildElement();
        measurement3 = (DivElement) childNodes.getItem(1);
    }

    protected boolean measureMarginsAndSpacing() {
        if (!isAttached()) {
            return false;
        }

        // Measure spacing (actually CSS padding)
        measurement3.setClassName(STYLENAME_SPACING
                + (spacingEnabled ? "-on" : "-off"));

        String sn = getStylePrimaryName() + "-margin";

        if (activeMarginsInfo.hasTop()) {
            sn += " " + STYLENAME_MARGIN_TOP;
        }
        if (activeMarginsInfo.hasBottom()) {
            sn += " " + STYLENAME_MARGIN_BOTTOM;
        }
        if (activeMarginsInfo.hasLeft()) {
            sn += " " + STYLENAME_MARGIN_LEFT;
        }
        if (activeMarginsInfo.hasRight()) {
            sn += " " + STYLENAME_MARGIN_RIGHT;
        }

        // Measure top and left margins (actually CSS padding)
        measurement.setClassName(sn);

        root.appendChild(helper);

        activeSpacing.vSpacing = measurement3.getOffsetHeight();
        activeSpacing.hSpacing = measurement3.getOffsetWidth();

        activeMargins.setMarginTop(measurement2.getOffsetTop());
        activeMargins.setMarginLeft(measurement2.getOffsetLeft());
        activeMargins.setMarginRight(measurement.getOffsetWidth()
                - activeMargins.getMarginLeft());
        activeMargins.setMarginBottom(measurement.getOffsetHeight()
                - activeMargins.getMarginTop());

        // ApplicationConnection.getConsole().log("Margins: " + activeMargins);
        // ApplicationConnection.getConsole().log("Spacing: " + activeSpacing);
        // Util.alert("Margins: " + activeMargins);
        root.removeChild(helper);

        // apply margin
        Style style = root.getStyle();
        style.setPropertyPx("marginLeft", activeMargins.getMarginLeft());
        style.setPropertyPx("marginRight", activeMargins.getMarginRight());
        style.setPropertyPx("marginTop", activeMargins.getMarginTop());
        style.setPropertyPx("marginBottom", activeMargins.getMarginBottom());

        return true;
    }

    protected ChildComponentContainer getFirstChildComponentContainer() {
        int size = getChildren().size();
        if (size < 1) {
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
            Paintable p = (Paintable) child.getWidget();
            client.unregisterPaintable(p);
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
