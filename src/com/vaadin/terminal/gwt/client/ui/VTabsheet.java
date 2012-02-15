/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;

public class VTabsheet extends VTabsheetBase {

    private static class VCloseEvent {
        private Tab tab;

        VCloseEvent(Tab tab) {
            this.tab = tab;
        }

        public Tab getTab() {
            return tab;
        }

    }

    private interface VCloseHandler {
        public void onClose(VCloseEvent event);
    }

    /**
     * Representation of a single "tab" shown in the TabBar
     * 
     */
    private static class Tab extends SimplePanel {
        private static final String TD_CLASSNAME = CLASSNAME + "-tabitemcell";
        private static final String TD_FIRST_CLASSNAME = TD_CLASSNAME
                + "-first";
        private static final String TD_SELECTED_CLASSNAME = TD_CLASSNAME
                + "-selected";
        private static final String TD_SELECTED_FIRST_CLASSNAME = TD_SELECTED_CLASSNAME
                + "-first";
        private static final String TD_DISABLED_CLASSNAME = TD_CLASSNAME
                + "-disabled";

        private static final String DIV_CLASSNAME = CLASSNAME + "-tabitem";
        private static final String DIV_SELECTED_CLASSNAME = DIV_CLASSNAME
                + "-selected";

        private TabCaption tabCaption;
        Element td = getElement();
        private VCloseHandler closeHandler;

        private boolean enabledOnServer = true;
        private Element div;
        private TabBar tabBar;
        private boolean hiddenOnServer = false;

        private String styleName;

        private Tab(TabBar tabBar) {
            super(DOM.createTD());
            this.tabBar = tabBar;
            setStyleName(td, TD_CLASSNAME);

            div = DOM.createDiv();
            setStyleName(div, DIV_CLASSNAME);

            DOM.appendChild(td, div);

            tabCaption = new TabCaption(this, getTabsheet()
                    .getApplicationConnection());
            add(tabCaption);

        }

        public boolean isHiddenOnServer() {
            return hiddenOnServer;
        }

        public void setHiddenOnServer(boolean hiddenOnServer) {
            this.hiddenOnServer = hiddenOnServer;
        }

        @Override
        protected Element getContainerElement() {
            // Attach caption element to div, not td
            return div;
        }

        public boolean isEnabledOnServer() {
            return enabledOnServer;
        }

        public void setEnabledOnServer(boolean enabled) {
            enabledOnServer = enabled;
            setStyleName(td, TD_DISABLED_CLASSNAME, !enabled);
        }

        public void addClickHandler(ClickHandler handler) {
            tabCaption.addClickHandler(handler);
        }

        public void setCloseHandler(VCloseHandler closeHandler) {
            this.closeHandler = closeHandler;
        }

        /**
         * Toggles the style names for the Tab
         * 
         * @param selected
         *            true if the Tab is selected
         * @param first
         *            true if the Tab is the first visible Tab
         */
        public void setStyleNames(boolean selected, boolean first) {
            setStyleName(td, TD_FIRST_CLASSNAME, first);
            setStyleName(td, TD_SELECTED_CLASSNAME, selected);
            setStyleName(td, TD_SELECTED_FIRST_CLASSNAME, selected && first);
            setStyleName(div, DIV_SELECTED_CLASSNAME, selected);
        }

        public void onClose() {
            closeHandler.onClose(new VCloseEvent(this));
        }

        public VTabsheet getTabsheet() {
            return tabBar.getTabsheet();
        }

        public void updateFromUIDL(UIDL tabUidl) {
            tabCaption.updateCaption(tabUidl);

            // Apply the styleName set for the tab
            String newStyleName = tabUidl.getStringAttribute(TAB_STYLE_NAME);
            // Find the nth td element
            if (newStyleName != null && newStyleName.length() != 0) {
                if (!newStyleName.equals(styleName)) {
                    // If we have a new style name
                    if (styleName != null && styleName.length() != 0) {
                        // Remove old style name if present
                        td.removeClassName(TD_CLASSNAME + "-" + styleName);
                    }
                    // Set new style name
                    td.addClassName(TD_CLASSNAME + "-" + newStyleName);
                    styleName = newStyleName;
                }
            } else if (styleName != null) {
                // Remove the set stylename if no stylename is present in the
                // uidl
                td.removeClassName(TD_CLASSNAME + "-" + styleName);
                styleName = null;
            }
        }

        public void recalculateCaptionWidth() {
            tabCaption.setWidth(tabCaption.getRequiredWidth() + "px");
        }

    }

