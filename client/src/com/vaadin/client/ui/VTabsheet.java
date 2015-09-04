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

import com.google.gwt.aria.client.Id;
import com.google.gwt.aria.client.LiveValue;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.aria.client.SelectedValue;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.Focusable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.VCaption;
import com.vaadin.client.VTooltip;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.tabsheet.TabState;
import com.vaadin.shared.ui.tabsheet.TabsheetServerRpc;
import com.vaadin.shared.ui.tabsheet.TabsheetState;

public class VTabsheet extends VTabsheetBase implements Focusable, SubPartAware {

    private static final String PREV_SCROLLER_DISABLED_CLASSNAME = "Prev-disabled";

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
            HasBlurHandlers, HasMouseDownHandlers, HasKeyDownHandlers {
        private static final String TD_CLASSNAME = CLASSNAME + "-tabitemcell";
        private static final String TD_FIRST_CLASSNAME = TD_CLASSNAME
                + "-first";
        private static final String TD_SELECTED_CLASSNAME = TD_CLASSNAME
                + "-selected";
        private static final String TD_SELECTED_FIRST_CLASSNAME = TD_SELECTED_CLASSNAME
                + "-first";
        private static final String TD_FOCUS_CLASSNAME = TD_CLASSNAME
                + "-focus";
        private static final String TD_FOCUS_FIRST_CLASSNAME = TD_FOCUS_CLASSNAME
                + "-first";
        private static final String TD_DISABLED_CLASSNAME = TD_CLASSNAME
                + "-disabled";

        private static final String DIV_CLASSNAME = CLASSNAME + "-tabitem";
        private static final String DIV_SELECTED_CLASSNAME = DIV_CLASSNAME
                + "-selected";
        private static final String DIV_FOCUS_CLASSNAME = DIV_CLASSNAME
                + "-focus";

        private TabCaption tabCaption;
        Element td = getElement();
        private VCloseHandler closeHandler;

        private boolean enabledOnServer = true;
        private Element div;
        private TabBar tabBar;
        private boolean hiddenOnServer = false;

        private String styleName;

        private String id;

        private Tab(TabBar tabBar) {
            super(DOM.createTD());
            this.tabBar = tabBar;
            setStyleName(td, TD_CLASSNAME);

            Roles.getTabRole().set(getElement());
            Roles.getTabRole().setAriaSelectedState(getElement(),
                    SelectedValue.FALSE);

            div = DOM.createDiv();
            setTabulatorIndex(-1);
            setStyleName(div, DIV_CLASSNAME);

            DOM.appendChild(td, div);

            tabCaption = new TabCaption(this);
            add(tabCaption);

            Roles.getTabRole().setAriaLabelledbyProperty(getElement(),
                    Id.of(tabCaption.getElement()));
        }

        public boolean isHiddenOnServer() {
            return hiddenOnServer;
        }

        public void setHiddenOnServer(boolean hiddenOnServer) {
            this.hiddenOnServer = hiddenOnServer;
            Roles.getTabRole().setAriaHiddenState(getElement(), hiddenOnServer);
        }

        @Override
        protected com.google.gwt.user.client.Element getContainerElement() {
            // Attach caption element to div, not td
            return DOM.asOld(div);
        }

        public boolean isEnabledOnServer() {
            return enabledOnServer;
        }

        public void setEnabledOnServer(boolean enabled) {
            enabledOnServer = enabled;
            Roles.getTabRole().setAriaDisabledState(getElement(), !enabled);

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
            setStyleNames(selected, first, false);
        }

        public void setStyleNames(boolean selected, boolean first,
                boolean keyboardFocus) {
            setStyleName(td, TD_FIRST_CLASSNAME, first);
            setStyleName(td, TD_SELECTED_CLASSNAME, selected);
            setStyleName(td, TD_SELECTED_FIRST_CLASSNAME, selected && first);
            setStyleName(div, DIV_SELECTED_CLASSNAME, selected);
            setStyleName(td, TD_FOCUS_CLASSNAME, keyboardFocus);
            setStyleName(td, TD_FOCUS_FIRST_CLASSNAME, keyboardFocus && first);
            setStyleName(div, DIV_FOCUS_CLASSNAME, keyboardFocus);
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

        private void updateFromState(TabState tabState) {
            tabCaption.setCaptionAsHtml(getTabsheet().isTabCaptionsAsHtml());
            tabCaption.update(tabState);
            // Apply the styleName set for the tab
            String newStyleName = tabState.styleName;
            // Find the nth td element
            if (newStyleName != null && !newStyleName.isEmpty()) {
                if (!newStyleName.equals(styleName)) {
                    // If we have a new style name
                    if (styleName != null && !styleName.isEmpty()) {
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

            String newId = tabState.id;
            if (newId != null && !newId.isEmpty()) {
                td.setId(newId);
                id = newId;
            } else if (id != null) {
                td.removeAttribute("id");
                id = null;
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
        public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
            return addDomHandler(handler, MouseDownEvent.getType());
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

        public boolean hasTooltip() {
            return tabCaption.getTooltipInfo() != null;
        }

        public TooltipInfo getTooltipInfo() {
            return tabCaption.getTooltipInfo();
        }

        public void setAssistiveDescription(String descriptionId) {
            Roles.getTablistRole().setAriaDescribedbyProperty(getElement(),
                    Id.of(descriptionId));
        }

        public void removeAssistiveDescription() {
            Roles.getTablistRole().removeAriaDescribedbyProperty(getElement());
        }
    }

    public static class TabCaption extends VCaption {

        private boolean closable = false;
        private Element closeButton;
        private Tab tab;

        TabCaption(Tab tab) {
            super(tab.getTabsheet().connector.getConnection());
            this.tab = tab;

            AriaHelper.ensureHasId(getElement());
        }

        private boolean update(TabState tabState) {
            if (tabState.description != null || tabState.componentError != null) {
                setTooltipInfo(new TooltipInfo(tabState.description,
                        tabState.componentError, this));
            } else {
                setTooltipInfo(null);
            }

            // TODO need to call this instead of super because the caption does
            // not have an owner
            String captionString = tabState.caption.isEmpty() ? null
                    : tabState.caption;
            boolean ret = updateCaptionWithoutOwner(captionString,
                    !tabState.enabled, hasAttribute(tabState.description),
                    hasAttribute(tabState.componentError),
                    tab.getTabsheet().connector
                            .getResourceUrl(ComponentConstants.ICON_RESOURCE
                                    + tabState.key), tabState.iconAltText);

            setClosable(tabState.closable);

            return ret;
        }

        private boolean hasAttribute(String string) {
            return string != null && !string.trim().isEmpty();
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

                Roles.getTabRole().setAriaHiddenState(closeButton, true);
                Roles.getTabRole().setAriaDisabledState(closeButton, true);

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
                width += WidgetUtil.getRequiredWidth(closeButton);
            }
            return width;
        }

        public com.google.gwt.user.client.Element getCloseButton() {
            return DOM.asOld(closeButton);
        }

    }

    static class TabBar extends ComplexPanel implements VCloseHandler {

        private final Element tr = DOM.createTR();

        private final Element spacerTd = DOM.createTD();

        private Tab selected;

        private VTabsheet tabsheet;

        TabBar(VTabsheet tabsheet) {
            this.tabsheet = tabsheet;

            Element el = DOM.createTable();
            Roles.getPresentationRole().set(el);

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

        protected com.google.gwt.user.client.Element getContainerElement() {
            return DOM.asOld(tr);
        }

        /**
         * Gets the number of tabs from the tab bar.
         *
         * @return the number of tabs from the tab bar.
         */
        public int getTabCount() {
            return getWidgetCount();
        }

        /**
         * Adds a tab to the tab bar.
         *
         * @return the added tab.
         */
        public Tab addTab() {
            Tab t = new Tab(this);
            int tabIndex = getTabCount();

            // Logical attach
            insert(t, tr, tabIndex, true);

            if (tabIndex == 0) {
                // Set the "first" style
                t.setStyleNames(false, true);
            }

            getTabsheet().selectionHandler.registerTab(t);

            t.setCloseHandler(this);

            return t;
        }

        /**
         * Gets the tab sheet instance where the tab bar is attached to.
         *
         * @return the tab sheet instance where the tab bar is attached to.
         */
        public VTabsheet getTabsheet() {
            return tabsheet;
        }

        public Tab getTab(int index) {
            if (index < 0 || index >= getTabCount()) {
                return null;
            }
            return (Tab) super.getWidget(index);
        }

        private int getTabIndex(String tabId) {
            if (tabId == null) {
                return -1;
            }
            for (int i = 0; i < getTabCount(); i++) {
                if (tabId.equals(getTab(i).id)) {
                    return i;
                }
            }
            return -1;
        }

        public void selectTab(int index) {
            final Tab newSelected = getTab(index);
            final Tab oldSelected = selected;

            newSelected.setStyleNames(true, isFirstVisibleTab(index), true);
            newSelected.setTabulatorIndex(getTabsheet().tabulatorIndex);
            Roles.getTabRole().setAriaSelectedState(newSelected.getElement(),
                    SelectedValue.TRUE);

            if (oldSelected != null && oldSelected != newSelected) {
                oldSelected.setStyleNames(false,
                        isFirstVisibleTab(getWidgetIndex(oldSelected)));
                oldSelected.setTabulatorIndex(-1);

                Roles.getTabRole().setAriaSelectedState(
                        oldSelected.getElement(), SelectedValue.FALSE);
            }

            // Update the field holding the currently selected tab
            selected = newSelected;

            // The selected tab might need more (or less) space
            newSelected.recalculateCaptionWidth();
            getTab(tabsheet.activeTabIndex).recalculateCaptionWidth();
        }

        public Tab navigateTab(int fromIndex, int toIndex) {
            Tab newNavigated = getTab(toIndex);
            if (newNavigated == null) {
                throw new IllegalArgumentException(
                        "Tab at provided index toIndex was not found");
            }

            Tab oldNavigated = getTab(fromIndex);
            newNavigated.setStyleNames(newNavigated.equals(selected),
                    isFirstVisibleTab(toIndex), true);

            if (oldNavigated != null && fromIndex != toIndex) {
                oldNavigated.setStyleNames(oldNavigated.equals(selected),
                        isFirstVisibleTab(fromIndex), false);
            }

            return newNavigated;
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

            int scrollerIndexCandidate = getTabIndex(getTabsheet().scrollerPositionTabId);
            if (scrollerIndexCandidate < 0) {
                // The tab with id scrollerPositionTabId has been removed
                scrollerIndexCandidate = getTabsheet().scrollerIndex;
            }
            scrollerIndexCandidate = selectNewShownTab(scrollerIndexCandidate);
            if (scrollerIndexCandidate >= 0
                    && scrollerIndexCandidate < getTabCount()) {
                getTabsheet().scrollIntoView(getTab(scrollerIndexCandidate));
            }
        }

        private int selectNewShownTab(int oldPosition) {
            // After removing a tab, find a new scroll position. In most
            // cases the scroll position does not change, but if the tab
            // at the scroll position was removed, need to find a nearby
            // tab that is visible.
            for (int i = oldPosition; i < getTabCount(); i++) {
                Tab tab = getTab(i);
                if (!tab.isHiddenOnServer()) {
                    return i;
                }
            }

            for (int i = oldPosition - 1; i >= 0; i--) {
                Tab tab = getTab(i);
                if (!tab.isHiddenOnServer()) {
                    return i;
                }
            }

            return -1;
        }

        private boolean isFirstVisibleTab(int index) {
            return getFirstVisibleTab() == index;
        }

        /**
         * Returns the index of the first visible tab on the server
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
         * Returns the index of the first visible tab in browser.
         */
        private int getFirstVisibleTabClient() {
            int tabs = getTabCount();
            int i = 0;
            while (i < tabs && !getTab(i).isVisible()) {
                i++;
            }

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

        private void recalculateCaptionWidths() {
            for (int i = 0; i < getTabCount(); ++i) {
                getTab(i).recalculateCaptionWidth();
            }
        }
    }

    // TODO using the CLASSNAME directly makes primaryStyleName for TabSheet of
    // very limited use - all use of style names should be refactored in the
    // future
    public static final String CLASSNAME = TabsheetState.PRIMARY_STYLE_NAME;

    public static final String TABS_CLASSNAME = CLASSNAME + "-tabcontainer";
    public static final String SCROLLER_CLASSNAME = CLASSNAME + "-scroller";

    /** For internal use only. May be removed or replaced in the future. */
    // tabbar and 'scroller' container
    public final Element tabs;

    /**
     * The tabindex property (position in the browser's focus cycle.) Named like
     * this to avoid confusion with activeTabIndex.
     */
    int tabulatorIndex = 0;

    private static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

    // tab-scroller element
    private final Element scroller;
    // tab-scroller next button element
    private final Element scrollerNext;
    // tab-scroller prev button element
    private final Element scrollerPrev;

    /**
     * The index of the first visible tab (when scrolled)
     */
    private int scrollerIndex = 0;
    /**
     * The id of the tab at position scrollerIndex. This is used for keeping the
     * scroll position unchanged when a tab is removed from the server side and
     * the removed tab lies to the left of the current scroll position. For
     * other cases scrollerIndex alone would be sufficient. Since the tab at the
     * current scroll position can be removed, scrollerIndex is required in
     * addition to this variable.
     */
    private String scrollerPositionTabId;

    final TabBar tb = new TabBar(this);
    /** For internal use only. May be removed or replaced in the future. */
    protected final VTabsheetPanel tabPanel = new VTabsheetPanel();
    /** For internal use only. May be removed or replaced in the future. */
    public final Element contentNode;

    private final Element deco;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean waitingForResponse;

    private String currentStyle;

    /**
     * @return Whether the tab could be selected or not.
     */
    private boolean canSelectTab(final int tabIndex) {
        Tab tab = tb.getTab(tabIndex);
        if (getApplicationConnection() == null || disabled
                || waitingForResponse) {
            return false;
        }
        if (!tab.isEnabledOnServer() || tab.isHiddenOnServer()) {
            return false;
        }

        // Note that we return true when tabIndex == activeTabIndex; the active
        // tab could be selected, it's just a no-op.
        return true;
    }

    /**
     * Load the content of a tab of the provided index.
     *
     * @param index
     *            of the tab to load
     *
     * @return true if the specified sheet gets loaded, otherwise false.
     */
    public boolean loadTabSheet(int tabIndex) {
        if (activeTabIndex != tabIndex && canSelectTab(tabIndex)) {
            tb.selectTab(tabIndex);

            activeTabIndex = tabIndex;

            addStyleDependentName("loading");
            // Hide the current contents so a loading indicator can be shown
            // instead
            getCurrentlyDisplayedWidget().getElement().getParentElement()
                    .getStyle().setVisibility(Visibility.HIDDEN);

            getRpcProxy().setSelected(tabKeys.get(tabIndex).toString());

            waitingForResponse = true;

            tb.getTab(tabIndex).focus(); // move keyboard focus to active tab

            return true;
        }

        return false;
    }

    /**
     * Returns the currently displayed widget in the tab panel.
     *
     * @since 7.2
     * @return currently displayed content widget
     */
    public Widget getCurrentlyDisplayedWidget() {
        return tabPanel.getWidget(tabPanel.getVisibleWidget());
    }

    /**
     * Returns the client to server RPC proxy for the tabsheet.
     *
     * @since 7.2
     * @return RPC proxy
     */
    protected TabsheetServerRpc getRpcProxy() {
        return connector.getRpcProxy(TabsheetServerRpc.class);
    }

    /**
     * For internal use only.
     *
     * Avoid using this method directly and use appropriate superclass methods
     * where applicable.
     *
     * @deprecated since 7.2 - use more specific methods instead (getRpcProxy(),
     *             getConnectorForWidget(Widget) etc.)
     * @return ApplicationConnection
     */
    @Deprecated
    public ApplicationConnection getApplicationConnection() {
        return client;
    }

    private VTooltip getVTooltip() {
        return getApplicationConnection().getVTooltip();
    }

    public void tabSizeMightHaveChanged(Tab tab) {
        // icon onloads may change total width of tabsheet
        if (isDynamicWidth()) {
            updateDynamicWidth();
        }
        updateTabScroller();

    }

    void sendTabClosedEvent(int tabIndex) {
        getRpcProxy().closeTab(tabKeys.get(tabIndex));
    }

    public VTabsheet() {
        super(CLASSNAME);

        // Tab scrolling
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        tabs = DOM.createDiv();
        DOM.setElementProperty(tabs, "className", TABS_CLASSNAME);
        Roles.getTablistRole().set(tabs);
        Roles.getTablistRole().setAriaLiveProperty(tabs, LiveValue.OFF);
        scroller = DOM.createDiv();
        Roles.getTablistRole().setAriaHiddenState(scroller, true);

        DOM.setElementProperty(scroller, "className", SCROLLER_CLASSNAME);

        scrollerPrev = DOM.createButton();
        scrollerPrev.setTabIndex(-1);
        DOM.setElementProperty(scrollerPrev, "className", SCROLLER_CLASSNAME
                + "Prev");
        Roles.getTablistRole().setAriaHiddenState(scrollerPrev, true);
        DOM.sinkEvents(scrollerPrev, Event.ONCLICK | Event.ONMOUSEDOWN);

        scrollerNext = DOM.createButton();
        scrollerNext.setTabIndex(-1);
        DOM.setElementProperty(scrollerNext, "className", SCROLLER_CLASSNAME
                + "Next");
        Roles.getTablistRole().setAriaHiddenState(scrollerNext, true);
        DOM.sinkEvents(scrollerNext, Event.ONCLICK | Event.ONMOUSEDOWN);

        DOM.appendChild(getElement(), tabs);

        // Tabs
        tabPanel.setStyleName(CLASSNAME + "-tabsheetpanel");
        contentNode = DOM.createDiv();
        Roles.getTabpanelRole().set(contentNode);

        deco = DOM.createDiv();

        tb.setStyleName(CLASSNAME + "-tabs");
        DOM.setElementProperty(contentNode, "className", CLASSNAME + "-content");
        DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");

        add(tb, tabs);
        DOM.appendChild(scroller, scrollerPrev);
        DOM.appendChild(scroller, scrollerNext);

        DOM.appendChild(getElement(), contentNode);
        add(tabPanel, contentNode);
        DOM.appendChild(getElement(), deco);

        DOM.appendChild(tabs, scroller);

        // TODO Use for Safari only. Fix annoying 1px first cell in TabBar.
        // DOM.setStyleAttribute(DOM.getFirstChild(DOM.getFirstChild(DOM
        // .getFirstChild(tb.getElement()))), "display", "none");

    }

    @Override
    public void onBrowserEvent(Event event) {
        com.google.gwt.dom.client.Element eventTarget = DOM
                .eventGetTarget(event);

        if (event.getTypeInt() == Event.ONCLICK) {

            // Tab scrolling
            if (eventTarget == scrollerPrev || eventTarget == scrollerNext) {
                scrollAccordingToScrollTarget(eventTarget);

                event.stopPropagation();
            }

        } else if (event.getTypeInt() == Event.ONMOUSEDOWN) {

            if (eventTarget == scrollerPrev || eventTarget == scrollerNext) {
                // In case the focus was previously on a Tab, we need to cancel
                // the upcoming blur on the Tab which will follow this mouse
                // down event.
                focusBlurManager.cancelNextBlurSchedule();

                return;
            }
        }

        super.onBrowserEvent(event);
    }

    /*
     * Scroll the tab bar according to the last scrollTarget (the scroll button
     * pressed).
     */
    private void scrollAccordingToScrollTarget(
            com.google.gwt.dom.client.Element scrollTarget) {
        if (scrollTarget == null) {
            return;
        }

        int newFirstIndex = -1;

        // Scroll left.
        if (isScrolledTabs() && scrollTarget == scrollerPrev) {
            newFirstIndex = tb.scrollLeft(scrollerIndex);

            // Scroll right.
        } else if (isClippedTabs() && scrollTarget == scrollerNext) {
            newFirstIndex = tb.scrollRight(scrollerIndex);
        }

        if (newFirstIndex != -1) {
            scrollerIndex = newFirstIndex;
            scrollerPositionTabId = tb.getTab(scrollerIndex).id;
            updateTabScroller();
        }

        // For this to work well, make sure the method gets called only from
        // user events.
        selectionHandler.focusTabAtIndex(scrollerIndex);
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
    public void handleStyleNames(AbstractComponentState state) {
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
            }
        } else {
            tb.setStyleName(CLASSNAME + "-tabs");
            DOM.setElementProperty(tabs, "className", TABS_CLASSNAME);
            DOM.setElementProperty(contentNode, "className", CLASSNAME
                    + "-content");
            DOM.setElementProperty(deco, "className", CLASSNAME + "-deco");
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
        Style style = tabPanel.getElement().getStyle();
        String overflow = style.getProperty("overflow");
        style.setProperty("overflow", "hidden");
        style.setPropertyPx("width", tabsWidth);

        boolean hasTabs = tabPanel.getWidgetCount() > 0;

        Style wrapperstyle = null;
        if (hasTabs) {
            wrapperstyle = getCurrentlyDisplayedWidget().getElement()
                    .getParentElement().getStyle();
            wrapperstyle.setPropertyPx("width", tabsWidth);
        }
        // Get content width from actual widget

        int contentWidth = 0;
        if (hasTabs) {
            contentWidth = getCurrentlyDisplayedWidget().getOffsetWidth();
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

    private boolean isAllTabsBeforeIndexInvisible() {
        boolean invisible = true;
        for (int i = 0; i < scrollerIndex; i++) {
            invisible = invisible & !tb.getTab(i).isVisible();
        }
        return invisible;
    }

    private boolean isScrollerPrevDisabled() {
        return scrollerPrev.getClassName().contains(
                PREV_SCROLLER_DISABLED_CLASSNAME);
    }

    private boolean isScrollerHidden() {
        return scroller.getStyle().getDisplay()
                .equals(Display.NONE.getCssName());
    }

    private boolean isIndexSkippingHiddenTabs() {
        return isAllTabsBeforeIndexInvisible()
                && (isScrollerPrevDisabled() || isScrollerHidden());
    }

    @Override
    public void renderTab(final TabState tabState, int index) {
        Tab tab = tb.getTab(index);
        if (tab == null) {
            tab = tb.addTab();
        }

        tab.updateFromState(tabState);
        tab.setEnabledOnServer((!disabledTabKeys.contains(tabKeys.get(index))));
        tab.setHiddenOnServer(!tabState.visible);

        if (scrolledOutOfView(index) && !isIndexSkippingHiddenTabs()) {
            // Should not set tabs visible if they are scrolled out of view
            tab.setVisible(false);
        } else {
            // When the tab was hidden and then turned visible again
            // and there is space for it, it should be in view (#17096) (#17333)
            if (isTabSetVisibleBeforeScroller(tabState, index, tab)) {
                scrollerIndex = index;
                tab.setVisible(true);
                tab.setStyleNames(false, true);

                // scroll to the currently selected tab if it got clipped
                // after making another tab visible
                if (isClippedTabs()) {
                    scrollIntoView(getActiveTab());
                }
            } else {
                tab.setVisible(tabState.visible);
            }
        }

        /*
         * Force the width of the caption container so the content will not wrap
         * and tabs won't be too narrow in certain browsers
         */
        tab.recalculateCaptionWidth();
    }

    /**
     * Checks whether the tab has been set to visible and the scroller is at the
     * first visible tab. That means that the scroller has to be adjusted so
     * that the tab is visible again.
     */
    private boolean isTabSetVisibleBeforeScroller(TabState tabState, int index,
            Tab tab) {
        return isIndexSkippingHiddenTabs() && isScrollerAtFirstVisibleTab()
                && hasTabChangedVisibility(tabState, tab)
                && scrolledOutOfView(index);
    }

    /**
     * Checks whether the tab is visible on server but is not visible on client
     * yet.
     */
    private boolean hasTabChangedVisibility(TabState tabState, Tab tab) {
        return !tab.isVisible() && tabState.visible;
    }

    private boolean isScrollerAtFirstVisibleTab() {
        return tb.getFirstVisibleTabClient() == scrollerIndex;
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

    /**
     * Renders the widget content for a tab sheet.
     *
     * @param newWidget
     */
    public void renderContent(Widget newWidget) {
        assert tabPanel.getWidgetCount() <= 1;

        if (null == newWidget) {
            newWidget = new SimplePanel();
        }

        if (tabPanel.getWidgetCount() == 0) {
            tabPanel.add(newWidget);
        } else if (tabPanel.getWidget(0) != newWidget) {
            tabPanel.remove(0);
            tabPanel.add(newWidget);
        }

        assert tabPanel.getWidgetCount() <= 1;

        // There's never any other index than 0, but maintaining API for now
        tabPanel.showWidget(0);

        VTabsheet.this.iLayout();
        updateOpenTabSize();
        VTabsheet.this.removeStyleDependentName("loading");
    }

    /**
     * Recalculates the sizes of tab captions, causing the tabs to be rendered
     * the correct size.
     */
    private void updateTabCaptionSizes() {
        for (int tabIx = 0; tabIx < tb.getTabCount(); tabIx++) {
            tb.getTab(tabIx).recalculateCaptionWidth();
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateContentNodeHeight() {
        if (!isDynamicHeight()) {
            int contentHeight = getOffsetHeight();
            contentHeight -= deco.getOffsetHeight();
            contentHeight -= tb.getOffsetHeight();

            ComputedStyle cs = new ComputedStyle(contentNode);
            contentHeight -= Math.ceil(cs.getPaddingHeight());
            contentHeight -= Math.ceil(cs.getBorderHeight());

            if (contentHeight < 0) {
                contentHeight = 0;
            }

            // Set proper values for content element
            contentNode.getStyle().setHeight(contentHeight, Unit.PX);
        } else {
            contentNode.getStyle().clearHeight();
        }
    }

    /**
     * Run internal layouting.
     */
    public void iLayout() {
        updateTabScroller();
        updateTabCaptionSizes();
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
        tabPanel.fixVisibleTabSize(width, height, minWidth);

    }

    /**
     * Layouts the tab-scroller elements, and applies styles.
     */
    private void updateTabScroller() {
        if (!isDynamicWidth()) {
            tabs.getStyle().setWidth(100, Unit.PCT);
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
            scroller.getStyle().clearDisplay();
            DOM.setElementProperty(scrollerPrev, "className",
                    SCROLLER_CLASSNAME
                            + (scrolled ? "Prev"
                                    : PREV_SCROLLER_DISABLED_CLASSNAME));
            DOM.setElementProperty(scrollerNext, "className",
                    SCROLLER_CLASSNAME + (clipped ? "Next" : "Next-disabled"));

            // the active tab should be focusable if and only if it is visible
            boolean isActiveTabVisible = scrollerIndex <= activeTabIndex
                    && !isClipped(tb.selected);
            tb.selected.setTabulatorIndex(isActiveTabVisible ? tabulatorIndex
                    : -1);

        } else {
            scroller.getStyle().setDisplay(Display.NONE);
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
        scrollerPositionTabId = scrollerIndex < 0 ? null : tb
                .getTab(scrollerIndex).id;
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
        tabPanel.clear();

    }

    @Override
    public Iterator<Widget> getWidgetIterator() {
        return tabPanel.iterator();
    }

    /** For internal use only. May be removed or replaced in the future. */
    public int getContentAreaBorderWidth() {
        return WidgetUtil.measureHorizontalBorder(contentNode);
    }

    @Override
    public int getTabCount() {
        return tb.getTabCount();
    }

    @Override
    public ComponentConnector getTab(int index) {
        if (tabPanel.getWidgetCount() > index) {
            Widget widget = tabPanel.getWidget(index);
            return getConnectorForWidget(widget);
        }
        return null;
    }

    @Override
    public void removeTab(int index) {
        tb.removeTab(index);

        // Removing content from tp is handled by the connector
    }

    @Override
    public void selectTab(int index) {
        tb.selectTab(index);
    }

    @Override
    public void focus() {
        getActiveTab().focus();
    }

    public void blur() {
        getActiveTab().blur();
    }

    /*
     * Gets the active tab.
     */
    private Tab getActiveTab() {
        return tb.getTab(activeTabIndex);
    }

    @Override
    public void setConnector(AbstractComponentConnector connector) {
        super.setConnector(connector);

        focusBlurManager.connector = connector;
    }

    /*
     * The focus and blur manager instance.
     */
    private FocusBlurManager focusBlurManager = new FocusBlurManager();

    /*
     * Generate the correct focus/blur events for the main TabSheet component
     * (#14304).
     * 
     * The TabSheet must fire one focus event when the user clicks on the tab
     * bar (i.e. inner TabBar class) containing the Tabs or when the focus is
     * provided to the TabSheet by any means. Also one blur event should be
     * fired only when the user leaves the tab bar. After the user focus on the
     * tab bar and before leaving it, no matter how many times he's pressing the
     * Tabs or the scroll buttons, the TabSheet component should not fire any of
     * those blur/focus events.
     * 
     * The only focusable elements contained in the tab bar are the Tabs (see
     * inner class Tab). The reason is the accessibility support.
     * 
     * Having this in mind, the chosen solution path for our problem is to match
     * a sequence of focus/blur events on the tabs, choose only the first focus
     * and last blur events and pass only those further to the main component.
     * Any consecutive blur/focus events on 2 Tabs must be ignored.
     * 
     * Because in a blur event we don't know whether or not a focus will follow,
     * we just defer a command initiated on the blur event to wait and see if
     * any focus will appear. The command will be executed after the next focus,
     * so if no focus was triggered in the mean while it'll submit the blur
     * event to the main component, otherwise it'll do nothing, so the main
     * component will not generate the blur..
     */
    private static class FocusBlurManager {

        // The real tab with focus on it. If the focus goes to another element
        // in the page this will be null.
        private Tab focusedTab;

        /*
         * Gets the focused tab.
         */
        private Tab getFocusedTab() {
            return focusedTab;
        }

        /*
         * Sets the local field tracking the focused tab.
         */
        private void setFocusedTab(Tab focusedTab) {
            this.focusedTab = focusedTab;
        }

        /*
         * The ultimate focus/blur event dispatcher.
         */
        private AbstractComponentConnector connector;

        /**
         * Delegate method for the onFocus event occurring on Tab.
         *
         * @since 7.2.6
         * @param newFocusTab
         *            the new focused tab.
         * @see #onBlur(Tab)
         */
        public void onFocus(Tab newFocusTab) {

            if (connector.hasEventListener(EventId.FOCUS)) {

                // Send the focus event only first time when we focus on any
                // tab. The focused tab will be reseted on the last blur.
                if (focusedTab == null) {
                    connector.getRpcProxy(FocusAndBlurServerRpc.class).focus();
                }
            }

            cancelLastBlurSchedule();

            setFocusedTab(newFocusTab);
        }

        /**
         * Delegate method for the onBlur event occurring on Tab.
         *
         * @param blurSource
         *            the source of the blur.
         *
         * @see #onFocus(Tab)
         */
        public void onBlur(Tab blurSource) {
            if (focusedTab != null && focusedTab == blurSource) {

                if (connector.hasEventListener(EventId.BLUR)) {
                    scheduleBlur(focusedTab);
                }
            }
        }

        /*
         * The last blur command to be executed.
         */
        private BlurCommand blurCommand;

        /*
         * Execute the final blur command.
         */
        private class BlurCommand implements Command {

            /*
             * The blur source.
             */
            private Tab blurSource;

            /**
             * Create the blur command using the blur source.
             *
             * @param blurSource
             *            the source.
             * @param focusedTabProvider
             *            provides the current focused tab.
             */
            public BlurCommand(Tab blurSource) {
                this.blurSource = blurSource;
            }

            /**
             * Stop the command from being executed.
             *
             * @since 7.4
             */
            public void stopSchedule() {
                blurSource = null;
            }

            /**
             * Schedule the command for a deferred execution.
             *
             * @since 7.4
             */
            public void scheduleDeferred() {
                Scheduler.get().scheduleDeferred(this);
            }

            @Override
            public void execute() {

                Tab focusedTab = getFocusedTab();

                if (blurSource == null) {
                    return;
                }

                // The focus didn't change since this blur triggered, so
                // the new focused element is not a tab.
                if (focusedTab == blurSource) {

                    // We're certain there's no focus anymore.
                    focusedTab.removeAssistiveDescription();
                    setFocusedTab(null);

                    connector.getRpcProxy(FocusAndBlurServerRpc.class).blur();
                }

                // Call this to set it to null and be consistent.
                cancelLastBlurSchedule();
            }
        }

        /*
         * Schedule a new blur event for a deferred execution.
         */
        private void scheduleBlur(Tab blurSource) {

            if (nextBlurScheduleCancelled) {

                // This will set the stopNextBlurCommand back to false as well.
                cancelLastBlurSchedule();

                // Reset the status.
                nextBlurScheduleCancelled = false;
                return;
            }

            cancelLastBlurSchedule();

            blurCommand = new BlurCommand(blurSource);
            blurCommand.scheduleDeferred();
        }

        /**
         * Remove the last blur deferred command from execution.
         */
        public void cancelLastBlurSchedule() {
            if (blurCommand != null) {
                blurCommand.stopSchedule();
                blurCommand = null;
            }

            // We really want to make sure this flag gets reseted at any time
            // when something interact with the blur manager and ther's no blur
            // command scheduled (as we just canceled it).
            nextBlurScheduleCancelled = false;
        }

        /**
         * Cancel the next scheduled execution. This method must be called only
         * from an event occurring before the onBlur event. It's the case of IE
         * which doesn't trigger the focus event, so we're using this approach
         * to cancel the next blur event prior it's execution, calling the
         * method from mouse down event.
         */
        public void cancelNextBlurSchedule() {

            // Make sure there's still no other command to be executed.
            cancelLastBlurSchedule();

            nextBlurScheduleCancelled = true;
        }

        /*
         * Flag that the next deferred command won't get executed. This is
         * useful in case of IE where the user focus event don't fire and we're
         * using the mouse down event to track the focus. But the mouse down
         * event triggers before the blur, so we need to cancel the deferred
         * execution in advance.
         */
        private boolean nextBlurScheduleCancelled = false;

    }

    /*
     * The tabs selection handler instance.
     */
    private final TabSelectionHandler selectionHandler = new TabSelectionHandler();

    /*
     * Handle the events for selecting the tabs.
     */
    private class TabSelectionHandler implements FocusHandler, BlurHandler,
            KeyDownHandler, ClickHandler, MouseDownHandler {

        /** For internal use only. May be removed or replaced in the future. */
        // The current visible focused index.
        private int focusedTabIndex = 0;

        /**
         * Register the tab to the selection handler.
         *
         * @param tab
         *            the tab to register.
         */
        public void registerTab(Tab tab) {

            tab.addBlurHandler(this);
            tab.addFocusHandler(this);
            tab.addKeyDownHandler(this);
            tab.addClickHandler(this);
            tab.addMouseDownHandler(this);
        }

        @Override
        public void onBlur(final BlurEvent event) {

            getVTooltip().hideTooltip();

            Object blurSource = event.getSource();

            if (blurSource instanceof Tab) {
                focusBlurManager.onBlur((Tab) blurSource);
            }
        }

        @Override
        public void onFocus(FocusEvent event) {

            if (event.getSource() instanceof Tab) {
                Tab focusSource = (Tab) event.getSource();
                focusBlurManager.onFocus(focusSource);

                if (focusSource.hasTooltip()) {
                    focusSource.setAssistiveDescription(getVTooltip()
                            .getUniqueId());
                    getVTooltip().showAssistive(focusSource.getTooltipInfo());
                }

            }
        }

        @Override
        public void onClick(ClickEvent event) {

            // IE doesn't trigger focus when click, so we need to make sure
            // the previous blur deferred command will get killed.
            focusBlurManager.cancelLastBlurSchedule();

            TabCaption caption = (TabCaption) event.getSource();
            Element targetElement = event.getNativeEvent().getEventTarget()
                    .cast();
            // the tab should not be focused if the close button was clicked
            if (targetElement == caption.getCloseButton()) {
                return;
            }

            int index = tb.getWidgetIndex(caption.getParent());

            tb.navigateTab(focusedTabIndex, index);

            focusedTabIndex = index;

            if (!loadTabSheet(index)) {

                // This needs to be called at the end, as the activeTabIndex
                // is set in the loadTabSheet.
                focus();
            }
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {

            if (event.getSource() instanceof Tab) {

                // IE doesn't trigger focus when click, so we need to make sure
                // the
                // next blur deferred command will get killed.
                focusBlurManager.cancelNextBlurSchedule();
            }
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (event.getSource() instanceof Tab) {
                int keycode = event.getNativeEvent().getKeyCode();

                if (!event.isAnyModifierKeyDown()) {
                    if (keycode == getPreviousTabKey()) {
                        selectPreviousTab();
                        event.stopPropagation();

                    } else if (keycode == getNextTabKey()) {
                        selectNextTab();
                        event.stopPropagation();

                    } else if (keycode == getCloseTabKey()) {
                        Tab tab = tb.getTab(activeTabIndex);
                        if (tab.isClosable()) {
                            tab.onClose();
                        }

                    } else if (keycode == getSelectTabKey()) {
                        loadTabSheet(focusedTabIndex);

                        // Prevent the page from scrolling when hitting space
                        // (select key) to select the current tab.
                        event.preventDefault();
                    }
                }
            }
        }

        /*
         * Left arrow key selection.
         */
        private void selectPreviousTab() {
            int newTabIndex = focusedTabIndex;
            // Find the previous visible and enabled tab if any.
            do {
                newTabIndex--;
            } while (newTabIndex >= 0 && !canSelectTab(newTabIndex));

            if (newTabIndex >= 0) {
                keySelectTab(newTabIndex);
            }
        }

        /*
         * Right arrow key selection.
         */
        private void selectNextTab() {
            int newTabIndex = focusedTabIndex;
            // Find the next visible and enabled tab if any.
            do {
                newTabIndex++;
            } while (newTabIndex < getTabCount() && !canSelectTab(newTabIndex));

            if (newTabIndex < getTabCount()) {
                keySelectTab(newTabIndex);
            }
        }

        /*
         * Select the specified tab using left/right key.
         */
        private void keySelectTab(int newTabIndex) {
            Tab tab = tb.getTab(newTabIndex);
            if (tab == null) {
                return;
            }

            // Focus the tab, otherwise the selected one will loose focus and
            // TabSheet will get blurred.
            focusTabAtIndex(newTabIndex);

            tb.navigateTab(focusedTabIndex, newTabIndex);

            focusedTabIndex = newTabIndex;
        }

        /**
         * Focus the specified tab. Make sure to call this only from user
         * events, otherwise will break things.
         *
         * @param tabIndex
         *            the index of the tab to set.
         */
        void focusTabAtIndex(int tabIndex) {
            Tab tabToFocus = tb.getTab(tabIndex);
            if (tabToFocus != null) {
                tabToFocus.focus();
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
     * Gets the key to activate the selected tab when navigating using
     * previous/next (left/right) keys.
     * 
     * @return the key to activate the selected tab.
     * 
     * @see #getNextTabKey()
     * @see #getPreviousTabKey()
     */
    protected int getSelectTabKey() {
        return KeyCodes.KEY_SPACE;
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

    private void scrollIntoView(Tab tab) {

        if (!tab.isHiddenOnServer()) {

            // Check for visibility first as clipped tabs to the right are
            // always visible.
            // On IE8 a tab with false visibility would have the bounds of the
            // full TabBar.
            if (!tab.isVisible()) {
                while (!tab.isVisible()) {
                    scrollerIndex = tb.scrollLeft(scrollerIndex);
                }
                updateTabScroller();

            } else if (isClipped(tab)) {
                while (isClipped(tab) && scrollerIndex != -1) {
                    scrollerIndex = tb.scrollRight(scrollerIndex);
                }
                updateTabScroller();
            }
            if (scrollerIndex >= 0 && scrollerIndex < tb.getTabCount()) {
                scrollerPositionTabId = tb.getTab(scrollerIndex).id;
            } else {
                scrollerPositionTabId = null;
            }
        }
    }

    /**
     * Makes tab bar visible.
     * 
     * @since 7.2
     */
    public void showTabs() {
        tb.setVisible(true);
        removeStyleName(CLASSNAME + "-hidetabs");
        tb.recalculateCaptionWidths();
    }

    /**
     * Makes tab bar invisible.
     *
     * @since 7.2
     */
    public void hideTabs() {
        tb.setVisible(false);
        addStyleName(CLASSNAME + "-hidetabs");
    }

    /** Matches tab[ix] - used for extracting the index of the targeted tab */
    private static final RegExp SUBPART_TAB_REGEXP = RegExp
            .compile("tab\\[(\\d+)](.*)");

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if ("tabpanel".equals(subPart)) {
            return DOM.asOld(tabPanel.getElement().getFirstChildElement());
        } else if (SUBPART_TAB_REGEXP.test(subPart)) {
            MatchResult result = SUBPART_TAB_REGEXP.exec(subPart);
            int tabIx = Integer.valueOf(result.getGroup(1));
            Tab tab = tb.getTab(tabIx);
            if (tab != null) {
                if ("/close".equals(result.getGroup(2))) {
                    if (tab.isClosable()) {
                        return tab.tabCaption.getCloseButton();
                    }
                } else {
                    return tab.tabCaption.getElement();
                }
            }
        }
        return null;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (tabPanel.getElement().equals(subElement.getParentElement())
                || tabPanel.getElement().equals(subElement)) {
            return "tabpanel";
        } else {
            for (int i = 0; i < tb.getTabCount(); ++i) {
                Tab tab = tb.getTab(i);
                if (tab.isClosable()
                        && tab.tabCaption.getCloseButton().isOrHasChild(
                                subElement)) {
                    return "tab[" + i + "]/close";
                } else if (tab.getElement().isOrHasChild(subElement)) {
                    return "tab[" + i + "]";
                }
            }
        }
        return null;
    }
}
