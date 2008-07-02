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
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * Abstract base class for ordered layouts. Use either vertical or horizontal
 * subclass.
 * 
 * @author IT Mill Ltd
 */
public abstract class IOrderedLayout extends Panel implements Container {

    public static final String CLASSNAME = "i-orderedlayout";

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

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
    private Element root;

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
     * List of child widget wrappers. These wrappers are in exact same indexes
     * as the widgets in childWidgets list.
     */
    private final Vector childWidgetWrappers = new Vector();

    /** Whether the component has spacing enabled. */
    private boolean hasComponentSpacing;

    /** Information about margin states. */
    private MarginInfo margins = new MarginInfo(0);

    /** Construct a nre IOrderedLayout in given orientation mode. */
    public IOrderedLayout(int orientation) {
        orientationMode = orientation;
        constructDOM();
        setStyleName(CLASSNAME);
    }

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
    private void constructDOM() {
        root = DOM.createDiv();
        margin = DOM.createDiv();
        DOM.appendChild(root, margin);
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            final String structure = "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr></tr></tbody></table>";
            DOM.setInnerHTML(margin, structure);
            wrappedChildContainer = DOM.getFirstChild(DOM.getFirstChild(DOM
                    .getFirstChild(margin)));
        } else {
            wrappedChildContainer = margin;
        }
        setElement(root);
    }

    /** Update the contents of the layout from UIDL. */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        this.client = client;

        // Ensure correct implementation
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // Handle layout margins
        if (margins.getBitMask() != uidl.getIntAttribute("margins")) {
            handleMargins(uidl);
        }

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
    }

    protected void handleMargins(UIDL uidl) {
        margins = new MarginInfo(uidl.getIntAttribute("margins"));
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_TOP,
                margins.hasTop());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_RIGHT,
                margins.hasRight());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_BOTTOM,
                margins.hasBottom());
        setStyleName(margin, CLASSNAME + "-" + StyleConstants.MARGIN_LEFT,
                margins.hasLeft());
    }

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
     * WidgetWrapper classe. Helper classe for spacing and alignment handling.
     * 
     */
    class WidgetWrapper extends UIObject {

        Element td;
        Caption caption = null;

        public WidgetWrapper() {
            if (orientationMode == ORIENTATION_VERTICAL) {
                setElement(DOM.createDiv());
                // Apply 'hasLayout' for IE (needed to get accurate dimension
                // calculations)
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(getElement(), "zoom", "1");
                }
            } else {
                setElement(DOM.createTD());
            }
        }

        public void updateCaption(UIDL uidl, Paintable paintable) {
            final Widget widget = (Widget) paintable;
            if (Caption.isNeeded(uidl)) {
                boolean justAdded = false;
                if (caption == null) {
                    justAdded = true;
                    caption = new Caption(paintable, client);
                }
                caption.updateCaption(uidl);
                final boolean after = caption.shouldBePlacedAfterComponent();
                final Element captionElement = caption.getElement();
                final Element widgetElement = widget.getElement();
                if (justAdded) {
                    if (after) {
                        DOM.appendChild(getElement(), captionElement);
                        DOM.setElementAttribute(getElement(), "class",
                                "i-orderedlayout-wrap");
                        widget.addStyleName("i-orderedlayout-wrap-e");
                    } else {
                        DOM.insertChild(getElement(), captionElement, 0);
                    }

                } else
                // Swap caption and widget if needed or add
                if (after == (DOM.getChildIndex(getElement(), widgetElement) > DOM
                        .getChildIndex(getElement(), captionElement))) {
                    Element firstElement = DOM.getChild(getElement(), DOM
                            .getChildCount(getElement()) - 2);
                    DOM.removeChild(getElement(), firstElement);
                    DOM.appendChild(getElement(), firstElement);
                    DOM.setElementAttribute(getElement(), "class",
                            after ? "i-orderedlayout-wrap" : "");
                    if (after) {
                        widget.addStyleName("i-orderedlayout-wrap-e");
                    } else {
                        widget.removeStyleName("i-orderedlayout-wrap-e");
                    }
                }

            } else {
                if (caption != null) {
                    DOM.removeChild(getElement(), caption.getElement());
                    caption = null;
                    DOM.setElementAttribute(getElement(), "class", "");
                    widget.removeStyleName("i-orderedlayout-wrap-e");
                }
            }
        }

        Element getContainerElement() {
            if (td != null) {
                return td;
            } else {
                return getElement();
            }
        }

        void setAlignment(String verticalAlignment, String horizontalAlignment) {

            // Set vertical alignment
            if (BrowserInfo.get().isIE()) {
                DOM.setElementAttribute(getElement(), "vAlign",
                        verticalAlignment);
            } else {
                DOM.setStyleAttribute(getElement(), "verticalAlign",
                        verticalAlignment);
            }

            // Set horizontal alignment
            if (BrowserInfo.get().isIE()) {
                DOM.setElementAttribute(getElement(), "align",
                        horizontalAlignment);
            } else {
                // use one-cell table to implement horizontal alignments, only
                // for values other than "left" (which is default)
                // build one cell table
                if (!horizontalAlignment.equals("left")) {
                    if (td == null) {
                        final Element table = DOM.createTable();
                        final Element tBody = DOM.createTBody();
                        final Element tr = DOM.createTR();
                        td = DOM.createTD();
                        DOM.appendChild(table, tBody);
                        DOM.appendChild(tBody, tr);
                        DOM.appendChild(tr, td);
                        DOM.setElementAttribute(table, "cellpadding", "0");
                        DOM.setElementAttribute(table, "cellspacing", "0");
                        DOM.setStyleAttribute(table, "width", "100%");
                        // use className for identification
                        DOM.setElementProperty(td, "className", "i_align");
                        // move possible content to cell
                        while (DOM.getChildCount(getElement()) > 0) {
                            Element content = DOM.getFirstChild(getElement());
                            if (content != null) {
                                DOM.removeChild(getElement(), content);
                                DOM.appendChild(td, content);
                            }
                        }
                        DOM.appendChild(getElement(), table);
                    }
                    DOM.setElementAttribute(td, "align", horizontalAlignment);
                } else if (td != null) {
                    // Move content to main container
                    while (DOM.getChildCount(td) > 0) {
                        Element content = DOM.getFirstChild(td);
                        if (content != null) {
                            DOM.removeChild(td, content);
                            DOM.appendChild(getElement(), content);
                        }
                    }
                    // Remove unneeded table element
                    DOM.removeChild(getElement(), DOM
                            .getFirstChild(getElement()));
                }
            }
        }

        void setSpacingEnabled(boolean b) {
            setStyleName(
                    getElement(),
                    CLASSNAME
                            + "-"
                            + (orientationMode == ORIENTATION_HORIZONTAL ? StyleConstants.HORIZONTAL_SPACING
                                    : StyleConstants.VERTICAL_SPACING), b);
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

}
