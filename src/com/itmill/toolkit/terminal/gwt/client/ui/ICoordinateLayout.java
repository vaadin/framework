package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICoordinateLayout extends ComplexPanel implements Container,
        ContainerResizedListener {

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "i-coordinatelayout";

    /** CSS class identifier for margin values */
    public static final String CSSID = "margin-values";

    /** For server-client communication */
    protected String uidlId;
    protected ApplicationConnection client;

    /** For inner classes */
    protected final ICoordinateLayout hostReference = this;

    /** Indexes to coordinate arrays */
    protected static final int LEFT = 0;
    protected static final int TOP = 1;
    protected static final int WIDTH = 2;
    protected static final int HEIGHT = 3;
    protected static final int RIGHT = 4;
    protected static final int BOTTOM = 5;

    /** Data structures for components */
    // current components, including captions
    protected final ArrayList<Widget> componentList;

    // component -> caption mappers
    protected final HashMap<Paintable, CustomCaption> componentToHeader;
    protected final HashMap<Paintable, CustomCaption> componentToMarker;

    // component -> properties mappers
    protected final HashMap<Widget, String> componentToCoords;
    protected final HashMap<Widget, Integer> componentToZ;
    protected final HashMap<Widget, Element> componentToArea;

    // components to draw on the next layout update
    protected ArrayList<Widget> toUpdate;

    // current layout margins and width/height
    protected int[] layout = new int[6];

    protected MarginInfo marginInfo; // from UIDL
    protected int[] margins; // from CSS

    public ICoordinateLayout() {
        super();
        setElement(DOM.createDiv());

        // Set style attributes
        setStyleName(CLASSNAME);
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");

        if (BrowserInfo.get().isIE()) {
            DOM.setStyleAttribute(getElement(), "zoom", "1");
        }

        marginInfo = new MarginInfo(0);

        // Init data structures
        componentList = new ArrayList<Widget>();
        toUpdate = new ArrayList<Widget>();
        componentToCoords = new HashMap<Widget, String>();
        componentToHeader = new HashMap<Paintable, CustomCaption>();
        componentToMarker = new HashMap<Paintable, CustomCaption>();
        componentToZ = new HashMap<Widget, Integer>();
        componentToArea = new HashMap<Widget, Element>();

    }

    /**
     * This enables us to read the margins when the layout is actually attached
     * to the DOM-tree.
     * 
     * @see com.google.gwt.user.client.ui.Panel#onLoad()
     */
    protected void onLoad() {
        margins = readMarginsFromCSS();
        super.onLoad();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Paintable#updateFromUIDL(com.itmill
     * .toolkit.terminal.gwt.client.UIDL,
     * com.itmill.toolkit.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and don't let the containing coordinateLayout manage caption, etc.

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // These are for future server connections
        this.client = client;

        // Enable / disable margins
        if (uidl.hasAttribute("margins")) {
            marginInfo = new MarginInfo(uidl.getIntAttribute("margins"));
        } else {
            marginInfo = new MarginInfo(0);
        }

        // Start going through the component tree
        UIDL componentsFromUIDL = uidl;
        ArrayList<Widget> newComponents = new ArrayList<Widget>();
        ArrayList<Widget> removedComponents = new ArrayList<Widget>();
        int zIndex = 0;

        for (Iterator<UIDL> componentIterator = componentsFromUIDL
                .getChildIterator(); componentIterator.hasNext();) {

            // Extract the values from the UIDL
            final UIDL componentUIDL = componentIterator.next();
            final UIDL componentDataUIDL = componentUIDL.getChildUIDL(0);
            final Paintable componentDataPaintable = client
                    .getPaintable(componentDataUIDL);
            final Widget componentWidget = (Widget) componentDataPaintable;
            final String coordString = componentUIDL
                    .getStringAttribute("position");

            if (!componentList.contains(componentWidget)) { // Initial draw
                add(componentWidget);
                componentDataPaintable
                        .updateFromUIDL(componentDataUIDL, client);

            } else { // normal update
                componentDataPaintable
                        .updateFromUIDL(componentDataUIDL, client);
            }

            // Save the coordinate string from the UIDL
            if (!coordString.equals(componentToCoords.get(componentWidget))) {
                toUpdate.add(componentWidget);
            }
            componentToCoords.put(componentWidget, coordString);

            // Components come from the server in the correct order, client just
            // needs to set the z-index
            updateZ(componentWidget, zIndex++);

            // Add the component to the list. This list is later checked against
            // the list of current components
            newComponents.add(componentWidget);

        }// for

        // Clean children that don't exist in the current UIDL
        // (except for captions)
        for (Iterator<Widget> iterator = componentList.iterator(); iterator
                .hasNext();) {
            Widget w = iterator.next();
            if (!newComponents.contains(w) && !(w instanceof CustomCaption)) {
                removedComponents.add(w);
            }
        }

        while (!removedComponents.isEmpty()) {
            removePaintable((Paintable) removedComponents.get(0));
            removedComponents.remove(0);
        }

        componentList.clear();
        componentList.addAll(newComponents);

        // Make sure coordinateLayout gets done every time
        iLayout();

    }// updateFromUIDL

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener#iLayout()
     */
    public void iLayout() {
        // shake
        // TODO is this necessary?
        getOffsetWidth();
        getOffsetHeight();

        // Get our current area and check if it has changed
        // If it has, we need to recalculate all
        int[] newSize = getDimensionsArray();
        if ((newSize[WIDTH] != layout[WIDTH])
                || (newSize[HEIGHT] != layout[HEIGHT])) {
            toUpdate.clear();
            toUpdate = getDrawableComponents();
            layout = newSize;
        }

        if (!toUpdate.isEmpty()) {

            // Go over all children and calculate their positions
            for (Iterator<Widget> componentIterator = toUpdate.iterator(); componentIterator
                    .hasNext();) {

                Widget componentWidget = componentIterator.next();
                CustomCaption componentHeader = componentToHeader
                        .get(componentWidget);
                CustomCaption componentMarker = componentToMarker
                        .get(componentWidget);

                // Reset position info
                resetPositionAttributes(componentWidget);
                maximizeArea(componentWidget);

                // Calculate real pixel values from the coordinate string
                int[] coords = parseCoordString(componentToCoords
                        .get(componentWidget));

                // Determine which sides of the component must be attached
                // ( == sides with a determined position)
                boolean[] sidesToAttach = new boolean[6];
                for (int i = 0; i < coords.length; i++) {
                    sidesToAttach[i] = (coords[i] != -1);
                }

                // At least one of (TOP, BOTTOM) must be attached to ensure the
                // captions are displayed properly
                if (!sidesToAttach[TOP] && !sidesToAttach[BOTTOM]) {
                    sidesToAttach[TOP] = true;
                }
                // At least one of (LEFT, RIGHT) must be attached to ensure the
                // captions are displayed properly
                if (!sidesToAttach[LEFT] && !sidesToAttach[RIGHT]) {
                    sidesToAttach[LEFT] = true;
                }

                // If width or height is not given, try to calculate from other
                // values
                if (coords[WIDTH] == -1) {
                    coords[WIDTH] = layout[WIDTH];

                    if (coords[LEFT] != -1) {
                        coords[WIDTH] -= coords[LEFT];
                    }
                    if (coords[RIGHT] != -1) {
                        coords[WIDTH] -= coords[RIGHT];
                    }

                }

                if (coords[HEIGHT] == -1) {
                    coords[HEIGHT] = layout[HEIGHT];

                    if (coords[TOP] != -1) {
                        coords[HEIGHT] -= coords[TOP];
                    }
                    if (coords[BOTTOM] != -1) {
                        coords[HEIGHT] -= coords[BOTTOM];
                    }
                }

                // Sanity check for width & height
                if (coords[HEIGHT] > layout[HEIGHT]) {
                    coords[HEIGHT] = layout[HEIGHT];
                } else if (coords[HEIGHT] < 0) {
                    coords[HEIGHT] = 0;
                }

                if (coords[WIDTH] > layout[WIDTH]) {
                    coords[WIDTH] = layout[WIDTH];
                } else if (coords[WIDTH] < 0) {
                    coords[WIDTH] = 0;
                }

                // Force width and height on margins so that they
                // have some effect
                calculateMargins(coords);

                // Sanity checks. Some browsers render incorrectly when for
                // example relative[LEFT] + relative[RIGHT] > layout[WIDTH]
                for (int i = 0; i < coords.length; i++) {
                    if (coords[i] < 0) {
                        coords[i] = 0;
                    }
                }

                // height overflows
                if ((coords[TOP] + coords[BOTTOM]) > getOffsetHeight()) {
                    if (coords[TOP] > coords[BOTTOM]) {
                        coords[TOP] -= coords[TOP] - coords[BOTTOM];
                    } else {
                        coords[BOTTOM] -= coords[BOTTOM] - coords[TOP];
                    }
                }

                // width overflows
                if (coords[RIGHT] + coords[LEFT] > getOffsetWidth()) {
                    if (coords[RIGHT] > coords[LEFT]) {
                        coords[RIGHT] -= coords[RIGHT] - coords[LEFT];
                    } else {
                        coords[LEFT] -= coords[LEFT] - coords[RIGHT];
                    }
                }

                // this call puts the component to the componentToPosition
                // map and sets the style attributes
                setWidgetPosition(componentWidget, coords, sidesToAttach);

                // Update caption position
                // Header has icon and caption text
                if (componentHeader != null) {
                    updateCaptionPosition(componentHeader, componentWidget);
                }
                // Marker has error and required
                if (componentMarker != null) {
                    updateCaptionPosition(componentMarker, componentWidget);
                }
            }

            // Run layout functions for children
            client.runDescendentsLayout(this);
        }

        toUpdate.clear();
    }

    /**
     * Set positional attributes for given widget
     * 
     * @param w
     * @param newCoords
     * @param sidesToAttach
     */
    public void setWidgetPosition(Widget w, int[] newCoords,
            boolean[] sidesToAttach) {

        resetPositionAttributes(w);
        Element widgetElement = w.getElement();
        Element areaElement;
        CustomCaption componentHeader = componentToHeader.get(w);
        CustomCaption componentMarker = componentToMarker.get(w);

        // Attach the widget to the sides of the surrounding area element
        if (sidesToAttach[TOP] || sidesToAttach[HEIGHT]) {
            int margin = 0;
            if (componentHeader != null) {
                margin += componentHeader.getOffsetHeight();
            }
            DOM.setStyleAttribute(widgetElement, "top", Integer
                    .toString(margin)
                    + "px");
        }
        if (sidesToAttach[RIGHT] || sidesToAttach[WIDTH]) {
            int margin = 0;
            if (componentMarker != null) {
                margin += componentMarker.getOffsetWidth();
            }
            DOM.setStyleAttribute(widgetElement, "right", Integer
                    .toString(margin)
                    + "px");
        }
        if (sidesToAttach[BOTTOM] || sidesToAttach[HEIGHT]) {
            DOM.setStyleAttribute(widgetElement, "bottom", "0px");
        }
        if (sidesToAttach[LEFT] || sidesToAttach[WIDTH]) {
            DOM.setStyleAttribute(widgetElement, "left", "0px");
        }

        // Create / get the area element for this component
        if (componentToArea.get(w) == null) {
            areaElement = DOM.createDiv();
            // Element tree should be layoutElement -> areaElement ->
            // componentElement
            DOM.appendChild(getElement(), areaElement);
            DOM.removeChild(getElement(), widgetElement);
            DOM.appendChild(areaElement, widgetElement);

            DOM.setStyleAttribute(areaElement, "position", "absolute");
            DOM.setStyleAttribute(areaElement, "overflow", "hidden");

            // Component growth areas are hidden so that they don't interfere
            // with other components
            DOM.setStyleAttribute(areaElement, "visibility", "hidden");

            componentToArea.put(w, areaElement);
        } else {
            areaElement = componentToArea.get(w);
        }

        // set the margin according to the values given
        DOM.setStyleAttribute(areaElement, "margin", (newCoords[TOP] + "px "
                + newCoords[RIGHT] + "px " + newCoords[BOTTOM] + "px "
                + newCoords[LEFT] + "px"));
        DOM.setStyleAttribute(areaElement, "width", newCoords[WIDTH] + "px");
        DOM.setStyleAttribute(areaElement, "height", newCoords[HEIGHT] + "px");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Container#updateCaption(com.itmill
     * .toolkit.terminal.gwt.client.Paintable,
     * com.itmill.toolkit.terminal.gwt.client.UIDL)
     */
    public void updateCaption(Paintable component, UIDL uidl) {
        CustomCaption header = componentToHeader.get(component);
        CustomCaption marker = componentToMarker.get(component);

        if (ICaption.isNeeded(uidl)) {
            boolean headerNeeded = uidl.getStringAttribute("caption") != null
                    || uidl.hasAttribute("icon");

            boolean markerNeeded = uidl.hasAttribute("error")
                    || uidl.hasAttribute("required");

            // if we have both, we display them on the header
            if (headerNeeded && markerNeeded) {
                // header might be in wrong format (separate marker & header)
                if (header != null
                        && !header.isMode(CustomCaption.COMBINED_MODE)) {
                    componentToHeader.remove(component);
                    remove(header);
                    header = null;
                }
                // header might not exist
                if (header == null) {
                    header = new CustomCaption(
                            component,
                            client,
                            (CustomCaption.HEADER_MODE | CustomCaption.MARKER_MODE));
                    add(header);
                    componentToHeader.put(component, header);
                }

                // update
                header.updateCaption(uidl);
                updateCaptionPosition(header, (Widget) component);

                // if we have both we don't need a separate marker
                if (marker != null) {
                    componentToMarker.remove(component);
                    remove(marker);
                }

            }
            // if we only need a header
            else if (headerNeeded) {

                if (header == null) {

                    header = new CustomCaption(component, client,
                            CustomCaption.HEADER_MODE);
                    add(header);
                    componentToHeader.put(component, header);

                }

                header.updateCaption(uidl);

                // caption position
                updateCaptionPosition(header, (Widget) component);

            }

            // if we only need a marker
            else if (markerNeeded) {

                if (marker == null) {

                    marker = new CustomCaption(component, client,
                            CustomCaption.MARKER_MODE);
                    add(marker);
                    componentToMarker.put(component, marker);

                }

                marker.updateCaption(uidl);

                // caption position
                updateCaptionPosition(marker, (Widget) component);
            }
        } else { // No caption
            if (header != null) {
                remove(header);
                componentToHeader.remove(component);
            } else if (marker != null) {
                remove(marker);
                componentToMarker.remove(component);
            }
        }

    }

    /**
     * Update caption position according to the parent component position.
     * 
     * @param c
     * @param parent
     */
    public void updateCaptionPosition(CustomCaption c, Widget parent) {

        int parentTop, parentRight, parentBottom, parentLeft, parentHeight, parentWidth;

        Element captionElement = c.getElement();
        Element parentElement = parent.getElement();
        Element areaElement = componentToArea.get(parent);

        if (areaElement != null
                && !DOM.isOrHasChild(areaElement, c.getElement())) {
            DOM.removeChild(getElement(), c.getElement());
            DOM.appendChild(areaElement, c.getElement());
        }

        resetPositionAttributes(c);

        parentTop = getPositionValue(parentElement, "top");
        parentRight = getPositionValue(parentElement, "right");
        parentBottom = getPositionValue(parentElement, "bottom");
        parentLeft = getPositionValue(parentElement, "left");
        parentHeight = parent.getOffsetHeight();
        parentWidth = parent.getOffsetWidth();

        if (c.isMode(CustomCaption.HEADER_MODE)) {
            if (parentBottom == -1 || parentTop != -1) {
                DOM.setStyleAttribute(captionElement, "top", "0px");
            } else {
                DOM.setStyleAttribute(captionElement, "bottom", parentHeight
                        + "px");
            }

            if (parentRight == -1 || parentLeft != -1) {
                DOM.setStyleAttribute(captionElement, "left", "0px");
            } else {
                int right = parentRight + parentWidth - c.getOffsetWidth();
                DOM.setStyleAttribute(captionElement, "right", right + "px");
            }
        } else if (c.isMode(CustomCaption.MARKER_MODE)) {
            if (parentBottom == -1 || parentTop != -1) {
                DOM.setStyleAttribute(captionElement, "top", parentTop + "px");
            } else {
                int bottom = parentBottom + parentHeight - c.getOffsetHeight();
                DOM.setStyleAttribute(captionElement, "bottom", bottom + "px");
            }

            if (parentRight == -1 || parentLeft != -1) {
                DOM.setStyleAttribute(captionElement, "left", parentWidth
                        + "px");
            } else {
                int right = parentRight - c.getOffsetWidth();
                DOM.setStyleAttribute(captionElement, "right", right + "px");
            }
        }

    }

    /**
     * Update the z-index of a component + the z-index of the caption
     * 
     * @param component
     * @param zIndex
     */
    public void updateZ(Widget component, int zIndex) {

        DOM.setStyleAttribute(component.getElement(), "zIndex", ""
                + String.valueOf(zIndex));
        componentToZ.put(component, new Integer(zIndex));

        // Set caption z-index (same as parent components z)
        if (componentToHeader.get(component) != null) {
            CustomCaption h = componentToHeader.get(component);
            DOM.setStyleAttribute(h.getElement(), "zIndex", "" + zIndex);
        }
        if (componentToMarker.get(component) != null) {
            CustomCaption m = componentToMarker.get(component);
            DOM.setStyleAttribute(m.getElement(), "zIndex", "" + zIndex);
        }
    }

    /**
     * Remove & unregister a paintable component
     * 
     * @param p
     * @return
     */
    public boolean removePaintable(Paintable p) {
        CustomCaption header = componentToHeader.get(p);
        CustomCaption marker = componentToMarker.get(p);
        if (header != null) {
            componentToHeader.remove(p);
            remove(header);
        }
        if (marker != null) {
            componentToHeader.remove(p);
            remove(marker);
        }
        client.unregisterPaintable(p);
        return remove((Widget) p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Container#childComponentSizesUpdated
     * ()
     */
    public boolean childComponentSizesUpdated() {
        return false;
    }

    /**
     * Remove all position-related attributes from a given element
     * 
     * @param e
     */
    protected void resetPositionAttributes(Widget w) {
        Element e = w.getElement();
        DOM.setStyleAttribute(e, "top", "");
        DOM.setStyleAttribute(e, "right", "");
        DOM.setStyleAttribute(e, "bottom", "");
        DOM.setStyleAttribute(e, "left", "");
        DOM.setStyleAttribute(e, "width", "");
        DOM.setStyleAttribute(e, "height", "");
    }

    /**
     * Calculates percentage from value if needed
     * 
     * @param coordString
     * @return
     */
    protected int[] parseCoordString(String coordString) {
        int[] relative = new int[6];
        boolean[] isPercent = new boolean[6];
        String[] values = coordString.split(",");

        for (int i = 0; i < values.length; i++) {

            // string parsing
            if (values[i].startsWith("-")) { // anything with '-' equals -1
                relative[i] = -1;
            } else {
                if (values[i].endsWith("%")) {
                    isPercent[i] = true;
                    relative[i] = Integer.parseInt(values[i].substring(0,
                            values[i].length() - 1)); // without '%'
                } else {
                    relative[i] = Integer.parseInt(values[i]);
                }

            }

            // relative calculations
            if (isPercent[i]) {
                float multiplier = (float) relative[i] / 100;

                if (i == LEFT || i == RIGHT || i == WIDTH) {
                    float width = layout[WIDTH];
                    relative[i] = (int) (width * multiplier);
                } else {
                    float height = layout[HEIGHT];
                    relative[i] = (int) (height * multiplier);
                }
            }
        }
        return relative;
    }

    /**
     * Get the dimensions for the layout, including margins.
     * 
     * @return
     */
    protected int[] getDimensionsArray() {
        int[] newSize = new int[6];

        newSize[LEFT] = marginInfo.hasLeft() ? margins[3] : 0;
        newSize[TOP] = marginInfo.hasTop() ? margins[0] : 0;
        newSize[BOTTOM] = marginInfo.hasBottom() ? margins[2] : 0;
        newSize[RIGHT] = marginInfo.hasRight() ? margins[1] : 0;
        newSize[WIDTH] = getOffsetWidth() - newSize[LEFT] - newSize[RIGHT];
        newSize[HEIGHT] = getOffsetHeight() - newSize[TOP] - newSize[BOTTOM];

        return newSize;
    }

    /*
     * This is called to "reset" area information so we can calculate component
     * size correctly. The layout must have correct dimensions when this is
     * called.
     */
    protected void maximizeArea(Widget component) {
        if (componentToArea.get(component) != null) {
            Element areaElement = componentToArea.get(component);
            DOM.setStyleAttribute(areaElement, "margin", "0");
            DOM.setStyleAttribute(areaElement, "width", layout[WIDTH] + "px");
            DOM.setStyleAttribute(areaElement, "height", layout[HEIGHT] + "px");
        }
    }

    /*
     * This method calculates the final coordinates for a given component. It
     * "fills in" the gaps from the given coordinates.
     */
    protected void calculateMargins(int[] coords) {
        int left, top, right, bottom;

        // left
        if (coords[LEFT] == -1 & coords[RIGHT] == -1) {
            left = layout[LEFT];
        } else if (coords[LEFT] == -1) {
            left = layout[WIDTH] - coords[RIGHT] - coords[WIDTH] + layout[LEFT];

        } else {
            left = coords[LEFT] + layout[LEFT];
        }

        // right
        if (coords[RIGHT] == -1 & coords[LEFT] == -1) {
            right = layout[RIGHT];
        } else if (coords[RIGHT] == -1) {
            right = layout[WIDTH] - coords[LEFT] - coords[WIDTH]
                    + layout[RIGHT];
        } else {
            right = coords[RIGHT] + layout[RIGHT];
        }

        // top
        if (coords[TOP] == -1 & coords[BOTTOM] == -1) {
            top = layout[TOP];
        } else if (coords[TOP] == -1) {
            top = layout[HEIGHT] - coords[HEIGHT] - coords[BOTTOM]
                    + layout[TOP];
        } else {
            top = coords[TOP] + layout[TOP];
        }

        // bottom
        if (coords[BOTTOM] == -1 & coords[TOP] == -1) {
            bottom = layout[BOTTOM];
        } else if (coords[BOTTOM] == -1) {
            bottom = layout[HEIGHT] - coords[TOP] - coords[HEIGHT]
                    + layout[BOTTOM];
        } else {
            bottom = coords[BOTTOM] + layout[BOTTOM];
        }

        coords[LEFT] = left;
        coords[TOP] = top;
        coords[RIGHT] = right;
        coords[BOTTOM] = bottom;

    }

    /*
     * Used to retrieve e.g. the "top" and "right" values for an element
     */
    protected int getPositionValue(Element e, String position) {
        String value = DOM.getStyleAttribute(e, position);
        if (!"".equals(value)) {
            return parsePixel(value);
        } else {
            return -1;
        }
    }

    /* Read margin info from CSS */
    protected int[] readMarginsFromCSS() {
        int[] margins = new int[4];
        String style = CLASSNAME + "-" + CSSID;
        String[] cssProperties = new String[] { "margin-top", "margin-right",
                "margin-bottom", "margin-left" };

        String cssValue = "";
        String[] cssValues = new String[cssProperties.length];

        Element e = DOM.createDiv();
        DOM.appendChild(getElement(), e);
        DOM.setStyleAttribute(e, "position", "absolute");
        DOM.setStyleAttribute(e, "height", "100px");
        DOM.setStyleAttribute(e, "width", "100px");
        DOM.setElementProperty(e, "className", style);
        DOM.setInnerHTML(e, ".");

        try {
            if (BrowserInfo.get().isIE()) {
                cssValue = getIEMargin(e);

                cssValues = cssValue.split("\\s");

                switch (cssValues.length) {

                case 4:
                    for (int i = 0; i < 4; i++) {
                        margins[i] = parsePixel(cssValues[i]);
                    }
                    break;

                case 3:
                    margins[0] = parsePixel(cssValues[0]);
                    margins[1] = margins[3] = parsePixel(cssValues[1]);
                    margins[2] = parsePixel(cssValues[2]);
                    break;

                case 2:
                    margins[0] = margins[2] = parsePixel(cssValues[0]);
                    margins[1] = margins[3] = parsePixel(cssValues[1]);
                    break;

                case 1:
                    int intValue = parsePixel(cssValues[0]);
                    for (int i = 0; i < margins.length; i++) {
                        margins[i] = intValue;
                    }
                }
            } else {
                for (int i = 0; i < cssValues.length; i++) {
                    cssValues[i] = getMargin(e, cssProperties[i]);
                    margins[i] = parsePixel(cssValues[i]);
                }
            }

        } catch (Exception exc) {
            // Error in CSS margin format or missing value, defaulting all to 0
            GWT.log("Error reading CSS", exc);
            for (int i = 0; i < margins.length; i++) {
                margins[i] = 0;
            }
        }
        DOM.removeChild(getElement(), e);
        return margins;
    }

    /* "10px" => 10 */
    protected int parsePixel(String str) throws NumberFormatException {
        return Integer.parseInt(str.substring(0, str.length() - 2));
    }

    /**
     * Retrieve margin for other browsers
     * 
     * @param e
     * @param CSSProp
     * @return
     */
    protected native String getMargin(Element e, String CSSProp)/*-{
                                                                                                                                                                                                                                                                                                                                                                                                                                                        return $wnd.getComputedStyle(e,null).getPropertyValue(CSSProp);
                                                                                                                                                                                                                                                                                                                                                                                                                                                        }-*/;

    /**
     * Retrieves margin info in IE
     * 
     * @param e
     * @return
     */
    protected native String getIEMargin(Element e)/*-{ 
                                                                                                                                                                                                                                                                                                                                                                                                                                                        return e.currentStyle.margin;
                                                                                                                                                                                                                                                                                                                                                                                                                                                        }-*/;

    /**
     * @return all components that are not captions
     */
    protected ArrayList<Widget> getDrawableComponents() {
        ArrayList<Widget> list = new ArrayList<Widget>();
        for (Widget w : componentList) {
            if (!(w instanceof CustomCaption)) {
                list.add(w);
            }
        }
        return list;
    }

    public boolean requestLayout(Set<Paintable> child) {
        return true;
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        Element area = componentToArea.get(child);
        if (area != null) {
            return new RenderSpace(area.getOffsetWidth(), area
                    .getOffsetHeight());
        } else {
            return new RenderSpace(layout[WIDTH], layout[HEIGHT]);
        }
    }

    /*
     * Widget methods
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.ComplexPanel#iterator()
     */
    public Iterator<Widget> iterator() {
        return componentList.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Container#replaceChildComponent
     * (com.google.gwt.user.client.ui.Widget,
     * com.google.gwt.user.client.ui.Widget)
     */
    public void replaceChildComponent(Widget from, Widget to) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.itmill.toolkit.terminal.gwt.client.Container#hasChildComponent(com
     * .google.gwt.user.client.ui.Widget)
     */
    public boolean hasChildComponent(Widget w) {
        return componentList.contains(w);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Panel#add(com.google.gwt.user.client.ui
     * .Widget)
     */
    public void add(Widget w) {
        if (!componentList.contains(w)) {
            w.removeFromParent();
        }

        // these attributes are common to all elements
        DOM.setStyleAttribute(w.getElement(), "position", "absolute");
        DOM.setStyleAttribute(w.getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(w.getElement(), "visibility", "visible");
        componentList.add(w);

        super.add(w, getElement());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.ComplexPanel#remove(com.google.gwt.user
     * .client.ui.Widget)
     */
    public boolean remove(Widget w) {
        boolean wasRemoved = super.remove(w);
        if (wasRemoved) {
            resetPositionAttributes(w);

            // If removing a component, remove its area
            if (!(w instanceof CustomCaption)) {
                DOM.removeChild(getElement(), componentToArea.get(w));
            }

            componentList.remove(w);
            componentToZ.remove(w);
            componentToArea.remove(w);
        }
        return wasRemoved;
    }

    /**
     * Custom caption class to encapsulate different caption parts
     */
    public class CustomCaption extends HTML {
        public static final String CLASSNAME = "i-caption";

        // Bit masks for different modes
        public static final int HEADER_MODE = 1;
        public static final int MARKER_MODE = 2;
        public static final int COMBINED_MODE = HEADER_MODE | MARKER_MODE;

        private final Paintable owner;

        private Element errorIndicatorElement;

        private Element requiredFieldIndicator;

        private Icon icon;

        private Element captionText;

        private final ApplicationConnection client;

        private int mode;

        /**
         * 
         * @param component
         *            optional owner of caption. If not set, getOwner will
         *            return null
         * @param client
         */
        public CustomCaption(Paintable component, ApplicationConnection client,
                int mode) {
            super();
            this.client = client;
            owner = component;
            setStyleName(CLASSNAME);
            this.mode = mode;
        }

        public void updateCaption(UIDL uidl) {
            setVisible(!uidl.getBooleanAttribute("invisible"));

            setStyleName(getElement(), "i-disabled", uidl
                    .hasAttribute("disabled"));

            boolean isEmpty = true;

            if (uidl.hasAttribute("description")) {
                if (captionText != null) {
                    addStyleDependentName("hasdescription");
                } else {
                    removeStyleDependentName("hasdescription");
                }
            }

            if (isMode(HEADER_MODE)) {
                if (uidl.hasAttribute("icon")) {
                    if (icon == null) {
                        icon = new Icon(client);

                        DOM.insertChild(getElement(), icon.getElement(), 0);
                    }
                    icon.setUri(uidl.getStringAttribute("icon"));
                    isEmpty = false;
                } else {
                    if (icon != null) {
                        DOM.removeChild(getElement(), icon.getElement());
                        icon = null;
                    }
                }

                if (uidl.hasAttribute("caption")) {
                    if (captionText == null) {
                        captionText = DOM.createSpan();
                        DOM.insertChild(getElement(), captionText,
                                icon == null ? 0 : 1);
                    }
                    String c = uidl.getStringAttribute("caption");
                    if (c == null) {
                        c = "";
                    } else {
                        isEmpty = false;
                    }
                    DOM.setInnerText(captionText, c);
                } else {
                    // TODO should span also be removed
                }
            }

            if (isMode(MARKER_MODE)) {
                if (uidl.getBooleanAttribute("required")) {
                    isEmpty = false;
                    if (requiredFieldIndicator == null) {
                        requiredFieldIndicator = DOM.createSpan();
                        DOM.setInnerText(requiredFieldIndicator, "*");
                        DOM.setElementProperty(requiredFieldIndicator,
                                "className", "i-required-field-indicator");
                        DOM.appendChild(getElement(), requiredFieldIndicator);
                    }
                } else {
                    if (requiredFieldIndicator != null) {
                        DOM.removeChild(getElement(), requiredFieldIndicator);
                        requiredFieldIndicator = null;
                    }
                }

                if (uidl.hasAttribute("error")) {
                    isEmpty = false;
                    if (errorIndicatorElement == null) {
                        errorIndicatorElement = DOM.createDiv();
                        DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                        DOM.setElementProperty(errorIndicatorElement,
                                "className", "i-errorindicator");
                        DOM.appendChild(getElement(), errorIndicatorElement);
                    }
                } else if (errorIndicatorElement != null) {
                    DOM.removeChild(getElement(), errorIndicatorElement);
                    errorIndicatorElement = null;
                }
            }
            // Workaround for IE weirdness, sometimes returns bad height in some
            // circumstances when Caption is empty. See #1444
            // IE7 bugs more often. I wonder what happens when IE8 arrives...
            if (BrowserInfo.get().isIE()) {
                if (isEmpty) {
                    setHeight("0px");
                    DOM.setStyleAttribute(getElement(), "overflow", "hidden");
                } else {
                    setHeight("");
                    DOM.setStyleAttribute(getElement(), "overflow", "");
                }

            }

        }

        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            final Element target = DOM.eventGetTarget(event);
            if (client != null && !(target == getElement())) {
                client.handleTooltipEvent(event, owner);
            }
        }

        /**
         * Returns Paintable for which this Caption belongs to.
         * 
         * @return owner Widget
         */
        public Paintable getOwner() {
            return owner;
        }

        /**
         * Test if this CustomCaption is in a certain mode
         * 
         * @param value
         * @return
         */
        public boolean isMode(int value) {
            return (mode & value) == value;
        }
    }

}// class ICoordinateLayout
