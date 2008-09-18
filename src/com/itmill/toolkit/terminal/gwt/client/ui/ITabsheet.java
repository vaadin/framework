/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ITabsheet extends ITabsheetBase implements
        ContainerResizedListener {

    class TabBar extends ComplexPanel implements ClickListener {

        private Element tr = DOM.createTR();

        private Element spacerTd = DOM.createTD();

        TabBar() {
            Element el = DOM.createTable();
            Element tbody = DOM.createTBody();
            DOM.appendChild(el, tbody);
            DOM.appendChild(tbody, tr);
            setStyleName(spacerTd, CLASSNAME + "-spacertd");
            DOM.appendChild(tr, spacerTd);
            DOM.appendChild(spacerTd, DOM.createDiv());
            setElement(el);
        }

        protected Element getContainerElement() {
            return tr;
        }

        private Widget oldSelected;

        public int getTabCount() {
            return getWidgetCount();
        }

        public void addTab(ICaption c) {
            Element td = DOM.createTD();
            setStyleName(td, CLASSNAME + "-tabitemcell");

            if (getWidgetCount() == 0) {
                setStyleName(td, CLASSNAME + "-tabitemcell-first", true);
            }

            Element div = DOM.createDiv();
            setStyleName(div, CLASSNAME + "-tabitem");
            DOM.appendChild(td, div);
            DOM.insertBefore(tr, td, spacerTd);
            c.addClickListener(this);
            add(c, div);
        }

        public void onClick(Widget sender) {
            int index = getWidgetIndex(sender);
            onTabSelected(index);
        }

        public void selectTab(int index) {
            Widget newSelected = getWidget(index);
            Widget.setStyleName(DOM.getParent(newSelected.getElement()),
                    CLASSNAME + "-tabitem-selected", true);
            if (oldSelected != null && oldSelected != newSelected) {
                Widget.setStyleName(DOM.getParent(oldSelected.getElement()),
                        CLASSNAME + "-tabitem-selected", false);
            }
            oldSelected = newSelected;
        }

        public void removeTab(int i) {
            remove(i);
        }

        public boolean remove(Widget w) {
            ((ICaption) w).removeClickListener(this);
            return super.remove(w);
        }

        public ICaption getTab(int index) {
            if (index >= getWidgetCount()) {
                return null;
            }
            return (ICaption) getWidget(index);
        }

    }

    public static final String CLASSNAME = "i-tabsheet";

    public static final String TABS_CLASSNAME = "i-tabsheet-tabcontainer";
    public static final String SCROLLER_CLASSNAME = "i-tabsheet-scroller";
    private final Element tabs; // tabbar and 'scroller' container
    private final Element scroller; // tab-scroller element
    private final Element scrollerNext; // tab-scroller next button element
    private final Element scrollerPrev; // tab-scroller prev button element
    private int scrollerIndex = 0;

    private final TabBar tb;
    private final ITabsheetPanel tp;
    private final Element contentNode, deco;

    private final HashMap captions = new HashMap();

    private String height;
    private String width;

    private boolean waitingForResponse;

    /**
     * Previous visible widget is set invisible with CSS (not display: none, but
     * visibility: hidden), to avoid flickering during render process. Normal
     * visibility must be returned later when new widget is rendered.
     */
    private Widget previousVisibleWidget;

    private void onTabSelected(final int tabIndex) {
        if (disabled || waitingForResponse) {
            return;
        }
        final Object tabKey = tabKeys.get(tabIndex);
        if (disabledTabKeys.contains(tabKey)) {
            return;
        }
        if (client != null && activeTabIndex != tabIndex) {
            tb.selectTab(tabIndex);
            addStyleDependentName("loading");
            // run updating variables in deferred command to bypass some
            // FF
            // optimization issues
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    previousVisibleWidget = tp.getWidget(tp.getVisibleWidget());
                    DOM.setStyleAttribute(previousVisibleWidget.getElement(),
                            "visibility", "hidden");
                    client.updateVariable(id, "selected", tabKeys.get(tabIndex)
                            .toString(), true);
                }
            });
            waitingForResponse = true;
        }
    }

    public ITabsheet() {
        super(CLASSNAME);

        // Tab scrolling
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        tabs = DOM.createDiv();
        DOM.setElementProperty(tabs, "className", TABS_CLASSNAME);
        scroller = DOM.createDiv();

        DOM.setElementProperty(scroller, "className", SCROLLER_CLASSNAME);
        scrollerPrev = DOM.createButton();
        DOM.setElementProperty(scrollerPrev, "className", SCROLLER_CLASSNAME
                + "Prev");
        DOM.sinkEvents(scrollerPrev, Event.ONCLICK);
        scrollerNext = DOM.createButton();
        DOM.setElementProperty(scrollerNext, "className", SCROLLER_CLASSNAME
                + "Next");
        DOM.sinkEvents(scrollerNext, Event.ONCLICK);
        DOM.appendChild(getElement(), tabs);

        // Tabs
        tb = new TabBar();
        tp = new ITabsheetPanel();
        tp.setStyleName(CLASSNAME + "-tabsheetpanel");
        contentNode = DOM.createDiv();

        deco = DOM.createDiv();

        addStyleDependentName("loading"); // Indicate initial progress
        tb.setStyleName(CLASSNAME + "-tabs");
        DOM
                .setElementProperty(contentNode, "className", CLASSNAME
                        + "-content");
        DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");

        add(tb, tabs);
        DOM.appendChild(scroller, scrollerPrev);
        DOM.appendChild(scroller, scrollerNext);

        DOM.appendChild(getElement(), contentNode);
        add(tp, contentNode);
        DOM.appendChild(getElement(), deco);

        DOM.appendChild(tabs, scroller);

        // TODO Use for Safari only. Fix annoying 1px first cell in TabBar.
        // DOM.setStyleAttribute(DOM.getFirstChild(DOM.getFirstChild(DOM
        // .getFirstChild(tb.getElement()))), "display", "none");

    }

    public void onBrowserEvent(Event event) {

        // Tab scrolling
        if (isScrolledTabs()
                && DOM.compare(DOM.eventGetTarget(event), scrollerPrev)) {
            if (scrollerIndex > 0) {
                scrollerIndex--;
                DOM.setStyleAttribute(DOM.getChild(DOM.getFirstChild(DOM
                        .getFirstChild(tb.getElement())), scrollerIndex),
                        "display", "");
                updateTabScroller();
            }
        } else if (isClippedTabs()
                && DOM.compare(DOM.eventGetTarget(event), scrollerNext)) {
            int tabs = tb.getTabCount();
            if (scrollerIndex + 1 <= tabs) {
                DOM.setStyleAttribute(DOM.getChild(DOM.getFirstChild(DOM
                        .getFirstChild(tb.getElement())), scrollerIndex),
                        "display", "none");
                scrollerIndex++;
                updateTabScroller();
            }
        } else {
            super.onBrowserEvent(event);
        }
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        // Add proper stylenames for all elements (easier to prevent unwanted
        // style inheritance)
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            final String contentBaseClass = CLASSNAME + "-content";
            String contentClass = contentBaseClass;
            final String decoBaseClass = CLASSNAME + "-deco";
            String decoClass = decoBaseClass;
            for (int i = 0; i < styles.length; i++) {
                tb.addStyleDependentName(styles[i]);
                contentClass += " " + contentBaseClass + "-" + styles[i];
                decoClass += " " + decoBaseClass + "-" + styles[i];
            }
            DOM.setElementProperty(contentNode, "className", contentClass);
            DOM.setElementProperty(deco, "className", decoClass);
        } else {
            tb.setStyleName(CLASSNAME + "-tabs");
            DOM.setElementProperty(contentNode, "className", CLASSNAME
                    + "-content");
            DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");
        }

        if (uidl.hasAttribute("hidetabs")) {
            tb.setVisible(false);
            addStyleName(CLASSNAME + "-hidetabs");
        } else {
            tb.setVisible(true);
            removeStyleName(CLASSNAME + "-hidetabs");
        }

        // tabs; push or not
        if (uidl.hasAttribute("width")) {
            // update width later, in updateTabScroller();
            DOM.setStyleAttribute(tabs, "width", "1px");
            DOM.setStyleAttribute(tabs, "overflow", "hidden");
        } else {
            showAllTabs();
            DOM.setStyleAttribute(tabs, "width", "");
            DOM.setStyleAttribute(tabs, "overflow", "visible");
        }

        updateTabScroller();
        waitingForResponse = false;
    }

    protected void renderTab(final UIDL tabUidl, int index, boolean selected) {
        ICaption c = tb.getTab(index);
        if (c == null) {
            c = new ICaption(null, client);
            tb.addTab(c);
        }
        c.updateCaption(tabUidl);
        captions.put("" + index, c);
        if (selected) {
            renderContent(tabUidl.getChildUIDL(0));
            tb.selectTab(index);
        } else {
            if (tabUidl.getChildCount() > 0) {
                // updating a drawn child on hidden tab
                Paintable paintable = client.getPaintable(tabUidl
                        .getChildUIDL(0));

                if (tp.getWidgetIndex((Widget) paintable) < 0) {
                    tp.insert((Widget) paintable, index);
                }
                paintable.updateFromUIDL(tabUidl.getChildUIDL(0), client);
            } else if (tp.getWidgetCount() <= index) {
                tp.add(new Label(""));
            }
        }
    }

    protected void selectTab(int index, final UIDL contentUidl) {
        if (index != activeTabIndex) {
            activeTabIndex = index;
            tb.selectTab(activeTabIndex);
        }
        renderContent(contentUidl);
    }

    private void renderContent(final UIDL contentUIDL) {
        final Paintable content = client.getPaintable(contentUIDL);
        if (tp.getWidgetCount() > activeTabIndex) {
            Widget old = tp.getWidget(activeTabIndex);
            if (old != content) {
                tp.remove(activeTabIndex);
                if (old instanceof Paintable) {
                    client.unregisterPaintable((Paintable) old);
                }
                tp.insert((Widget) content, activeTabIndex);
            }
        } else {
            tp.add((Widget) content);
        }

        tp.showWidget(activeTabIndex);

        ITabsheet.this.iLayout();
        (content).updateFromUIDL(contentUIDL, client);
        ITabsheet.this.removeStyleDependentName("loading");
        if (previousVisibleWidget != null) {
            DOM.setStyleAttribute(previousVisibleWidget.getElement(),
                    "visibility", "");
            previousVisibleWidget = null;
        }
    }

    public void setHeight(String height) {
        if (this.height == null && height == null) {
            return;
        }
        String oldHeight = this.height;
        this.height = height;
        if ((this.height != null && height == null)
                || (this.height == null && height != null)
                || !height.equals(oldHeight)) {
            iLayout();
        }
    }

    public void setWidth(String width) {
        String oldWidth = this.width;
        this.width = width;
        if ("100%".equals(width)) {
            // Allow browser to calculate width
            super.setWidth("");
        } else {
            super.setWidth(width);
        }
        if ((this.width != null && width == null)
                || (this.width == null && width != null)
                || !width.equals(oldWidth)) {
            // Run descendant layout functions
            Util.runDescendentsLayout(this);
        }
    }

    private void iLayout() {
        iLayout(-1, -1);
    }

    public void iLayout(int availableWidth, int availableHeight) {
        if (height != null && height != "") {
            super.setHeight(height);

            int contentHeight = getOffsetHeight()
                    - DOM.getElementPropertyInt(deco, "offsetHeight")
                    - tb.getOffsetHeight();
            if (contentHeight < 0) {
                contentHeight = 0;
            }

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", contentHeight + "px");
            DOM.setStyleAttribute(contentNode, "overflow", "auto");
            tp.setHeight("100%");

        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
            DOM.setStyleAttribute(contentNode, "overflow", "");
        }
        Util.runDescendentsLayout(this);

        updateTabScroller();
    }

    /**
     * Layouts the tab-scroller elements, and applies styles.
     */
    private void updateTabScroller() {
        if (width != null) {
            DOM.setStyleAttribute(tabs, "width", width);
        }
        if (scrollerIndex > tb.getTabCount()) {
            scrollerIndex = 0;
        }
        boolean scrolled = isScrolledTabs();
        boolean clipped = isClippedTabs();
        if (tb.isVisible() && (scrolled || clipped)) {
            DOM.setStyleAttribute(scroller, "display", "");
            DOM.setElementProperty(scrollerPrev, "className",
                    SCROLLER_CLASSNAME + (scrolled ? "Prev" : "Prev-disabled"));
            DOM.setElementProperty(scrollerNext, "className",
                    SCROLLER_CLASSNAME + (clipped ? "Next" : "Next-disabled"));
        } else {
            DOM.setStyleAttribute(scroller, "display", "none");
        }

    }

    private void showAllTabs() {
        scrollerIndex = 0;
        Element tr = DOM.getFirstChild(DOM.getFirstChild(tb.getElement()));
        for (int i = 0; i < tb.getTabCount(); i++) {
            DOM.setStyleAttribute(DOM.getChild(tr, i), "display", "");
        }
    }

    private boolean isScrolledTabs() {
        return scrollerIndex > 0;
    }

    private boolean isClippedTabs() {
        return tb.getOffsetWidth() > getOffsetWidth();
    }

    protected void clearPaintables() {

        int i = tb.getTabCount();
        while (i > 0) {
            tb.removeTab(--i);
        }
        tp.clear();

    }

    protected Iterator getPaintableIterator() {
        return tp.iterator();
    }

    public boolean hasChildComponent(Widget component) {
        if (tp.getWidgetIndex(component) < 0) {
            return false;
        } else {
            return true;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        int widgetIndex = tp.getWidgetIndex(oldComponent);
        tp.remove(oldComponent);
        tp.insert(newComponent, widgetIndex);
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        int i = tp.getWidgetIndex((Widget) component);
        ICaption c = (ICaption) captions.get("" + i);
        c.updateCaption(uidl);
    }
}
