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
import com.vaadin.client.Focusable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.VTooltip;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.tabsheet.TabState;
import com.vaadin.shared.ui.tabsheet.TabsheetServerRpc;
import com.vaadin.shared.ui.tabsheet.TabsheetState;

public class VTabsheet extends VTabsheetBase implements Focusable,
        FocusHandler, BlurHandler, KeyDownHandler, SubPartAware {

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

            addFocusHandler(getTabsheet());
            addBlurHandler(getTabsheet());
            addKeyDownHandler(getTabsheet());
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
                        tabState.componentError));
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
                width += Util.getRequiredWidth(closeButton);
            }
            return width;
        }

        public com.google.gwt.user.client.Element getCloseButton() {
            return DOM.asOld(closeButton);
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

            navigateTab(getTabsheet().focusedTabIndex, index);
            getTabsheet().focusedTabIndex = index;
            getTabsheet().focusedTab = getTab(index);
            getTabsheet().focus();
            getTabsheet().loadTabSheet(index);
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

        public void navigateTab(int fromIndex, int toIndex) {
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
    Tab focusedTab;
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

    final TabBar tb = new TabBar(this);
    /** For internal use only. May be removed or replaced in the future. */
    protected final VTabsheetPanel tabPanel = new VTabsheetPanel();
    /** For internal use only. May be removed or replaced in the future. */
    public final Element contentNode;

    private final Element deco;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean waitingForResponse;

    private String currentStyle;

    /** For internal use only. May be removed or replaced in the future. */
    private int focusedTabIndex = 0;

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
     */
    public void loadTabSheet(int tabIndex) {
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
        }
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

        addHandler(this, FocusEvent.getType());
        addHandler(this, BlurEvent.getType());

        // Tab scrolling
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
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
        DOM.sinkEvents(scrollerPrev, Event.ONCLICK);
        scrollerNext = DOM.createButton();
        scrollerNext.setTabIndex(-1);
        DOM.setElementProperty(scrollerNext, "className", SCROLLER_CLASSNAME
                + "Next");
        Roles.getTablistRole().setAriaHiddenState(scrollerNext, true);
        DOM.sinkEvents(scrollerNext, Event.ONCLICK);
        DOM.appendChild(getElement(), tabs);

        // Tabs
        tabPanel.setStyleName(CLASSNAME + "-tabsheetpanel");
        contentNode = DOM.createDiv();
        Roles.getTabpanelRole().set(contentNode);

        deco = DOM.createDiv();

        addStyleDependentName("loading"); // Indicate initial progress
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

    @Override
    public void renderTab(final TabState tabState, int index) {
        Tab tab = tb.getTab(index);
        if (tab == null) {
            tab = tb.addTab();
        }

        tab.updateFromState(tabState);
        tab.setEnabledOnServer((!disabledTabKeys.contains(tabKeys.get(index))));
        tab.setHiddenOnServer(!tabState.visible);

        if (scrolledOutOfView(index)) {
            // Should not set tabs visible if they are scrolled out of view
            tabState.visible = false;
        }
        // Set the current visibility of the tab (in the browser)
        tab.setVisible(tabState.visible);

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
     * Recalculates the sizes of tab captions, causing the tabs to be
     * rendered the correct size.
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
        tabPanel.clear();

    }

    @Override
    public Iterator<Widget> getWidgetIterator() {
        return tabPanel.iterator();
    }

    /** For internal use only. May be removed or replaced in the future. */
    public int getContentAreaBorderWidth() {
        return Util.measureHorizontalBorder(contentNode);
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
    public void onBlur(BlurEvent event) {
        getVTooltip().hideTooltip();

        if (focusedTab != null && focusedTab == event.getSource()) {
            focusedTab.removeAssistiveDescription();
            focusedTab = null;
            if (connector.hasEventListener(EventId.BLUR)) {
                connector.getRpcProxy(FocusAndBlurServerRpc.class).blur();
            }
        }
    }

    @Override
    public void onFocus(FocusEvent event) {
        if (focusedTab == null && event.getSource() instanceof Tab) {
            focusedTab = (Tab) event.getSource();
            if (connector.hasEventListener(EventId.FOCUS)) {
                connector.getRpcProxy(FocusAndBlurServerRpc.class).focus();
            }

            if (focusedTab.hasTooltip()) {
                focusedTab.setAssistiveDescription(getVTooltip().getUniqueId());
                getVTooltip().showAssistive(focusedTab.getTooltipInfo());
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

    protected int getSelectTabKey() {
        return 32; // Space key
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
        int newTabIndex = focusedTabIndex;
        // Find the previous visible and enabled tab if any.
        do {
            newTabIndex--;
        } while (newTabIndex >= 0 && !canSelectTab(newTabIndex));

        if (newTabIndex >= 0) {
            tb.navigateTab(focusedTabIndex, newTabIndex);
            focusedTabIndex = newTabIndex;

            // If this TabSheet already has focus, set the new selected tab
            // as focused.
            if (focusedTab != null) {
                focusedTab = tb.getTab(focusedTabIndex);
                focusedTab.focus();
            }
        }
    }

    private void selectNextTab() {
        int newTabIndex = focusedTabIndex;
        // Find the next visible and enabled tab if any.
        do {
            newTabIndex++;
        } while (newTabIndex < getTabCount() && !canSelectTab(newTabIndex));

        if (newTabIndex < getTabCount()) {

            tb.navigateTab(focusedTabIndex, newTabIndex);
            focusedTabIndex = newTabIndex;

            // If this TabSheet already has focus, set the new selected tab
            // as focused.
            if (focusedTab != null) {
                focusedTab = tb.getTab(focusedTabIndex);
                focusedTab.focus();
            }
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
