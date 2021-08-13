/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

/**
 * Widget class for the TabSheet component. Displays one child item's contents
 * at a time.
 *
 * @author Vaadin Ltd
 *
 */
public class VTabsheet extends VTabsheetBase
        implements Focusable, SubPartAware {

    private static final String PREV_SCROLLER_DISABLED_CLASSNAME = "Prev-disabled";

    /**
     * Event class for tab closing requests.
     */
    private static class VCloseEvent {
        private Tab tab;

        /**
         * Construct a tab closing request event.
         *
         * @param tab
         *            the tab to close
         */
        VCloseEvent(Tab tab) {
            this.tab = tab;
        }

        /**
         * Returns the tab whose closing has been requested.
         *
         * @return the tab to close
         */
        public Tab getTab() {
            return tab;
        }

    }

    /**
     * Handler interface for dealing with tab closing requests.
     */
    private interface VCloseHandler {
        /**
         * Handle a tab closing request.
         *
         * @param event
         *            the close event
         */
        public void onClose(VCloseEvent event);
    }

    /**
     * Representation of a single "tab" shown in the {@link TabBar}.
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

        /**
         * Returns whether the tab is hidden on server (as opposed to simply
         * hidden because it's scrolled out of view).
         *
         * @return {@code true} if hidden on server, {@code false} otherwise
         */
        public boolean isHiddenOnServer() {
            return hiddenOnServer;
        }

        /**
         * Set tab hidden state on server (as opposed to simply hidden because
         * it's scrolled out of view).
         *
         * @param hiddenOnServer
         *            {@code true} if hidden on server, {@code false} otherwise
         */
        public void setHiddenOnServer(boolean hiddenOnServer) {
            this.hiddenOnServer = hiddenOnServer;
            Roles.getTabRole().setAriaHiddenState(getElement(), hiddenOnServer);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected com.google.gwt.user.client.Element getContainerElement() {
            // Attach caption element to div, not td
            return DOM.asOld(div);
        }

        /**
         * Returns whether the tab is enabled on server (there is no client-side
         * disabling, but the naming convention matches
         * {@link #isHiddenOnServer()}).
         *
         * @return {@code true} if enabled on server, {@code false} otherwise
         */
        public boolean isEnabledOnServer() {
            return enabledOnServer;
        }

        /**
         * Set tab enabled state on server (there is no client-side disabling,
         * but the naming convention matches
         * {@link #setHiddenOnServer(boolean)}).
         *
         * @param enabled
         *            {@code true} if enabled on server, {@code false} otherwise
         */
        public void setEnabledOnServer(boolean enabled) {
            enabledOnServer = enabled;
            Roles.getTabRole().setAriaDisabledState(getElement(), !enabled);

            setStyleName(td, TD_DISABLED_CLASSNAME, !enabled);
            if (!enabled) {
                FOCUS_IMPL.setTabIndex(td, -1);
            }
        }

        /**
         * Adds a {@link ClickEvent} handler to the tab caption.
         *
         * @param handler
         *            the click handler
         */
        public void addClickHandler(ClickHandler handler) {
            tabCaption.addClickHandler(handler);
        }

        /**
         * Sets the close handler for this tab. This handler should be called
         * whenever closing of a tab is requested (by clicking the close button
         * or pressing the close key).
         *
         * @param closeHandler
         *            the close handler
         *
         * @see VTabsheet#getCloseTabKey()
         */
        public void setCloseHandler(VCloseHandler closeHandler) {
            this.closeHandler = closeHandler;
        }

        /**
         * Toggles the style names for the Tab.
         *
         * @param selected
         *            {@code true} if the Tab is selected, {@code false}
         *            otherwise
         * @param first
         *            {@code true} if the Tab is the first visible Tab,
         *            {@code false} otherwise
         */
        public void setStyleNames(boolean selected, boolean first) {
            setStyleNames(selected, first, false);
        }

        /**
         * Sets the style names for this tab according to the given parameters.
         *
         * @param selected
         *            {@code true} if the tab is selected, {@code false}
         *            otherwise
         * @param first
         *            {@code true} if the tab is the first one from the left,
         *            {@code false} otherwise
         * @param keyboardFocus
         *            {@code true} if the tab should display keyboard navigation
         *            focus styles, {@code false} otherwise -- the focus style
         *            name is used by the compatibility themes like
         *            {@code reindeer} ({@code valo} relies on {@code :focus}
         *            pseudo-class)
         */
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

        /**
         * Sets the index that represents the tab's position in the browser's
         * focus cycle. Negative index means that this tab element is not
         * reachable via tabulator navigation.
         * <p>
         * By default only the selected tab has a non-negative tabulator index,
         * and represents the entire tab sheet. If there are any other navigable
         * tabs in the same tab sheet those can be navigated into with
         * next/previous buttons, which does not update the selection until
         * confirmed with a selection key press.
         *
         * @param tabIndex
         *            the tabulator index
         *
         * @see VTabsheet#getNextTabKey()
         * @see VTabsheet#getPreviousTabKey()
         * @see VTabsheet#getSelectTabKey()
         */
        public void setTabulatorIndex(int tabIndex) {
            getElement().setTabIndex(tabIndex);
        }

        /**
         * Returns whether the tab can be closed or not.
         *
         * @return {@code true} if the tab is closable, {@code false} otherwise
         *
         * @see TabCaption#setClosable(boolean)
         */
        public boolean isClosable() {
            return tabCaption.isClosable();
        }

        /**
         * Handles a request to close this tab. Closability should be checked
         * before calling this method. The close request will be delivered to
         * the server, where the actual closing is handled.
         *
         * @see #isClosable()
         */
        public void onClose() {
            closeHandler.onClose(new VCloseEvent(this));
        }

        /**
         * Returns the tab sheet instance where this tab is attached to.
         *
         * @return the current tab sheet
         */
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

        /**
         * Recalculates the required caption width and sets it as the new width.
         * Also updates the tab width bookkeeping of the tab bar if needed. The
         * default implementation for the bookkeeping logic attempts to account
         * for different margins and paddings in the first tab element and its
         * caption element versus the same values in the next visible tab.
         */
        public void recalculateCaptionWidth() {
            boolean visible = isVisible();
            boolean first = td.hasClassName(Tab.TD_FIRST_CLASSNAME);
            if (visible && !tabBar.firstAdjusted) {
                if (first) {
                    tabBar.pendingTab = this;
                } else if (tabBar.pendingTab != null) {
                    // the first visible tab usually has different styling than
                    // the rest, compare the styles against the second visible
                    // tab in order to adjust the saved width for the first tab
                    ComputedStyle tabStyle = new ComputedStyle(getElement());
                    ComputedStyle captionStyle = new ComputedStyle(
                            tabCaption.getElement());
                    ComputedStyle pendingTabStyle = new ComputedStyle(
                            tabBar.pendingTab.getElement());
                    ComputedStyle pendingCaptionStyle = new ComputedStyle(
                            tabBar.pendingTab.tabCaption.getElement());
                    double tabPadding = tabStyle.getPaddingWidth();
                    double tabMargin = tabStyle.getMarginWidth();
                    double captionPadding = captionStyle.getPaddingWidth();
                    double captionMargin = captionStyle.getMarginWidth();
                    double pendingTabPadding = pendingTabStyle
                            .getPaddingWidth();
                    double pendingTabMargin = pendingTabStyle.getMarginWidth();
                    double pendingCaptionPadding = pendingCaptionStyle
                            .getPaddingWidth();
                    double pendingCaptionMargin = pendingCaptionStyle
                            .getMarginWidth();
                    // update the adjuster
                    tabBar.firstTabWidthAdjuster = (int) Math.ceil(tabPadding
                            + tabMargin + captionPadding + captionMargin
                            - pendingTabPadding - pendingTabMargin
                            - pendingCaptionPadding - pendingCaptionMargin);
                    // update the pending tab
                    tabBar.tabWidths.put(tabBar.pendingTab,
                            tabBar.pendingTab.getOffsetWidth()
                                    + tabBar.firstTabWidthAdjuster);
                    // mark adjusting done
                    tabBar.firstAdjusted = true;
                    tabBar.pendingTab = null;
                }
            }
            tabCaption.setWidth(tabCaption.getRequiredWidth() + "px");
            if (visible) {
                if (first) {
                    tabBar.tabWidths.put(this,
                            getOffsetWidth() + tabBar.firstTabWidthAdjuster);
                } else {
                    tabBar.tabWidths.put(this, getOffsetWidth());
                }
            }
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
        public HandlerRegistration addMouseDownHandler(
                MouseDownHandler handler) {
            return addDomHandler(handler, MouseDownEvent.getType());
        }

        @Override
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return addDomHandler(handler, KeyDownEvent.getType());
        }

        /**
         * Scrolls the tab into view and focuses it.
         */
        public void focus() {
            getTabsheet().scrollIntoView(this);
            FOCUS_IMPL.focus(td);
        }

        /**
         * Removes focus from the tab.
         */
        public void blur() {
            FOCUS_IMPL.blur(td);
        }

        /**
         * Returns whether the tab caption has a configured tooltip or not.
         *
         * @return {@code true} if the tab caption has a tooltip, {@code false}
         *         otherwise
         */
        public boolean hasTooltip() {
            return tabCaption.getTooltipInfo() != null;
        }

        /**
         * Returns the tab caption's tooltip info if it has been configured.
         *
         * @return the tooltip info, or {@code null} if no tooltip configuration
         *         found
         */
        public TooltipInfo getTooltipInfo() {
            return tabCaption.getTooltipInfo();
        }

        /**
         * Sets the {@code aria-describedby} attribute for this tab element to
         * the referenced id. This should be called when this tab receives focus
         * and has a tooltip configured.
         *
         * @param descriptionId
         *            the unique id of the tooltip element
         */
        public void setAssistiveDescription(String descriptionId) {
            Roles.getTablistRole().setAriaDescribedbyProperty(getElement(),
                    Id.of(descriptionId));
        }

        /**
         * Removes the {@code aria-describedby} attribute from this tab element.
         * This should be called when this tab loses focus.
         */
        public void removeAssistiveDescription() {
            Roles.getTablistRole().removeAriaDescribedbyProperty(getElement());
        }
    }

    /**
     * Caption implementation for a {@link Tab}.
     */
    public static class TabCaption extends VCaption {

        private boolean closable = false;
        private Element closeButton;
        private Tab tab;

        @SuppressWarnings("deprecation")
        TabCaption(Tab tab) {
            super(tab.getTabsheet().connector.getConnection());
            this.tab = tab;

            AriaHelper.ensureHasId(getElement());
        }

        private boolean update(TabState tabState) {
            if (tabState.description != null
                    || tabState.componentError != null) {
                setTooltipInfo(new TooltipInfo(tabState.description,
                        tabState.descriptionContentMode,
                        tabState.componentError, this,
                        tabState.componentErrorLevel));
            } else {
                setTooltipInfo(null);
            }

            // Need to call this because the caption does not have an owner, and
            // cannot have an owner, because only the selected tab's connector
            // is sent to the client.
            String captionString = tabState.caption.isEmpty() ? null
                    : tabState.caption;
            @SuppressWarnings("deprecation")
            boolean ret = updateCaptionWithoutOwner(captionString,
                    !tabState.enabled, hasAttribute(tabState.description),
                    hasAttribute(tabState.componentError),
                    tabState.componentErrorLevel,
                    tab.getTabsheet().connector.getResourceUrl(
                            ComponentConstants.ICON_RESOURCE + tabState.key),
                    tabState.iconAltText);

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

        /**
         * Returns the tab this caption belongs to.
         *
         * @return the corresponding tab
         */
        public Tab getTab() {
            return tab;
        }

        /**
         * Adds or removes the button for closing the corresponding tab and the
         * style name for a closable tab.
         *
         * @param closable
         *            {@code true} if the tab is closable, {@code false}
         *            otherwise
         */
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

        /**
         * Returns whether the corresponding tab is closable or not.
         *
         * @return {@code true} if the tab is closable, {@code false} otherwise
         */
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

        /**
         * Returns the close button if one exists.
         *
         * @return the close button, or {@code null} if not found
         */
        @SuppressWarnings("deprecation")
        public com.google.gwt.user.client.Element getCloseButton() {
            return DOM.asOld(closeButton);
        }

    }

    /**
     * Container widget that houses all {@link Tab} widgets of a single tab
     * sheet. Only one tab can be selected at the same time, and the selected
     * tab's assigned component is displayed within a {@link VTabsheetPanel}
     * (outside of this tab bar).
     * <p>
     * If there are more tabs that can fit to be visible at the same time, those
     * 'scrolled' out of view to the left are set temporarily hidden, although
     * the elements are still in the DOM tree with {@code display: none;}. The
     * excess tabs to the right don't get the same explicit hiding and are
     * simply not shown because of {@code overflow: hidden;} in the tab
     * container element (parent of this tab bar).
     */
    static class TabBar extends ComplexPanel implements VCloseHandler {

        private final Element tr = DOM.createTR();

        /**
         * Spacer element for filling the gap to the right from the tabs and/or
         * for reserving room for the scroller. By default hidden by Valo theme.
         */
        private final Element spacerTd = DOM.createTD();

        private Tab selected;

        private VTabsheet tabsheet;

        /**
         * For internal use only. May be removed or replaced in the future.
         * <p>
         * Map for saving the closest approximation for how much width each of
         * these tabs would add to this tab bar when made visible. The first
         * visible tab usually has different styling, but as these values are
         * only used in scrolling, there should always be a tab with those
         * styles in view already. Therefore the width to save should
         * approximate the width when the tab is not the first one.
         */
        private Map<Tab, Integer> tabWidths = new HashMap<Tab, Integer>();

        /** Adjuster for countering the different styling for the first tab. */
        private int firstTabWidthAdjuster = 0;
        /** Has the first tab's different styling been adjusted. */
        private boolean firstAdjusted = false;
        /** First visible tab that is pending for saved width adjustment. */
        private Tab pendingTab = null;

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

        @SuppressWarnings("deprecation")
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

            // Save the size that is expected to be needed if this tab is
            // scrolled back to view after getting temporarily hidden. The tab
            // hasn't been initialized from tab state yet so this value is a
            // placeholder.
            tabWidths.put(t, t.getOffsetWidth());

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

        private int getTabIndex(Tab tab) {
            if (tab == null) {
                return -1;
            }
            for (int i = 0; i < getTabCount(); i++) {
                if (tab.equals(getTab(i))) {
                    return i;
                }
            }
            return -1;
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

        /**
         * Selects the indicated tab, deselects the previously selected tab, and
         * updates the style names, tabulator indices, and the
         * {@code aria-selected} roles to match. Also recalculates the tab
         * caption widths in case the addition or removal of the selection style
         * changed them, and schedules a scroll for moving the newly selected
         * tab into view (at the end of the event loop to allow for layouting).
         * If the previously selected item is the same as the new one, nothing
         * is done.
         *
         * @param index
         *            the index of the tab to select
         *
         * @see Tab#setTabulatorIndex(int)
         */
        public void selectTab(int index) {
            final Tab newSelected = getTab(index);
            final Tab oldSelected = selected;
            if (oldSelected == newSelected) {
                return;
            }

            newSelected.setStyleNames(true, isFirstVisibleTabClient(index),
                    true);
            newSelected.setTabulatorIndex(getTabsheet().tabulatorIndex);
            Roles.getTabRole().setAriaSelectedState(newSelected.getElement(),
                    SelectedValue.TRUE);

            if (oldSelected != null) {
                oldSelected.setStyleNames(false,
                        isFirstVisibleTabClient(getWidgetIndex(oldSelected)));
                oldSelected.setTabulatorIndex(-1);

                Roles.getTabRole().setAriaSelectedState(
                        oldSelected.getElement(), SelectedValue.FALSE);

                // The unselected tab might need less (or more) space
                oldSelected.recalculateCaptionWidth();
            }

            // Update the field holding the currently selected tab
            selected = newSelected;

            // The selected tab might need more (or less) space
            newSelected.recalculateCaptionWidth();

            // Scroll the tab into view if it is not already, after layout
            Scheduler.get().scheduleFinally(() -> getTabsheet()
                    .scrollIntoView(getTab(tabsheet.activeTabIndex)));
        }

        /**
         * Updates tab focus styles when navigating from one tab to another.
         * <p>
         * This method should be called when there is either a mouse click at
         * the new tab (which should also trigger selection) or a next/previous
         * key navigation event (which should not, unless confirmed with
         * selection key).
         *
         * @param fromIndex
         *            the index of the previously selected tab
         * @param toIndex
         *            the index of the tab that is getting navigated into
         * @return the tab that gets navigated to
         *
         * @see VTabsheet#getNextTabKey()
         * @see VTabsheet#getPreviousTabKey()
         * @see VTabsheet#getSelectTabKey()
         */
        public Tab navigateTab(int fromIndex, int toIndex) {
            Tab newNavigated = getTab(toIndex);
            if (newNavigated == null) {
                throw new IllegalArgumentException(
                        "Tab at provided index toIndex was not found");
            }

            Tab oldNavigated = getTab(fromIndex);
            newNavigated.setStyleNames(newNavigated.equals(selected),
                    isFirstVisibleTabClient(toIndex), true);

            if (oldNavigated != null && fromIndex != toIndex) {
                oldNavigated.setStyleNames(oldNavigated.equals(selected),
                        isFirstVisibleTabClient(fromIndex), false);
            }

            return newNavigated;
        }

        /**
         * Removes a tab from this tab bar and updates the scroll position if
         * needed. If there is no tab that corresponds with the given index,
         * nothing is done.
         * <p>
         * Tab removal should always get triggered via the connector, even when
         * a tab's close button is clicked. That ensures that the states stay in
         * sync, and that logic such as selection change forced by tab removal
         * only needs to be implemented once.
         *
         * @param i
         *            the index of the tab to remove
         */
        public void removeTab(int i) {
            Tab tab = getTab(i);
            if (tab == null) {
                return;
            }

            remove(tab);
            tabWidths.remove(tab);

            /*
             * If this widget was still selected we need to unselect it. This
             * should only be necessary if there are no other tabs left that the
             * selection could move to. Otherwise the server-side updates the
             * selection when a component is removed from the tab sheet, and the
             * connector handles that selection change before triggering tab
             * removal.
             */
            if (tab == selected) {
                selected = null;
            }

            int scrollerIndexCandidate = getTabIndex(
                    getTabsheet().scrollerPositionTabId);
            if (scrollerIndexCandidate < 0) {
                // The tab with id scrollerPositionTabId has been removed
                scrollerIndexCandidate = getTabsheet().scrollerIndex;
            }
            scrollerIndexCandidate = getNearestShownTabIndex(
                    scrollerIndexCandidate);
            if (scrollerIndexCandidate >= 0
                    && scrollerIndexCandidate < getTabCount()) {
                getTabsheet().scrollIntoView(getTab(scrollerIndexCandidate));
            }
        }

        private int getLastKnownTabWidth(Tab tab) {
            if (tabWidths.containsKey(tab)) {
                return tabWidths.get(tab);
            }
            return 0;
        }

        /**
         * After removing a tab, find a new scroll position. In most cases the
         * scroll position does not change, but if the tab at the scroll
         * position was removed, we need to find a nearby tab that is visible.
         * The search is performed first to the right from the original tab
         * (less need to scroll), then to the left.
         *
         * @param oldPosition
         *            the index to start the search from
         * @return the index of the nearest shown tab, or {@code -1} if there
         *         are none
         */
        private int getNearestShownTabIndex(int oldPosition) {
            for (int i = oldPosition; i < getTabCount(); i++) {
                Tab tab = getTab(i);
                if (!tab.isHiddenOnServer()) {
                    return i;
                }
            }

            for (int i = oldPosition - 1; i >= 0; i--) {
                Tab tab = getTab(i);
                if (tab != null && !tab.isHiddenOnServer()) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * Returns whether the given tab index matches the first visible tab on
         * the client.
         *
         * @param index
         *            the index to check
         * @return {@code true} if the given index matches the first visible tab
         *         that hasn't been scrolled out of view, {@code false}
         *         otherwise
         */
        private boolean isFirstVisibleTabClient(int index) {
            return getNextVisibleTab(tabsheet.scrollerIndex - 1) == index;
        }

        /**
         * Returns the index of the first visible tab on the server.
         *
         * @return the index, or {@code -1} if not found
         */
        private int getFirstVisibleTab() {
            return getNextVisibleTab(-1);
        }

        /**
         * Find the next tab that is visible on the server. Being scrolled out
         * of view or clipped on the client does not make a difference. Returns
         * -1 if none is found.
         *
         * @param i
         *            the index to start the search from
         * @return the index of the first visible tab to the right from the
         *         starting point, or {@code -1} if not found
         *
         * @see Tab#isHiddenOnServer()
         * @see VTabsheet#scrolledOutOfView(int)
         * @see VTabsheet#isClipped(Tab)
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
         * Returns the index of the last visible tab on the server.
         *
         * @return the index, or {@code -1} if not found
         */
        private int getLastVisibleTab() {
            return getPreviousVisibleTab(getTabCount());
        }

        /**
         * Find the previous tab that is visible on the server. Being scrolled
         * out of view or clipped on the client does not make a difference.
         * Returns -1 if none is found.
         *
         * @param i
         *            the index to start the search from
         * @return the index of the first visible tab to the left from the
         *         starting point, or {@code -1} if not found
         *
         * @see Tab#isHiddenOnServer()
         * @see VTabsheet#scrolledOutOfView(int)
         * @see VTabsheet#isClipped(Tab)
         */
        private int getPreviousVisibleTab(int i) {
            do {
                i--;
            } while (i >= 0 && getTab(i).isHiddenOnServer());

            return i;
        }

        /**
         * Finds a plausible scroll position to the closest tab on the left that
         * hasn't been set hidden on the server. If a suitable tab is found,
         * also sets that tab visible and removes the first visible style from
         * the previous tab. Does not update the scroller index or set the new
         * first visible style, in case there are multiple calls in a row. Does
         * not update any visibilities or styles if a suitable tab is not found.
         *
         * @param currentFirstVisible
         *            the index of the current first visible tab
         * @return the index of the closest visible tab to the left from the
         *         starting point, or {@code -1} if not found
         */
        public int scrollLeft(int currentFirstVisible) {
            int prevVisible = getPreviousVisibleTab(currentFirstVisible);
            if (prevVisible < 0) {
                return -1;
            }

            Tab newFirst = getTab(prevVisible);
            newFirst.setVisible(true);
            newFirst.recalculateCaptionWidth();
            Tab oldFirst = getTab(currentFirstVisible);
            if (oldFirst != null) {
                oldFirst.setStyleNames(
                        currentFirstVisible == tabsheet.activeTabIndex, false);
            }

            return prevVisible;
        }

        /**
         * Finds a plausible scroll position to the closest tab on the right
         * that hasn't been set hidden on the server. If a suitable tab is
         * found, also sets the previous leftmost tab hidden and remove the
         * first visible styles. Does not update the scroller index or set the
         * new first visible style, in case there are multiple calls in a row.
         * Does not update any visibilities or styles if a suitable tab is not
         * found.
         *
         * @param currentFirstVisible
         *            the index of the current first visible tab
         * @return the index of the closest visible tab to the right from the
         *         starting point, or {@code -1} if not found
         */
        public int scrollRight(int currentFirstVisible) {
            int nextVisible = getNextVisibleTab(currentFirstVisible);
            if (nextVisible < 0) {
                return -1;
            }
            Tab currentFirst = getTab(currentFirstVisible);
            currentFirst.setVisible(false);
            currentFirst.setStyleNames(
                    currentFirstVisible == tabsheet.activeTabIndex, false);
            currentFirst.recalculateCaptionWidth();
            return nextVisible;
        }

        /**
         * Recalculates the caption widths for all tabs within this tab bar, and
         * updates the tab width bookkeeping if necessary.
         */
        private void recalculateCaptionWidths() {
            for (int i = 0; i < getTabCount(); ++i) {
                getTab(i).recalculateCaptionWidth();
            }
        }
    }

    // TODO using the CLASSNAME directly makes primaryStyleName for TabSheet of
    // very limited use - all use of style names should be refactored in the
    // future
    /** Default classname for this widget. */
    public static final String CLASSNAME = TabsheetState.PRIMARY_STYLE_NAME;

    /** Default classname for the element that contains tab bar and scroller. */
    public static final String TABS_CLASSNAME = CLASSNAME + "-tabcontainer";
    /** Default classname for the scroller element. */
    public static final String SCROLLER_CLASSNAME = CLASSNAME + "-scroller";

    /** Focus implementation for creating and manipulating tab sheet focus. */
    private static final FocusImpl FOCUS_IMPL = FocusImpl
            .getFocusImplForPanel();

    /**
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * Container element for tab bar and 'scroller'.
     */
    public final Element tabs;

    /**
     * The tabindex property (position in the browser's focus cycle.) Named like
     * this to avoid confusion with activeTabIndex.
     */
    int tabulatorIndex = 0;

    /**
     * Tab-scroller element, wrapper for the previous and next buttons. No
     * scrollbars are involved, 'scrolling' happens by hiding tabs on the left.
     */
    private final Element scroller;
    /**
     * Tab-scroller next button element. If clicked when active, hides one more
     * tab from the left, which moves more content in view from the right. Focus
     * is moved to the first visible tab.
     */
    private final Element scrollerNext;
    /**
     * Tab-scroller prev button element. If clicked when active, shows one more
     * tab from the left, which moves more content out of view from the right.
     * Focus is moved to the first visible tab.
     */
    private final Element scrollerPrev;

    /** The index of the first visible tab (when scrolled). */
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

    /** Tab bar widget that contains all {@link Tab}s and a spacer. */
    final TabBar tb = new TabBar(this);
    /**
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * The content panel that contains the widget of the content component that
     * has been assigned to the selected tab. There should be at most one tab's
     * content widget added to the panel at the same time.
     */
    protected final VTabsheetPanel tabPanel = new VTabsheetPanel();
    /**
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * The content wrapper element around the content panel.
     */
    public final Element contentNode;

    /**
     * A decorator element at the bottom of the tab sheet, styled in different
     * ways with different themes. The Valo implementation contains a loading
     * animation positioned in the middle of the content panel area, only
     * displayed while the contents are waiting to load, and otherwise the
     * element has {@code display: none;}.
     */
    private final Element deco;

    /**
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * {@code true} if waiting for a server roundtrip to return after requesting
     * selection change, {@code false} otherwise
     */
    public boolean waitingForResponse;

    /**
     * String representation of the currently used list of style names given to
     * this tab sheet. Only used to check whether the list has changed in order
     * to avoid unnecessary updating of all the element styles.
     */
    private String currentStyle;

    /**
     * For internal use only. May be renamed or removed in a future release.
     * <p>
     * Sets the tabulator index for the active tab of the tab sheet. The active
     * tab represents the entire tab sheet in the browser's focus cycle
     * (excluding any focusable elements within the content panel).
     * <p>
     * This value is delegated from the TabsheetState.
     *
     * @param tabIndex
     *            tabulator index for the active tab of the tab sheet
     * @since 8.1.7
     */
    public void setTabIndex(int tabIndex) {
        tabulatorIndex = tabIndex;
        Tab activeTab = getActiveTab();
        if (activeTab != null) {
            activeTab.setTabulatorIndex(tabIndex);
        }
    }

    /**
     * Returns whether the tab could be selected or not. In addition to 'usual'
     * selection blockers like being disabled or hidden, if the tab sheet is
     * already waiting for selection confirmation from the server, any further
     * selections are blocked until the response has been received.
     *
     * @param tabIndex
     *            the index of the tab to check
     *
     * @return {@code true} if selectable, {@code false} otherwise
     */
    private boolean canSelectTab(final int tabIndex) {
        if (getApplicationConnection() == null || disabled
                || waitingForResponse) {
            return false;
        }
        Tab tab = tb.getTab(tabIndex);
        if (!tab.isEnabledOnServer() || tab.isHiddenOnServer()) {
            return false;
        }

        // Note that we return true when tabIndex == activeTabIndex; the active
        // tab could be selected, it's just a no-op.
        return true;
    }

    /**
     * Begin loading of the content of a tab of the provided index. The actual
     * content widget will only be available later, after a server round-trip
     * confirms the selection and switches to send the required child connector.
     * If the tab in the given index is already active, nothing is done.
     *
     * @param tabIndex
     *            The index of the tab to load
     *
     * @return {@code true} if loading of the specified sheet gets successfully
     *         initialized, {@code false} otherwise.
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

            getRpcProxy().setSelected(tabKeys.get(tabIndex));

            waitingForResponse = true;

            // Once upon a time it was necessary to re-establish the tab focus
            // here. This should not be the case with modern browsers.

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

    /**
     * This should be triggered from an onload event within the given tab's
     * caption to signal that icon contents have finished loading. The contents
     * may have changed the tab's width. This might in turn require changes in
     * the scroller (hidden tabs might need to be scrolled back into view), or
     * even the width of the entire tab sheet if it has been configured to be
     * dynamic.
     *
     * @param tab
     *            the tab whose size may have changed
     */
    public void tabSizeMightHaveChanged(Tab tab) {
        // icon onloads may change total width of tabsheet
        if (isDynamicWidth()) {
            updateDynamicWidth();
        }
        updateTabScroller();
    }

    /**
     * Informs the server that closing of a tab has been requested.
     *
     * @param tabIndex
     *            the index of the closed to close
     */
    void sendTabClosedEvent(int tabIndex) {
        getRpcProxy().closeTab(tabKeys.get(tabIndex));
    }

    /**
     * Constructs a widget for a TabSheet component.
     */
    public VTabsheet() {
        super(CLASSNAME);

        // Tab scrolling
        getElement().getStyle().setOverflow(Overflow.HIDDEN);
        tabs = DOM.createDiv();
        tabs.setPropertyString("className", TABS_CLASSNAME);
        Roles.getTablistRole().set(tabs);
        Roles.getTablistRole().setAriaLiveProperty(tabs, LiveValue.OFF);
        scroller = DOM.createDiv();
        Roles.getTablistRole().setAriaHiddenState(scroller, true);

        scroller.setPropertyString("className", SCROLLER_CLASSNAME);

        scrollerPrev = DOM.createButton();
        scrollerPrev.setTabIndex(-1);
        scrollerPrev.setPropertyString("className",
                SCROLLER_CLASSNAME + "Prev");
        Roles.getTablistRole().setAriaHiddenState(scrollerPrev, true);
        DOM.sinkEvents(scrollerPrev, Event.ONCLICK | Event.ONMOUSEDOWN);

        scrollerNext = DOM.createButton();
        scrollerNext.setTabIndex(-1);
        scrollerNext.setPropertyString("className",
                SCROLLER_CLASSNAME + "Next");
        Roles.getTablistRole().setAriaHiddenState(scrollerNext, true);
        DOM.sinkEvents(scrollerNext, Event.ONCLICK | Event.ONMOUSEDOWN);

        DOM.appendChild(getElement(), tabs);

        // Tabs
        tabPanel.setStyleName(CLASSNAME + "-tabsheetpanel");
        contentNode = DOM.createDiv();
        Roles.getTabpanelRole().set(contentNode);

        deco = DOM.createDiv();

        tb.setStyleName(CLASSNAME + "-tabs");
        contentNode.setPropertyString("className", CLASSNAME + "-content");
        deco.setPropertyString("className", CLASSNAME + "-deco");

        add(tb, tabs);
        DOM.appendChild(scroller, scrollerPrev);
        DOM.appendChild(scroller, scrollerNext);

        DOM.appendChild(getElement(), contentNode);
        add(tabPanel, contentNode);
        DOM.appendChild(getElement(), deco);

        DOM.appendChild(tabs, scroller);
    }

    @Override
    public void onBrowserEvent(Event event) {
        com.google.gwt.dom.client.Element eventTarget = DOM
                .eventGetTarget(event);

        if (event.getTypeInt() == Event.ONCLICK) {

            // Tab scrolling
            if (scrollerPrev.equals(eventTarget)
                    || scrollerNext.equals(eventTarget)) {
                scrollAccordingToScrollTarget(eventTarget);

                event.stopPropagation();
            }

        } else if (event.getTypeInt() == Event.ONMOUSEDOWN) {

            if (scrollerPrev.equals(eventTarget)
                    || scrollerNext.equals(eventTarget)) {
                // In case the focus was previously on a Tab, we need to cancel
                // the upcoming blur on the Tab which will follow this mouse
                // down event.
                focusBlurManager.cancelNextBlurSchedule();

                return;
            }
        }

        super.onBrowserEvent(event);
    }

    /**
     * Scroll the tab bar according to the last scrollTarget.
     *
     * @param scrollTarget
     *            the scroll button that was pressed
     */
    private void scrollAccordingToScrollTarget(
            com.google.gwt.dom.client.Element scrollTarget) {
        if (scrollTarget == null) {
            return;
        }

        int newFirstIndex = -1;

        // Scroll left.
        if (hasScrolledTabs() && scrollTarget == scrollerPrev) {
            newFirstIndex = tb.scrollLeft(scrollerIndex);

            // Scroll right.
        } else if (hasClippedTabs() && scrollTarget == scrollerNext) {
            newFirstIndex = tb.scrollRight(scrollerIndex);
        }

        if (newFirstIndex != -1) {
            scrollerIndex = newFirstIndex;
            Tab currentFirst = tb.getTab(newFirstIndex);
            currentFirst.setStyleNames(scrollerIndex == activeTabIndex, true,
                    true);
            scrollerPositionTabId = currentFirst.id;
            updateTabScroller();
        }

        // scrolling updated first visible styles but only removed the previous
        // focus style if the focused tab was also the first tab
        if (selectionHandler.focusedTabIndex >= 0
                && selectionHandler.focusedTabIndex != scrollerIndex) {
            tb.getTab(selectionHandler.focusedTabIndex).setStyleNames(
                    selectionHandler.focusedTabIndex == activeTabIndex, false);
        }

        // For this to work well, make sure the method gets called only from
        // user events.
        selectionHandler.focusTabAtIndex(scrollerIndex);
        /*
         * Update the bookkeeping or the next keyboard navigation starts from
         * the wrong tab.
         *
         * Note: unusually, this can move the focusedTabIndex to point to a
         * disabled tab. We could add more logic that only focuses an
         * unselectable first tab if there are no selectable tabs in view at
         * all, but for now it's left like this for simplicity. Another option
         * would be to put canSelectTab(scrollerIndex) around both of these
         * lines, but that would have more impact on the experienced behavior
         * (using only keyboard or only the arrow buttons seems more likely than
         * mixing them up actively).
         */
        selectionHandler.focusedTabIndex = scrollerIndex;
    }

    /**
     * Checks if the tab with the selected index has been scrolled out of the
     * view (on the left side).
     *
     * @param index
     *            the index of the tab to check
     * @return {@code true} if the index is smaller than the first visible tab's
     *         index, {@code false} otherwise
     */
    private boolean scrolledOutOfView(int index) {
        return scrollerIndex > index;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param state
     *            the state object for this component
     */
    public void handleStyleNames(AbstractComponentState state) {
        // Add proper stylenames for all elements (easier to prevent unwanted
        // style inheritance)
        if (ComponentStateUtil.hasStyles(state)) {
            final List<String> styles = state.styles;
            String newStyles = styles.toString();
            if (currentStyle == null || !currentStyle.equals(newStyles)) {
                currentStyle = newStyles;
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
                tabs.setPropertyString("className", tabsClass);
                contentNode.setPropertyString("className", contentClass);
                deco.setPropertyString("className", decoClass);
            }
        } else {
            tb.setStyleName(CLASSNAME + "-tabs");
            tabs.setPropertyString("className", TABS_CLASSNAME);
            contentNode.setPropertyString("className", CLASSNAME + "-content");
            deco.setPropertyString("className", CLASSNAME + "-deco");
        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @see #isDynamicWidth()
     */
    public void updateDynamicWidth() {
        // Find width consumed by tabs

        // spacer is a filler cell that covers the gap beside the tabs when
        // the content is wider than the collective width of the tabs (also
        // ensures there's room for the scroller element but that is usually
        // hidden in dynamic width tab sheets), by default hidden by Valo
        TableCellElement spacerCell = ((TableCellElement) tb.spacerTd.cast());
        int spacerWidth = spacerCell.getOffsetWidth();
        DivElement spacerContent = (DivElement) spacerCell
                .getFirstChildElement();

        int spacerMinWidth = spacerWidth - spacerContent.getOffsetWidth();

        int tabsWidth = tb.getOffsetWidth() - spacerWidth + spacerMinWidth;

        // Find content width
        Style style = tabPanel.getElement().getStyle();
        String overflow = style.getProperty("overflow");
        style.setProperty("overflow", "hidden");
        // set temporary width to match the tab widths in case the content
        // component is relatively sized and previously calculated width is now
        // too wide
        style.setPropertyPx("width", tabsWidth);

        boolean hasContent = tabPanel.getWidgetCount() > 0;

        Style wrapperstyle = null;
        int contentWidth = 0;
        if (hasContent) {
            wrapperstyle = getCurrentlyDisplayedWidget().getElement()
                    .getParentElement().getStyle();
            wrapperstyle.setPropertyPx("width", tabsWidth);

            // Get content width from actual widget
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
        if (hasContent) {
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
        tab.setEnabledOnServer(!disabledTabKeys.contains(tabKeys.get(index)));

        boolean previouslyVisibleOnServer = !tab.isHiddenOnServer();
        boolean serverVisibilityChanged = previouslyVisibleOnServer != tabState.visible;

        if (serverVisibilityChanged) {
            Tab activeTab = tb.selected;
            boolean activeInView = activeTab != null
                    && !scrolledOutOfView(activeTabIndex)
                    && !isClipped(activeTab);

            if (tabState.visible
                    && needsToScrollIntoViewIfBecomesVisible(index)) {
                scrollerIndex = index;
                scrollerPositionTabId = tab.id;
            }
            /*
             * Technically the scroller position also needs to change if the
             * currently updated tab was the first visible one and is now
             * hidden, but that is dealt with at the end of the state change
             * handling, when layouting is triggered for the whole tab sheet at
             * once. It would be premature to do those calculations here, since
             * the following tabs haven't got refreshed to match the current
             * state yet.
             */

            tab.setHiddenOnServer(!tabState.visible);
            tab.setVisible(tabState.visible && !scrolledOutOfView(index));

            if (activeInView && tab.isVisible() && index < activeTabIndex) {
                // ensure the newly visible tab didn't push the active tab out
                // of view
                if (isClipped(activeTab)) {
                    scrollIntoView(activeTab);
                }
            }

            tab.setStyleNames(activeTabIndex == index, scrollerIndex == index);
        }
        /*
         * There is no need to update the tab visibility if the server
         * visibility didn't change, because the scroller index can only have
         * changed for two reasons while rendering previous tabs:
         *
         * 1) If all previously hidden tabs were also hidden on server, in which
         * case the only tabs that could get automatically scrolled into view
         * are ones that had their hiddenOnServer state updated.
         *
         * 2) If the active tab got clipped and needed to get scrolled into
         * again, in which case the visibilities of all relevant tabs already
         * got refreshed anyway.
         */

        /*
         * Force the width of the caption container so the content will not wrap
         * and tabs won't be too narrow in certain browsers
         */
        tab.recalculateCaptionWidth();
    }

    /**
     * If the tab bar was previously scrolled as far left as it can go, i.e.
     * every scrolled out tab was also hidden on server, and the tab that is
     * getting its visibility updated is among them, it should become the first
     * visible tab instead. If the tab was not among those tabs, the scroller
     * index doesn't need adjusting. If any visible-on-server tabs were already
     * scrolled out of view, scroll position likewise doesn't need adjusting
     * regardless of which side of the line this tab falls.
     * <p>
     * This check must be performed before the tab's hiddenOnServer state is
     * updated, and only if the server visibility is changed from hidden to
     * visible.
     *
     * @param index
     *            the index of the tab that is getting updated
     * @return {@code true} if the given index should become the new scroller
     *         index, {@code false} otherwise
     */
    private boolean needsToScrollIntoViewIfBecomesVisible(int index) {
        // note that these methods use different definition for word 'scrolled',
        // the first one accepts hidden-on-server tabs as scrolled while the
        // second one only cares about tabs that end-user considers scrolled
        return scrolledOutOfView(index) && !hasScrolledTabs();
    }

    /**
     * @deprecated as of 7.1, VTabsheet only keeps the active tab in the DOM
     *             without any place holders.
     */
    @Deprecated
    public class PlaceHolder extends VLabel {
        /** @deprecated This class is not used by the framework code anymore. */
        @Deprecated
        public PlaceHolder() {
            super("");
        }
    }

    /**
     * Renders the widget content for a tab sheet.
     *
     * @param newWidget
     *            the content widget or {@code null} if there is none
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

        iLayout();
        updateOpenTabSize();
        removeStyleDependentName("loading");
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
        // reset the width adjuster, in case the styles have changed
        tb.firstAdjusted = false;
        tb.pendingTab = null;
        tb.firstTabWidthAdjuster = 0;
        updateTabScroller();
        tb.recalculateCaptionWidths();
    }

    /**
     * Sets the size of the visible tab content (component). As the tab is set
     * to position: absolute (to work around a firefox flickering bug) we must
     * keep this up-to-date by hand.
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
             * In case the tab bar happens to be wider than the content we need
             * to use the tab bar width as minimum width to ensure scrollbars
             * get placed correctly (at the right edge).
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
        boolean changed = false;
        if (scrollerIndex < 0 || scrollerIndex > tb.getTabCount()) {
            scrollerIndex = tb.getFirstVisibleTab();
            changed = true;
        } else if (tb.getTabCount() > 0
                && tb.getTab(scrollerIndex).isHiddenOnServer()) {
            scrollerIndex = tb.getNextVisibleTab(scrollerIndex);
            changed = true;
        }

        // This element is hidden by Valo, test with legacy themes.
        TableCellElement spacerCell = ((TableCellElement) tb.spacerTd.cast());
        if (scroller.getStyle().getDisplay() != "none") {
            spacerCell.getStyle().setPropertyPx("minWidth",
                    scroller.getOffsetWidth());
            spacerCell.getStyle().setPropertyPx("minHeight", 1);
        } else {
            spacerCell.getStyle().setProperty("minWidth", "0");
            spacerCell.getStyle().setProperty("minHeight", "0");
        }

        // check if hidden tabs need to be scrolled back into view
        while (hasScrolledTabs()
                && (getLeftGap() + getRightGap() >= getFirstOutOfViewWidth())) {
            scrollerIndex = tb.scrollLeft(scrollerIndex);
            Tab currentFirst = tb.getTab(scrollerIndex);
            scrollerPositionTabId = currentFirst.id;
            // the styles might affect the next round of calculations, must
            // update on every round
            currentFirst.setStyleNames(scrollerIndex == activeTabIndex, true,
                    true);
            currentFirst.recalculateCaptionWidth();
            // everything up to date, can remove the check
            changed = false;
        }

        if (changed) {
            Tab currentFirst = tb.getTab(scrollerIndex);
            currentFirst.setStyleNames(scrollerIndex == activeTabIndex, true,
                    true);
            scrollerPositionTabId = currentFirst.id;
        }

        boolean scrolled = hasScrolledTabs();
        boolean clipped = hasClippedTabs();
        if (tb.getTabCount() > 0 && tb.isVisible() && (scrolled || clipped)) {
            scroller.getStyle().clearDisplay();
            scrollerPrev.setPropertyString("className", SCROLLER_CLASSNAME
                    + (scrolled ? "Prev" : PREV_SCROLLER_DISABLED_CLASSNAME));
            scrollerNext.setPropertyString("className",
                    SCROLLER_CLASSNAME + (clipped
                            && scrollerIndex != tb.getLastVisibleTab() ? "Next"
                                    : "Next-disabled"));

            // the active tab should be focusable if and only if it is visible
            boolean isActiveTabVisible = scrollerIndex <= activeTabIndex
                    && !isClipped(tb.selected);
            tb.selected.setTabulatorIndex(
                    isActiveTabVisible ? tabulatorIndex : -1);

        } else {
            scroller.getStyle().setDisplay(Display.NONE);
        }

        if (BrowserInfo.get().isSafariOrIOS()) {
            /*
             * another hack for webkits. tabscroller sometimes drops without
             * "shaking it" reproducable in
             * com.vaadin.tests.components.tabsheet.TabSheetIcons
             */
            final Style style = scroller.getStyle();
            style.setProperty("whiteSpace", "normal");
            Scheduler.get().scheduleDeferred(
                    () -> style.setProperty("whiteSpace", ""));
        }
    }

    /**
     * Returns the gap between the leftmost visible tab and the tab container
     * edge. By default there should be no gap at all, unless the tabs have been
     * right-aligned by styling (e.g. Valo style {@code right-aligned-tabs} or
     * {@code centered-tabs}).
     *
     * @return the left gap (in pixels), or zero if no gap
     */
    private int getLeftGap() {
        int firstVisibleIndex = tb.getFirstVisibleTab() < 0 ? -1
                : scrollerIndex;
        int gap;
        if (firstVisibleIndex < 0) {
            // no tabs are visible, the entire empty space is returned
            // through getRightGap()
            gap = 0;
        } else {
            Element tabContainer = tb.getElement().getParentElement();
            Tab firstVisibleTab = tb.getTab(firstVisibleIndex);
            gap = firstVisibleTab.getAbsoluteLeft()
                    - tabContainer.getAbsoluteLeft();
        }
        return gap > 0 ? gap : 0;
    }

    /**
     * Returns the gap between the rightmost visible tab and the tab container
     * edge. If the tabs have been right-aligned by styling (e.g. Valo style
     * {@code right-aligned-tabs}) there should be no gap at all.
     *
     * @return the right gap (in pixels), or zero if no gap
     */
    private int getRightGap() {
        int lastVisibleIndex = tb.getLastVisibleTab();
        Element tabContainer = tb.getElement().getParentElement();
        int gap;
        if (lastVisibleIndex < 0) {
            // no tabs visible, return the whole available width
            gap = getOffsetWidth() - scroller.getOffsetWidth();
        } else {
            Tab lastVisibleTab = tb.getTab(lastVisibleIndex);
            gap = tabContainer.getAbsoluteRight()
                    - lastVisibleTab.getAbsoluteLeft()
                    - lastVisibleTab.getOffsetWidth()
                    - scroller.getOffsetWidth();
        }
        return gap > 0 ? gap : 0;
    }

    private int getFirstOutOfViewWidth() {
        Tab firstTabOutOfView = tb
                .getTab(tb.getPreviousVisibleTab(scrollerIndex));
        if (firstTabOutOfView != null) {
            return tb.getLastKnownTabWidth(firstTabOutOfView);
        }
        return 0;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void showAllTabs() {
        scrollerIndex = tb.getFirstVisibleTab();
        scrollerPositionTabId = scrollerIndex < 0 ? null
                : tb.getTab(scrollerIndex).id;
        for (int i = 0; i < tb.getTabCount(); i++) {
            Tab t = tb.getTab(i);
            if (!t.isHiddenOnServer()) {
                t.setVisible(true);
            }
        }
    }

    /**
     * Checks whether there are any tabs scrolled out of view that could be
     * scrolled back into (not hidden on the server). If no such tabs are
     * scrolled out, this check returns {@code false}. Disabled but
     * visible-on-server tabs count as viewable.
     *
     * @return {@code true} if any viewable tabs are scrolled out of view,
     *         {@code false} otherwise
     */
    private boolean hasScrolledTabs() {
        return scrollerIndex > 0 && scrollerIndex > tb.getFirstVisibleTab();
    }

    /**
     * Checks whether there are any tabs clipped out of view (hidden behind the
     * scroller element or overflowing further) that could be scrolled into (not
     * hidden on the server). If no such tabs are clipped, this check returns
     * {@code false}. Disabled but visible-on-server tabs count as viewable.
     *
     * @return {@code true} if any viewable tabs are clipped, {@code false}
     *         otherwise
     */
    private boolean hasClippedTabs() {
        // scroller should only be taken into account if some potentially
        // visible tabs are already scrolled out of view
        return (tb.getOffsetWidth() - getSpacerWidth()) > getOffsetWidth()
                - (hasScrolledTabs() ? scroller.getOffsetWidth() : 0);
    }

    /**
     * Checks whether the given tab is clipped out of view (hidden behind the
     * scroller element or overflowing further). Does not check whether hiding
     * the scroller element would bring this tab fully into view.
     *
     * @return {@code true} if the given tab is clipped, {@code false} otherwise
     */
    private boolean isClipped(Tab tab) {
        return tab.getAbsoluteLeft() + tab.getOffsetWidth() > getAbsoluteLeft()
                + getOffsetWidth() - scroller.getOffsetWidth();
    }

    /**
     * Returns the width of the spacer cell. Valo theme has the element hidden
     * by default, in which case the this returns zero.
     *
     * @return the width of the spacer cell in pixels
     */
    private int getSpacerWidth() {
        return tb.spacerTd.getOffsetWidth();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This method is not called by the framework code anymore.
     */
    @Deprecated
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

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @return the horizontal width consumed by borders of the content area
     */
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

    /**
     * Removes focus from the active tab.
     *
     * @deprecated This method is not called by the framework code anymore.
     */
    @Deprecated
    public void blur() {
        getActiveTab().blur();
    }

    /**
     * Returns the active tab. This method uses
     * {@link VTabsheetBase#activeTabIndex} to identify the tab, which usually
     * matches the value saved to {@link TabBar#selected}, but the former has a
     * default value and the latter doesn't.
     *
     * @return the active tab
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

    /**
     * Generate the correct focus/blur events for the main TabSheet component
     * (#14304).
     *
     * The TabSheet must fire one focus event when the user clicks on the tab
     * bar (i.e. inner {@link TabBar} class) containing the Tabs or when the
     * focus is provided to the TabSheet by any means. Also one blur event
     * should be fired only when the user leaves the tab bar. After the user
     * focus on the tab bar and before leaving it, no matter how many times the
     * Tabs or the scroll buttons are pressed, the TabSheet component should not
     * fire any of those blur/focus events.
     *
     * The only focusable elements contained in the tab bar are the Tabs (see
     * inner class {@link Tab}). The reason is the accessibility support.
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

        /**
         * The real tab with focus on it. If the focus goes to another element
         * in the page this will be null.
         */
        private Tab focusedTab;

        /**
         * Returns the tab that has the focus currently.
         *
         * @return the focused tab or {@code null} if one doesn't exist
         */
        private Tab getFocusedTab() {
            return focusedTab;
        }

        /**
         * Sets the tab that has the focus currently.
         *
         * @param focusedTab
         *            the focused tab or {@code null} if no tab should be
         *            focused anymore
         */
        private void setFocusedTab(Tab focusedTab) {
            this.focusedTab = focusedTab;
        }

        /** The ultimate focus/blur event dispatcher. */
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

        /** The last blur command to be executed. */
        private BlurCommand blurCommand;

        /**
         * Command class for executing the final blur event.
         */
        private class BlurCommand implements Command {

            /** The blur source. */
            private Tab blurSource;

            /**
             * Create the blur command using the blur source.
             *
             * @param blurSource
             *            the source.
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
                if (blurSource == null) {
                    return;
                }

                Tab focusedTab = getFocusedTab();

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

        /**
         * Schedule a new blur event for a deferred execution.
         *
         * @param blurSource
         *            the source tab
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

        /**
         * Flag that the next deferred command won't get executed. This is
         * useful in case of IE where the user focus event don't fire and we're
         * using the mouse down event to track the focus. But the mouse down
         * event triggers before the blur, so we need to cancel the deferred
         * execution in advance.
         */
        private boolean nextBlurScheduleCancelled = false;

    }

    /** The tab selection handler instance. */
    private final TabSelectionHandler selectionHandler = new TabSelectionHandler();

    /**
     * Handler class for tab selection events.
     */
    private class TabSelectionHandler implements FocusHandler, BlurHandler,
            KeyDownHandler, ClickHandler, MouseDownHandler {

        /**
         * For internal use only. May be removed or replaced in the future.
         * <p>
         * The current visible focused index.
         */
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
                    focusSource.setAssistiveDescription(
                            getVTooltip().getUniqueId());
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

            // save the previous focus index in case the clicked tab isn't
            // selectable
            int previouslyFocusedTabIndex = focusedTabIndex;

            focusedTabIndex = index;

            if (!loadTabSheet(index)) {
                // no loading attempted, return focus to the previous tab (which
                // might be the current tab, if the same tab was clicked again)
                if (focusedTabIndex != activeTabIndex) {
                    focusedTabIndex = previouslyFocusedTabIndex;
                }
                tb.getTab(focusedTabIndex).focus();
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
                        focusPreviousTab();
                        event.stopPropagation();

                    } else if (keycode == getNextTabKey()) {
                        focusNextTab();
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

        /**
         * Left arrow key focus move. Selection won't change until the selection
         * key is pressed, but the target tab must be selectable. If no
         * selectable tabs are found before currently focused tab, focus isn't
         * moved.
         */
        private void focusPreviousTab() {
            int newTabIndex = focusedTabIndex;
            // Find the previous visible and enabled tab if any.
            do {
                newTabIndex--;
            } while (newTabIndex >= 0 && !canSelectTab(newTabIndex));

            if (newTabIndex >= 0) {
                keyFocusTab(newTabIndex);
            }
        }

        /**
         * Right arrow key focus move. Selection won't change until the
         * selection key is pressed, but the target tab must be selectable. If
         * no selectable tabs are found after currently focused tab, focus isn't
         * moved.
         */
        private void focusNextTab() {
            int newTabIndex = focusedTabIndex;
            // Find the next visible and enabled tab if any.
            do {
                newTabIndex++;
            } while (newTabIndex < getTabCount() && !canSelectTab(newTabIndex));

            if (newTabIndex < getTabCount()) {
                keyFocusTab(newTabIndex);
            }
        }

        /**
         * Focus the specified tab using left/right key. Selection won't change
         * until the selection key is pressed. Selectability should be checked
         * before calling this method.
         */
        private void keyFocusTab(int newTabIndex) {
            Tab tab = tb.getTab(newTabIndex);
            if (tab == null) {
                return;
            }

            // Focus the tab, otherwise the selected one will lose focus and
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
     * Returns the key code of the keyboard shortcut that focuses the previous
     * tab in a focused tabsheet.
     *
     * @return the key to move focus to the previous tab
     */
    protected int getPreviousTabKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * Gets the key to select the focused tab when navigating using
     * previous/next (left/right) keys.
     *
     * @return the key to select the focused tab.
     *
     * @see #getNextTabKey()
     * @see #getPreviousTabKey()
     */
    protected int getSelectTabKey() {
        return KeyCodes.KEY_SPACE;
    }

    /**
     * Returns the key code of the keyboard shortcut that focuses the next tab
     * in a focused tabsheet.
     *
     * @return the key to move focus to the next tab
     */
    protected int getNextTabKey() {
        return KeyCodes.KEY_RIGHT;
    }

    /**
     * Returns the key code of the keyboard shortcut that closes the currently
     * focused tab (if closable) in a focused tabsheet.
     *
     * @return the key to close the current tab
     */
    protected int getCloseTabKey() {
        return KeyCodes.KEY_DELETE;
    }

    /**
     * Scrolls the given tab into view. If the tab is hidden on the server,
     * nothing is done.
     *
     * @param tab
     *            the tab to scroll to
     */
    private void scrollIntoView(Tab tab) {

        if (!tab.isHiddenOnServer()) {

            // Check for visibility first as clipped tabs to the right are
            // always visible.
            // On IE8 a tab with false visibility would have the bounds of the
            // full TabBar.
            if (!tab.isVisible()) {
                while (!tab.isVisible() && scrollerIndex > 0) {
                    scrollerIndex = tb.scrollLeft(scrollerIndex);
                }
                updateTabScroller();

            } else if (isClipped(tab)
                    && scrollerIndex < tb.getLastVisibleTab()) {
                int tabIndex = tb.getTabIndex(tab);
                while (isClipped(tab) && scrollerIndex >= 0
                        && scrollerIndex < tabIndex) {
                    scrollerIndex = tb.scrollRight(scrollerIndex);
                }
                updateTabScroller();
            }
            if (scrollerIndex >= 0 && scrollerIndex < tb.getTabCount()) {
                Tab currentFirst = tb.getTab(scrollerIndex);
                // keep the previous keyboard focus style, focus change should
                // be handled elsewhere if needed
                currentFirst.setStyleNames(scrollerIndex == activeTabIndex,
                        true, currentFirst.td
                                .hasClassName(Tab.TD_FOCUS_FIRST_CLASSNAME));
                scrollerPositionTabId = currentFirst.id;
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

    @SuppressWarnings("deprecation")
    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
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

    @SuppressWarnings("deprecation")
    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
        if (tabPanel.getElement().equals(subElement.getParentElement())
                || tabPanel.getElement().equals(subElement)) {
            return "tabpanel";
        } else {
            for (int i = 0; i < tb.getTabCount(); ++i) {
                Tab tab = tb.getTab(i);
                if (tab.isClosable() && tab.tabCaption.getCloseButton()
                        .isOrHasChild(subElement)) {
                    return "tab[" + i + "]/close";
                } else if (tab.getElement().isOrHasChild(subElement)) {
                    return "tab[" + i + "]";
                }
            }
        }
        return null;
    }
}
