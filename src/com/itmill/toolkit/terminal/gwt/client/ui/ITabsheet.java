/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.ICaption;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ITabsheet extends ITabsheetBase {

    private class TabSheetCaption extends ICaption {
        TabSheetCaption() {
            super(null, client);
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONLOAD && isDynamicWidth()) {
                // icon onloads may change total width of tabsheet
                updateDynamicWidth();
            }
        }

    }

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
            Widget w = getWidget(i);
            if (w == null) {
                return;
            }

            Element caption = w.getElement();
            Element div = DOM.getParent(caption);
            Element td = DOM.getParent(div);
            Element tr = DOM.getParent(td);
            remove(w);

            /*
             * Widget is the Caption but we want to remove everything up to and
             * including the parent TD
             */

            DOM.removeChild(tr, td);
        }

        @Override
        public boolean remove(Widget w) {
            ((ICaption) w).removeClickListener(this);
            return super.remove(w);
        }

        public TabSheetCaption getTab(int index) {
            if (index >= getWidgetCount()) {
                return null;
            }
            return (TabSheetCaption) getWidget(index);
        }

        public void setVisible(int index, boolean visible) {
            Element e = DOM.getParent(getTab(index).getElement());
            if (visible) {
                DOM.setStyleAttribute(e, "display", "");
            } else {
                DOM.setStyleAttribute(e, "display", "none");
            }
        }

        public void updateCaptionSize(int index) {
            ICaption c = getTab(index);
            c.setWidth(c.getRequiredWidth() + "px");

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

    private final TabBar tb = new TabBar();
    private final ITabsheetPanel tp = new ITabsheetPanel();
    private final Element contentNode, deco;

    private final HashMap<String, ICaption> captions = new HashMap<String, ICaption>();

    private String height;
    private String width;

    private boolean waitingForResponse;

    private RenderInformation renderInformation = new RenderInformation();

    /**
     * Previous visible widget is set invisible with CSS (not display: none, but
     * visibility: hidden), to avoid flickering during render process. Normal
     * visibility must be returned later when new widget is rendered.
     */
    private Widget previousVisibleWidget;

    private boolean rendering = false;

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
            // run updating variables in deferred command to bypass some FF
            // optimization issues
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    previousVisibleWidget = tp.getWidget(tp.getVisibleWidget());
                    DOM.setStyleAttribute(DOM.getParent(previousVisibleWidget
                            .getElement()), "visibility", "hidden");
                    client.updateVariable(id, "selected", tabKeys.get(tabIndex)
                            .toString(), true);
                }
            });
            waitingForResponse = true;
        }
    }

    private boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    private boolean isDynamicHeight() {
        return height == null || height.equals("");
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

    @Override
    public void onBrowserEvent(Event event) {

        // Tab scrolling
        if (isScrolledTabs() && DOM.eventGetTarget(event) == scrollerPrev) {
            if (scrollerIndex > 0) {
                scrollerIndex--;
                DOM.setStyleAttribute(DOM.getChild(DOM.getFirstChild(DOM
                        .getFirstChild(tb.getElement())), scrollerIndex),
                        "display", "");
                tb.updateCaptionSize(scrollerIndex);
                updateTabScroller();
            }
        } else if (isClippedTabs() && DOM.eventGetTarget(event) == scrollerNext) {
            int tabs = tb.getTabCount();
            if (scrollerIndex + 1 <= tabs) {
                DOM.setStyleAttribute(DOM.getChild(DOM.getFirstChild(DOM
                        .getFirstChild(tb.getElement())), scrollerIndex),
                        "display", "none");
                tb.updateCaptionSize(scrollerIndex);
                scrollerIndex++;
                updateTabScroller();
            }
        } else {
            super.onBrowserEvent(event);
        }
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        super.updateFromUIDL(uidl, client);
        if (cachedUpdate) {
            return;
        }

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
        if (!isDynamicWidth()) {
            // FIXME: This makes tab sheet tabs go to 1px width on every update
            // and then back to original width
            // update width later, in updateTabScroller();
            DOM.setStyleAttribute(tabs, "width", "1px");
            DOM.setStyleAttribute(tabs, "overflow", "hidden");
        } else {
            showAllTabs();
            DOM.setStyleAttribute(tabs, "width", "");
            DOM.setStyleAttribute(tabs, "overflow", "visible");
            updateDynamicWidth();
        }

        if (!isDynamicHeight()) {
            // Must update height after the styles have been set
            updateContentNodeHeight();
            updateOpenTabSize();
        }

        iLayout();

        // Re run relative size update to ensure optimal scrollbars
        // TODO isolate to situation that visible tab has undefined height
        try {
            client.handleComponentRelativeSize(tp.getWidget(tp
                    .getVisibleWidget()));
        } catch (Exception e) {
            // Ignore, most likely empty tabsheet
        }

        renderInformation.updateSize(getElement());

        waitingForResponse = false;
        rendering = false;
    }

    private void updateDynamicWidth() {
        // Find tab width
        int tabsWidth = 0;

        int count = tb.getTabCount();
        for (int i = 0; i < count; i++) {
            Element tabTd = tb.getTab(i).getElement().getParentElement().cast();
            tabsWidth += tabTd.getOffsetWidth();
        }

        // Find content width
        Style style = tp.getElement().getStyle();
        String overflow = style.getProperty("overflow");
        style.setProperty("overflow", "hidden");
        style.setPropertyPx("width", tabsWidth);
        Style wrapperstyle = tp.getWidget(tp.getVisibleWidget()).getElement()
                .getParentElement().getStyle();
        wrapperstyle.setPropertyPx("width", tabsWidth);
        // Get content width from actual widget

        int contentWidth = 0;
        if (tp.getWidgetCount() > 0) {
            contentWidth = tp.getWidget(tp.getVisibleWidget()).getOffsetWidth();
        }
        style.setProperty("overflow", overflow);

        // Set widths to max(tabs,content)
        if (tabsWidth < contentWidth) {
            tabsWidth = contentWidth;
        }

        int outerWidth = tabsWidth + getContentAreaBorderWidth();

        tabs.getStyle().setPropertyPx("width", outerWidth);
        style.setPropertyPx("width", tabsWidth);
        wrapperstyle.setPropertyPx("width", tabsWidth);

        contentNode.getStyle().setPropertyPx("width", tabsWidth);
        super.setWidth(outerWidth + "px");
        updateOpenTabSize();
    }

    @Override
    protected void renderTab(final UIDL tabUidl, int index, boolean selected,
            boolean hidden) {
        TabSheetCaption c = tb.getTab(index);
        if (c == null) {
            c = new TabSheetCaption();
            tb.addTab(c);
        }
        c.updateCaption(tabUidl);

        tb.setVisible(index, !hidden);

        /*
         * Force the width of the caption container so the content will not wrap
         * and tabs won't be too narrow in certain browsers
         */
        c.setWidth(c.getRequiredWidth() + "px");
        captions.put("" + index, c);

        UIDL tabContentUIDL = null;
        Paintable tabContent = null;
        if (tabUidl.getChildCount() > 0) {
            tabContentUIDL = tabUidl.getChildUIDL(0);
            tabContent = client.getPaintable(tabContentUIDL);
        }

        if (tabContent != null) {
            /* This is a tab with content information */

            int oldIndex = tp.getWidgetIndex((Widget) tabContent);
            if (oldIndex != -1 && oldIndex != index) {
                /*
                 * The tab has previously been rendered in another position so
                 * we must move the cached content to correct position
                 */
                tp.insert((Widget) tabContent, index);
            }
        } else {
            /* A tab whose content has not yet been loaded */

            /*
             * Make sure there is a corresponding empty tab in tp. The same
             * operation as the moving above but for not-loaded tabs.
             */
            if (index < tp.getWidgetCount()) {
                Widget oldWidget = tp.getWidget(index);
                if (!(oldWidget instanceof PlaceHolder)) {
                    tp.insert(new PlaceHolder(), index);
                }
            }

        }

        if (selected) {
            renderContent(tabContentUIDL);
            tb.selectTab(index);
        } else {
            if (tabContentUIDL != null) {
                // updating a drawn child on hidden tab
                if (tp.getWidgetIndex((Widget) tabContent) < 0) {
                    tp.insert((Widget) tabContent, index);
                }
                tabContent.updateFromUIDL(tabContentUIDL, client);
            } else if (tp.getWidgetCount() <= index) {
                tp.add(new PlaceHolder());
            }
        }
    }

    public class PlaceHolder extends ILabel {
        public PlaceHolder() {
            super("");
        }
    }

    @Override
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
        /*
         * The size of a cached, relative sized component must be updated to
         * report correct size to updateOpenTabSize().
         */
        if (contentUIDL.getBooleanAttribute("cached")) {
            client.handleComponentRelativeSize((Widget) content);
        }
        updateOpenTabSize();
        ITabsheet.this.removeStyleDependentName("loading");
        if (previousVisibleWidget != null) {
            DOM.setStyleAttribute(previousVisibleWidget.getElement(),
                    "visibility", "");
            previousVisibleWidget = null;
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        this.height = height;
        updateContentNodeHeight();

        if (!rendering) {
            updateOpenTabSize();
            iLayout();
            // TODO Check if this is needed
            client.runDescendentsLayout(this);
        }
    }

    private void updateContentNodeHeight() {
        if (height != null && !"".equals(height)) {
            int contentHeight = getOffsetHeight();
            contentHeight -= DOM.getElementPropertyInt(deco, "offsetHeight");
            contentHeight -= tb.getOffsetHeight();
            if (contentHeight < 0) {
                contentHeight = 0;
            }

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", contentHeight + "px");
            renderSpace.setHeight(contentHeight);
        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
            renderSpace.setHeight(0);
        }
    }

    @Override
    public void setWidth(String width) {
        if ((this.width == null && width.equals(""))
                || (this.width != null && this.width.equals(width))) {
            return;
        }

        super.setWidth(width);
        if (width.equals("")) {
            width = null;
        }
        this.width = width;
        if (width == null) {
            renderSpace.setWidth(0);
            contentNode.getStyle().setProperty("width", "");
        } else {
            int contentWidth = getOffsetWidth() - getContentAreaBorderWidth();
            if (contentWidth < 0) {
                contentWidth = 0;
            }
            contentNode.getStyle().setProperty("width", contentWidth + "px");
            renderSpace.setWidth(contentWidth);
        }

        if (!rendering) {
            if (isDynamicHeight()) {
                Util.updateRelativeChildrenAndSendSizeUpdateEvent(client, tp,
                        this);
            }

            updateOpenTabSize();
            iLayout();
            // TODO Check if this is needed
            client.runDescendentsLayout(this);

        }

    }

    public void iLayout() {
        updateTabScroller();
        tp.runWebkitOverflowAutoFix();
    }

    /**
     * Sets the size of the visible tab (component). As the tab is set to
     * position: absolute (to work around a firefox flickering bug) we must keep
     * this up-to-date by hand.
     */
    private void updateOpenTabSize() {
        /*
         * The overflow=auto element must have a height specified, otherwise it
         * will be just as high as the contents and no scrollbars will appear
         */
        int height = -1;
        int width = -1;
        int minWidth = 0;

        if (!isDynamicHeight()) {
            height = renderSpace.getHeight();
        }
        if (!isDynamicWidth()) {
            width = renderSpace.getWidth();
        } else {
            /*
             * If the tabbar is wider than the content we need to use the tabbar
             * width as minimum width so scrollbars get placed correctly (at the
             * right edge).
             */
            minWidth = tb.getOffsetWidth() - getContentAreaBorderWidth();
        }
        tp.fixVisibleTabSize(width, height, minWidth);

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

        if (BrowserInfo.get().isSafari()) {
            // fix tab height for safari, bugs sometimes if tabs contain icons
            String property = tabs.getStyle().getProperty("height");
            if (property == null || property.equals("")) {
                tabs.getStyle().setPropertyPx("height", tb.getOffsetHeight());
            }
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

    @Override
    protected void clearPaintables() {

        int i = tb.getTabCount();
        while (i > 0) {
            tb.removeTab(--i);
        }
        tp.clear();

    }

    @Override
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
        tp.replaceComponent(oldComponent, newComponent);
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        int i = tp.getWidgetIndex((Widget) component);
        ICaption c = captions.get("" + i);
        boolean visible = c.isVisible();
        c.updateCaption(uidl);
        if (c.isVisible() != visible) {
            tb.setVisible(i, c.isVisible());
            c.setWidth(c.getRequiredWidth() + "px");
        }
    }

    public boolean requestLayout(Set<Paintable> child) {
        if (!isDynamicHeight() && !isDynamicWidth()) {
            /*
             * If the height and width has been specified for this container the
             * child components cannot make the size of the layout change
             */

            return true;
        }

        updateOpenTabSize();

        if (renderInformation.updateSize(getElement())) {
            /*
             * Size has changed so we let the child components know about the
             * new size.
             */
            iLayout();
            client.runDescendentsLayout(this);

            return false;
        } else {
            /*
             * Size has not changed so we do not need to propagate the event
             * further
             */
            return true;
        }

    }

    private int borderW = -1;

    private int getContentAreaBorderWidth() {
        if (borderW < 0) {
            borderW = Util.measureHorizontalBorder(contentNode);
        }
        return borderW;
    }

    private RenderSpace renderSpace = new RenderSpace(0, 0, true);

    public RenderSpace getAllocatedSpace(Widget child) {
        // All tabs have equal amount of space allocated
        return renderSpace;
    }

    @Override
    protected int getTabCount() {
        return tb.getWidgetCount();
    }

    @Override
    protected Paintable getTab(int index) {
        if (tp.getWidgetCount() > index) {
            return (Paintable) tp.getWidget(index);
        }
        return null;
    }

    @Override
    protected void removeTab(int index) {
        tb.removeTab(index);
        /*
         * This must be checked because renderTab automatically removes the
         * active tab content when it changes
         */
        if (tp.getWidgetCount() > index) {
            tp.remove(index);
        }
    }

}
