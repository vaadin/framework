/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Focusable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.tabsheet.TabsheetBaseConstants;
import com.vaadin.shared.ui.tabsheet.TabsheetConstants;

public class VTabsheet extends VTabsheetBase implements Focusable,
        FocusHandler, BlurHandler, KeyDownHandler {

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
    public static class Tab extends SimplePanel implements HasFocusHandlers,
            HasBlurHandlers, HasKeyDownHandlers {
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
            setTabulatorIndex(-1);
            setStyleName(div, DIV_CLASSNAME);

            DOM.appendChild(td, div);

            tabCaption = new TabCaption(this, getTabsheet()
                    .getApplicationConnection());
            add(tabCaption);

            addFocusHandler(getTabsheet());
            addBlurHandler(getTabsheet());
            addKeyDownHandler(getTabsheet());
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
            if (!enabled) {
                focusImpl.setTabIndex(td, -1);
            }
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

        public void setTabulatorIndex(int tabIndex) {
            getElement().setTabIndex(tabIndex);
        }

        public boolean isClosable() {
            return tabCaption.isClosable();
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
            String newStyleName = tabUidl
                    .getStringAttribute(TabsheetConstants.TAB_STYLE_NAME);
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

        @Override
        public HandlerRegistration addFocusHandler(FocusHandler handler) {
            return addDomHandler(handler, FocusEvent.getType());
        }

        @Override
        public HandlerRegistration addBlurHandler(BlurHandler handler) {
            return addDomHandler(handler, BlurEvent.getType());
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return addDomHandler(handler, KeyDownEvent.getType());
        }

        public void focus() {
            getTabsheet().scrollIntoView(this);
            focusImpl.focus(td);
        }

        public void blur() {
            focusImpl.blur(td);
        }

        public boolean isSelectable() {
            VTabsheet ts = getTabsheet();
            if (ts.client == null || ts.disabled || ts.waitingForResponse) {
                return false;
            }
            if (!isEnabledOnServer() || isHiddenOnServer()) {
                return false;
            }
            return true;
        }
    }

    public static class TabCaption extends VCaption {

        private boolean closable = false;
        private Element closeButton;
        private Tab tab;
        private ApplicationConnection client;

        TabCaption(Tab tab, ApplicationConnection client) {
            super(client);
            this.client = client;
            this.tab = tab;
        }

        public boolean updateCaption(UIDL uidl) {
            if (uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DESCRIPTION)
                    || uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ERROR_MESSAGE)) {
                setTooltipInfo(new TooltipInfo(
                        uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DESCRIPTION),
                        uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ERROR_MESSAGE)));
            } else {
                setTooltipInfo(null);
            }

            // TODO need to call this instead of super because the caption does
            // not have an owner
            boolean ret = updateCaptionWithoutOwner(
                    uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_CAPTION),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DISABLED),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DESCRIPTION),
                    uidl.hasAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ERROR_MESSAGE),
                    uidl.getStringAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_ICON));

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
        }

        public Tab getTab() {
            return tab;
        }

        public void setClosable(boolean closable) {
            this.closable = closable;
            if (closable && closeButton == null) {
                closeButton = DOM.createSpan();
                closeButton.setInnerHTML("&times;");
                closeButton
                        .setClassName(VTabsheet.CLASSNAME + "-caption-close");
                getElement().appendChild(closeButton);
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

        public boolean isClosable() {
            return closable;
        }

        @Override
        public int getRequiredWidth() {
            int width = super.getRequiredWidth();
            if (closeButton != null) {
                width += Util.getRequiredWidth(closeButton);
            }
            return width;
        }

        public Element getCloseButton() {
            return closeButton;
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

        @Override
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

        @Override
        public void onClick(ClickEvent event) {
            TabCaption caption = (TabCaption) event.getSource();
            Element targetElement = event.getNativeEvent().getEventTarget()
                    .cast();
            // the tab should not be focused if the close button was clicked
            if (targetElement == caption.getCloseButton()) {
                return;
            }

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
            newSelected.setTabulatorIndex(getTabsheet().tabulatorIndex);

            if (oldSelected != null && oldSelected != newSelected) {
                oldSelected.setStyleNames(false,
                        isFirstVisibleTab(getWidgetIndex(oldSelected)));
                oldSelected.setTabulatorIndex(-1);
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

    /** For internal use only. May be removed or replaced in the future. */
    public final Element tabs; // tabbar and 'scroller' container
    Tab focusedTab;
    /**
     * The tabindex property (position in the browser's focus cycle.) Named like
     * this to avoid confusion with activeTabIndex.
     */
    int tabulatorIndex = 0;

    private static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

    private final Element scroller; // tab-scroller element
    private final Element scrollerNext; // tab-scroller next button element
    private final Element scrollerPrev; // tab-scroller prev button element

    /**
     * The index of the first visible tab (when scrolled)
     */
    private int scrollerIndex = 0;

    final TabBar tb = new TabBar(this);
    /** For internal use only. May be removed or replaced in the future. */
    public final VTabsheetPanel tp = new VTabsheetPanel();
    /** For internal use only. May be removed or replaced in the future. */
    public final Element contentNode;

    private final Element deco;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean waitingForResponse;

    private String currentStyle;

    private void onTabSelected(final int tabIndex) {
        if (activeTabIndex != tabIndex) {
            tb.selectTab(tabIndex);

            // If this TabSheet already has focus, set the new selected tab
            // as focused.
            if (focusedTab != null) {
                focusedTab = tb.getTab(tabIndex);
            }

            addStyleDependentName("loading");
            // Hide the current contents so a loading indicator can be shown
            // instead
            Widget currentlyDisplayedWidget = tp.getWidget(tp
                    .getVisibleWidget());
            currentlyDisplayedWidget.getElement().getParentElement().getStyle()
                    .setVisibility(Visibility.HIDDEN);
            client.updateVariable(id, "selected", tabKeys.get(tabIndex)
                    .toString(), true);
            waitingForResponse = true;

            tb.getTab(tabIndex).focus(); // move keyboard focus to active tab
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

    boolean isDynamicWidth() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        return paintable.isUndefinedWidth();
    }

    boolean isDynamicHeight() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        return paintable.isUndefinedHeight();
    }

    public VTabsheet() {
        super(CLASSNAME);

        addHandler(this, FocusEvent.getType());
        addHandler(this, BlurEvent.getType());

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
        if (event.getTypeInt() == Event.ONCLICK) {
            // Tab scrolling
            if (isScrolledTabs() && DOM.eventGetTarget(event) == scrollerPrev) {
                int newFirstIndex = tb.scrollLeft(scrollerIndex);
                if (newFirstIndex != -1) {
                    scrollerIndex = newFirstIndex;
                    updateTabScroller();
                }
                event.stopPropagation();
                return;
            } else if (isClippedTabs()
                    && DOM.eventGetTarget(event) == scrollerNext) {
                int newFirstIndex = tb.scrollRight(scrollerIndex);

                if (newFirstIndex != -1) {
                    scrollerIndex = newFirstIndex;
                    updateTabScroller();
                }
                event.stopPropagation();
                return;
            }
        }
        super.onBrowserEvent(event);
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

    /** For internal use only. May be removed or replaced in the future. */
    public void handleStyleNames(UIDL uidl, AbstractComponentState state) {
        // Add proper stylenames for all elements (easier to prevent unwanted
        // style inheritance)
        if (ComponentStateUtil.hasStyles(state)) {
            final List<String> styles = state.styles;
            if (currentStyle == null || !currentStyle.equals(styles.toString())) {
                currentStyle = styles.toString();
                final String tabsBaseClass = TABS_CLASSNAME;
                String tabsClass = tabsBaseClass;
                final String contentBaseClass = CLASSNAME + "-content";
                String contentClass = contentBaseClass;
                final String decoBaseClass = CLASSNAME + "-deco";
                String decoClass = decoBaseClass;
                for (String style : styles) {
                    tb.addStyleDependentName(style);
                    tabsClass += " " + tabsBaseClass + "-" + style;
                    contentClass += " " + contentBaseClass + "-" + style;
                    decoClass += " " + decoBaseClass + "-" + style;
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

    /** For internal use only. May be removed or replaced in the future. */
    public void updateDynamicWidth() {
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
    public void renderTab(final UIDL tabUidl, int index, boolean selected,
            boolean hidden) {
        Tab tab = tb.getTab(index);
        if (tab == null) {
            tab = tb.addTab();
        }
        if (selected) {
            tb.selectTab(index);
            renderContent(tabUidl.getChildUIDL(0));
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
    }

    /**
     * @deprecated as of 7.1, VTabsheet only keeps the active tab in the DOM
     *             without any place holders.
     */
    @Deprecated
    public class PlaceHolder extends VLabel {
        public PlaceHolder() {
            super("");
        }
    }

    private void renderContent(final UIDL contentUIDL) {
        final ComponentConnector content = client.getPaintable(contentUIDL);
        Widget newWidget = content.getWidget();

        assert tp.getWidgetCount() <= 1;

        if (tp.getWidgetCount() == 0) {
            tp.add(newWidget);
        } else if (tp.getWidget(0) != newWidget) {
            tp.remove(0);
            tp.add(newWidget);
        }

        assert tp.getWidgetCount() <= 1;

        // There's never any other index than 0, but maintaining API for now
        tp.showWidget(0);

        VTabsheet.this.iLayout();
        updateOpenTabSize();
        VTabsheet.this.removeStyleDependentName("loading");
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateContentNodeHeight() {
        if (!isDynamicHeight()) {
            int contentHeight = getOffsetHeight();
            contentHeight -= DOM.getElementPropertyInt(deco, "offsetHeight");
            contentHeight -= tb.getOffsetHeight();
            if (contentHeight < 0) {
                contentHeight = 0;
            }

            // Set proper values for content element
            DOM.setStyleAttribute(contentNode, "height", contentHeight + "px");
        } else {
            DOM.setStyleAttribute(contentNode, "height", "");
        }
    }

    public void iLayout() {
        updateTabScroller();
    }

    /**
     * Sets the size of the visible tab (component). As the tab is set to
     * position: absolute (to work around a firefox flickering bug) we must keep
     * this up-to-date by hand.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateOpenTabSize() {
        /*
         * The overflow=auto element must have a height specified, otherwise it
         * will be just as high as the contents and no scrollbars will appear
         */
        int height = -1;
        int width = -1;
        int minWidth = 0;

        if (!isDynamicHeight()) {
            height = contentNode.getOffsetHeight();
        }
        if (!isDynamicWidth()) {
            width = contentNode.getOffsetWidth() - getContentAreaBorderWidth();
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
        if (!isDynamicWidth()) {
            DOM.setStyleAttribute(tabs, "width", "100%");
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

            // the active tab should be focusable if and only if it is visible
            boolean isActiveTabVisible = scrollerIndex <= activeTabIndex
                    && !isClipped(tb.selected);
            tb.selected.setTabulatorIndex(isActiveTabVisible ? tabulatorIndex
                    : -1);

        } else {
            DOM.setStyleAttribute(scroller, "display", "none");
        }

        if (BrowserInfo.get().isSafari()) {
            /*
             * another hack for webkits. tabscroller sometimes drops without
             * "shaking it" reproducable in
             * com.vaadin.tests.components.tabsheet.TabSheetIcons
             */
            final Style style = scroller.getStyle();
            style.setProperty("whiteSpace", "normal");
            Scheduler.get().scheduleDeferred(new Command() {

                @Override
                public void execute() {
                    style.setProperty("whiteSpace", "");
                }
            });
        }

    }

    /** For internal use only. May be removed or replaced in the future. */
    public void showAllTabs() {
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

    private boolean isClipped(Tab tab) {
        return tab.getAbsoluteLeft() + tab.getOffsetWidth() > getAbsoluteLeft()
                + getOffsetWidth() - scroller.getOffsetWidth();
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
    public Iterator<Widget> getWidgetIterator() {
        return tp.iterator();
    }

    private int borderW = -1;

    /** For internal use only. May be removed or replaced in the future. */
    public int getContentAreaBorderWidth() {
        if (borderW < 0) {
            borderW = Util.measureHorizontalBorder(contentNode);
        }
        return borderW;
    }

    @Override
    public int getTabCount() {
        return tb.getTabCount();
    }

    @Override
    public ComponentConnector getTab(int index) {
        if (tp.getWidgetCount() > index) {
            Widget widget = tp.getWidget(index);
            return ConnectorMap.get(client).getConnector(widget);
        }
        return null;
    }

    @Override
    public void removeTab(int index) {
        tb.removeTab(index);

        // Removing content from tp is handled by the connector
    }

    @Override
    public void onBlur(BlurEvent event) {
        if (focusedTab != null && focusedTab == event.getSource()) {
            focusedTab = null;
            if (client.hasEventListeners(this, EventId.BLUR)) {
                client.updateVariable(id, EventId.BLUR, "", true);
            }
        }
    }

    @Override
    public void onFocus(FocusEvent event) {
        if (focusedTab == null && event.getSource() instanceof Tab) {
            focusedTab = (Tab) event.getSource();
            if (client.hasEventListeners(this, EventId.FOCUS)) {
                client.updateVariable(id, EventId.FOCUS, "", true);
            }
        }
    }

    @Override
    public void focus() {
        tb.getTab(activeTabIndex).focus();
    }

    public void blur() {
        tb.getTab(activeTabIndex).blur();
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getSource() instanceof Tab) {
            int keycode = event.getNativeEvent().getKeyCode();

            if (!event.isAnyModifierKeyDown()) {
                if (keycode == getPreviousTabKey()) {
                    selectPreviousTab();
                } else if (keycode == getNextTabKey()) {
                    selectNextTab();
                } else if (keycode == getCloseTabKey()) {
                    Tab tab = tb.getTab(activeTabIndex);
                    if (tab.isClosable()) {
                        tab.onClose();
                    }
                }
            }
        }
    }

    /**
     * @return The key code of the keyboard shortcut that selects the previous
     *         tab in a focused tabsheet.
     */
    protected int getPreviousTabKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * @return The key code of the keyboard shortcut that selects the next tab
     *         in a focused tabsheet.
     */
    protected int getNextTabKey() {
        return KeyCodes.KEY_RIGHT;
    }

    /**
     * @return The key code of the keyboard shortcut that closes the currently
     *         selected tab in a focused tabsheet.
     */
    protected int getCloseTabKey() {
        return KeyCodes.KEY_DELETE;
    }

    private void selectPreviousTab() {
        int newTabIndex = activeTabIndex;
        Tab newTab;
        // Find the previous visible and enabled tab if any.
        do {
            newTabIndex--;
            newTab = tb.getTab(newTabIndex);
        } while (newTabIndex >= 0 && !newTab.isSelectable());

        if (newTabIndex >= 0) {
            onTabSelected(newTabIndex);
            activeTabIndex = newTabIndex;
        }
    }

    private void selectNextTab() {
        int newTabIndex = activeTabIndex;
        Tab newTab;
        // Find the next visible and enabled tab if any.
        do {
            newTabIndex++;
            newTab = tb.getTab(newTabIndex);
        } while (newTabIndex < getTabCount() && !newTab.isSelectable());

        if (newTabIndex < getTabCount()) {
            onTabSelected(newTabIndex);
            activeTabIndex = newTabIndex;
        }
    }

    private void scrollIntoView(Tab tab) {
        if (!tab.isHiddenOnServer()) {
            if (isClipped(tab)) {
                while (isClipped(tab) && scrollerIndex != -1) {
                    scrollerIndex = tb.scrollRight(scrollerIndex);
                }
                updateTabScroller();
            } else if (!tab.isVisible()) {
                while (!tab.isVisible()) {
                    scrollerIndex = tb.scrollLeft(scrollerIndex);
                }
                updateTabScroller();
            }
        }
    }
}