    private static class TabCaption extends VCaption {

        private boolean closable = false;
        private Element closeButton;
        private Tab tab;
        private ApplicationConnection client;

        TabCaption(Tab tab, ApplicationConnection client) {
            super(null, client);
            this.client = client;
            this.tab = tab;
        }

        @Override
        public boolean updateCaption(UIDL uidl) {
            if (uidl.hasAttribute(ATTRIBUTE_DESCRIPTION)
                    || uidl.hasAttribute(ATTRIBUTE_ERROR)) {
                TooltipInfo tooltipInfo = new TooltipInfo();
                tooltipInfo.setTitle(uidl
                        .getStringAttribute(ATTRIBUTE_DESCRIPTION));
                if (uidl.hasAttribute(ATTRIBUTE_ERROR)) {
                    tooltipInfo.setErrorUidl(uidl.getErrors());
                }
                client.registerTooltip(getTabsheet(), getElement(), tooltipInfo);
            } else {
                client.registerTooltip(getTabsheet(), getElement(), null);
            }

            boolean ret = super.updateCaption(uidl);

            setClosable(uidl.hasAttribute("closable"));

            return ret;
        }

        private VTabsheet getTabsheet() {
            return tab.getTabsheet();
        }

        @Override
        public void onBrowserEvent(Event event) {
            if (closable && event.getTypeInt() == Event.ONCLICK
                    && event.getEventTarget().cast() == closeButton) {
                tab.onClose();
                event.stopPropagation();
                event.preventDefault();
            }

            super.onBrowserEvent(event);

            if (event.getTypeInt() == Event.ONLOAD) {
                getTabsheet().tabSizeMightHaveChanged(getTab());
            }
            client.handleTooltipEvent(event, getTabsheet(), getElement());
        }

        public Tab getTab() {
            return tab;
        }

        @Override
        public void setWidth(String width) {
            super.setWidth(width);
            if (BrowserInfo.get().isIE7()) {
                /*
                 * IE7 apparently has problems with calculating width for
                 * floated elements inside a DIV with padding. Set the width
                 * explicitly for the caption.
                 */
                fixTextWidth();
            }
        }

        private void fixTextWidth() {
            Element captionText = getTextElement();
            if (captionText == null) {
                return;
            }

            int captionWidth = Util.getRequiredWidth(captionText);
            int scrollWidth = captionText.getScrollWidth();
            if (scrollWidth > captionWidth) {
                captionWidth = scrollWidth;
            }
            captionText.getStyle().setPropertyPx("width", captionWidth);
        }

        public void setClosable(boolean closable) {
            this.closable = closable;
            if (closable && closeButton == null) {
                closeButton = DOM.createSpan();
                closeButton.setInnerHTML("&times;");
                closeButton
                        .setClassName(VTabsheet.CLASSNAME + "-caption-close");
                getElement().insertBefore(closeButton,
                        getElement().getLastChild());
            } else if (!closable && closeButton != null) {
                getElement().removeChild(closeButton);
                closeButton = null;
            }
            if (closable) {
                addStyleDependentName("closable");
            } else {
                removeStyleDependentName("closable");
            }
        }

        @Override
        public int getRequiredWidth() {
            int width = super.getRequiredWidth();
            if (closeButton != null) {
                width += Util.getRequiredWidth(closeButton);
            }
            return width;
        }

    }

