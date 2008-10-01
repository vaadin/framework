/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.StyleConstants;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;

/**
 * @author IT Mill Ltd
 */
public class IExpandLayout extends ComplexPanel implements
        ContainerResizedListener, Container {

    public static final String CLASSNAME = "i-expandlayout";
    public static final int ORIENTATION_HORIZONTAL = 1;

    public static final int ORIENTATION_VERTICAL = 0;

    /**
     * Minimum pixels reserved for expanded element to avoid "odd" situations
     * where expanded element is 0 size. Default is 5 pixels to show user a hint
     * that there is a component. Then user can often use splitpanel or resize
     * window to show component properly. This value may be insane in some
     * applications. Override this to specify a proper for your case.
     */
    protected static final int EXPANDED_ELEMENTS_MIN_WIDTH = 5;

    /**
     * Contains reference to Element where Paintables are wrapped.
     */
    protected Element childContainer;

    protected ApplicationConnection client = null;

    protected HashMap<Paintable, ICaption> componentToCaption = new HashMap<Paintable, ICaption>();

    /*
     * Elements that provides the Layout interface implementation.
     */
    protected Element element;
    private Widget expandedWidget = null;

    private UIDL expandedWidgetUidl;

    int orientationMode = ORIENTATION_VERTICAL;

    protected int topMargin = -1;
    private String width;
    private String height;
    private Element marginElement;
    private Element breakElement;
    private int bottomMargin = -1;
    private boolean hasComponentSpacing;
    private int spacingSize = -1;
    private boolean rendering;

    private RenderInformation renderInformation = new RenderInformation();
    private int spaceForExpandedWidget;

    public IExpandLayout() {
        this(IExpandLayout.ORIENTATION_VERTICAL);
    }

    public IExpandLayout(int orientation) {
        orientationMode = orientation;
        constructDOM();
        setStyleName(CLASSNAME);
    }

    public void add(Widget w) {
        final WidgetWrapper wrapper = createWidgetWrappper();
        DOM.appendChild(childContainer, wrapper.getElement());
        super.add(w, wrapper.getContainerElement());
    }

    protected void constructDOM() {
        element = DOM.createDiv();
        // DOM.setStyleAttribute(element, "overflow", "hidden");

        if (orientationMode == ORIENTATION_HORIZONTAL) {
            marginElement = DOM.createDiv();
            if (BrowserInfo.get().isIE()) {
                DOM.setStyleAttribute(marginElement, "zoom", "1");
                DOM.setStyleAttribute(marginElement, "overflow", "hidden");
            }
            childContainer = DOM.createDiv();
            if (BrowserInfo.get().isIE()) {
                DOM.setStyleAttribute(childContainer, "zoom", "1");
                DOM.setStyleAttribute(childContainer, "overflow", "hidden");
            }
            DOM.setStyleAttribute(childContainer, "height", "100%");
            breakElement = DOM.createDiv();
            DOM.setStyleAttribute(breakElement, "overflow", "hidden");
            DOM.setStyleAttribute(breakElement, "height", "0px");
            DOM.setStyleAttribute(breakElement, "clear", "both");
            DOM.appendChild(marginElement, childContainer);
            DOM.appendChild(marginElement, breakElement);
            DOM.appendChild(element, marginElement);
        } else {
            childContainer = DOM.createDiv();
            DOM.appendChild(element, childContainer);
            marginElement = childContainer;
        }
        setElement(element);
    }

    protected WidgetWrapper createWidgetWrappper() {
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            return new HorizontalWidgetWrapper();
        } else {
            return new VerticalWidgetWrapper();
        }
    }

    /**
     * Returns given widgets WidgetWrapper
     * 
     * @param child
     * @return
     */
    public WidgetWrapper getWidgetWrapperFor(Widget child) {
        final Element containerElement = DOM.getParent(child.getElement());
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            return new HorizontalWidgetWrapper(containerElement);
        } else {
            return new VerticalWidgetWrapper(DOM.getParent(containerElement));
        }
    }

    abstract class WidgetWrapper extends UIObject {

        /**
         * @return element that contains Widget
         */
        public Element getContainerElement() {
            return getElement();
        }

        public Element getCaptionContainer() {
            return getElement();
        }

        abstract void setExpandedSize(int pixels);

        abstract void setAlignment(String verticalAlignment,
                String horizontalAlignment);

        abstract void setSpacingEnabled(boolean b);
    }

    class VerticalWidgetWrapper extends WidgetWrapper {

        public VerticalWidgetWrapper(Element div) {
            setElement(div);
        }

        public VerticalWidgetWrapper() {
            setElement(DOM.createDiv());
            DOM.appendChild(getElement(), DOM.createDiv());
            DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            // Set to 'hidden' at first (prevent IE6 content overflows), and set
            // to 'auto' later.
            DOM.setStyleAttribute(getContainerElement(), "overflow", "hidden");
        }

        public void setExpandedSize(int pixels) {
            Element firstChild = DOM.getFirstChild(getElement());
            int captionHeight = 0;
            if (firstChild != getContainerElement()) {
                captionHeight = firstChild.getOffsetHeight();
            }
            int fixedInnerSize = pixels - captionHeight;
            if (fixedInnerSize < 0) {
                fixedInnerSize = 0;
            }
            DOM.setStyleAttribute(getContainerElement(), "height",
                    fixedInnerSize + "px");
        }

        void setAlignment(String verticalAlignment, String horizontalAlignment) {
            DOM.setStyleAttribute(getElement(), "textAlign",
                    horizontalAlignment);
            // ignoring vertical alignment
        }

        void setSpacingEnabled(boolean b) {
            setStyleName(getElement(), CLASSNAME + "-"
                    + StyleConstants.VERTICAL_SPACING, b);
        }

        public Element getContainerElement() {
            return getElement().getLastChild().cast();
        }

        public Element getCaptionElement() {
            return getElement();
        }

    }

    class HorizontalWidgetWrapper extends WidgetWrapper {

        private Element td;
        private String valign = "top";
        private String align = "left";

        public HorizontalWidgetWrapper(Element element) {
            if (DOM.getElementProperty(element, "nodeName").equals("TD")) {
                td = element;
                setElement(DOM.getParent(DOM.getParent(DOM.getParent(DOM
                        .getParent(td)))));
            } else {
                setElement(element);
            }
        }

        public HorizontalWidgetWrapper() {
            setElement(DOM.createDiv());
            DOM.setStyleAttribute(getElement(), "cssFloat", "left");
            if (BrowserInfo.get().isIE()) {
                DOM.setStyleAttribute(getElement(), "styleFloat", "left");
            }
            DOM.setStyleAttribute(getElement(), "height", "100%");
        }

        public void setExpandedSize(int pixels) {
            setWidth(pixels + "px");
            DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        }

        void setAlignment(String verticalAlignment, String horizontalAlignment) {
            DOM.setStyleAttribute(getElement(), "verticalAlign",
                    verticalAlignment);
            if (!valign.equals(verticalAlignment)) {
                if (verticalAlignment.equals("top")) {
                    // remove table, move content to div

                } else {
                    if (td == null) {
                        // build one cell table
                        final Element table = DOM.createTable();
                        final Element tBody = DOM.createTBody();
                        final Element tr = DOM.createTR();
                        td = DOM.createTD();
                        DOM.appendChild(table, tBody);
                        DOM.appendChild(tBody, tr);
                        DOM.appendChild(tr, td);
                        DOM.setElementProperty(table, "className", CLASSNAME
                                + "-valign");
                        DOM.setElementProperty(tr, "className", CLASSNAME
                                + "-valign");
                        DOM.setElementProperty(td, "className", CLASSNAME
                                + "-valign");
                        // move possible content to cell
                        final Element content = DOM.getFirstChild(getElement());
                        if (content != null) {
                            DOM.removeChild(getElement(), content);
                            DOM.appendChild(td, content);
                        }
                        DOM.appendChild(getElement(), table);
                    }
                    // set alignment
                    DOM.setStyleAttribute(td, "verticalAlign",
                            verticalAlignment);
                }
                valign = verticalAlignment;
            }
            if (!align.equals(horizontalAlignment)) {
                DOM.setStyleAttribute(getContainerElement(), "textAlign",
                        horizontalAlignment);
                align = horizontalAlignment;
            }
        }

        public Element getContainerElement() {
            if (td == null) {
                return super.getContainerElement();
            } else {
                return td;
            }
        }

        void setSpacingEnabled(boolean b) {
            setStyleName(getElement(), CLASSNAME + "-"
                    + StyleConstants.HORIZONTAL_SPACING, b);
        }
    }

    protected ArrayList getPaintables() {
        final ArrayList al = new ArrayList();
        final Iterator it = iterator();
        while (it.hasNext()) {
            final Widget w = (Widget) it.next();
            if (w instanceof Paintable) {
                al.add(w);
            }
        }
        return al;
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

    protected void handleAlignments(UIDL uidl) {
        // Component alignments as a comma separated list.
        // See com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.java for
        // possible values.
        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;
        // Set alignment attributes
        final Iterator it = getPaintables().iterator();
        boolean first = true;
        while (it.hasNext()) {
            // Calculate alignment info
            final AlignmentInfo ai = new AlignmentInfo(
                    alignments[alignmentIndex++]);
            final WidgetWrapper wr = getWidgetWrapperFor((Widget) it.next());
            wr.setAlignment(ai.getVerticalAlignment(), ai
                    .getHorizontalAlignment());
            if (first) {
                wr.setSpacingEnabled(false);
                first = false;
            } else {
                wr.setSpacingEnabled(hasComponentSpacing);
            }

        }
    }

    protected void handleMargins(UIDL uidl) {
        if (uidl.hasAttribute("margins")) {
            final MarginInfo margins = new MarginInfo(uidl
                    .getIntAttribute("margins"));
            setStyleName(marginElement, CLASSNAME + "-"
                    + StyleConstants.MARGIN_TOP, margins.hasTop());
            setStyleName(marginElement, CLASSNAME + "-"
                    + StyleConstants.MARGIN_RIGHT, margins.hasRight());
            setStyleName(marginElement, CLASSNAME + "-"
                    + StyleConstants.MARGIN_BOTTOM, margins.hasBottom());
            setStyleName(marginElement, CLASSNAME + "-"
                    + StyleConstants.MARGIN_LEFT, margins.hasLeft());
        }
    }

    public boolean hasChildComponent(Widget component) {
        return getWidgetIndex(component) >= 0;
    }

    public void iLayout() {
        renderInformation.updateSize(getElement());

        if (orientationMode == ORIENTATION_HORIZONTAL) {
            int pixels;
            if ("".equals(height)) {
                // try to find minimum height by looping all widgets
                int maxHeight = 0;
                Iterator iterator = getPaintables().iterator();
                while (iterator.hasNext()) {
                    Widget w = (Widget) iterator.next();
                    int h = w.getOffsetHeight();
                    if (h > maxHeight) {
                        maxHeight = h;
                    }
                }
                pixels = maxHeight;
            } else {
                pixels = getOffsetHeight() - getTopMargin() - getBottomMargin();
                if (pixels < 0) {
                    pixels = 0;
                }
            }
            DOM.setStyleAttribute(marginElement, "height", pixels + "px");
            DOM.setStyleAttribute(marginElement, "overflow", "hidden");
        }

        if (expandedWidget == null) {
            return;
        }

        final int availableSpace = getAvailableSpace();

        // Cannot use root element for layout as it contains margins
        Element expandElement = expandedWidget.getElement();
        Element expandedParentElement = DOM.getParent(expandElement);
        if (orientationMode == ORIENTATION_VERTICAL) {
            renderInformation.setContentAreaWidth(expandedParentElement
                    .getOffsetWidth());
        } else {
            renderInformation.setContentAreaHeight(expandedParentElement
                    .getOffsetHeight());

        }

        final int usedSpace = getUsedSpace();

        spaceForExpandedWidget = availableSpace - usedSpace;

        if (spaceForExpandedWidget < EXPANDED_ELEMENTS_MIN_WIDTH) {
            // TODO fire warning for developer
            spaceForExpandedWidget = EXPANDED_ELEMENTS_MIN_WIDTH;
        }

        final WidgetWrapper wr = getWidgetWrapperFor(expandedWidget);
        wr.setExpandedSize(spaceForExpandedWidget);

        /*
         * Workaround for issue #2093. Gecko base brosers have some rounding
         * issues every now and then. If all elements didn't fit on same row,
         * decrease expanded space until they do.
         */
        if (orientationMode == ORIENTATION_HORIZONTAL
                && BrowserInfo.get().isGecko()) {
            int tries = 0;
            while (tries < 30
                    && spaceForExpandedWidget > EXPANDED_ELEMENTS_MIN_WIDTH
                    && isLastElementDropped()) {
                spaceForExpandedWidget--;
                wr.setExpandedSize(spaceForExpandedWidget);
            }
        }

        // setting overflow auto lazy off during layout function
        DOM.setStyleAttribute(expandedParentElement, "overflow", "hidden");

        // TODO save previous size and only propagate if really changed
        if (client != null) {
            client.runDescendentsLayout(this);
        }

        // setting overflow back to auto
        DOM.setStyleAttribute(expandedParentElement, "overflow", "auto");

    }

    /**
     * Helper method to build workaround for Gecko issue.
     * 
     * @return true if last element has dropped to another line
     */
    private boolean isLastElementDropped() {
        int firstTop = DOM.getAbsoluteTop(DOM.getFirstChild(childContainer));
        int lastTop = DOM.getAbsoluteTop(DOM.getChild(childContainer, (DOM
                .getChildCount(childContainer) - 1)));
        return firstTop != lastTop;
    }

    private int getTopMargin() {
        if (topMargin < 0) {
            topMargin = DOM.getElementPropertyInt(childContainer, "offsetTop")
                    - DOM.getElementPropertyInt(getElement(), "offsetTop");
        }
        if (topMargin < 0) {
            // FIXME shouldn't happen
            return 0;
        } else {
            return topMargin;
        }
    }

    private int getBottomMargin() {
        if (bottomMargin < 0) {
            bottomMargin = DOM
                    .getElementPropertyInt(marginElement, "offsetTop")
                    + DOM.getElementPropertyInt(marginElement, "offsetHeight")
                    - DOM.getElementPropertyInt(breakElement, "offsetTop");
            if (bottomMargin < 0) {
                // FIXME shouldn't happen
                return 0;
            }
        }
        return bottomMargin;
    }

    private int getUsedSpace() {
        int total = 0;
        final int widgetCount = getWidgetCount();
        final Iterator it = iterator();
        while (it.hasNext()) {
            final Widget w = (Widget) it.next();

            if (w instanceof Paintable && w != expandedWidget) {
                final WidgetWrapper wr = getWidgetWrapperFor(w);
                if (orientationMode == ORIENTATION_VERTICAL) {
                    total += wr.getOffsetHeight();
                } else {
                    total += wr.getOffsetWidth();
                }
            }
        }
        total += getSpacingSize() * (widgetCount - 1);
        return total;
    }

    private int getSpacingSize() {
        if (hasComponentSpacing) {
            if (spacingSize < 0) {
                final Element temp = DOM.createDiv();
                final WidgetWrapper wr = createWidgetWrappper();
                wr.setSpacingEnabled(true);
                DOM.appendChild(temp, wr.getElement());
                DOM.setStyleAttribute(temp, "position", "absolute");
                DOM.setStyleAttribute(temp, "top", "0");
                DOM.setStyleAttribute(temp, "visibility", "hidden");
                DOM.appendChild(RootPanel.getBodyElement(), temp);
                if (orientationMode == ORIENTATION_HORIZONTAL) {
                    spacingSize = DOM.getElementPropertyInt(wr.getElement(),
                            "offsetLeft");
                } else {
                    spacingSize = DOM.getElementPropertyInt(wr.getElement(),
                            "offsetTop");
                }
                DOM.removeChild(RootPanel.getBodyElement(), temp);
            }
            return spacingSize;
        } else {
            return 0;
        }
    }

    private int getAvailableSpace() {
        int size;
        if (orientationMode == ORIENTATION_VERTICAL) {
            if (BrowserInfo.get().isIE6()) {
                DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            }
            size = getOffsetHeight();
            if (BrowserInfo.get().isIE6()) {
                DOM.setStyleAttribute(getElement(), "overflow", "visible");
            }

            final int marginTop = DOM.getElementPropertyInt(DOM
                    .getFirstChild(marginElement), "offsetTop")
                    - DOM.getElementPropertyInt(element, "offsetTop");

            final Element lastElement = DOM.getChild(marginElement, (DOM
                    .getChildCount(marginElement) - 1));
            final int marginBottom = DOM.getElementPropertyInt(marginElement,
                    "offsetHeight")
                    + DOM.getElementPropertyInt(marginElement, "offsetTop")
                    - (DOM.getElementPropertyInt(lastElement, "offsetTop") + DOM
                            .getElementPropertyInt(lastElement, "offsetHeight"));
            size -= (marginTop + marginBottom);
        } else {
            // horizontal mode
            size = DOM.getElementPropertyInt(breakElement, "offsetWidth");
        }
        return size;
    }

    protected void insert(Widget w, int beforeIndex) {
        if (w instanceof ICaption) {
            final ICaption c = (ICaption) w;
            WidgetWrapper wrapper = getWidgetWrapperFor((Widget) c.getOwner());
            Element captionContainer = wrapper.getCaptionContainer();
            final Element captionElement = DOM.createDiv();
            DOM.insertChild(captionContainer, captionElement, 0);
            insert(w, captionElement, beforeIndex, false);
        } else {
            final WidgetWrapper wrapper = createWidgetWrappper();
            DOM.insertChild(childContainer, wrapper.getElement(), beforeIndex);
            insert(w, wrapper.getContainerElement(), beforeIndex, false);
        }
    }

    public boolean remove(int index) {
        return remove(getWidget(index));
    }

    public boolean remove(Widget w) {
        final WidgetWrapper ww = getWidgetWrapperFor(w);
        final boolean removed = super.remove(w);
        if (removed) {
            if (!(w instanceof ICaption)) {
                DOM.removeChild(childContainer, ww.getElement());
            }
            return true;
        }
        return false;
    }

    public void removeCaption(Widget w) {
        final ICaption c = componentToCaption.get(w);
        if (c != null) {
            this.remove(c);
            componentToCaption.remove(w);
        }
    }

    public boolean removePaintable(Paintable p) {
        final ICaption c = componentToCaption.get(p);
        if (c != null) {
            componentToCaption.remove(c);
            remove(c);
        }
        client.unregisterPaintable(p);
        if (expandedWidget == p) {
            expandedWidget = null;
        }
        return remove((Widget) p);
    }

    public void replaceChildComponent(Widget from, Widget to) {
        client.unregisterPaintable((Paintable) from);
        final ICaption c = componentToCaption.get(from);
        if (c != null) {
            remove(c);
            componentToCaption.remove(c);
        }
        final int index = getWidgetIndex(from);
        if (index >= 0) {
            remove(index);
            insert(to, index);
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {

        ICaption c = componentToCaption.get(component);

        boolean captionSizeMayHaveChanged = false;
        if (ICaption.isNeeded(uidl)) {
            if (c == null) {
                final int index = getWidgetIndex((Widget) component);
                c = new ICaption(component, client);
                insert(c, index);
                componentToCaption.put(component, c);
                captionSizeMayHaveChanged = true;
            }
            c.updateCaption(uidl);
        } else {
            if (c != null) {
                remove(c);
                componentToCaption.remove(component);
                captionSizeMayHaveChanged = true;
            }
        }
        if (!rendering && captionSizeMayHaveChanged) {
            iLayout();
        }
    }

    public void setWidth(String newWidth) {
        if (newWidth.equals(width)) {
            return;
        }
        width = newWidth;
        super.setWidth(width);
    }

    public void setHeight(String newHeight) {
        if (newHeight.equals(height)) {
            return;
        }
        height = newHeight;
        super.setHeight(height);
        if (orientationMode == ORIENTATION_HORIZONTAL) {
            iLayout();
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        rendering = true;

        this.client = client;

        // Modify layout margins
        handleMargins(uidl);

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }

        hasComponentSpacing = uidl.getBooleanAttribute("spacing");

        final ArrayList uidlWidgets = new ArrayList();
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL cellUidl = (UIDL) it.next();
            final Paintable child = client.getPaintable(cellUidl
                    .getChildUIDL(0));
            uidlWidgets.add(child);
            if (cellUidl.hasAttribute("expanded")) {
                expandedWidget = (Widget) child;
                expandedWidgetUidl = cellUidl.getChildUIDL(0);
            }
        }

        final ArrayList oldWidgets = getPaintables();

        final Iterator oldIt = oldWidgets.iterator();
        final Iterator newIt = uidlWidgets.iterator();
        final Iterator newUidl = uidl.getChildIterator();

        Widget oldChild = null;
        while (newIt.hasNext()) {
            final Widget child = (Widget) newIt.next();
            final UIDL childUidl = ((UIDL) newUidl.next()).getChildUIDL(0);
            if (oldChild == null && oldIt.hasNext()) {
                // search for next old Paintable which still exists in layout
                // and delete others
                while (oldIt.hasNext()) {
                    oldChild = (Widget) oldIt.next();
                    // now oldChild is an instance of Paintable
                    if (uidlWidgets.contains(oldChild)) {
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
                removeCaption(child);
                int index = getWidgetIndex(oldChild);
                if (componentToCaption.containsKey(oldChild)) {
                    index--;
                }
                remove(child);
                insert(child, index);
            } else {
                // insert new child before old one
                final int index = getWidgetIndex(oldChild);
                insert(child, index);
            }
            if (child != expandedWidget) {
                ((Paintable) child).updateFromUIDL(childUidl, client);
            }
        }
        // remove possibly remaining old Paintable object which were not updated
        while (oldIt.hasNext()) {
            oldChild = (Widget) oldIt.next();
            final Paintable p = (Paintable) oldChild;
            if (!uidlWidgets.contains(p)) {
                removePaintable(p);
            }
        }

        if (uidlWidgets.size() == 0) {
            return;
        }

        // Set component alignments
        handleAlignments(uidl);

        iLayout();

        /*
         * Expanded widget is updated after layout function so it has its
         * container fixed at the moment of updateFromUIDL.
         */
        if (expandedWidget != null) {
            ((Paintable) expandedWidget).updateFromUIDL(expandedWidgetUidl,
                    client);
            // setting overflow auto lazy, not to disturb possible layout
            // functions
            DOM.setStyleAttribute(DOM.getParent(expandedWidget.getElement()),
                    "overflow", "auto");

            /*
             * If a caption has been added we need to recalculate the space
             * available for the component
             */
            getWidgetWrapperFor(expandedWidget).setExpandedSize(
                    spaceForExpandedWidget);

        }

        // workaround for safari bug #1870
        float wkv = BrowserInfo.get().getWebkitVersion();
        if (wkv > 0 && wkv < 526.9) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    iLayout();
                }
            });
        }
        rendering = false;
    }

    public boolean requestLayout(Set<Paintable> child) {
        if (height != null && width != null) {
            /*
             * If the height and width has been specified for this container the
             * child components cannot make the size of the layout change
             */

            return true;
        }

        if (renderInformation.updateSize(getElement())) {
            /*
             * Size has changed so we let the child components know about the
             * new size.
             */
            iLayout();

            return false;
        } else {
            /*
             * Size has not changed so we do not need to propagate the event
             * further
             */
            return true;
        }

    }

    public Size getAllocatedSpace(Widget child) {
        int width = 0, height = 0;

        if (orientationMode == ORIENTATION_HORIZONTAL) {
            height = renderInformation.getContentAreaSize().getHeight();
            if (child == expandedWidget) {
                width = spaceForExpandedWidget;
            }
        } else {
            // VERTICAL
            width = renderInformation.getContentAreaSize().getWidth();
            if (child == expandedWidget) {
                height = spaceForExpandedWidget;

                ICaption caption = componentToCaption.get(child);
                if (caption != null) {
                    height -= caption.getOffsetHeight();
                }
            }
        }

        return new Size(width, height);
    }

}
