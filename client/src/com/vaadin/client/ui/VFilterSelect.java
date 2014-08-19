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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.Focusable;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.aria.HandlesAriaCaption;
import com.vaadin.client.ui.aria.HandlesAriaInvalid;
import com.vaadin.client.ui.aria.HandlesAriaRequired;
import com.vaadin.client.ui.menubar.MenuBar;
import com.vaadin.client.ui.menubar.MenuItem;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.combobox.FilteringMode;

/**
 * Client side implementation of the Select component.
 * 
 * TODO needs major refactoring (to be extensible etc)
 */
@SuppressWarnings("deprecation")
public class VFilterSelect extends Composite implements Field, KeyDownHandler,
        KeyUpHandler, ClickHandler, FocusHandler, BlurHandler, Focusable,
        SubPartAware, HandlesAriaCaption, HandlesAriaInvalid,
        HandlesAriaRequired {

    /**
     * Represents a suggestion in the suggestion popup box
     */
    public class FilterSelectSuggestion implements Suggestion, Command {

        private final String key;
        private final String caption;
        private String iconUri;

        /**
         * Constructor
         * 
         * @param uidl
         *            The UIDL recieved from the server
         */
        public FilterSelectSuggestion(UIDL uidl) {
            key = uidl.getStringAttribute("key");
            caption = uidl.getStringAttribute("caption");
            if (uidl.hasAttribute("icon")) {
                iconUri = client.translateVaadinUri(uidl
                        .getStringAttribute("icon"));
            }
        }

        /**
         * Gets the visible row in the popup as a HTML string. The string
         * contains an image tag with the rows icon (if an icon has been
         * specified) and the caption of the item
         */

        @Override
        public String getDisplayString() {
            final StringBuffer sb = new StringBuffer();
            final Icon icon = client.getIcon(iconUri);
            if (icon != null) {
                sb.append(icon.getElement().getString());
            }
            String content;
            if ("".equals(caption)) {
                // Ensure that empty options use the same height as other
                // options and are not collapsed (#7506)
                content = "&nbsp;";
            } else {
                content = Util.escapeHTML(caption);
            }
            sb.append("<span>" + content + "</span>");
            return sb.toString();
        }

        /**
         * Get a string that represents this item. This is used in the text box.
         */

        @Override
        public String getReplacementString() {
            return caption;
        }

        /**
         * Get the option key which represents the item on the server side.
         * 
         * @return The key of the item
         */
        public String getOptionKey() {
            return key;
        }

        /**
         * Get the URI of the icon. Used when constructing the displayed option.
         * 
         * @return
         */
        public String getIconUri() {
            return iconUri;
        }

        /**
         * Executes a selection of this item.
         */

        @Override
        public void execute() {
            onSuggestionSelected(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FilterSelectSuggestion)) {
                return false;
            }
            FilterSelectSuggestion other = (FilterSelectSuggestion) obj;
            if ((key == null && other.key != null)
                    || (key != null && !key.equals(other.key))) {
                return false;
            }
            if ((caption == null && other.caption != null)
                    || (caption != null && !caption.equals(other.caption))) {
                return false;
            }
            if ((iconUri == null && other.iconUri != null)
                    || (iconUri != null && !iconUri.equals(other.iconUri))) {
                return false;
            }
            return true;
        }
    }

    /**
     * Represents the popup box with the selection options. Wraps a suggestion
     * menu.
     */
    public class SuggestionPopup extends VOverlay implements PositionCallback,
            CloseHandler<PopupPanel> {

        private static final int Z_INDEX = 30000;

        /** For internal use only. May be removed or replaced in the future. */
        public final SuggestionMenu menu;

        private final Element up = DOM.createDiv();
        private final Element down = DOM.createDiv();
        private final Element status = DOM.createDiv();

        private boolean isPagingEnabled = true;

        private long lastAutoClosed;

        private int popupOuterPadding = -1;

        private int topPosition;

        /**
         * Default constructor
         */
        SuggestionPopup() {
            super(true, false, true);
            debug("VFS.SP: constructor()");
            setOwner(VFilterSelect.this);
            menu = new SuggestionMenu();
            setWidget(menu);

            getElement().getStyle().setZIndex(Z_INDEX);

            final Element root = getContainerElement();

            up.setInnerHTML("<span>Prev</span>");
            DOM.sinkEvents(up, Event.ONCLICK);

            down.setInnerHTML("<span>Next</span>");
            DOM.sinkEvents(down, Event.ONCLICK);

            root.insertFirst(up);
            root.appendChild(down);
            root.appendChild(status);

            DOM.sinkEvents(root, Event.ONMOUSEDOWN | Event.ONMOUSEWHEEL);
            addCloseHandler(this);

            Roles.getListRole().set(getElement());
        }

        /**
         * Shows the popup where the user can see the filtered options
         * 
         * @param currentSuggestions
         *            The filtered suggestions
         * @param currentPage
         *            The current page number
         * @param totalSuggestions
         *            The total amount of suggestions
         */
        public void showSuggestions(
                final Collection<FilterSelectSuggestion> currentSuggestions,
                final int currentPage, final int totalSuggestions) {

            debug("VFS.SP: showSuggestions(" + currentSuggestions + ", "
                    + currentPage + ", " + totalSuggestions + ")");

            /*
             * We need to defer the opening of the popup so that the parent DOM
             * has stabilized so we can calculate an absolute top and left
             * correctly. This issue manifests when a Combobox is placed in
             * another popupView which also needs to calculate the absoluteTop()
             * to position itself. #9768
             * 
             * After deferring the showSuggestions method, a problem with
             * navigating in the combo box occurs. Because of that the method
             * navigateItemAfterPageChange in ComboBoxConnector class, which
             * navigates to the exact item after page was changed also was
             * marked as deferred. #11333
             */
            final SuggestionPopup popup = this;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    // Add TT anchor point
                    getElement().setId("VAADIN_COMBOBOX_OPTIONLIST");

                    menu.setSuggestions(currentSuggestions);
                    final int x = VFilterSelect.this.getAbsoluteLeft();

                    topPosition = tb.getAbsoluteTop();
                    topPosition += tb.getOffsetHeight();

                    setPopupPosition(x, topPosition);

                    int nullOffset = (nullSelectionAllowed
                            && "".equals(lastFilter) ? 1 : 0);
                    boolean firstPage = (currentPage == 0);
                    final int first = currentPage * pageLength + 1
                            - (firstPage ? 0 : nullOffset);
                    final int last = first
                            + currentSuggestions.size()
                            - 1
                            - (firstPage && "".equals(lastFilter) ? nullOffset
                                    : 0);
                    final int matches = totalSuggestions - nullOffset;
                    if (last > 0) {
                        // nullsel not counted, as requested by user
                        status.setInnerText((matches == 0 ? 0 : first) + "-"
                                + last + "/" + matches);
                    } else {
                        status.setInnerText("");
                    }
                    // We don't need to show arrows or statusbar if there is
                    // only one page
                    if (totalSuggestions <= pageLength || pageLength == 0) {
                        setPagingEnabled(false);
                    } else {
                        setPagingEnabled(true);
                    }
                    setPrevButtonActive(first > 1);
                    setNextButtonActive(last < matches);

                    // clear previously fixed width
                    menu.setWidth("");
                    menu.getElement().getFirstChildElement().getStyle()
                            .clearWidth();

                    setPopupPositionAndShow(popup);
                    // Fix for #14173
                    // IE9 and IE10 have a bug, when resize an a element with
                    // box-shadow.
                    // IE9 and IE10 need explicit update to remove extra
                    // box-shadows
                    if (BrowserInfo.get().isIE9() || BrowserInfo.get().isIE10()) {
                        forceReflow();
                    }
                }
            });
        }

        /**
         * Should the next page button be visible to the user?
         * 
         * @param active
         */
        private void setNextButtonActive(boolean active) {
            if (enableDebug) {
                debug("VFS.SP: setNextButtonActive(" + active + ")");
            }
            if (active) {
                DOM.sinkEvents(down, Event.ONCLICK);
                down.setClassName(VFilterSelect.this.getStylePrimaryName()
                        + "-nextpage");
            } else {
                DOM.sinkEvents(down, 0);
                down.setClassName(VFilterSelect.this.getStylePrimaryName()
                        + "-nextpage-off");
            }
        }

        /**
         * Should the previous page button be visible to the user
         * 
         * @param active
         */
        private void setPrevButtonActive(boolean active) {
            if (enableDebug) {
                debug("VFS.SP: setPrevButtonActive(" + active + ")");
            }

            if (active) {
                DOM.sinkEvents(up, Event.ONCLICK);
                up.setClassName(VFilterSelect.this.getStylePrimaryName()
                        + "-prevpage");
            } else {
                DOM.sinkEvents(up, 0);
                up.setClassName(VFilterSelect.this.getStylePrimaryName()
                        + "-prevpage-off");
            }

        }

        /**
         * Selects the next item in the filtered selections
         */
        public void selectNextItem() {
            debug("VFS.SP: selectNextItem()");

            final int index = menu.getSelectedIndex() + 1;
            if (menu.getItems().size() > index) {
                selectItem(menu.getItems().get(index));

            } else {
                selectNextPage();
            }
        }

        /**
         * Selects the previous item in the filtered selections
         */
        public void selectPrevItem() {
            debug("VFS.SP: selectPrevItem()");

            final int index = menu.getSelectedIndex() - 1;
            if (index > -1) {
                selectItem(menu.getItems().get(index));

            } else if (index == -1) {
                selectPrevPage();

            } else {
                selectItem(menu.getItems().get(menu.getItems().size() - 1));
            }
        }

        /**
         * Select the first item of the suggestions list popup.
         * 
         * @since 7.2.6
         */
        public void selectFirstItem() {
            debug("VFS.SP: selectFirstItem()");
            selectItem(menu.getFirstItem());
        }

        /**
         * Select the last item of the suggestions list popup.
         * 
         * @since 7.2.6
         */
        public void selectLastItem() {
            debug("VFS.SP: selectLastItem()");
            selectItem(menu.getLastItem());
        }

        /*
         * Sets the selected item in the popup menu.
         */
        private void selectItem(final MenuItem newSelectedItem) {
            menu.selectItem(newSelectedItem);

            String text = newSelectedItem != null ? newSelectedItem.getText()
                    : "";

            // Set the icon.
            FilterSelectSuggestion suggestion = (FilterSelectSuggestion) newSelectedItem
                    .getCommand();
            setSelectedItemIcon(suggestion.getIconUri());

            // Set the text.
            setText(text);

            menu.updateKeyboardSelectedItem();
        }

        /*
         * Using a timer to scroll up or down the pages so when we receive lots
         * of consecutive mouse wheel events the pages does not flicker.
         */
        private LazyPageScroller lazyPageScroller = new LazyPageScroller();

        private class LazyPageScroller extends Timer {
            private int pagesToScroll = 0;

            @Override
            public void run() {
                debug("VFS.SP.LPS: run()");
                if (pagesToScroll != 0) {
                    if (!waitingForFilteringResponse) {
                        /*
                         * Avoid scrolling while we are waiting for a response
                         * because otherwise the waiting flag will be reset in
                         * the first response and the second response will be
                         * ignored, causing an empty popup...
                         * 
                         * As long as the scrolling delay is suitable
                         * double/triple clicks will work by scrolling two or
                         * three pages at a time and this should not be a
                         * problem.
                         */
                        filterOptions(currentPage + pagesToScroll, lastFilter);
                    }
                    pagesToScroll = 0;
                }
            }

            public void scrollUp() {
                debug("VFS.SP.LPS: scrollUp()");
                if (pageLength > 0 && currentPage + pagesToScroll > 0) {
                    pagesToScroll--;
                    cancel();
                    schedule(200);
                }
            }

            public void scrollDown() {
                debug("VFS.SP.LPS: scrollDown()");
                if (pageLength > 0
                        && totalMatches > (currentPage + pagesToScroll + 1)
                                * pageLength) {
                    pagesToScroll++;
                    cancel();
                    schedule(200);
                }
            }
        }

        @Override
        public void onBrowserEvent(Event event) {
            debug("VFS.SP: onBrowserEvent()");

            if (event.getTypeInt() == Event.ONCLICK) {
                final Element target = DOM.eventGetTarget(event);
                if (target == up || target == DOM.getChild(up, 0)) {
                    lazyPageScroller.scrollUp();
                } else if (target == down || target == DOM.getChild(down, 0)) {
                    lazyPageScroller.scrollDown();
                }

            } else if (event.getTypeInt() == Event.ONMOUSEWHEEL) {

                boolean scrollNotActive = !menu.isScrollActive();

                debug("VFS.SP: onBrowserEvent() scrollNotActive: "
                        + scrollNotActive);

                if (scrollNotActive) {
                    int velocity = event.getMouseWheelVelocityY();

                    debug("VFS.SP: onBrowserEvent() velocity: " + velocity);

                    if (velocity > 0) {
                        lazyPageScroller.scrollDown();
                    } else {
                        lazyPageScroller.scrollUp();
                    }
                }
            }

            /*
             * Prevent the keyboard focus from leaving the textfield by
             * preventing the default behaviour of the browser. Fixes #4285.
             */
            handleMouseDownEvent(event);
        }

        /**
         * Should paging be enabled. If paging is enabled then only a certain
         * amount of items are visible at a time and a scrollbar or buttons are
         * visible to change page. If paging is turned of then all options are
         * rendered into the popup menu.
         * 
         * @param paging
         *            Should the paging be turned on?
         */
        public void setPagingEnabled(boolean paging) {
            debug("VFS.SP: setPagingEnabled(" + paging + ")");
            if (isPagingEnabled == paging) {
                return;
            }
            if (paging) {
                down.getStyle().clearDisplay();
                up.getStyle().clearDisplay();
                status.getStyle().clearDisplay();
            } else {
                down.getStyle().setDisplay(Display.NONE);
                up.getStyle().setDisplay(Display.NONE);
                status.getStyle().setDisplay(Display.NONE);
            }
            isPagingEnabled = paging;
        }

        @Override
        public void setPosition(int offsetWidth, int offsetHeight) {
            debug("VFS.SP: setPosition(" + offsetWidth + ", " + offsetHeight
                    + ")");

            int top = topPosition;
            int left = getPopupLeft();

            // reset menu size and retrieve its "natural" size
            menu.setHeight("");
            if (currentPage > 0 && !hasNextPage()) {
                // fix height to avoid height change when getting to last page
                menu.fixHeightTo(pageLength);
            }

            final int desiredHeight = offsetHeight = getOffsetHeight();
            final int desiredWidth = getMainWidth();

            debug("VFS.SP:     desired[" + desiredWidth + ", " + desiredHeight
                    + "]");

            Element menuFirstChild = menu.getElement().getFirstChildElement();
            final int naturalMenuWidth = menuFirstChild.getOffsetWidth();

            if (popupOuterPadding == -1) {
                popupOuterPadding = Util.measureHorizontalPaddingAndBorder(
                        getElement(), 2);
            }

            if (naturalMenuWidth < desiredWidth) {
                menu.setWidth((desiredWidth - popupOuterPadding) + "px");
                menuFirstChild.getStyle().setWidth(100, Unit.PCT);
            }

            if (BrowserInfo.get().isIE()) {
                /*
                 * IE requires us to specify the width for the container
                 * element. Otherwise it will be 100% wide
                 */
                int rootWidth = Math.max(desiredWidth, naturalMenuWidth)
                        - popupOuterPadding;
                getContainerElement().getStyle().setWidth(rootWidth, Unit.PX);
            }

            final int vfsHeight = VFilterSelect.this.getOffsetHeight();
            final int spaceAvailableAbove = top - vfsHeight;
            final int spaceAvailableBelow = Window.getClientHeight() - top;
            if (spaceAvailableBelow < offsetHeight
                    && spaceAvailableBelow < spaceAvailableAbove) {
                // popup on top of input instead
                top -= offsetHeight + vfsHeight;
                if (top < 0) {
                    offsetHeight += top;
                    top = 0;
                }
            } else {
                offsetHeight = Math.min(offsetHeight, spaceAvailableBelow);
            }

            // fetch real width (mac FF bugs here due GWT popups overflow:auto )
            offsetWidth = menuFirstChild.getOffsetWidth();

            if (offsetHeight < desiredHeight) {
                int menuHeight = offsetHeight;
                if (isPagingEnabled) {
                    menuHeight -= up.getOffsetHeight() + down.getOffsetHeight()
                            + status.getOffsetHeight();
                } else {
                    final ComputedStyle s = new ComputedStyle(menu.getElement());
                    menuHeight -= s.getIntProperty("marginBottom")
                            + s.getIntProperty("marginTop");
                }

                // If the available page height is really tiny then this will be
                // negative and an exception will be thrown on setHeight.
                int menuElementHeight = menu.getItemOffsetHeight();
                if (menuHeight < menuElementHeight) {
                    menuHeight = menuElementHeight;
                }

                menu.setHeight(menuHeight + "px");

                final int naturalMenuWidthPlusScrollBar = naturalMenuWidth
                        + Util.getNativeScrollbarSize();
                if (offsetWidth < naturalMenuWidthPlusScrollBar) {
                    menu.setWidth(naturalMenuWidthPlusScrollBar + "px");
                }
            }

            if (offsetWidth + left > Window.getClientWidth()) {
                left = VFilterSelect.this.getAbsoluteLeft()
                        + VFilterSelect.this.getOffsetWidth() - offsetWidth;
                if (left < 0) {
                    left = 0;
                    menu.setWidth(Window.getClientWidth() + "px");
                }
            }

            setPopupPosition(left, top);
            menu.scrollSelectionIntoView();
        }

        /**
         * Was the popup just closed?
         * 
         * @return true if popup was just closed
         */
        public boolean isJustClosed() {
            debug("VFS.SP: justClosed()");
            final long now = (new Date()).getTime();
            return (lastAutoClosed > 0 && (now - lastAutoClosed) < 200);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google
         * .gwt.event.logical.shared.CloseEvent)
         */

        @Override
        public void onClose(CloseEvent<PopupPanel> event) {
            if (enableDebug) {
                debug("VFS.SP: onClose(" + event.isAutoClosed() + ")");
            }
            if (event.isAutoClosed()) {
                lastAutoClosed = (new Date()).getTime();
            }
        }

        /**
         * Updates style names in suggestion popup to help theme building.
         * 
         * @param uidl
         *            UIDL for the whole combo box
         * @param componentState
         *            shared state of the combo box
         */
        public void updateStyleNames(UIDL uidl,
                AbstractComponentState componentState) {
            debug("VFS.SP: updateStyleNames()");
            setStyleName(VFilterSelect.this.getStylePrimaryName()
                    + "-suggestpopup");
            menu.setStyleName(VFilterSelect.this.getStylePrimaryName()
                    + "-suggestmenu");
            status.setClassName(VFilterSelect.this.getStylePrimaryName()
                    + "-status");
            if (ComponentStateUtil.hasStyles(componentState)) {
                for (String style : componentState.styles) {
                    if (!"".equals(style)) {
                        addStyleDependentName(style);
                    }
                }
            }
        }

    }

    /**
     * The menu where the suggestions are rendered
     */
    public class SuggestionMenu extends MenuBar implements SubPartAware,
            LoadHandler {

        /**
         * Tracks the item that is currently selected using the keyboard. This
         * is need only because mouseover changes the selection and we do not
         * want to use that selection when pressing enter to select the item.
         */
        private MenuItem keyboardSelectedItem;

        private VLazyExecutor delayedImageLoadExecutioner = new VLazyExecutor(
                100, new ScheduledCommand() {

                    @Override
                    public void execute() {
                        debug("VFS.SM: delayedImageLoadExecutioner()");
                        if (suggestionPopup.isVisible()
                                && suggestionPopup.isAttached()) {
                            setWidth("");
                            getElement().getFirstChildElement().getStyle()
                                    .clearWidth();
                            suggestionPopup
                                    .setPopupPositionAndShow(suggestionPopup);
                        }

                    }
                });

        /**
         * Default constructor
         */
        SuggestionMenu() {
            super(true);
            debug("VFS.SM: constructor()");
            addDomHandler(this, LoadEvent.getType());

            setScrollEnabled(true);
        }

        /**
         * Fixes menus height to use same space as full page would use. Needed
         * to avoid height changes when quickly "scrolling" to last page.
         */
        public void fixHeightTo(int pageItemsCount) {
            setHeight(getPreferredHeight(pageItemsCount));
        }

        /*
         * Gets the preferred height of the menu including pageItemsCount items.
         */
        String getPreferredHeight(int pageItemsCount) {
            if (currentSuggestions.size() > 0) {
                final int pixels = (getPreferredHeight() / currentSuggestions
                        .size()) * pageItemsCount;
                return pixels + "px";
            } else {
                return "";
            }
        }

        /**
         * Sets the suggestions rendered in the menu
         * 
         * @param suggestions
         *            The suggestions to be rendered in the menu
         */
        public void setSuggestions(
                Collection<FilterSelectSuggestion> suggestions) {
            if (enableDebug) {
                debug("VFS.SM: setSuggestions(" + suggestions + ")");
            }
            // Reset keyboard selection when contents is updated to avoid
            // reusing old, invalid data
            setKeyboardSelectedItem(null);

            clearItems();
            final Iterator<FilterSelectSuggestion> it = suggestions.iterator();
            while (it.hasNext()) {
                final FilterSelectSuggestion s = it.next();
                final MenuItem mi = new MenuItem(s.getDisplayString(), true, s);
                Roles.getListitemRole().set(mi.getElement());

                Util.sinkOnloadForImages(mi.getElement());

                this.addItem(mi);
                if (s == currentSuggestion) {
                    selectItem(mi);
                }
            }
        }

        /**
         * Send the current selection to the server. Triggered when a selection
         * is made or on a blur event.
         */
        public void doSelectedItemAction() {
            debug("VFS.SM: doSelectedItemAction()");
            // do not send a value change event if null was and stays selected
            final String enteredItemValue = tb.getText();
            if (nullSelectionAllowed && "".equals(enteredItemValue)
                    && selectedOptionKey != null
                    && !"".equals(selectedOptionKey)) {
                if (nullSelectItem) {
                    reset();
                    return;
                }
                // null is not visible on pages != 0, and not visible when
                // filtering: handle separately
                client.updateVariable(paintableId, "filter", "", false);
                client.updateVariable(paintableId, "page", 0, false);
                client.updateVariable(paintableId, "selected", new String[] {},
                        immediate);
                afterUpdateClientVariables();

                suggestionPopup.hide();
                return;
            }

            updateSelectionWhenReponseIsReceived = waitingForFilteringResponse;
            if (!waitingForFilteringResponse) {
                doPostFilterSelectedItemAction();
            }
        }

        /**
         * Triggered after a selection has been made
         */
        public void doPostFilterSelectedItemAction() {
            debug("VFS.SM: doPostFilterSelectedItemAction()");
            final MenuItem item = getSelectedItem();
            final String enteredItemValue = tb.getText();

            updateSelectionWhenReponseIsReceived = false;

            // check for exact match in menu
            int p = getItems().size();
            if (p > 0) {
                for (int i = 0; i < p; i++) {
                    final MenuItem potentialExactMatch = getItems().get(i);
                    if (potentialExactMatch.getText().equals(enteredItemValue)) {
                        selectItem(potentialExactMatch);
                        // do not send a value change event if null was and
                        // stays selected
                        if (!"".equals(enteredItemValue)
                                || (selectedOptionKey != null && !""
                                        .equals(selectedOptionKey))) {
                            doItemAction(potentialExactMatch, true);
                        }
                        suggestionPopup.hide();
                        return;
                    }
                }
            }
            if (allowNewItem) {

                if (!prompting && !enteredItemValue.equals(lastNewItemString)) {
                    /*
                     * Store last sent new item string to avoid double sends
                     */
                    lastNewItemString = enteredItemValue;
                    client.updateVariable(paintableId, "newitem",
                            enteredItemValue, immediate);
                    afterUpdateClientVariables();
                }
            } else if (item != null
                    && !"".equals(lastFilter)
                    && (filteringmode == FilteringMode.CONTAINS ? item
                            .getText().toLowerCase()
                            .contains(lastFilter.toLowerCase()) : item
                            .getText().toLowerCase()
                            .startsWith(lastFilter.toLowerCase()))) {
                doItemAction(item, true);
            } else {
                // currentSuggestion has key="" for nullselection
                if (currentSuggestion != null
                        && !currentSuggestion.key.equals("")) {
                    // An item (not null) selected
                    String text = currentSuggestion.getReplacementString();
                    setText(text);
                    selectedOptionKey = currentSuggestion.key;
                } else {
                    // Null selected
                    setText("");
                    selectedOptionKey = null;
                }
            }
            suggestionPopup.hide();
        }

        private static final String SUBPART_PREFIX = "item";

        @Override
        public com.google.gwt.user.client.Element getSubPartElement(
                String subPart) {
            int index = Integer.parseInt(subPart.substring(SUBPART_PREFIX
                    .length()));

            MenuItem item = getItems().get(index);

            return item.getElement();
        }

        @Override
        public String getSubPartName(
                com.google.gwt.user.client.Element subElement) {
            if (!getElement().isOrHasChild(subElement)) {
                return null;
            }

            Element menuItemRoot = subElement;
            while (menuItemRoot != null
                    && !menuItemRoot.getTagName().equalsIgnoreCase("td")) {
                menuItemRoot = menuItemRoot.getParentElement().cast();
            }
            // "menuItemRoot" is now the root of the menu item

            final int itemCount = getItems().size();
            for (int i = 0; i < itemCount; i++) {
                if (getItems().get(i).getElement() == menuItemRoot) {
                    String name = SUBPART_PREFIX + i;
                    return name;
                }
            }
            return null;
        }

        @Override
        public void onLoad(LoadEvent event) {
            debug("VFS.SM: onLoad()");
            // Handle icon onload events to ensure shadow is resized
            // correctly
            delayedImageLoadExecutioner.trigger();

        }

        private MenuItem getKeyboardSelectedItem() {
            return keyboardSelectedItem;
        }

        public void setKeyboardSelectedItem(MenuItem menuItem) {
            keyboardSelectedItem = menuItem;
        }

        /**
         * @deprecated use {@link SuggestionPopup#selectFirstItem()} instead.
         */
        @Deprecated
        public void selectFirstItem() {
            debug("VFS.SM: selectFirstItem()");
            MenuItem firstItem = getItems().get(0);
            selectItem(firstItem);
        }

        /**
         * @deprecated use {@link SuggestionPopup#selectLastItem()} instead.
         */
        @Deprecated
        public void selectLastItem() {
            debug("VFS.SM: selectLastItem()");
            List<MenuItem> items = getItems();
            MenuItem lastItem = items.get(items.size() - 1);
            selectItem(lastItem);
        }

        /*
         * Sets the keyboard item as the current selected one.
         */
        void updateKeyboardSelectedItem() {
            setKeyboardSelectedItem(getSelectedItem());
        }

        /*
         * Gets the height of one menu item.
         */
        int getItemOffsetHeight() {
            List<MenuItem> items = getItems();
            return items != null && items.size() > 0 ? items.get(0)
                    .getOffsetHeight() : 0;
        }

        /*
         * Gets the width of one menu item.
         */
        int getItemOffsetWidth() {
            List<MenuItem> items = getItems();
            return items != null && items.size() > 0 ? items.get(0)
                    .getOffsetWidth() : 0;
        }

        /**
         * Returns true if the scroll is active on the menu element or if the
         * menu currently displays the last page with less items then the
         * maximum visibility (in which case the scroll is not active, but the
         * scroll is active for any other page in general).
         * 
         * @since 7.2.6
         */
        @Override
        public boolean isScrollActive() {
            String height = getElement().getStyle().getHeight();
            String preferredHeight = getPreferredHeight(pageLength);

            return !(height == null || height.length() == 0 || height
                    .equals(preferredHeight));
        }

    }

    /**
     * TextBox variant used as input element for filter selects, which prevents
     * selecting text when disabled.
     * 
     * @since 7.1.5
     */
    public class FilterSelectTextBox extends TextBox {

        /**
         * Overridden to avoid selecting text when text input is disabled
         */
        @Override
        public void setSelectionRange(int pos, int length) {
            if (textInputEnabled) {
                /*
                 * set selection range with a backwards direction: anchor at the
                 * back, focus at the front. This means that items that are too
                 * long to display will display from the start and not the end
                 * even on Firefox.
                 * 
                 * We need the JSNI function to set selection range so that we
                 * can use the optional direction attribute to set the anchor to
                 * the end and the focus to the start. This makes Firefox work
                 * the same way as other browsers (#13477)
                 */
                Util.setSelectionRange(getElement(), pos, length, "backward");

            } else {
                /*
                 * Setting the selectionrange for an uneditable textbox leads to
                 * unwanted behaviour when the width of the textbox is narrower
                 * than the width of the entry: the end of the entry is shown
                 * instead of the beginning. (see #13477)
                 * 
                 * To avoid this, we set the caret to the beginning of the line.
                 */

                super.setSelectionRange(0, 0);
            }
        }

    }

    @Deprecated
    public static final FilteringMode FILTERINGMODE_OFF = FilteringMode.OFF;
    @Deprecated
    public static final FilteringMode FILTERINGMODE_STARTSWITH = FilteringMode.STARTSWITH;
    @Deprecated
    public static final FilteringMode FILTERINGMODE_CONTAINS = FilteringMode.CONTAINS;

    public static final String CLASSNAME = "v-filterselect";
    private static final String STYLE_NO_INPUT = "no-input";

    /** For internal use only. May be removed or replaced in the future. */
    public int pageLength = 10;

    private boolean enableDebug = false;

    private final FlowPanel panel = new FlowPanel();

    /**
     * The text box where the filter is written
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public final TextBox tb;

    /** For internal use only. May be removed or replaced in the future. */
    public final SuggestionPopup suggestionPopup;

    /**
     * Used when measuring the width of the popup
     */
    private final HTML popupOpener = new HTML("") {

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
         * .user.client.Event)
         */

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);

            /*
             * Prevent the keyboard focus from leaving the textfield by
             * preventing the default behaviour of the browser. Fixes #4285.
             */
            handleMouseDownEvent(event);
        }
    };

    private class IconWidget extends Widget {
        IconWidget(Icon icon) {
            setElement(icon.getElement());
        }
    }

    private IconWidget selectedItemIcon;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public int currentPage;

    /**
     * A collection of available suggestions (options) as received from the
     * server.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public final List<FilterSelectSuggestion> currentSuggestions = new ArrayList<FilterSelectSuggestion>();

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    /** For internal use only. May be removed or replaced in the future. */
    public String selectedOptionKey;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean waitingForFilteringResponse = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean updateSelectionWhenReponseIsReceived = false;

    private boolean tabPressedWhenPopupOpen = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean initDone = false;

    /** For internal use only. May be removed or replaced in the future. */
    public String lastFilter = "";

    /** For internal use only. May be removed or replaced in the future. */
    public enum Select {
        NONE, FIRST, LAST
    }

    /** For internal use only. May be removed or replaced in the future. */
    public Select selectPopupItemWhenResponseIsReceived = Select.NONE;

    /**
     * The current suggestion selected from the dropdown. This is one of the
     * values in currentSuggestions except when filtering, in this case
     * currentSuggestion might not be in currentSuggestions.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public FilterSelectSuggestion currentSuggestion;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean allowNewItem;

    /** For internal use only. May be removed or replaced in the future. */
    public int totalMatches;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectionAllowed;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectItem;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean enabled;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean readonly;

    /** For internal use only. May be removed or replaced in the future. */
    public FilteringMode filteringmode = FilteringMode.OFF;

    // shown in unfocused empty field, disappears on focus (e.g "Search here")
    private static final String CLASSNAME_PROMPT = "prompt";

    /** For internal use only. May be removed or replaced in the future. */
    public String inputPrompt = "";

    /** For internal use only. May be removed or replaced in the future. */
    public boolean prompting = false;

    /**
     * Set true when popupopened has been clicked. Cleared on each UIDL-update.
     * This handles the special case where are not filtering yet and the
     * selected value has changed on the server-side. See #2119
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean popupOpenerClicked;

    /** For internal use only. May be removed or replaced in the future. */
    public int suggestionPopupMinWidth = 0;

    private int popupWidth = -1;
    /**
     * Stores the last new item string to avoid double submissions. Cleared on
     * uidl updates.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String lastNewItemString;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean focused = false;

    /**
     * If set to false, the component should not allow entering text to the
     * field even for filtering.
     */
    private boolean textInputEnabled = true;

    /**
     * Default constructor.
     */
    public VFilterSelect() {
        tb = createTextBox();
        suggestionPopup = createSuggestionPopup();

        popupOpener.sinkEvents(Event.ONMOUSEDOWN);
        Roles.getButtonRole()
                .setAriaHiddenState(popupOpener.getElement(), true);
        Roles.getButtonRole().set(popupOpener.getElement());

        panel.add(tb);
        panel.add(popupOpener);
        initWidget(panel);
        Roles.getComboboxRole().set(panel.getElement());

        tb.addKeyDownHandler(this);
        tb.addKeyUpHandler(this);

        tb.addFocusHandler(this);
        tb.addBlurHandler(this);
        tb.addClickHandler(this);

        popupOpener.addClickHandler(this);

        setStyleName(CLASSNAME);

        sinkEvents(Event.ONPASTE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt
     * .user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        if (event.getTypeInt() == Event.ONPASTE) {
            if (textInputEnabled) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        filterOptions(currentPage);
                    }
                });
            }
        }
    }

    /**
     * This method will create the TextBox used by the VFilterSelect instance.
     * It is invoked during the Constructor and should only be overridden if a
     * custom TextBox shall be used. The overriding method cannot use any
     * instance variables.
     * 
     * @since 7.1.5
     * @return TextBox instance used by this VFilterSelect
     */
    protected TextBox createTextBox() {
        return new FilterSelectTextBox();
    }

    /**
     * This method will create the SuggestionPopup used by the VFilterSelect
     * instance. It is invoked during the Constructor and should only be
     * overridden if a custom SuggestionPopup shall be used. The overriding
     * method cannot use any instance variables.
     * 
     * @since 7.1.5
     * @return SuggestionPopup instance used by this VFilterSelect
     */
    protected SuggestionPopup createSuggestionPopup() {
        return new SuggestionPopup();
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames();
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        updateStyleNames();
    }

    protected void updateStyleNames() {
        tb.setStyleName(getStylePrimaryName() + "-input");
        popupOpener.setStyleName(getStylePrimaryName() + "-button");
        suggestionPopup.setStyleName(getStylePrimaryName() + "-suggestpopup");
    }

    /**
     * Does the Select have more pages?
     * 
     * @return true if a next page exists, else false if the current page is the
     *         last page
     */
    public boolean hasNextPage() {
        if (pageLength > 0 && totalMatches > (currentPage + 1) * pageLength) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Filters the options at a certain page. Uses the text box input as a
     * filter
     * 
     * @param page
     *            The page which items are to be filtered
     */
    public void filterOptions(int page) {
        filterOptions(page, tb.getText());
    }

    /**
     * Filters the options at certain page using the given filter
     * 
     * @param page
     *            The page to filter
     * @param filter
     *            The filter to apply to the components
     */
    public void filterOptions(int page, String filter) {
        filterOptions(page, filter, true);
    }

    /**
     * Filters the options at certain page using the given filter
     * 
     * @param page
     *            The page to filter
     * @param filter
     *            The filter to apply to the options
     * @param immediate
     *            Whether to send the options request immediately
     */
    private void filterOptions(int page, String filter, boolean immediate) {
        debug("VFS: filterOptions(" + page + ", " + filter + ", " + immediate
                + ")");

        if (filter.equals(lastFilter) && currentPage == page) {
            if (!suggestionPopup.isAttached()) {
                suggestionPopup.showSuggestions(currentSuggestions,
                        currentPage, totalMatches);
            }
            return;
        }
        if (!filter.equals(lastFilter)) {
            // we are on subsequent page and text has changed -> reset page
            if ("".equals(filter)) {
                // let server decide
                page = -1;
            } else {
                page = 0;
            }
        }

        waitingForFilteringResponse = true;
        client.updateVariable(paintableId, "filter", filter, false);
        client.updateVariable(paintableId, "page", page, immediate);
        afterUpdateClientVariables();

        lastFilter = filter;
        currentPage = page;

    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateReadOnly() {
        debug("VFS: updateReadOnly()");
        tb.setReadOnly(readonly || !textInputEnabled);
    }

    public void setTextInputEnabled(boolean textInputEnabled) {
        debug("VFS: setTextInputEnabled()");
        // Always update styles as they might have been overwritten
        if (textInputEnabled) {
            removeStyleDependentName(STYLE_NO_INPUT);
            Roles.getTextboxRole().removeAriaReadonlyProperty(tb.getElement());
        } else {
            addStyleDependentName(STYLE_NO_INPUT);
            Roles.getTextboxRole().setAriaReadonlyProperty(tb.getElement(),
                    true);
        }

        if (this.textInputEnabled == textInputEnabled) {
            return;
        }

        this.textInputEnabled = textInputEnabled;
        updateReadOnly();
    }

    /**
     * Sets the text in the text box.
     * 
     * @param text
     *            the text to set in the text box
     */
    public void setTextboxText(final String text) {
        if (enableDebug) {
            debug("VFS: setTextboxText(" + text + ")");
        }
        setText(text);
    }

    private void setText(final String text) {
        /**
         * To leave caret in the beginning of the line. SetSelectionRange
         * wouldn't work on IE (see #13477)
         */
        Direction previousDirection = tb.getDirection();
        tb.setDirection(Direction.RTL);
        tb.setText(text);
        tb.setDirection(previousDirection);
    }

    /**
     * Turns prompting on. When prompting is turned on a command prompt is shown
     * in the text box if nothing has been entered.
     */
    public void setPromptingOn() {
        debug("VFS: setPromptingOn()");
        if (!prompting) {
            prompting = true;
            addStyleDependentName(CLASSNAME_PROMPT);
        }
        setTextboxText(inputPrompt);
    }

    /**
     * Turns prompting off. When prompting is turned on a command prompt is
     * shown in the text box if nothing has been entered.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param text
     *            The text the text box should contain.
     */
    public void setPromptingOff(String text) {
        debug("VFS: setPromptingOff()");
        setTextboxText(text);
        if (prompting) {
            prompting = false;
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
    }

    /**
     * Triggered when a suggestion is selected
     * 
     * @param suggestion
     *            The suggestion that just got selected.
     */
    public void onSuggestionSelected(FilterSelectSuggestion suggestion) {
        if (enableDebug) {
            debug("VFS: onSuggestionSelected(" + suggestion.caption + ": "
                    + suggestion.key + ")");
        }
        updateSelectionWhenReponseIsReceived = false;

        currentSuggestion = suggestion;
        String newKey;
        if (suggestion.key.equals("")) {
            // "nullselection"
            newKey = "";
        } else {
            // normal selection
            newKey = suggestion.getOptionKey();
        }

        String text = suggestion.getReplacementString();
        if ("".equals(newKey) && !focused) {
            setPromptingOn();
        } else {
            setPromptingOff(text);
        }
        setSelectedItemIcon(suggestion.getIconUri());

        if (!(newKey.equals(selectedOptionKey) || ("".equals(newKey) && selectedOptionKey == null))) {
            selectedOptionKey = newKey;
            client.updateVariable(paintableId, "selected",
                    new String[] { selectedOptionKey }, immediate);
            afterUpdateClientVariables();

            // currentPage = -1; // forget the page
        }
        suggestionPopup.hide();
    }

    /**
     * Sets the icon URI of the selected item. The icon is shown on the left
     * side of the item caption text. Set the URI to null to remove the icon.
     * 
     * @param iconUri
     *            The URI of the icon
     */
    public void setSelectedItemIcon(String iconUri) {

        if (iconUri == null || iconUri.length() == 0) {
            if (selectedItemIcon != null) {
                panel.remove(selectedItemIcon);
                selectedItemIcon = null;
                afterSelectedItemIconChange();
            }
        } else {
            if (selectedItemIcon != null) {
                panel.remove(selectedItemIcon);
            }
            selectedItemIcon = new IconWidget(client.getIcon(iconUri));
            // Older IE versions don't scale icon correctly if DOM
            // contains height and width attributes.
            selectedItemIcon.getElement().removeAttribute("height");
            selectedItemIcon.getElement().removeAttribute("width");
            selectedItemIcon.addDomHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    afterSelectedItemIconChange();
                }
            }, LoadEvent.getType());
            panel.insert(selectedItemIcon, 0);
            afterSelectedItemIconChange();
        }
    }

    private void afterSelectedItemIconChange() {
        if (BrowserInfo.get().isWebkit() || BrowserInfo.get().isIE8()) {
            // Some browsers need a nudge to reposition the text field
            forceReflow();
        }
        updateRootWidth();
        if (selectedItemIcon != null) {
            updateSelectedIconPosition();
        }
    }

    private void forceReflow() {
        Util.setStyleTemporarily(tb.getElement(), "zoom", "1");
    }

    /**
     * Positions the icon vertically in the middle. Should be called after the
     * icon has loaded
     */
    private void updateSelectedIconPosition() {
        // Position icon vertically to middle
        int availableHeight = 0;
        availableHeight = getOffsetHeight();

        int iconHeight = Util.getRequiredHeight(selectedItemIcon);
        int marginTop = (availableHeight - iconHeight) / 2;
        selectedItemIcon.getElement().getStyle()
                .setMarginTop(marginTop, Unit.PX);
    }

    private static Set<Integer> navigationKeyCodes = new HashSet<Integer>();
    static {
        navigationKeyCodes.add(KeyCodes.KEY_DOWN);
        navigationKeyCodes.add(KeyCodes.KEY_UP);
        navigationKeyCodes.add(KeyCodes.KEY_PAGEDOWN);
        navigationKeyCodes.add(KeyCodes.KEY_PAGEUP);
        navigationKeyCodes.add(KeyCodes.KEY_ENTER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (enabled && !readonly) {
            int keyCode = event.getNativeKeyCode();

            if (enableDebug) {
                debug("VFS: key down: " + keyCode);
            }
            if (waitingForFilteringResponse
                    && navigationKeyCodes.contains(keyCode)) {
                /*
                 * Keyboard navigation events should not be handled while we are
                 * waiting for a response. This avoids flickering, disappearing
                 * items, wrongly interpreted responses and more.
                 */
                if (enableDebug) {
                    debug("Ignoring "
                            + keyCode
                            + " because we are waiting for a filtering response");
                }
                DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
                event.stopPropagation();
                return;
            }

            if (suggestionPopup.isAttached()) {
                if (enableDebug) {
                    debug("Keycode " + keyCode + " target is popup");
                }
                popupKeyDown(event);
            } else {
                if (enableDebug) {
                    debug("Keycode " + keyCode + " target is text field");
                }
                inputFieldKeyDown(event);
            }
        }
    }

    private void debug(String string) {
        if (enableDebug) {
            VConsole.error(string);
        }
    }

    /**
     * Triggered when a key is pressed in the text box
     * 
     * @param event
     *            The KeyDownEvent
     */
    private void inputFieldKeyDown(KeyDownEvent event) {
        if (enableDebug) {
            debug("VFS: inputFieldKeyDown(" + event.getNativeKeyCode() + ")");
        }
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_DOWN:
        case KeyCodes.KEY_UP:
        case KeyCodes.KEY_PAGEDOWN:
        case KeyCodes.KEY_PAGEUP:
            // open popup as from gadget
            filterOptions(-1, "");
            lastFilter = "";
            tb.selectAll();
            break;
        case KeyCodes.KEY_ENTER:
            /*
             * This only handles the case when new items is allowed, a text is
             * entered, the popup opener button is clicked to close the popup
             * and enter is then pressed (see #7560).
             */
            if (!allowNewItem) {
                return;
            }

            if (currentSuggestion != null
                    && tb.getText().equals(
                            currentSuggestion.getReplacementString())) {
                // Retain behavior from #6686 by returning without stopping
                // propagation if there's nothing to do
                return;
            }
            suggestionPopup.menu.doSelectedItemAction();

            event.stopPropagation();
            break;
        }

    }

    /**
     * Triggered when a key was pressed in the suggestion popup.
     * 
     * @param event
     *            The KeyDownEvent of the key
     */
    private void popupKeyDown(KeyDownEvent event) {
        if (enableDebug) {
            debug("VFS: popupKeyDown(" + event.getNativeKeyCode() + ")");
        }
        // Propagation of handled events is stopped so other handlers such as
        // shortcut key handlers do not also handle the same events.
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_DOWN:
            suggestionPopup.selectNextItem();

            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            event.stopPropagation();
            break;
        case KeyCodes.KEY_UP:
            suggestionPopup.selectPrevItem();

            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            event.stopPropagation();
            break;
        case KeyCodes.KEY_PAGEDOWN:
            selectNextPage();
            event.stopPropagation();
            break;
        case KeyCodes.KEY_PAGEUP:
            selectPrevPage();
            event.stopPropagation();
            break;
        case KeyCodes.KEY_TAB:
            tabPressedWhenPopupOpen = true;
            filterOptions(currentPage);
            // onBlur() takes care of the rest
            break;
        case KeyCodes.KEY_ESCAPE:
            reset();
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            event.stopPropagation();
            break;
        case KeyCodes.KEY_ENTER:
            if (suggestionPopup.menu.getKeyboardSelectedItem() == null) {
                /*
                 * Nothing selected using up/down. Happens e.g. when entering a
                 * text (causes popup to open) and then pressing enter.
                 */
                if (!allowNewItem) {
                    /*
                     * New items are not allowed: If there is only one
                     * suggestion, select that. If there is more than one
                     * suggestion Enter key should work as Escape key. Otherwise
                     * do nothing.
                     */
                    if (currentSuggestions.size() == 1) {
                        onSuggestionSelected(currentSuggestions.get(0));
                    } else if (currentSuggestions.size() > 1) {
                        reset();
                    }
                } else {
                    // Handle addition of new items.
                    suggestionPopup.menu.doSelectedItemAction();
                }
            } else {
                /*
                 * Get the suggestion that was navigated to using up/down.
                 */
                currentSuggestion = ((FilterSelectSuggestion) suggestionPopup.menu
                        .getKeyboardSelectedItem().getCommand());
                onSuggestionSelected(currentSuggestion);
            }

            event.stopPropagation();
            break;
        }

    }

    /*
     * Show the prev page.
     */
    private void selectPrevPage() {
        if (currentPage > 0) {
            filterOptions(currentPage - 1, lastFilter);
            selectPopupItemWhenResponseIsReceived = Select.LAST;
        }
    }

    /*
     * Show the next page.
     */
    private void selectNextPage() {
        if (hasNextPage()) {
            filterOptions(currentPage + 1, lastFilter);
            selectPopupItemWhenResponseIsReceived = Select.FIRST;
        }
    }

    /**
     * Triggered when a key was depressed
     * 
     * @param event
     *            The KeyUpEvent of the key depressed
     */

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (enableDebug) {
            debug("VFS: onKeyUp(" + event.getNativeKeyCode() + ")");
        }
        if (enabled && !readonly) {
            switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_ENTER:
            case KeyCodes.KEY_TAB:
            case KeyCodes.KEY_SHIFT:
            case KeyCodes.KEY_CTRL:
            case KeyCodes.KEY_ALT:
            case KeyCodes.KEY_DOWN:
            case KeyCodes.KEY_UP:
            case KeyCodes.KEY_PAGEDOWN:
            case KeyCodes.KEY_PAGEUP:
            case KeyCodes.KEY_ESCAPE:
                // NOP
                break;
            default:
                if (textInputEnabled) {
                    filterOptions(currentPage);
                }
                break;
            }
        }
    }

    /**
     * Resets the Select to its initial state
     */
    private void reset() {
        debug("VFS: reset()");
        if (currentSuggestion != null) {
            String text = currentSuggestion.getReplacementString();
            setPromptingOff(text);
            setSelectedItemIcon(currentSuggestion.getIconUri());

            selectedOptionKey = currentSuggestion.key;

        } else {
            if (focused || readonly || !enabled) {
                setPromptingOff("");
            } else {
                setPromptingOn();
            }
            setSelectedItemIcon(null);

            selectedOptionKey = null;
        }

        lastFilter = "";
        suggestionPopup.hide();
    }

    /**
     * Listener for popupopener
     */

    @Override
    public void onClick(ClickEvent event) {
        debug("VFS: onClick()");
        if (textInputEnabled
                && event.getNativeEvent().getEventTarget().cast() == tb
                        .getElement()) {
            // Don't process clicks on the text field if text input is enabled
            return;
        }
        if (enabled && !readonly) {
            // ask suggestionPopup if it was just closed, we are using GWT
            // Popup's auto close feature
            if (!suggestionPopup.isJustClosed()) {
                // If a focus event is not going to be sent, send the options
                // request immediately; otherwise queue in the same burst as the
                // focus event. Fixes #8321.
                boolean immediate = focused
                        || !client.hasEventListeners(this, EventId.FOCUS);
                filterOptions(-1, "", immediate);
                popupOpenerClicked = true;
                lastFilter = "";
            }
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            focus();
            tb.selectAll();
        }
    }

    /**
     * Update minimum width for FilterSelect textarea based on input prompt and
     * suggestions.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateSuggestionPopupMinWidth() {
        // used only to calculate minimum width
        String captions = Util.escapeHTML(inputPrompt);

        for (FilterSelectSuggestion suggestion : currentSuggestions) {
            // Collect captions so we can calculate minimum width for
            // textarea
            if (captions.length() > 0) {
                captions += "|";
            }
            captions += Util.escapeHTML(suggestion.getReplacementString());
        }

        // Calculate minimum textarea width
        suggestionPopupMinWidth = minWidth(captions);
    }

    /**
     * Calculate minimum width for FilterSelect textarea.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public native int minWidth(String captions)
    /*-{
        if(!captions || captions.length <= 0)
                return 0;
        captions = captions.split("|");
        var d = $wnd.document.createElement("div");
        var html = "";
        for(var i=0; i < captions.length; i++) {
                html += "<div>" + captions[i] + "</div>";
                // TODO apply same CSS classname as in suggestionmenu
        }
        d.style.position = "absolute";
        d.style.top = "0";
        d.style.left = "0";
        d.style.visibility = "hidden";
        d.innerHTML = html;
        $wnd.document.body.appendChild(d);
        var w = d.offsetWidth;
        $wnd.document.body.removeChild(d);
        return w;
    }-*/;

    /**
     * A flag which prevents a focus event from taking place
     */
    boolean iePreventNextFocus = false;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */

    @Override
    public void onFocus(FocusEvent event) {
        debug("VFS: onFocus()");

        /*
         * When we disable a blur event in ie we need to refocus the textfield.
         * This will cause a focus event we do not want to process, so in that
         * case we just ignore it.
         */
        if (BrowserInfo.get().isIE() && iePreventNextFocus) {
            iePreventNextFocus = false;
            return;
        }

        focused = true;
        if (prompting && !readonly) {
            setPromptingOff("");
        }
        addStyleDependentName("focus");

        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(paintableId, EventId.FOCUS, "", true);
            afterUpdateClientVariables();
        }

        ComponentConnector connector = ConnectorMap.get(client).getConnector(
                this);
        client.getVTooltip().showAssistive(
                connector.getTooltipInfo(getElement()));
    }

    /**
     * A flag which cancels the blur event and sets the focus back to the
     * textfield if the Browser is IE
     */
    boolean preventNextBlurEventInIE = false;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */

    @Override
    public void onBlur(BlurEvent event) {
        debug("VFS: onBlur()");

        if (BrowserInfo.get().isIE() && preventNextBlurEventInIE) {
            /*
             * Clicking in the suggestion popup or on the popup button in IE
             * causes a blur event to be sent for the field. In other browsers
             * this is prevented by canceling/preventing default behavior for
             * the focus event, in IE we handle it here by refocusing the text
             * field and ignoring the resulting focus event for the textfield
             * (in onFocus).
             */
            preventNextBlurEventInIE = false;

            Element focusedElement = Util.getIEFocusedElement();
            if (getElement().isOrHasChild(focusedElement)
                    || suggestionPopup.getElement()
                            .isOrHasChild(focusedElement)) {

                // IF the suggestion popup or another part of the VFilterSelect
                // was focused, move the focus back to the textfield and prevent
                // the triggered focus event (in onFocus).
                iePreventNextFocus = true;
                tb.setFocus(true);
                return;
            }
        }

        focused = false;
        if (!readonly) {
            // much of the TAB handling takes place here
            if (tabPressedWhenPopupOpen) {
                tabPressedWhenPopupOpen = false;
                waitingForFilteringResponse = false;
                suggestionPopup.menu.doSelectedItemAction();
                suggestionPopup.hide();
            } else if ((!suggestionPopup.isAttached() && waitingForFilteringResponse)
                    || suggestionPopup.isJustClosed()) {
                // typing so fast the popup was never opened, or it's just
                // closed
                waitingForFilteringResponse = false;
                suggestionPopup.menu.doSelectedItemAction();
            }
            if (selectedOptionKey == null) {
                setPromptingOn();
            } else if (currentSuggestion != null) {
                setPromptingOff(currentSuggestion.caption);
            }
        }
        removeStyleDependentName("focus");

        if (client.hasEventListeners(this, EventId.BLUR)) {
            client.updateVariable(paintableId, EventId.BLUR, "", true);
            afterUpdateClientVariables();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Focusable#focus()
     */

    @Override
    public void focus() {
        debug("VFS: focus()");
        focused = true;
        if (prompting && !readonly) {
            setPromptingOff("");
        }
        tb.setFocus(true);
    }

    /**
     * Calculates the width of the select if the select has undefined width.
     * Should be called when the width changes or when the icon changes.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateRootWidth() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);

        if (paintable.isUndefinedWidth()) {

            /*
             * When the select has a undefined with we need to check that we are
             * only setting the text box width relative to the first page width
             * of the items. If this is not done the text box width will change
             * when the popup is used to view longer items than the text box is
             * wide.
             */
            int w = Util.getRequiredWidth(this);

            if ((!initDone || currentPage + 1 < 0)
                    && suggestionPopupMinWidth > w) {
                /*
                 * We want to compensate for the paddings just to preserve the
                 * exact size as in Vaadin 6.x, but we get here before
                 * MeasuredSize has been initialized.
                 * Util.measureHorizontalPaddingAndBorder does not work with
                 * border-box, so we must do this the hard way.
                 */
                Style style = getElement().getStyle();
                String originalPadding = style.getPadding();
                String originalBorder = style.getBorderWidth();
                style.setPaddingLeft(0, Unit.PX);
                style.setBorderWidth(0, Unit.PX);
                style.setProperty("padding", originalPadding);
                style.setProperty("borderWidth", originalBorder);

                // Use util.getRequiredWidth instead of getOffsetWidth here

                int iconWidth = selectedItemIcon == null ? 0 : Util
                        .getRequiredWidth(selectedItemIcon);
                int buttonWidth = popupOpener == null ? 0 : Util
                        .getRequiredWidth(popupOpener);

                /*
                 * Instead of setting the width of the wrapper, set the width of
                 * the combobox. Subtract the width of the icon and the
                 * popupopener
                 */

                tb.setWidth((suggestionPopupMinWidth - iconWidth - buttonWidth)
                        + "px");

            }

            /*
             * Lock the textbox width to its current value if it's not already
             * locked
             */
            if (!tb.getElement().getStyle().getWidth().endsWith("px")) {
                int iconWidth = selectedItemIcon == null ? 0 : selectedItemIcon
                        .getOffsetWidth();
                tb.setWidth((tb.getOffsetWidth() - iconWidth) + "px");
            }
        }
    }

    /**
     * Get the width of the select in pixels where the text area and icon has
     * been included.
     * 
     * @return The width in pixels
     */
    private int getMainWidth() {
        return getOffsetWidth();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (width.length() != 0) {
            tb.setWidth("100%");
        }
    }

    /**
     * Handles special behavior of the mouse down event
     * 
     * @param event
     */
    private void handleMouseDownEvent(Event event) {
        /*
         * Prevent the keyboard focus from leaving the textfield by preventing
         * the default behaviour of the browser. Fixes #4285.
         */
        if (event.getTypeInt() == Event.ONMOUSEDOWN) {
            event.preventDefault();
            event.stopPropagation();

            /*
             * In IE the above wont work, the blur event will still trigger. So,
             * we set a flag here to prevent the next blur event from happening.
             * This is not needed if do not already have focus, in that case
             * there will not be any blur event and we should not cancel the
             * next blur.
             */
            if (BrowserInfo.get().isIE() && focused) {
                preventNextBlurEventInIE = true;
                debug("VFS: Going to prevent next blur event on IE");
            }
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        suggestionPopup.hide();
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if ("textbox".equals(subPart)) {
            return tb.getElement();
        } else if ("button".equals(subPart)) {
            return popupOpener.getElement();
        } else if ("popup".equals(subPart) && suggestionPopup.isAttached()) {
            return suggestionPopup.getElement();
        }
        return null;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (tb.getElement().isOrHasChild(subElement)) {
            return "textbox";
        } else if (popupOpener.getElement().isOrHasChild(subElement)) {
            return "button";
        } else if (suggestionPopup.getElement().isOrHasChild(subElement)) {
            return "popup";
        }
        return null;
    }

    @Override
    public void setAriaRequired(boolean required) {
        AriaHelper.handleInputRequired(tb, required);
    }

    @Override
    public void setAriaInvalid(boolean invalid) {
        AriaHelper.handleInputInvalid(tb, invalid);
    }

    @Override
    public void bindAriaCaption(
            com.google.gwt.user.client.Element captionElement) {
        AriaHelper.bindCaption(tb, captionElement);
    }

    /*
     * Anything that should be set after the client updates the server.
     */
    private void afterUpdateClientVariables() {
        // We need this here to be consistent with the all the calls.
        // Then set your specific selection type only after
        // client.updateVariable() method call.
        selectPopupItemWhenResponseIsReceived = Select.NONE;
    }

}
