/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * Abstract base class for ordered layouts. Use either vertical or horizontal
 * subclass.
 * 
 * @author IT Mill Ltd
 */
public abstract class IOrderedLayout extends ComplexPanel implements Container {

    public static final String CLASSNAME = "i-orderedlayout";

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    int orientationMode = ORIENTATION_VERTICAL;

    private final HashMap widgetToWrapper = new HashMap();

    protected ApplicationConnection client;

    /**
     * Reference to Element where wrapped child Paintables are contained.
     * Normally a TR or a TBODY element.
     */
    private Element childContainer;

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
            childContainer = DOM.getFirstChild(DOM.getFirstChild(DOM
                    .getFirstChild(margin)));
        } else {
            childContainer = margin;
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
        final ArrayList newWidgets = new ArrayList();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL uidlForChild = (UIDL) it.next();
            final Paintable child = client.getPaintable(uidlForChild);
            newWidgets.add(child);
        }

        // Iterator for old widgets
        final Iterator oldWidgetsIterator = getChildrenAsArraylist().iterator();

        // Iterator for new widgets
        final Iterator newWidgetsIterator = newWidgets.iterator();

        // Iterator for new UIDL
        final Iterator newUIDLIterator = uidl.getChildIterator();

        // List to collect all now painted widgets to in order to remove
        // unpainted ones later
        final ArrayList paintedWidgets = new ArrayList();

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
                        removePaintable((Paintable) oldChild);
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
                // TODO this might be optimized by moving only container element
                // to correct position
                int index = getPaintableIndex(oldChild);
                remove(newChild);
                this.insert(newChild, index);
            } else {
                // insert new child before old one
                final int index = getPaintableIndex(oldChild); // TODO this
                insert(newChild, index);
            }

            // Update the child component
            ((Paintable) newChild).updateFromUIDL(newChildUIDL, client);

            // Add this newly handled component to the list of painted
            // components
            paintedWidgets.add(newChild);
        }

        // Remove possibly remaining old widgets which were not updated
        while (oldWidgetsIterator.hasNext()) {
            oldChild = (Widget) oldWidgetsIterator.next();
            final Paintable p = (Paintable) oldChild;
            if (!newWidgets.contains(p)) {
                removePaintable(p);
            }
        }

        // Handle component alignments
        handleAlignments(uidl);
    }

    private ArrayList getChildrenAsArraylist() {
        final ArrayList al = new ArrayList();
        final Iterator it = iterator();
        while (it.hasNext()) {
            al.add(it.next());
        }
        return al;
    }

    /**
     * Removes Paintable from DOM and its reference from ApplicationConnection.
     * 
     * Also removes Paintable's Caption if one exists
     * 
     * @param p
     *                Paintable to be removed
     */
    protected boolean removePaintable(Paintable p) {
        client.unregisterPaintable(p);
        return remove((Widget) p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.Layout#replaceChildComponent(com.google.gwt.user.client.ui.Widget,
     *      com.google.gwt.user.client.ui.Widget)
     */
    public void replaceChildComponent(Widget from, Widget to) {
        client.unregisterPaintable((Paintable) from);
        final int index = getPaintableIndex(from);
        if (index >= 0) {
            remove(index);
            insert(to, index);
        }
    }

    protected void insert(Widget w, int beforeIndex) {
        WidgetWrapper wr = new WidgetWrapper();
        widgetToWrapper.put(w, wr);
        DOM.insertChild(childContainer, wr.getElement(), beforeIndex);
        insert(w, wr.getContainerElement(), beforeIndex, false);
    }

    public boolean hasChildComponent(Widget component) {
        return getPaintableIndex(component) >= 0;
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        ((WidgetWrapper) widgetToWrapper.get(component)).updateCaption(uidl,
                component);
    }

    public void add(Widget w) {
        WidgetWrapper wr = new WidgetWrapper();
        widgetToWrapper.put(w, wr);
        DOM.appendChild(childContainer, wr.getElement());
        super.add(w, wr.getContainerElement());
    }

    public boolean remove(int index) {
        return remove(getWidget(index));
    }

    public boolean remove(Widget w) {
        final Element wrapper = ((WidgetWrapper) widgetToWrapper.get(w))
                .getElement();
        final boolean removed = super.remove(w);
        if (removed) {
            DOM.removeChild(childContainer, wrapper);
            widgetToWrapper.remove(w);
            return true;
        }
        return false;
    }

    public Widget getWidget(int index) {
        return getChildren().get(index);
    }

    public int getWidgetCount() {
        return getChildren().size();
    }

    public int getWidgetIndex(Widget child) {
        return getChildren().indexOf(child);
    }

    public int getPaintableCount() {
        int size = 0;
        for (Iterator it = getChildren().iterator(); it.hasNext();) {
            Widget w = (Widget) it.next();
            size++;
        }
        return size;
    }

    protected int getPaintableIndex(Widget child) {
        int i = 0;
        for (Iterator it = getChildren().iterator(); it.hasNext();) {
            Widget w = (Widget) it.next();
            if (w == child) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
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

    protected void handleAlignments(UIDL uidl) {
        // Component alignments as a comma separated list.
        // See com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.java for
        // possible values.
        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;
        // Insert alignment attributes
        final Iterator it = getChildrenAsArraylist().iterator();
        boolean first = true;
        while (it.hasNext()) {

            // Calculate alignment info
            final AlignmentInfo ai = new AlignmentInfo(
                    alignments[alignmentIndex++]);

            final WidgetWrapper wr = ((WidgetWrapper) widgetToWrapper.get(it
                    .next()));
            wr.setAlignment(ai.getVerticalAlignment(), ai
                    .getHorizontalAlignment());

            // Handle spacing in this loop as well
            if (first) {
                wr.setSpacingEnabled(false);
                first = false;
            } else {
                wr.setSpacingEnabled(hasComponentSpacing);
            }
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
                if (Util.isIE()) {
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
            if (Util.isIE()) {
                DOM.setElementAttribute(getElement(), "vAlign",
                        verticalAlignment);
            } else {
                DOM.setStyleAttribute(getElement(), "verticalAlign",
                        verticalAlignment);
            }

            // Set horizontal alignment
            if (Util.isIE()) {
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
}
