/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * Full implementation of OrderedLayout client peer.
 * 
 * This class implements all features of OrderedLayout. It currently only
 * supports use through UIDL updates. Direct client side use is not (currently)
 * suported in all operation modes.
 * 
 * @author IT Mill Ltd
 */
public class IOrderedLayout extends Panel implements Container,
        ContainerResizedListener {

    public static final String CLASSNAME = "i-orderedlayout";

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    private int hSpacing = -1;
    private int vSpacing = -1;
    private int marginTop = -1;
    private int marginBottom = -1;
    private int marginLeft = -1;
    private int marginRight = -1;

    int orientationMode = ORIENTATION_VERTICAL;

    protected ApplicationConnection client;

    /**
     * Reference to Element where wrapped childred are contained. Normally a
     * DIV, TR or a TBODY element.
     */
    private Element wrappedChildContainer;

    /**
     * Elements that provides the Layout interface implementation. Root element
     * of the component. This is the outmost div or table.
     */
    private Element root;

    /**
     * List of child widgets. This is not the list of wrappers, but the actual
     * widgets
     */
    private final Vector childWidgets = new Vector();

    /**
     * In table mode, the root element is table instead of div.
     */
    private boolean tableMode = false;

    /** Last set width of the component. Null if undefined (instead of being ""). */
    private String width = null;

    /**
     * Last set height of the component. Null if undefined (instead of being
     * "").
     */
    private String height = null;
    /**
     * List of child widget wrappers. These wrappers are in exact same indexes
     * as the widgets in childWidgets list.
     */
    private final Vector childWidgetWrappers = new Vector();

    /** Whether the component has spacing enabled. */
    private boolean hasComponentSpacing;

    /** Information about margin states. */
    private MarginInfo margins = new MarginInfo(0);

    /**
     * Flag that indicates that the child layouts must be updated as soon as
     * possible. This will be done in the end of updateFromUIDL.
     */
    private boolean childLayoutsHaveChanged = false;

    /**
     * Construct the DOM of the orderder layout.
     * 
     * <p>
     * There are two modes - vertical and horizontal.
     * <ul>
     * <li>Vertical mode uses structure: div-root ( div-wrap ( child ) div-wrap (
     * child ))).</li>
     * <li>Horizontal mode uses structure: table ( tbody ( tr-childcontainer (
     * td-wrap ( child ) td-wrap ( child) )) )</li>
     * </ul>
     * where root and childcontainer refer to the root element and the element
     * that contain WidgetWrappers.
     * </p>
     * 
     */
    public IOrderedLayout() {
        wrappedChildContainer = root = DOM.createDiv();
        setElement(root);
        setStyleName(CLASSNAME);
    }

    /**
     * Update orientation, if it has changed.
     * 
     * @param newOrientationMode
     */
    private void rebuildRootDomStructure(int oldOrientationMode) {

        // Should we have table as a root element?
        boolean newTableMode = !(orientationMode == ORIENTATION_VERTICAL && width != null);

        // Already in correct mode?
        if (oldOrientationMode == orientationMode && newTableMode == tableMode) {
            return;
        }
        tableMode = newTableMode;

        // Constuct base DOM-structure and clean any already attached
        // widgetwrappers from DOM.
        if (tableMode) {
            Element tmp = DOM.createDiv();
            final String structure = "<table cellspacing=\"0\" cellpadding=\"0\"><tbody>"
                    + (orientationMode == ORIENTATION_HORIZONTAL ? "<tr></tr>"
                            : "") + "</tbody></table>";
            DOM.setInnerHTML(tmp, structure);
            root = DOM.getFirstChild(tmp);
            DOM.removeChild(tmp, root);
            // set TBODY to be the wrappedChildContainer
            wrappedChildContainer = DOM.getFirstChild(root);
            // In case of horizontal layouts, we must user TR instead of TBODY
            if (orientationMode == ORIENTATION_HORIZONTAL) {
                wrappedChildContainer = DOM
                        .getFirstChild(wrappedChildContainer);
            }
        } else {
            wrappedChildContainer = root = DOM.createDiv();
        }

        // Restore component size
        if (width != null && !"".equals(width)) {
            DOM.setStyleAttribute(root, "width", width);
        }
        if (height != null && !"".equals(height)) {
            DOM.setStyleAttribute(root, "height", height);
        }

        // Reset widget main element
        String styles = getStyleName();
        setElement(root);
        setStyleName(styles);

        // Reinsert all widget wrappers to this container
        final int currentOrientationMode = orientationMode;
        for (int i = 0; i < childWidgetWrappers.size(); i++) {
            WidgetWrapper wr = (WidgetWrapper) childWidgetWrappers.get(i);
            orientationMode = oldOrientationMode;
            Element oldWrElement = wr.getElementWrappingWidgetAndCaption();
            orientationMode = currentOrientationMode;
            wr.resetRootElement();
            Element newWrElement = wr.getElementWrappingWidgetAndCaption();
            while (DOM.getChildCount(oldWrElement) > 0) {
                Element c = DOM.getFirstChild(oldWrElement);
                DOM.removeChild(oldWrElement, c);
                DOM.appendChild(newWrElement, c);
            }

            DOM.appendChild(wrappedChildContainer, wr.getElement());
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Update the contents of the layout from UIDL. */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        this.client = client;

        // Only non-cached UIDL:s can introduce changes
        if (uidl.getBooleanAttribute("cached")) {
            return;
        }

        updateMarginAndSpacingSizesFromCSS(uidl);

        // Update sizes, ...
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // Rebuild DOM tree root if necessary
        int oldO = orientationMode;
        orientationMode = "horizontal".equals(uidl
                .getStringAttribute("orientation")) ? ORIENTATION_HORIZONTAL
                : ORIENTATION_VERTICAL;
        rebuildRootDomStructure(oldO);

        // Handle component spacing later in handleAlignments() method
        hasComponentSpacing = uidl.getBooleanAttribute("spacing");

        // Collect the list of contained widgets after this update
        final Vector newWidgets = new Vector();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL uidlForChild = (UIDL) it.next();
            final Paintable child = client.getPaintable(uidlForChild);
            newWidgets.add(child);
        }

        // Iterator for old widgets
        final Iterator oldWidgetsIterator = (new Vector(childWidgets))
                .iterator();

        // Iterator for new widgets
        final Iterator newWidgetsIterator = newWidgets.iterator();

        // Iterator for new UIDL
        final Iterator newUIDLIterator = uidl.getChildIterator();

        // List to collect all now painted widgets to in order to remove
        // unpainted ones later
        final Vector paintedWidgets = new Vector();

        final Vector childsToPaint = new Vector();

        // Add any new widgets to the ordered layout
        Widget oldChild = null;
        while (newWidgetsIterator.hasNext()) {

            final Widget newChild = (Widget) newWidgetsIterator.next();
            final UIDL newChildUIDL = (UIDL) newUIDLIterator.next();

            // Remove any unneeded old widgets
            if (oldChild == null && oldWidgetsIterator.hasNext()) {
                // search for next old Paintable which still exists in layout
                // and delete others
                while (oldWidgetsIterator.hasNext()) {
                    oldChild = (Widget) oldWidgetsIterator.next();
                    // now oldChild is an instance of Paintable
                    if (paintedWidgets.contains(oldChild)) {
                        continue;
                    } else if (newWidgets.contains(oldChild)) {
                        break;
                    } else {
                        remove(oldChild);
                        oldChild = null;
                    }
                }
            }

            if (oldChild == null) {
                // we are adding components to the end of layout
                add(newChild);
            } else if (newChild == oldChild) {
                // child already attached in correct position
                oldChild = null;
            } else if (hasChildComponent(newChild)) {

                // current child has been moved, re-insert before current
                // oldChild
                add(newChild, childWidgets.indexOf(oldChild));

            } else {
                // insert new child before old one
                add(newChild, childWidgets.indexOf(oldChild));
            }

            // Update the child component
            childsToPaint.add(new Object[] { newChild, newChildUIDL });

            // Add this newly handled component to the list of painted
            // components
            paintedWidgets.add(newChild);
        }

        // Remove possibly remaining old widgets which were not in painted UIDL
        while (oldWidgetsIterator.hasNext()) {
            oldChild = (Widget) oldWidgetsIterator.next();
            if (!newWidgets.contains(oldChild)) {
                remove(oldChild);
            }
        }

        // Handle component alignments
        handleAlignmentsSpacingAndMargins(uidl);

        // Reset sizes for the children
        // TODO These might be optimized by combining these methods
        updateChildHeights();
        updateChildWidths();

        // Paint children
        for (int i = 0; i < childsToPaint.size(); i++) {
            Object[] t = (Object[]) childsToPaint.get(i);
            ((Paintable) t[0]).updateFromUIDL((UIDL) t[1], client);
        }

        // Update child layouts
        // TODO This is most probably unnecessary and should be done within
        // update Child H/W
        if (childLayoutsHaveChanged) {
            Util.runDescendentsLayout(this);
            childLayoutsHaveChanged = false;
        }
    }

    private void updateMarginAndSpacingSizesFromCSS(UIDL uidl) {
        // TODO Read spacing and margins from CSS as documented in #1904.
        // Somehow refresh after updates

        hSpacing = 8;
        vSpacing = 8;
        marginTop = 15;
        marginBottom = 15;
        marginLeft = 18;
        marginRight = 18;
    }

    /**
     * While setting width, ensure that margin div is also resized properly.
     * Furthermore, enable/disable fixed mode
     */
    public void setWidth(String newWidth) {

        width = newWidth == null || "".equals(newWidth) ? null : newWidth;

        // When we use divs at root - for them using 100% width should be
        // calculated with ""
        if (!tableMode && "100%".equals(newWidth)) {
            super.setWidth("");
        } else {
            super.setWidth(newWidth);
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /**
     * While setting height, ensure that margin div is also resized properly.
     * Furthermore, enable/disable fixed mode
     */
    public void setHeight(String newHeight) {
        super.setHeight(newHeight);
        height = newHeight == null || "".equals(newHeight) ? null : newHeight;

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Recalculate and apply child heights */
    private void updateChildHeights() {

        // Vertical layout is calculated by us
        if (height != null) {

            // Calculate the space for fixed contents minus marginals
            int size;
            if (tableMode) {

                // If we know explicitly set pixel-size, use that
                if (height != null && height.endsWith("px")) {
                    try {
                        size = Integer.parseInt(height.substring(0, height
                                .length() - 2));

                        // For negative sizes, use measurements
                        if (size < 0) {
                            size = rootOffsetMeasure("offsetHeight");
                        }
                    } catch (NumberFormatException e) {

                        // In case of invalid number, try to measure the size;
                        size = rootOffsetMeasure("offsetHeight");
                    }
                }
                // If not, try to measure the size
                else {
                    size = rootOffsetMeasure("offsetHeight");
                }

            } else {
                size = DOM.getElementPropertyInt(root, "offsetHeight");
            }

            size -= margins.hasTop() ? marginTop : 0;
            size -= margins.hasBottom() ? marginBottom : 0;

            // Reduce spacing from the size
            int numChild = childWidgets.size();
            if (hasComponentSpacing) {
                size -= ((orientationMode == ORIENTATION_HORIZONTAL) ? hSpacing
                        : vSpacing)
                        * (numChild - 1);
            }

            // Set the sizes for each child
            if (orientationMode == ORIENTATION_HORIZONTAL) {
                for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                    ((WidgetWrapper) i.next()).forceHeight(size);
                }
            } else {
                for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                    final int ws = Math.round(((float) size) / (numChild--));
                    size -= ws;
                    ((WidgetWrapper) i.next()).forceHeight(ws);
                }
            }
        }

        // Vertically layout is calculated by the browsers
        else {
            for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                ((WidgetWrapper) i.next()).forceHeight(-1);
            }
        }
    }

    /**
     * Measure how much space the root element could get.
     * 
     * This measures the space allocated by the parent for the root element
     * without letting root element to affect the calculation.
     * 
     * @param offset
     *                offsetWidth or offsetHeight
     */
    private int rootOffsetMeasure(String offset) {
        Element measure = DOM.createDiv();
        DOM.setStyleAttribute(measure, "height", "100%");
        Element parent = DOM.getParent(root);
        DOM.insertBefore(parent, measure, root);
        DOM.removeChild(parent, root);
        int size = DOM.getElementPropertyInt(measure, offset);
        DOM.insertBefore(parent, root, measure);
        DOM.removeChild(parent, measure);
        // In case the no space would be given for this element
        // without pushing, use the current side of the root
        return size;
    }

    /** Recalculate and apply child widths */
    private void updateChildWidths() {
        // Horizontal layout is calculated by us
        if (width != null && orientationMode == ORIENTATION_HORIZONTAL) {

            // Calculate the space for fixed contents minus marginals
            int size;
            // If we know explicitly set pixel-size, use that
            if (width != null && width.endsWith("px")) {
                try {
                    size = Integer.parseInt(width.substring(0,
                            width.length() - 2));

                    // For negative sizes, use measurements
                    if (size < 0) {
                        size = rootOffsetMeasure("offsetWidth");
                    }

                } catch (NumberFormatException e) {

                    // In case of invalid number, try to measure the size;
                    size = rootOffsetMeasure("offsetWidth");
                }
            }
            // If not, try to measure the size
            else {
                size = rootOffsetMeasure("offsetWidth");
            }

            size -= margins.hasLeft() ? marginLeft : 0;
            size -= margins.hasRight() ? marginRight : 0;

            // Reduce spacing from the size
            int numChild = childWidgets.size();
            if (hasComponentSpacing) {
                size -= hSpacing * (numChild - 1);
            }

            // Set the sizes for each child
            if (orientationMode == ORIENTATION_HORIZONTAL) {
                for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                    final int ws = Math.round(((float) size) / (numChild--));
                    size -= ws;
                    ((WidgetWrapper) i.next()).forceWidth(ws);
                }
            } else {
                for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                    ((WidgetWrapper) i.next()).forceWidth(size);
                }
            }
        }

        // Horizontal layout is calculated by the browsers
        else {
            for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
                ((WidgetWrapper) i.next()).forceWidth(-1);
            }
        }

    }

    /** Parse alignments from UIDL and pass whem to correct widgetwrappers */
    private void handleAlignmentsSpacingAndMargins(UIDL uidl) {

        // Only update margins when they have changed
        // TODO this should be optimized to avoid reupdating these
        margins = new MarginInfo(uidl.getIntAttribute("margins"));

        // Component alignments as a comma separated list.
        // See com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.java for
        // possible values.
        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        // Insert alignment attributes
        final Iterator it = childWidgetWrappers.iterator();

        while (it.hasNext()) {

            // Calculate alignment info
            final AlignmentInfo ai = new AlignmentInfo(
                    alignments[alignmentIndex++]);

            final WidgetWrapper wr = (WidgetWrapper) it.next();

            wr.setAlignment(ai.getVerticalAlignment(), ai
                    .getHorizontalAlignment());

            // Handle spacing and margins in this loop as well
            wr.setSpacingAndMargins(alignmentIndex == 1,
                    alignmentIndex == alignments.length);
        }
    }

    /**
     * Wrapper around single child in the layout.
     * 
     * This helper also manages spacing, margins and alignment for individual
     * cells handling. It also can put hard size limits for its contens by
     * clipping the content to given pixel size.
     * 
     */
    class WidgetWrapper extends UIObject {

        /**
         * When alignment table structure is used, these elements correspond to
         * the TD elements within the structure. If alignment is not used, these
         * are null.
         */
        Element alignmentTD, innermostTDinAlignmnetStructure;

        /**
         * When clipping must be done and the element wrapping clipped content
         * would be TD instead of DIV, this element points to additional DIV
         * that is used for clipping.
         */
        Element clipperDiv;

        /** Caption element when used. */
        Caption caption = null;

        /**
         * Last set pixel height for the wrapper. -1 if vertical clipping is not
         * used.
         */
        int lastForcedPixelHeight = -1;

        /**
         * Last set pidel width for the wrapper. -1 if horizontal clipping is
         * not used.
         */
        int lastForcedPixelWidth = -1;

        /** Set the root element */
        public WidgetWrapper() {
            resetRootElement();
        }

        /**
         * Set the height given for the wrapped widget in pixels.
         * 
         * -1 if unconstrained.
         */
        public void forceHeight(int pixelHeight) {

            // If we are already at the correct size, do nothing
            if (lastForcedPixelHeight == pixelHeight) {
                return;
            }

            // Clipper DIV is needed?
            if (tableMode) {
                if (pixelHeight >= 0) {
                    if (clipperDiv == null) {
                        createClipperDiv();
                    }
                }
                // Needed to remove unnecessary clipper DIV
                else if (clipperDiv != null && lastForcedPixelWidth < 0) {
                    removeClipperDiv();
                }
            }
            Element e = clipperDiv != null ? clipperDiv
                    : getElementWrappingAlignmentStructures();

            // Overflow
            DOM.setStyleAttribute(e, "overflowY", pixelHeight < 0 ? ""
                    : "hidden");

            // Set height
            DOM.setStyleAttribute(e, "height",
                    pixelHeight < 0 ? (e == clipperDiv || !tableMode ? "100%"
                            : "") : pixelHeight + "px");

            lastForcedPixelHeight = pixelHeight;
        }

        /**
         * Set the width given for the wrapped widget in pixels.
         * 
         * -1 if unconstrained.
         */
        public void forceWidth(int pixelWidth) {

            // If we are already at the correct size, do nothing
            if (lastForcedPixelWidth == pixelWidth) {
                return;
            }

            // Clipper DIV needed
            if (tableMode) {
                if (pixelWidth >= 0) {
                    if (clipperDiv == null) {
                        createClipperDiv();
                    }
                }
                // Needed to remove unnecessary clipper DIV
                else if (clipperDiv != null && lastForcedPixelHeight < 0) {
                    removeClipperDiv();
                }
            }
            Element e = clipperDiv != null ? clipperDiv
                    : getElementWrappingAlignmentStructures();

            // Overflow
            DOM.setStyleAttribute(e, "overflowX", pixelWidth < 0 ? ""
                    : "hidden");

            // Set width
            DOM.setStyleAttribute(e, "width", pixelWidth < 0 ? "" : pixelWidth
                    + "px");

            lastForcedPixelWidth = pixelWidth;
        }

        /** Create a DIV for clipping the child */
        private void createClipperDiv() {
            clipperDiv = DOM.createDiv();
            final Element e = getElementWrappingClipperDiv();
            while (DOM.getChildCount(e) > 0) {
                final Element c = DOM.getFirstChild(e);
                DOM.removeChild(e, c);
                DOM.appendChild(clipperDiv, c);
            }
            DOM.appendChild(e, clipperDiv);
        }

        /** Undo createClipperDiv() */
        private void removeClipperDiv() {
            final Element e = getElementWrappingClipperDiv();
            while (DOM.getChildCount(clipperDiv) > 0) {
                final Element c = DOM.getFirstChild(clipperDiv);
                DOM.removeChild(clipperDiv, c);
                DOM.appendChild(e, c);
            }
            DOM.removeChild(e, clipperDiv);
            clipperDiv = null;
        }

        /**
         * Get the element containing the caption and the wrapped widget.
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * <li>(c) clipperDiv inside the (a) or (b)</li>
         * <li>(d) The innermost TD within alignment structures located in (a),
         * (b) or (c)</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingWidgetAndCaption() {

            // When alignment is used, we will can safely return the innermost
            // TD
            if (innermostTDinAlignmnetStructure != null) {
                return innermostTDinAlignmnetStructure;
            }

            // In all other cases element wrapping the potential alignment
            // structures is the correct one
            return getElementWrappingAlignmentStructures();
        }

        /**
         * Get the element where alignment structures should be placed in if
         * they are in use.
         * 
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * <li>(c) clipperDiv inside the (a) or (b)</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingAlignmentStructures() {

            // Clipper DIV wraps the alignment structures if present
            if (clipperDiv != null) {
                return clipperDiv;
            }

            // When Clipper DIV is not used, we just give the element
            // that would wrap it if it would be used
            return getElementWrappingClipperDiv();
        }

        /**
         * Get the element where clipperDiv should be placed in if they it is in
         * use.
         * 
         * Returned element can one of the following:
         * <ul>
         * <li>(a) Root DIV of the WrapperElement when not in tableMode</li>
         * <li>(b) TD in just below the root TR of the WrapperElement when in
         * tableMode</li>
         * </ul>
         * 
         * @return Element described above
         */
        private Element getElementWrappingClipperDiv() {

            // Only vertical layouts in non-table mode use TR as root, for the
            // rest we can safely give root element
            if (!tableMode || orientationMode == ORIENTATION_HORIZONTAL) {
                return getElement();
            }

            // The root is TR, we'll thus give the TD that is immediately within
            // the root
            return DOM.getFirstChild(getElement());
        }

        /**
         * Create tr, td or div - depending on the orientation of the layout and
         * set it as root.
         * 
         * All contents of the wrapper are cleared. Caller is responsible for
         * preserving the contents and moving them into new root.
         * 
         * @return Previous root element.
         */
        private void resetRootElement() {
            if (tableMode) {
                if (orientationMode == ORIENTATION_HORIZONTAL) {
                    setElement(DOM.createTD());
                } else {
                    Element tr = DOM.createTR();
                    DOM.appendChild(tr, DOM.createTD());
                    setElement(tr);
                }
            } else {
                setElement(DOM.createDiv());
                // Apply 'hasLayout' for IE (needed to get accurate dimension
                // calculations)
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(getElement(), "zoom", "1");
                }
            }

            // Clear any references to intermediate elements
            clipperDiv = alignmentTD = innermostTDinAlignmnetStructure = null;
        }

        /** Update the caption of the element contained in this wrapper. */
        public void updateCaption(UIDL uidl, Paintable paintable) {

            final Widget widget = (Widget) paintable;
            final Element captionWrapper = getElementWrappingWidgetAndCaption();

            // The widget needs caption
            if (Caption.isNeeded(uidl)) {

                // If the caption element is missing, create it
                boolean justAdded = false;
                if (caption == null) {
                    justAdded = true;
                    caption = new Caption(paintable, client);
                }

                // Update caption contents
                caption.updateCaption(uidl);

                final boolean after = caption.shouldBePlacedAfterComponent();
                final Element captionElement = caption.getElement();
                final Element widgetElement = widget.getElement();

                if (justAdded) {

                    // As the caption has just been created, insert it to DOM
                    if (after) {
                        DOM.appendChild(captionWrapper, captionElement);
                        DOM.setElementAttribute(captionWrapper, "class",
                                "i-orderedlayout-w");
                        caption.addStyleName("i-orderedlayout-c");
                        widget.addStyleName("i-orderedlayout-w-e");
                    } else {
                        DOM.insertChild(captionWrapper, captionElement, 0);
                    }

                } else

                // Caption exists. Move it to correct position if needed
                if (after == (DOM.getChildIndex(captionWrapper, widgetElement) > DOM
                        .getChildIndex(captionWrapper, captionElement))) {
                    Element firstElement = DOM.getChild(captionWrapper, DOM
                            .getChildCount(captionWrapper) - 2);
                    if (firstElement != null) {
                        DOM.removeChild(captionWrapper, firstElement);
                        DOM.appendChild(captionWrapper, firstElement);
                    }
                    DOM.setElementAttribute(captionWrapper, "class",
                            after ? "i-orderedlayout-w" : "");
                    if (after) {
                        caption.addStyleName("i-orderedlayout-c");
                        widget.addStyleName("i-orderedlayout-w-e");
                    } else {
                        widget.removeStyleName("i-orderedlayout-w-e");
                        caption.removeStyleName("i-orderedlayout-w-c");
                    }
                }

            }

            // Caption is not needed
            else {

                // Remove existing caption from DOM
                if (caption != null) {
                    DOM.removeChild(captionWrapper, caption.getElement());
                    caption = null;
                    DOM.setElementAttribute(captionWrapper, "class", "");
                    widget.removeStyleName("i-orderedlayout-w-e");
                    caption.removeStyleName("i-orderedlayout-w-c");
                }
            }
        }

        /**
         * Set alignments for this wrapper.
         */
        void setAlignment(String verticalAlignment, String horizontalAlignment) {

            // use one-cell table to implement horizontal alignments, only
            // for values other than top-left (which is default)
            if (!horizontalAlignment.equals("left")
                    || !verticalAlignment.equals("top")) {

                // The previous positioning has been left (or unspecified).
                // Thus we need to create a one-cell-table to position
                // this element.
                if (alignmentTD == null) {

                    // Store and remove the current childs (widget and caption)
                    Element c1 = DOM
                            .getFirstChild(getElementWrappingWidgetAndCaption());
                    if (c1 != null) {
                        DOM.removeChild(getElementWrappingWidgetAndCaption(),
                                c1);
                    }
                    Element c2 = DOM
                            .getFirstChild(getElementWrappingWidgetAndCaption());
                    if (c2 != null) {
                        DOM.removeChild(getElementWrappingWidgetAndCaption(),
                                c2);
                    }

                    // Construct table structure to align children
                    final String t = "<table cellpadding='0' cellspacing='0' width='100%' height='100%'><tbody><tr><td>"
                            + "<table cellpadding='0' cellspacing='0' ><tbody><tr><td align='left'>"
                            + "</td></tr></tbody></table></td></tr></tbody></table>";
                    DOM.setInnerHTML(getElementWrappingWidgetAndCaption(), t);
                    alignmentTD = DOM
                            .getFirstChild(DOM
                                    .getFirstChild(DOM
                                            .getFirstChild(DOM
                                                    .getFirstChild(getElementWrappingWidgetAndCaption()))));
                    innermostTDinAlignmnetStructure = DOM.getFirstChild(DOM
                            .getFirstChild(DOM.getFirstChild(DOM
                                    .getFirstChild(alignmentTD))));

                    // Restore children inside the
                    if (c1 != null) {
                        DOM.appendChild(innermostTDinAlignmnetStructure, c1);
                        if (c2 != null) {
                            DOM
                                    .appendChild(
                                            innermostTDinAlignmnetStructure, c2);
                        }
                    }

                } else {

                    // Go around optimization bug in WebKit and ensure repaint
                    if (BrowserInfo.get().isSafari()) {
                        String prevValue = DOM.getElementAttribute(alignmentTD,
                                "align");
                        if (!horizontalAlignment.equals(prevValue)) {
                            Element parent = DOM.getParent(alignmentTD);
                            DOM.removeChild(parent, alignmentTD);
                            DOM.appendChild(parent, alignmentTD);
                        }
                    }

                }

                // Set the alignment in td
                DOM.setElementAttribute(alignmentTD, "align",
                        horizontalAlignment);
                DOM.setElementAttribute(alignmentTD, "valign",
                        verticalAlignment);

            } else {

                // In this case we are requested to position this left
                // while as it has had some other position in the past.
                // Thus the one-cell wrapper table must be removed.
                if (alignmentTD != null) {

                    // Move content to main container
                    final Element itd = innermostTDinAlignmnetStructure;
                    final Element alignmentTable = DOM.getParent(DOM
                            .getParent(DOM.getParent(alignmentTD)));
                    final Element target = DOM.getParent(alignmentTable);
                    while (DOM.getChildCount(itd) > 0) {
                        Element content = DOM.getFirstChild(itd);
                        if (content != null) {
                            DOM.removeChild(itd, content);
                            DOM.appendChild(target, content);
                        }
                    }

                    // Remove unneeded table element
                    DOM.removeChild(target, alignmentTable);

                    alignmentTD = innermostTDinAlignmnetStructure = null;
                }
            }
        }

        /** Set class for spacing */
        void setSpacingAndMargins(boolean first, boolean last) {

            final Element e = getElementWrappingWidgetAndCaption();

            if (orientationMode == ORIENTATION_HORIZONTAL) {
                DOM.setStyleAttribute(e, "paddingLeft", first ? (margins
                        .hasLeft() ? marginLeft + "px" : "0")
                        : (hasComponentSpacing ? hSpacing + "px" : "0"));
                DOM.setStyleAttribute(e, "paddingRight", last ? (margins
                        .hasRight() ? marginRight + "px" : "0") : "");
                DOM.setStyleAttribute(e, "paddingTop",
                        margins.hasTop() ? marginTop + "px" : "");
                DOM.setStyleAttribute(e, "paddingBottom",
                        margins.hasBottom() ? marginBottom + "px" : "");
            } else {
                DOM.setStyleAttribute(e, "paddingLeft",
                        margins.hasLeft() ? marginLeft + "px" : "0");
                DOM.setStyleAttribute(e, "paddingRight",
                        margins.hasRight() ? marginRight + "px" : "0");
                DOM.setStyleAttribute(e, "paddingTop", first ? (margins
                        .hasTop() ? marginTop + "px" : "")
                        : (hasComponentSpacing ? vSpacing + "px" : "0"));
                DOM.setStyleAttribute(e, "paddingBottom", last
                        && margins.hasBottom() ? marginBottom + "px" : "");
            }
        }
    }

    /* documented at super */
    public void add(Widget child) {
        add(child, childWidgets.size());
    }

    /**
     * Add widget to this layout at given position.
     * 
     * This methods supports reinserting exiting child into layout - it just
     * moves the position of the child in the layout.
     */
    public void add(Widget child, int atIndex) {
        /*
         * <b>Validate:</b> Perform any sanity checks to ensure the Panel can
         * accept a new Widget. Examples: checking for a valid index on
         * insertion; checking that the Panel is not full if there is a max
         * capacity.
         */
        if (atIndex < 0 || atIndex > childWidgets.size()) {
            return;
        }

        /*
         * <b>Adjust for Reinsertion:</b> Some Panels need to handle the case
         * where the Widget is already a child of this Panel. Example: when
         * performing a reinsert, the index might need to be adjusted to account
         * for the Widget's removal. See
         * {@link ComplexPanel#adjustIndex(Widget, int)}.
         */
        if (childWidgets.contains(child)) {
            if (childWidgets.indexOf(child) == atIndex) {
                return;
            }

            final int removeFromIndex = childWidgets.indexOf(child);
            final WidgetWrapper wrapper = (WidgetWrapper) childWidgetWrappers
                    .get(removeFromIndex);
            Element wrapperElement = wrapper.getElement();
            final int nonWidgetChildElements = DOM
                    .getChildCount(wrappedChildContainer)
                    - childWidgets.size();
            DOM.removeChild(wrappedChildContainer, wrapperElement);
            DOM.insertChild(wrappedChildContainer, wrapperElement, atIndex
                    + nonWidgetChildElements);
            childWidgets.remove(removeFromIndex);
            childWidgetWrappers.remove(removeFromIndex);
            childWidgets.insertElementAt(child, atIndex);
            childWidgetWrappers.insertElementAt(wrapper, atIndex);
            return;
        }

        /*
         * <b>Detach Child:</b> Remove the Widget from its existing parent, if
         * any. Most Panels will simply call {@link Widget#removeFromParent()}
         * on the Widget.
         */
        child.removeFromParent();

        /*
         * <b>Logical Attach:</b> Any state variables of the Panel should be
         * updated to reflect the addition of the new Widget. Example: the
         * Widget is added to the Panel's {@link WidgetCollection} at the
         * appropriate index.
         */
        childWidgets.insertElementAt(child, atIndex);

        /*
         * <b>Physical Attach:</b> The Widget's Element must be physically
         * attached to the Panel's Element, either directly or indirectly.
         */
        final WidgetWrapper wrapper = new WidgetWrapper();
        final int nonWidgetChildElements = DOM
                .getChildCount(wrappedChildContainer)
                - childWidgetWrappers.size();
        childWidgetWrappers.insertElementAt(wrapper, atIndex);
        DOM.insertChild(wrappedChildContainer, wrapper.getElement(), atIndex
                + nonWidgetChildElements);
        DOM.appendChild(wrapper.getElementWrappingWidgetAndCaption(), child
                .getElement());

        /*
         * <b>Adopt:</b> Call {@link #adopt(Widget)} to finalize the add as the
         * very last step.
         */
        adopt(child);
    }

    /* documented at super */
    public boolean remove(Widget child) {

        /*
         * <b>Validate:</b> Make sure this Panel is actually the parent of the
         * child Widget; return <code>false</code> if it is not.
         */
        if (!childWidgets.contains(child)) {
            return false;
        }

        /*
         * <b>Orphan:</b> Call {@link #orphan(Widget)} first while the child
         * Widget is still attached.
         */
        orphan(child);

        /*
         * <b>Physical Detach:</b> Adjust the DOM to account for the removal of
         * the child Widget. The Widget's Element must be physically removed
         * from the DOM.
         */
        final int index = childWidgets.indexOf(child);
        final WidgetWrapper wrapper = (WidgetWrapper) childWidgetWrappers
                .get(index);
        DOM.removeChild(wrappedChildContainer, wrapper.getElement());
        childWidgetWrappers.remove(index);

        /*
         * <b>Logical Detach:</b> Update the Panel's state variables to reflect
         * the removal of the child Widget. Example: the Widget is removed from
         * the Panel's {@link WidgetCollection}.
         */
        childWidgets.remove(index);

        return true;
    }

    /* documented at super */
    public boolean hasChildComponent(Widget component) {
        return childWidgets.contains(component);
    }

    /* documented at super */
    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        final int index = childWidgets.indexOf(oldComponent);
        if (index >= 0) {
            client.unregisterPaintable((Paintable) oldComponent);
            remove(oldComponent);
            add(newComponent, index);
        }
    }

    /* documented at super */
    public void updateCaption(Paintable component, UIDL uidl) {
        final int index = childWidgets.indexOf(component);
        if (index >= 0) {
            ((WidgetWrapper) childWidgetWrappers.get(index)).updateCaption(
                    uidl, component);
        }
    }

    /* documented at super */
    public Iterator iterator() {
        return childWidgets.iterator();
    }

    /* documented at super */
    public void iLayout() {
        updateChildHeights();
        updateChildWidths();
        Util.runDescendentsLayout(this);
        childLayoutsHaveChanged = false;
    }
}
