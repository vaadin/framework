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
     * Reference to Element where wrapped childred are contained. Normally a TR
     * or a TBODY element.
     */
    private Element wrappedChildContainer;

    /**
     * Elements that provides the Layout interface implementation. Root element
     * of the component. In vertical mode this is the outmost div.
     */
    private final Element root;

    /**
     * Margin element of the component. In vertical mode, this is div inside
     * root.
     */
    protected Element margin;

    /**
     * List of child widgets. This is not the list of wrappers, but the actual
     * widgets
     */
    private final Vector childWidgets = new Vector();

    /**
     * Fixed cell-size mode is used when height/width is explicitly given for
     * vertical/horizontal orderedlayout.
     */
    private boolean fixedCellSize = false;

    /**
     * List of child widget wrappers. These wrappers are in exact same indexes
     * as the widgets in childWidgets list.
     */
    private final Vector childWidgetWrappers = new Vector();

    /** Whether the component has spacing enabled. */
    private boolean hasComponentSpacing;

    /** Whether the component has spacing enabled. */
    private int previouslyAppliedFixedSize = -1;

    /** Information about margin states. */
    private MarginInfo margins = new MarginInfo(0);

    /**
     * Flag that indicates that the child layouts must be updated as soon as
     * possible.
     */
    private boolean childLayoutsHaveChanged = false;

    /**
     * Construct the DOM of the orderder layout.
     * 
     * <p>
     * There are two modes - vertical and horizontal.
     * <ul>
     * <li>Vertical mode uses structure: div-root ( div-margin-childcontainer (
     * div-wrap ( child ) div-wrap ( child )))).</li>
     * <li>Horizontal mode uses structure: div-root ( div-margin ( table (
     * tbody ( tr-childcontainer ( td-wrap ( child ) td-wrap ( child) )) )</li>
     * </ul>
     * where root, margin and childcontainer refer to the root element, margin
     * element and the element that contain WidgetWrappers.
     * </p>
     * 
     */
    public IOrderedLayout() {

        root = DOM.createDiv();
        margin = DOM.createDiv();
        DOM.appendChild(root, margin);
        createAndEmptyWrappedChildContainer();
        setElement(root);
        setStyleName(CLASSNAME);
    }

    /**
     * Constuct base DOM-scrtucture and clean any already attached
     * widgetwrappers from DOM.
     */
    private void createAndEmptyWrappedChildContainer() {
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            final String structure = "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr></tr></tbody></table>";
            DOM.setInnerHTML(margin, structure);
            wrappedChildContainer = DOM.getFirstChild(DOM.getFirstChild(DOM
                    .getFirstChild(margin)));
            DOM.setStyleAttribute(root, "display", "table");
            DOM.setStyleAttribute(margin, "display", "table");
        } else {
            wrappedChildContainer = margin;
            DOM.setInnerHTML(margin, "");
            DOM.setStyleAttribute(root, "display", "block");
            DOM.setStyleAttribute(margin, "display", "block");
        }
    }

    /**
     * Update orientation, if it has changed.
     * 
     * @param newOrientationMode
     */
    private void updateOrientation(UIDL uidl) {

        // Parse new mode from UIDL
        int newOrientationMode = "horizontal".equals(uidl
                .getStringAttribute("orientation")) ? ORIENTATION_HORIZONTAL
                : ORIENTATION_VERTICAL;

        // Only change the mode if when needed
        if (orientationMode == newOrientationMode) {
            return;
        }

        // Remove fixed state
        removeFixedSizes();

        orientationMode = newOrientationMode;

        createAndEmptyWrappedChildContainer();

        // Reinsert all widget wrappers to this container
        for (int i = 0; i < childWidgetWrappers.size(); i++) {
            WidgetWrapper wr = (WidgetWrapper) childWidgetWrappers.get(i);
            Element oldWrElement = wr.resetRootElement();
            Element newWrElement = wr.getElement();
            String oldStyle = DOM.getElementAttribute(oldWrElement, "class");
            if (oldStyle != null) {
                DOM.setElementAttribute(newWrElement, "class", oldStyle);
            }
            while (DOM.getChildCount(oldWrElement) > 0) {
                Element c = DOM.getFirstChild(oldWrElement);
                DOM.removeChild(oldWrElement, c);
                DOM.appendChild(newWrElement, c);
            }

            DOM.appendChild(wrappedChildContainer, newWrElement);
        }

        // Reconsider being fixed
        if ((orientationMode == ORIENTATION_HORIZONTAL && "100%".equals(DOM
                .getStyleAttribute(margin, "width")))
                || (orientationMode == ORIENTATION_VERTICAL && "100%"
                        .equals(DOM.getStyleAttribute(margin, "height")))) {
            fixedCellSize = true;
            updateFixedSizes();
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Update the contents of the layout from UIDL. */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        this.client = client;

        // Only non-cached UIDL:s can introduce changes
        if (!uidl.getBooleanAttribute("cached")) {

            updateMarginAndSpacingSizesFromCSS(uidl);

            // Swith between orientation modes if necessary
            updateOrientation(uidl);

            // Handle layout margins
            if (margins.getBitMask() != uidl.getIntAttribute("margins")) {
                handleMargins(uidl);
            }

            // Handle component spacing later in handleAlignments() method
            hasComponentSpacing = uidl.getBooleanAttribute("spacing");
        }

        // Update sizes, ...
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

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
            ((Paintable) newChild).updateFromUIDL(newChildUIDL, client);

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
        handleAlignments(uidl);

        // If the layout has fixed width|height, recalculate cell-sizes
        updateFixedSizes();

        // Update child layouts
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
    public void setWidth(String width) {
        super.setWidth(width);

        if (width == null || "".equals(width)) {
            DOM.setStyleAttribute(margin, "width", "");
            DOM.setStyleAttribute(margin, "overflowX", "");

            if (fixedCellSize && orientationMode == ORIENTATION_HORIZONTAL) {
                removeFixedSizes();
            }
        } else {

            // Calculate margin pixel width
            int cw = DOM.getElementPropertyInt(root, "offsetWidth");
            cw -= margins.hasLeft() ? marginLeft : 0;
            cw -= margins.hasRight() ? marginRight : 0;
            DOM.setStyleAttribute(margin, "width", cw + "px");
            DOM.setStyleAttribute(margin, "overflowX", "hidden");

            if (orientationMode == ORIENTATION_HORIZONTAL) {
                fixedCellSize = true;
            }
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /**
     * While setting height, ensure that margin div is also resized properly.
     * Furthermore, enable/disable fixed mode
     */
    public void setHeight(String height) {
        super.setHeight(height);

        if (height == null || "".equals(height)) {
            DOM.setStyleAttribute(margin, "height", "");
            DOM.setStyleAttribute(margin, "overflowY", "");

            // Removing fixed size is needed only when it is in use
            if (fixedCellSize && orientationMode == ORIENTATION_VERTICAL) {
                removeFixedSizes();
            }
        } else {

            // Calculate margin pixel height
            int ch = DOM.getElementPropertyInt(root, "offsetHeight");
            ch -= margins.hasTop() ? marginTop : 0;
            ch -= margins.hasBottom() ? marginBottom : 0;
            DOM.setStyleAttribute(margin, "height", ch + "px");
            DOM.setStyleAttribute(margin, "overflowY", "hidden");

            // Turn on vertical orientation mode if needed
            if (orientationMode == ORIENTATION_VERTICAL) {
                fixedCellSize = true;
            }
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Remove fixed sizes from use */
    private void removeFixedSizes() {

        // If already removed, do not do it twice
        if (!fixedCellSize) {
            return;
        }

        // Remove unneeded attributes from each wrapper
        String wh = (orientationMode == ORIENTATION_HORIZONTAL) ? "width"
                : "height";
        for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
            Element we = ((WidgetWrapper) i.next()).getElement();
            DOM.setStyleAttribute(we, wh, "");
            DOM.setStyleAttribute(we, "overflow", "");
        }

        // margin
        DOM.setStyleAttribute(margin,
                (orientationMode == ORIENTATION_HORIZONTAL) ? "width"
                        : "height", "");

        // Remove unneeded attributes from horizontal layouts table
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            Element table = DOM.getParent(DOM.getParent(wrappedChildContainer));
            DOM.setStyleAttribute(table, "tableLayout", "auto");
            DOM.setStyleAttribute(table, "width", "");
        }

        fixedCellSize = false;
        previouslyAppliedFixedSize = -1;

    }

    /** Reset the fixed cell-sizes for children. */
    private void updateFixedSizes() {

        // Do not do anything if we really should not be doing this
        if (!fixedCellSize) {
            return;
        }

        // Calculate the space for fixed contents minus marginals
        int size = DOM.getElementPropertyInt(root,
                (orientationMode == ORIENTATION_HORIZONTAL) ? "offsetWidth"
                        : "offsetHeight");
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            size -= margins.hasLeft() ? marginLeft : 0;
            size -= margins.hasRight() ? marginRight : 0;
        } else {
            size -= margins.hasTop() ? marginTop : 0;
            size -= margins.hasBottom() ? marginBottom : 0;
        }

        // Horizontal layouts need fixed mode tables
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            Element table = DOM.getParent(DOM.getParent(wrappedChildContainer));
            DOM.setStyleAttribute(table, "tableLayout", "fixed");
            DOM.setStyleAttribute(table, "width", "" + size + "px");
        }

        // Reduce spacing from the size
        int numChild = childWidgets.size();
        if (hasComponentSpacing) {
            size -= ((orientationMode == ORIENTATION_HORIZONTAL) ? hSpacing
                    : vSpacing)
                    * (numChild - 1);
        }

        // Have we set fixed sizes before?
        boolean firstTime = (previouslyAppliedFixedSize < 0);

        // If so, are they already correct?
        if (size == previouslyAppliedFixedSize) {
            return;
        }
        previouslyAppliedFixedSize = size;

        // Set the sizes for each child
        String wh = (orientationMode == ORIENTATION_HORIZONTAL) ? "width"
                : "height";
        for (Iterator i = childWidgetWrappers.iterator(); i.hasNext();) {
            Element we = ((WidgetWrapper) i.next()).getElement();
            final int ws = Math.round(((float) size) / (numChild--));
            size -= ws;
            DOM.setStyleAttribute(we, wh, "" + ws + "px");
            if (firstTime) {
                DOM.setStyleAttribute(we, "overflow", "hidden");
            }
        }

        fixedCellSize = true;

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Enable/disable margins classes for the margin div when needed */
    protected void handleMargins(UIDL uidl) {

        // Only update margins when they have changed
        MarginInfo newMargins = new MarginInfo(uidl.getIntAttribute("margins"));
        if (newMargins.equals(margins)) {
            return;
        }
        margins = newMargins;

        // Update margin classes
        DOM.setStyleAttribute(margin, "paddingTop",
                margins.hasTop() ? marginTop + "px" : "0");
        DOM.setStyleAttribute(margin, "paddingLeft",
                margins.hasLeft() ? marginLeft + "px" : "0");
        DOM.setStyleAttribute(margin, "paddingBottom",
                margins.hasBottom() ? marginBottom + "px" : "0");
        DOM.setStyleAttribute(margin, "paddingRight",
                margins.hasRight() ? marginRight + "px" : "0");

        // Update calculated height if needed
        String currentMarginHeight = DOM.getStyleAttribute(margin, "height");
        if (currentMarginHeight != null && !"".equals(currentMarginHeight)) {
            int ch = DOM.getElementPropertyInt(root, "offsetHeight");
            ch -= margins.hasTop() ? marginTop : 0;
            ch -= margins.hasBottom() ? marginBottom : 0;
            DOM.setStyleAttribute(margin, "height", ch + "px");
        }
        String currentMarginWidth = DOM.getStyleAttribute(margin, "width");
        if (currentMarginWidth != null && !"".equals(currentMarginWidth)) {
            int cw = DOM.getElementPropertyInt(root, "offsetWidth");
            cw -= margins.hasLeft() ? marginLeft : 0;
            cw -= margins.hasRight() ? marginRight : 0;
            DOM.setStyleAttribute(margin, "width", cw + "px");
        }

        // Update child layouts
        childLayoutsHaveChanged = true;
    }

    /** Parse alignments from UIDL and pass whem to correct widgetwrappers */
    private void handleAlignments(UIDL uidl) {

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

            // Handle spacing in this loop as well
            wr.setSpacingEnabled(alignmentIndex == 1 ? false
                    : hasComponentSpacing);
        }
    }

    /**
     * Cell contained in the orderedlayout. This helper also manages for spacing
     * and alignment for individual cells handling.
     * 
     */
    class WidgetWrapper extends UIObject {

        Element td;
        Caption caption = null;

        /** Set the root element */
        public WidgetWrapper() {
            resetRootElement();
        }

        /**
         * Create td or div - depending on the orientation of the layout and set
         * it as root.
         * 
         * @return Previous root element.
         */
        private Element resetRootElement() {
            Element e = getElement();
            if (orientationMode == ORIENTATION_VERTICAL) {
                setElement(DOM.createDiv());
                // Apply 'hasLayout' for IE (needed to get accurate dimension
                // calculations)
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(e, "zoom", "1");
                }
            } else {
                setElement(DOM.createTD());
            }
            return e;
        }

        /** Update the caption of the element contained in this wrapper. */
        public void updateCaption(UIDL uidl, Paintable paintable) {

            final Widget widget = (Widget) paintable;

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
                        DOM.appendChild(getElement(), captionElement);
                        DOM.setElementAttribute(getElement(), "class",
                                "i-orderedlayout-w");
                        caption.addStyleName("i-orderedlayout-c");
                        widget.addStyleName("i-orderedlayout-w-e");
                    } else {
                        DOM.insertChild(getElement(), captionElement, 0);
                    }

                } else

                // Caption exists. Move it to correct position if needed
                if (after == (DOM.getChildIndex(getElement(), widgetElement) > DOM
                        .getChildIndex(getElement(), captionElement))) {
                    Element firstElement = DOM.getChild(getElement(), DOM
                            .getChildCount(getElement()) - 2);
                    DOM.removeChild(getElement(), firstElement);
                    DOM.appendChild(getElement(), firstElement);
                    DOM.setElementAttribute(getElement(), "class",
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
                    DOM.removeChild(getElement(), caption.getElement());
                    caption = null;
                    DOM.setElementAttribute(getElement(), "class", "");
                    widget.removeStyleName("i-orderedlayout-w-e");
                    caption.removeStyleName("i-orderedlayout-w-c");
                }
            }
        }

        /**
         * Set alignments for this wrapper.
         */
        void setAlignment(String verticalAlignment, String horizontalAlignment) {

            // Set vertical alignment
            // TODO BROKEN #1903
            if (BrowserInfo.get().isIE()) {
                DOM.setElementAttribute(getElement(), "vAlign",
                        verticalAlignment);
            } else {
                DOM.setStyleAttribute(getElement(), "verticalAlign",
                        verticalAlignment);
            }

            // Set horizontal alignment

            // use one-cell table to implement horizontal alignments, only
            // for values other than "left" (which is default)
            // build one cell table
            if (!horizontalAlignment.equals("left")) {

                // The previous positioning has been left (or unspecified).
                // Thus we need to create a one-cell-table to position
                // this element.
                if (td == null) {

                    // Store and remove the current childs (widget and caption)
                    Element c1 = DOM.getFirstChild(getElement());
                    DOM.removeChild(getElement(), c1);
                    Element c2 = DOM.getFirstChild(getElement());
                    if (c2 != null) {
                        DOM.removeChild(getElement(), c2);
                    }

                    // Construct table structure to align children
                    final String t = "<table cellpadding='0' cellspacing='0' width='100%'><tbody><tr><td>"
                            + "<table cellpadding='0' cellspacing='0' ><tbody><tr><td align='left'>"
                            + "</td></tr></tbody></table></td></tr></tbody></table>";
                    DOM.setInnerHTML(getElement(), t);
                    td = DOM.getFirstChild(DOM.getFirstChild(DOM
                            .getFirstChild(DOM.getFirstChild(getElement()))));
                    Element itd = DOM.getFirstChild(DOM.getFirstChild(DOM
                            .getFirstChild(DOM.getFirstChild(td))));

                    // Restore children inside the
                    DOM.appendChild(itd, c1);
                    if (c2 != null) {
                        DOM.appendChild(itd, c2);
                    }

                } else {

                    // Go around optimization bug in WebKit and ensure repaint
                    if (BrowserInfo.get().isSafari()) {
                        String prevValue = DOM.getElementAttribute(td, "align");
                        if (!horizontalAlignment.equals(prevValue)) {
                            Element parent = DOM.getParent(td);
                            DOM.removeChild(parent, td);
                            DOM.appendChild(parent, td);
                        }
                    }

                }

                // Seth the alignment in td
                DOM.setElementAttribute(td, "align", horizontalAlignment);

            } else

            // In this case we are requested to position this left
            // while as it has had some other position in the past.
            // Thus the one-cell wrapper table must be removed.
            if (td != null) {

                // Move content to main container
                Element itd = DOM.getFirstChild(DOM.getFirstChild(DOM
                        .getFirstChild(DOM.getFirstChild(td))));
                while (DOM.getChildCount(itd) > 0) {
                    Element content = DOM.getFirstChild(itd);
                    if (content != null) {
                        DOM.removeChild(itd, content);
                        DOM.appendChild(getElement(), content);
                    }
                }

                // Remove unneeded table element
                DOM.removeChild(getElement(), DOM.getFirstChild(getElement()));

                td = null;
            }
        }

        /** Set class for spacing */
        void setSpacingEnabled(boolean b) {
            DOM.setStyleAttribute(getElement(),
                    orientationMode == ORIENTATION_HORIZONTAL ? "paddingLeft"
                            : "marginTop",
                    b ? (orientationMode == ORIENTATION_HORIZONTAL ? hSpacing
                            : vSpacing)
                            + "px" : "0");
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
        DOM.appendChild(wrapper.getElement(), child.getElement());

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
        updateFixedSizes();
        Util.runDescendentsLayout(this);
    }
}