    static class TabBar extends ComplexPanel implements ClickHandler,
            VCloseHandler {

        private final Element tr = DOM.createTR();

        private final Element spacerTd = DOM.createTD();

        private Tab selected;

        private VTabsheet tabsheet;

        TabBar(VTabsheet tabsheet) {
            this.tabsheet = tabsheet;

            Element el = DOM.createTable();
            Element tbody = DOM.createTBody();
            DOM.appendChild(el, tbody);
            DOM.appendChild(tbody, tr);
            setStyleName(spacerTd, CLASSNAME + "-spacertd");
            DOM.appendChild(tr, spacerTd);
            DOM.appendChild(spacerTd, DOM.createDiv());
            setElement(el);
        }

        public void onClose(VCloseEvent event) {
            Tab tab = event.getTab();
            if (!tab.isEnabledOnServer()) {
                return;
            }
            int tabIndex = getWidgetIndex(tab);
            getTabsheet().sendTabClosedEvent(tabIndex);
        }

        protected Element getContainerElement() {
            return tr;
        }

        public int getTabCount() {
            return getWidgetCount();
        }

        public Tab addTab() {
            Tab t = new Tab(this);
            int tabIndex = getTabCount();

            // Logical attach
            insert(t, tr, tabIndex, true);

            if (tabIndex == 0) {
                // Set the "first" style
                t.setStyleNames(false, true);
            }

            t.addClickHandler(this);
            t.setCloseHandler(this);

            return t;
        }

        public void onClick(ClickEvent event) {
            Widget caption = (Widget) event.getSource();
            int index = getWidgetIndex(caption.getParent());
            getTabsheet().onTabSelected(index);
        }

        public VTabsheet getTabsheet() {
            return tabsheet;
        }

        public Tab getTab(int index) {
            if (index < 0 || index >= getTabCount()) {
                return null;
            }
            return (Tab) super.getWidget(index);
        }

        public void selectTab(int index) {
            final Tab newSelected = getTab(index);
            final Tab oldSelected = selected;

            newSelected.setStyleNames(true, isFirstVisibleTab(index));

            if (oldSelected != null && oldSelected != newSelected) {
                oldSelected.setStyleNames(false,
                        isFirstVisibleTab(getWidgetIndex(oldSelected)));
            }

            // Update the field holding the currently selected tab
            selected = newSelected;

            // The selected tab might need more (or less) space
            newSelected.recalculateCaptionWidth();
            getTab(tabsheet.activeTabIndex).recalculateCaptionWidth();
        }

        public void removeTab(int i) {
            Tab tab = getTab(i);
            if (tab == null) {
                return;
            }

            remove(tab);

            /*
             * If this widget was selected we need to unmark it as the last
             * selected
             */
            if (tab == selected) {
                selected = null;
            }

            // FIXME: Shouldn't something be selected instead?
        }

        private boolean isFirstVisibleTab(int index) {
            return getFirstVisibleTab() == index;
        }

        /**
         * Returns the index of the first visible tab
         * 
         * @return
         */
        private int getFirstVisibleTab() {
            return getNextVisibleTab(-1);
        }

        /**
         * Find the next visible tab. Returns -1 if none is found.
         * 
         * @param i
         * @return
         */
        private int getNextVisibleTab(int i) {
            int tabs = getTabCount();
            do {
                i++;
            } while (i < tabs && getTab(i).isHiddenOnServer());

            if (i == tabs) {
                return -1;
            } else {
                return i;
            }
        }

        /**
         * Find the previous visible tab. Returns -1 if none is found.
         * 
         * @param i
         * @return
         */
        private int getPreviousVisibleTab(int i) {
            do {
                i--;
            } while (i >= 0 && getTab(i).isHiddenOnServer());

            return i;

        }

        public int scrollLeft(int currentFirstVisible) {
            int prevVisible = getPreviousVisibleTab(currentFirstVisible);
            if (prevVisible == -1) {
                return -1;
            }

            Tab newFirst = getTab(prevVisible);
            newFirst.setVisible(true);
            newFirst.recalculateCaptionWidth();

            return prevVisible;
        }

        public int scrollRight(int currentFirstVisible) {
            int nextVisible = getNextVisibleTab(currentFirstVisible);
            if (nextVisible == -1) {
                return -1;
            }
            Tab currentFirst = getTab(currentFirstVisible);
            currentFirst.setVisible(false);
            currentFirst.recalculateCaptionWidth();
            return nextVisible;
        }

    }

    public static final String CLASSNAME = "v-tabsheet";

    public static final String TABS_CLASSNAME = "v-tabsheet-tabcontainer";
    public static final String SCROLLER_CLASSNAME = "v-tabsheet-scroller";

    // Can't use "style" as it's already in use
    public static final String TAB_STYLE_NAME = "tabstyle";

    private final Element tabs; // tabbar and 'scroller' container
    private final Element scroller; // tab-scroller element
    private final Element scrollerNext; // tab-scroller next button element
    private final Element scrollerPrev; // tab-scroller prev button element

    /**
     * The index of the first visible tab (when scrolled)
     */
    private int scrollerIndex = 0;

    private final TabBar tb = new TabBar(this);
    private final VTabsheetPanel tp = new VTabsheetPanel();
    private final Element contentNode, deco;

    private String height;
    private String width;

    private boolean waitingForResponse;

    private final RenderInformation renderInformation = new RenderInformation();

    /**
     * Previous visible widget is set invisible with CSS (not display: none, but
     * visibility: hidden), to avoid flickering during render process. Normal
     * visibility must be returned later when new widget is rendered.
     */
    private Widget previousVisibleWidget;

