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
     * Contains reference to Element where Paintables are wrapped. Normally a TR
     * or a TBODY element.
     */
    protected Element childContainer;

    /*
     * Elements that provides the Layout interface implementation.
     */
    protected Element root;
    protected Element margin;

    private boolean hasComponentSpacing;

    private MarginInfo margins = new MarginInfo(0);

    public IOrderedLayout(int orientation) {
        orientationMode = orientation;
        constructDOM();
        setStyleName(CLASSNAME);
    }

    protected void constructDOM() {
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

        //
        hasComponentSpacing = uidl.getBooleanAttribute("spacing");

        // Update contained components

        final ArrayList uidlWidgets = new ArrayList();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL uidlForChild = (UIDL) it.next();
            final Paintable child = client.getPaintable(uidlForChild);
            uidlWidgets.add(child);
        }

        final ArrayList oldWidgets = getChildrenAsArraylist();
        final Iterator oldIt = oldWidgets.iterator();
        final Iterator newIt = uidlWidgets.iterator();
        final Iterator newUidl = uidl.getChildIterator();
        final ArrayList paintedWidgets = new ArrayList();

        Widget oldChild = null;
        while (newIt.hasNext()) {
            final Widget child = (Widget) newIt.next();
            final UIDL childUidl = (UIDL) newUidl.next();

            if (oldChild == null && oldIt.hasNext()) {
                // search for next old Paintable which still exists in layout
                // and delete others
                while (oldIt.hasNext()) {
                    oldChild = (Widget) oldIt.next();
                    // now oldChild is an instance of Paintable
                    if (paintedWidgets.contains(oldChild)) {
                        continue;
                    } else if (uidlWidgets.contains(oldChild)) {
                        break;
                    } else {
                        removePaintable((Paintable) oldChild);
                        oldChild = null;
                    }
                }
            }
            if (oldChild == null) {
                // we are adding components to layout
                add(child);
            } else if (child == oldChild) {
                // child already attached and updated
                oldChild = null;
            } else if (hasChildComponent(child)) {
                // current child has been moved, re-insert before current
                // oldChild
                // TODO this might be optimized by moving only container element
                // to correct position
                int index = getPaintableIndex(oldChild);
                remove(child);
                this.insert(child, index);
            } else {
                // insert new child before old one
                final int index = getPaintableIndex(oldChild); // TODO this
                insert(child, index);
            }
            ((Paintable) child).updateFromUIDL(childUidl, client);
            paintedWidgets.add(child);
        }

        // remove possibly remaining old Paintable object which were not updated
        while (oldIt.hasNext()) {
            oldChild = (Widget) oldIt.next();
            final Paintable p = (Paintable) oldChild;
            if (!uidlWidgets.contains(p)) {
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
            if (Caption.isNeeded(uidl)) {
                boolean justAdded = false;
                if (caption == null) {
                    justAdded = true;
                    caption = new Caption(paintable, client);
                }
                caption.updateCaption(uidl);
                final boolean after = caption.shouldBePlacedAfterComponent();
                final Element captionElement = caption.getElement();
                final Element widgetElement = ((Widget) paintable).getElement();
                String currentWidgetClass = DOM.getElementAttribute(
                        widgetElement, "class");
                if (null == currentWidgetClass) {
                    currentWidgetClass = "";
                }
                if (justAdded) {
                    if (after) {
                        DOM.appendChild(getElement(), captionElement);
                        DOM.setElementAttribute(getElement(), "class",
                                "i-orderedlayout-wrap");
                        DOM.setElementAttribute(widgetElement, "class",
                                currentWidgetClass + " i-orderedlayout-wrap-e");
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
                        DOM.setElementAttribute(widgetElement, "class",
                                currentWidgetClass + " i-orderedlayout-wrap-e");
                    } else {
                        removeClass(widgetElement, "i-orderedlayout-wrap-e");
                    }
                }

            } else {
                if (caption != null) {
                    DOM.removeChild(getElement(), caption.getElement());
                    caption = null;
                    DOM.setElementAttribute(getElement(), "class", "");
                    removeClass(DOM.getFirstChild(getElement()),
                            "i-orderedlayout-wrap-e");
                }
            }
        }

        private void removeClass(Element e, String name) {
            String classes = DOM.getElementAttribute(e, "class");
            if (e == null) {
                return;
            }
            int i = classes.indexOf(name);
            if (i < 0) {
                return;
            }
            while (i >= 0) {
                classes = classes.substring(0, i)
                        + classes.substring(i + name.length());
                i = classes.indexOf(name);
            }
            DOM.setElementAttribute(e, "class", classes);
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