    private boolean rendering = false;

    private String currentStyle;

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
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    previousVisibleWidget = tp.getWidget(tp.getVisibleWidget());
                    DOM.setStyleAttribute(
                            DOM.getParent(previousVisibleWidget.getElement()),
                            "visibility", "hidden");
                    client.updateVariable(id, "selected", tabKeys.get(tabIndex)
                            .toString(), true);
                }
            });
            waitingForResponse = true;
        }
    }

    public ApplicationConnection getApplicationConnection() {
        return client;
    }

    public void tabSizeMightHaveChanged(Tab tab) {
        // icon onloads may change total width of tabsheet
        if (isDynamicWidth()) {
            updateDynamicWidth();
        }
        updateTabScroller();

    }

    void sendTabClosedEvent(int tabIndex) {
        client.updateVariable(id, "close", tabKeys.get(tabIndex), true);
    }

    private boolean isDynamicWidth() {
        return width == null || width.equals("");
    }

    private boolean isDynamicHeight() {
        return height == null || height.equals("");
    }

    public VTabsheet() {
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
        DOM.setElementProperty(contentNode, "className", CLASSNAME + "-content");
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
            int newFirstIndex = tb.scrollLeft(scrollerIndex);
            if (newFirstIndex != -1) {
                scrollerIndex = newFirstIndex;
                updateTabScroller();
            }
        } else if (isClippedTabs() && DOM.eventGetTarget(event) == scrollerNext) {
            int newFirstIndex = tb.scrollRight(scrollerIndex);

            if (newFirstIndex != -1) {
                scrollerIndex = newFirstIndex;
                updateTabScroller();
            }
        } else {
            super.onBrowserEvent(event);
        }
    }

    /**
     * Checks if the tab with the selected index has been scrolled out of the
     * view (on the left side).
     * 
     * @param index
     * @return
     */
    private boolean scrolledOutOfView(int index) {
        return scrollerIndex > index;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        if (!uidl.getBooleanAttribute("cached")) {
            // Handle stylename changes before generics (might affect size
            // calculations)
            handleStyleNames(uidl);
        }

        super.updateFromUIDL(uidl, client);
        if (cachedUpdate) {
            rendering = false;
            return;
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

    private void handleStyleNames(UIDL uidl) {
        // Add proper stylenames for all elements (easier to prevent unwanted
        // style inheritance)
        if (uidl.hasAttribute("style")) {
            final String style = uidl.getStringAttribute("style");
            if (currentStyle != style) {
                currentStyle = style;
                final String[] styles = style.split(" ");
                final String tabsBaseClass = TABS_CLASSNAME;
                String tabsClass = tabsBaseClass;
                final String contentBaseClass = CLASSNAME + "-content";
                String contentClass = contentBaseClass;
                final String decoBaseClass = CLASSNAME + "-deco";
                String decoClass = decoBaseClass;
                for (int i = 0; i < styles.length; i++) {
                    tb.addStyleDependentName(styles[i]);
                    tabsClass += " " + tabsBaseClass + "-" + styles[i];
                    contentClass += " " + contentBaseClass + "-" + styles[i];
                    decoClass += " " + decoBaseClass + "-" + styles[i];
                }
                DOM.setElementProperty(tabs, "className", tabsClass);
                DOM.setElementProperty(contentNode, "className", contentClass);
                DOM.setElementProperty(deco, "className", decoClass);
                borderW = -1;
            }
        } else {
            tb.setStyleName(CLASSNAME + "-tabs");
            DOM.setElementProperty(tabs, "className", TABS_CLASSNAME);
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
    }

    private void updateDynamicWidth() {
        // Find width consumed by tabs
        TableCellElement spacerCell = ((TableElement) tb.getElement().cast())
                .getRows().getItem(0).getCells().getItem(tb.getTabCount());

        int spacerWidth = spacerCell.getOffsetWidth();
        DivElement div = (DivElement) spacerCell.getFirstChildElement();

        int spacerMinWidth = spacerCell.getOffsetWidth() - div.getOffsetWidth();

        int tabsWidth = tb.getOffsetWidth() - spacerWidth + spacerMinWidth;

        // Find content width
        Style style = tp.getElement().getStyle();
        String overflow = style.getProperty("overflow");
        style.setProperty("overflow", "hidden");
        style.setPropertyPx("width", tabsWidth);

        boolean hasTabs = tp.getWidgetCount() > 0;

        Style wrapperstyle = null;
        if (hasTabs) {
            wrapperstyle = tp.getWidget(tp.getVisibleWidget()).getElement()
                    .getParentElement().getStyle();
            wrapperstyle.setPropertyPx("width", tabsWidth);
        }
        // Get content width from actual widget

        int contentWidth = 0;
        if (hasTabs) {
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
        if (hasTabs) {
            wrapperstyle.setPropertyPx("width", tabsWidth);
        }

        contentNode.getStyle().setPropertyPx("width", tabsWidth);
        super.setWidth(outerWidth + "px");
        updateOpenTabSize();
    }

    @Override
    protected void renderTab(final UIDL tabUidl, int index, boolean selected,
            boolean hidden) {
        Tab tab = tb.getTab(index);
        if (tab == null) {
            tab = tb.addTab();
        }
        tab.updateFromUIDL(tabUidl);
        tab.setEnabledOnServer((!disabledTabKeys.contains(tabKeys.get(index))));
        tab.setHiddenOnServer(hidden);

        if (scrolledOutOfView(index)) {
            // Should not set tabs visible if they are scrolled out of view
            hidden = true;
        }
        // Set the current visibility of the tab (in the browser)
        tab.setVisible(!hidden);

        /*
         * Force the width of the caption container so the content will not wrap
         * and tabs won't be too narrow in certain browsers
         */
        tab.recalculateCaptionWidth();

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

    public class PlaceHolder extends VLabel {
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

        VTabsheet.this.iLayout();
        (content).updateFromUIDL(contentUIDL, client);
        /*
         * The size of a cached, relative sized component must be updated to
         * report correct size to updateOpenTabSize().
         */
        if (contentUIDL.getBooleanAttribute("cached")) {
            client.handleComponentRelativeSize((Widget) content);
        }
        updateOpenTabSize();
        VTabsheet.this.removeStyleDependentName("loading");
        if (previousVisibleWidget != null) {
            DOM.setStyleAttribute(
                    DOM.getParent(previousVisibleWidget.getElement()),
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

        // Make sure scrollerIndex is valid
        if (scrollerIndex < 0 || scrollerIndex > tb.getTabCount()) {
            scrollerIndex = tb.getFirstVisibleTab();
        } else if (tb.getTabCount() > 0
                && tb.getTab(scrollerIndex).isHiddenOnServer()) {
            scrollerIndex = tb.getNextVisibleTab(scrollerIndex);
        }

        boolean scrolled = isScrolledTabs();
        boolean clipped = isClippedTabs();
        if (tb.getTabCount() > 0 && tb.isVisible() && (scrolled || clipped)) {
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
            /*
             * another hack for webkits. tabscroller sometimes drops without
             * "shaking it" reproducable in
             * com.vaadin.tests.components.tabsheet.TabSheetIcons
             */
            final Style style = scroller.getStyle();
            style.setProperty("whiteSpace", "normal");
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    style.setProperty("whiteSpace", "");
                }
            });
        }

    }

    private void showAllTabs() {
        scrollerIndex = tb.getFirstVisibleTab();
        for (int i = 0; i < tb.getTabCount(); i++) {
            Tab t = tb.getTab(i);
            if (!t.isHiddenOnServer()) {
                t.setVisible(true);
            }
        }
    }

    private boolean isScrolledTabs() {
        return scrollerIndex > tb.getFirstVisibleTab();
    }

    private boolean isClippedTabs() {
        return (tb.getOffsetWidth() - DOM.getElementPropertyInt((Element) tb
                .getContainerElement().getLastChild().cast(), "offsetWidth")) > getOffsetWidth()
                - (isScrolledTabs() ? scroller.getOffsetWidth() : 0);
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
        /* Tabsheet does not render its children's captions */
    }

    public boolean requestLayout(Set<Paintable> child) {
        if (!isDynamicHeight() && !isDynamicWidth()) {
            /*
             * If the height and width has been specified for this container the
             * child components cannot make the size of the layout change
             */
            // layout size change may affect its available space (scrollbars)
            for (Paintable paintable : child) {
                client.handleComponentRelativeSize((Widget) paintable);
            }
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

    private final RenderSpace renderSpace = new RenderSpace(0, 0, true);

    public RenderSpace getAllocatedSpace(Widget child) {
        // All tabs have equal amount of space allocated
        return renderSpace;
    }

    @Override
    protected int getTabCount() {
        return tb.getTabCount();
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
