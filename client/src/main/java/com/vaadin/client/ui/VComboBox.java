/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
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
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.Focusable;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.aria.HandlesAriaCaption;
import com.vaadin.client.ui.aria.HandlesAriaInvalid;
import com.vaadin.client.ui.aria.HandlesAriaRequired;
import com.vaadin.client.ui.combobox.ComboBoxConnector;
import com.vaadin.client.ui.menubar.MenuBar;
import com.vaadin.client.ui.menubar.MenuItem;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.util.SharedUtil;

/**
 * Client side implementation of the ComboBox component.
 *
 * TODO needs major refactoring (to be extensible etc)
 *
 * @since 8.0
 */
@SuppressWarnings("deprecation")
public class VComboBox extends Composite implements Field, KeyDownHandler,
        KeyUpHandler, ClickHandler, FocusHandler, BlurHandler, Focusable,
        SubPartAware, HandlesAriaCaption, HandlesAriaInvalid,
        HandlesAriaRequired, DeferredWorker, MouseDownHandler {

    /**
     * Represents a suggestion in the suggestion popup box.
     */
    public class ComboBoxSuggestion implements Suggestion, Command {

        private final String key;
        private final String caption;
        private String untranslatedIconUri;
        private String style;

        /**
         * Constructor for a single suggestion.
         *
         * @param key
         *            item key, empty string for a special null item not in
         *            container
         * @param caption
         *            item caption
         * @param style
         *            item style name, can be empty string
         * @param untranslatedIconUri
         *            icon URI or null
         */
        public ComboBoxSuggestion(String key, String caption, String style,
                String untranslatedIconUri) {
            this.key = key;
            this.caption = caption;
            this.style = style;
            this.untranslatedIconUri = untranslatedIconUri;
        }

        /**
         * Gets the visible row in the popup as a HTML string. The string
         * contains an image tag with the rows icon (if an icon has been
         * specified) and the caption of the item
         */

        @Override
        public String getDisplayString() {
            final StringBuilder sb = new StringBuilder();
            ApplicationConnection client = connector.getConnection();
            final Icon icon = client
                    .getIcon(client.translateVaadinUri(untranslatedIconUri));
            if (icon != null) {
                sb.append(icon.getElement().getString());
            }
            String content;
            if ("".equals(caption)) {
                // Ensure that empty options use the same height as other
                // options and are not collapsed (#7506)
                content = "&nbsp;";
            } else {
                content = WidgetUtil.escapeHTML(caption);
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
         * @return real (translated) icon URI or null if none
         */
        public String getIconUri() {
            ApplicationConnection client = connector.getConnection();
            return client.translateVaadinUri(untranslatedIconUri);
        }

        /**
         * Gets the style set for this suggestion item. Styles are typically set
         * by a server-side {@link com.vaadin.ui.ComboBox.ItemStyleProvider}.
         * The returned style is prefixed by <code>v-filterselect-item-</code>.
         *
         * @since 7.5.6
         * @return the style name to use, or <code>null</code> to not apply any
         *         custom style.
         */
        public String getStyle() {
            return style;
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
            if (!(obj instanceof ComboBoxSuggestion)) {
                return false;
            }
            ComboBoxSuggestion other = (ComboBoxSuggestion) obj;
            if (key == null && other.key != null
                    || key != null && !key.equals(other.key)) {
                return false;
            }
            if (caption == null && other.caption != null
                    || caption != null && !caption.equals(other.caption)) {
                return false;
            }
            if (!SharedUtil.equals(untranslatedIconUri,
                    other.untranslatedIconUri)) {
                return false;
            }
            if (!SharedUtil.equals(style, other.style)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + VComboBox.this.hashCode();
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result
                    + ((caption == null) ? 0 : caption.hashCode());
            result = prime * result + ((untranslatedIconUri == null) ? 0
                    : untranslatedIconUri.hashCode());
            result = prime * result + ((style == null) ? 0 : style.hashCode());
            return result;
        }
    }

    /** An inner class that handles all logic related to mouse wheel. */
    private class MouseWheeler extends JsniMousewheelHandler {

        public MouseWheeler() {
            super(VComboBox.this);
        }

        @Override
        protected native JavaScriptObject createMousewheelListenerFunction(
                Widget widget)
        /*-{
            return $entry(function(e) {
                var deltaX = e.deltaX ? e.deltaX : -0.5*e.wheelDeltaX;
                var deltaY = e.deltaY ? e.deltaY : -0.5*e.wheelDeltaY;

                // IE8 has only delta y
                if (isNaN(deltaY)) {
                    deltaY = -0.5*e.wheelDelta;
                }

                @com.vaadin.client.ui.VComboBox.JsniUtil::moveScrollFromEvent(*)(widget, deltaX, deltaY, e, e.deltaMode);
            });
        }-*/;

    }

    /**
     * A utility class that contains utility methods that are usually called
     * from JSNI.
     * <p>
     * The methods are moved in this class to minimize the amount of JSNI code
     * as much as feasible.
     */
    static class JsniUtil {
        private static final int DOM_DELTA_PIXEL = 0;
        private static final int DOM_DELTA_LINE = 1;
        private static final int DOM_DELTA_PAGE = 2;

        // Rough estimation of item height
        private static final int SCROLL_UNIT_PX = 25;

        private static double deltaSum = 0;

        public static void moveScrollFromEvent(final Widget widget,
                final double deltaX, final double deltaY,
                final NativeEvent event, final int deltaMode) {

            if (!Double.isNaN(deltaY)) {
                VComboBox filterSelect = (VComboBox) widget;

                switch (deltaMode) {
                case DOM_DELTA_LINE:
                    if (deltaY >= 0) {
                        filterSelect.suggestionPopup.selectNextItem();
                    } else {
                        filterSelect.suggestionPopup.selectPrevItem();
                    }
                    break;
                case DOM_DELTA_PAGE:
                    if (deltaY >= 0) {
                        filterSelect.selectNextPage();
                    } else {
                        filterSelect.selectPrevPage();
                    }
                    break;
                case DOM_DELTA_PIXEL:
                default:
                    // Accumulate dampened deltas
                    deltaSum += Math.pow(Math.abs(deltaY), 0.7)
                            * Math.signum(deltaY);

                    // "Scroll" if change exceeds item height
                    while (Math.abs(deltaSum) >= SCROLL_UNIT_PX) {
                        if (!filterSelect.dataReceivedHandler
                                .isWaitingForFilteringResponse()) {
                            // Move selection if page flip is not in progress
                            if (deltaSum < 0) {
                                filterSelect.suggestionPopup.selectPrevItem();
                            } else {
                                filterSelect.suggestionPopup.selectNextItem();
                            }
                        }
                        deltaSum -= SCROLL_UNIT_PX * Math.signum(deltaSum);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Represents the popup box with the selection options. Wraps a suggestion
     * menu.
     */
    public class SuggestionPopup extends VOverlay
            implements PositionCallback, CloseHandler<PopupPanel> {

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
        private int leftPosition;

        private final MouseWheeler mouseWheeler = new MouseWheeler();

        private boolean scrollPending = false;

        /**
         * Default constructor
         */
        SuggestionPopup() {
            super(true, false);
            debug("VComboBox.SP: constructor()");
            setOwner(VComboBox.this);
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

            setPreviewingAllNativeEvents(true);
        }

        @Override
        protected void onLoad() {
            super.onLoad();

            // Register mousewheel listener on paged select
            if (pageLength > 0) {
                mouseWheeler.attachMousewheelListener(getElement());
            }
        }

        @Override
        protected void onUnload() {
            mouseWheeler.detachMousewheelListener(getElement());
            super.onUnload();
        }

        /**
         * Shows the popup where the user can see the filtered options that have
         * been set with a call to
         * {@link SuggestionMenu#setSuggestions(Collection)}.
         *
         * @param currentPage
         *            The current page number
         */
        public void showSuggestions(final int currentPage) {

            debug("VComboBox.SP: showSuggestions(" + currentPage + ", "
                    + getTotalSuggestions() + ")");

            final SuggestionPopup popup = this;
            // Add TT anchor point
            getElement().setId("VAADIN_COMBOBOX_OPTIONLIST");

            leftPosition = getDesiredLeftPosition();
            topPosition = getDesiredTopPosition();

            setPopupPosition(leftPosition, topPosition);

            int nullOffset = getNullSelectionItemShouldBeVisible() ? 1 : 0;
            boolean firstPage = currentPage == 0;
            final int first = currentPage * pageLength + 1
                    - (firstPage ? 0 : nullOffset);
            final int last = first + currentSuggestions.size() - 1
                    - (firstPage && "".equals(lastFilter) ? nullOffset : 0);
            final int matches = getTotalSuggestions();
            if (last > 0) {
                // nullsel not counted, as requested by user
                status.setInnerText((matches == 0 ? 0 : first) + "-" + last
                        + "/" + matches);
            } else {
                status.setInnerText("");
            }
            // We don't need to show arrows or statusbar if there is
            // only one page
            setPagingEnabled(
                    getTotalSuggestionsIncludingNullSelectionItem() > pageLength
                            && pageLength > 0);
            setPrevButtonActive(first > 1);
            setNextButtonActive(last < matches);

            // clear previously fixed width
            menu.setWidth("");
            menu.getElement().getFirstChildElement().getStyle().clearWidth();

            setPopupPositionAndShow(popup);
        }

        private int getDesiredTopPosition() {
            return toInt32(WidgetUtil.getBoundingClientRect(tb.getElement())
                    .getBottom()) + Window.getScrollTop();
        }

        private int getDesiredLeftPosition() {
            return toInt32(WidgetUtil
                    .getBoundingClientRect(VComboBox.this.getElement())
                    .getLeft());
        }

        private native int toInt32(double val)
        /*-{
            return val | 0;
        }-*/;

        /**
         * Should the next page button be visible to the user?
         *
         * @param active
         */
        private void setNextButtonActive(boolean active) {
            if (enableDebug) {
                debug("VComboBox.SP: setNextButtonActive(" + active + ")");
            }
            if (active) {
                DOM.sinkEvents(down, Event.ONCLICK);
                down.setClassName(
                        VComboBox.this.getStylePrimaryName() + "-nextpage");
            } else {
                DOM.sinkEvents(down, 0);
                down.setClassName(
                        VComboBox.this.getStylePrimaryName() + "-nextpage-off");
            }
        }

        /**
         * Should the previous page button be visible to the user
         *
         * @param active
         */
        private void setPrevButtonActive(boolean active) {
            if (enableDebug) {
                debug("VComboBox.SP: setPrevButtonActive(" + active + ")");
            }

            if (active) {
                DOM.sinkEvents(up, Event.ONCLICK);
                up.setClassName(
                        VComboBox.this.getStylePrimaryName() + "-prevpage");
            } else {
                DOM.sinkEvents(up, 0);
                up.setClassName(
                        VComboBox.this.getStylePrimaryName() + "-prevpage-off");
            }
        }

        /**
         * Selects the next item in the filtered selections.
         */
        public void selectNextItem() {
            debug("VComboBox.SP: selectNextItem()");

            final int index = menu.getSelectedIndex() + 1;
            if (menu.getItems().size() > index) {
                selectItem(menu.getItems().get(index));

            } else {
                selectNextPage();
            }
        }

        /**
         * Selects the previous item in the filtered selections.
         */
        public void selectPrevItem() {
            debug("VComboBox.SP: selectPrevItem()");

            final int index = menu.getSelectedIndex() - 1;
            if (index > -1) {
                selectItem(menu.getItems().get(index));

            } else if (index == -1) {
                selectPrevPage();

            } else {
                if (!menu.getItems().isEmpty()) {
                    selectLastItem();
                }
            }
        }

        /**
         * Select the first item of the suggestions list popup.
         *
         * @since 7.2.6
         */
        public void selectFirstItem() {
            debug("VComboBox.SP: selectFirstItem()");
            selectItem(menu.getFirstItem());
        }

        /**
         * Select the last item of the suggestions list popup.
         *
         * @since 7.2.6
         */
        public void selectLastItem() {
            debug("VComboBox.SP: selectLastItem()");
            selectItem(menu.getLastItem());
        }

        /*
         * Sets the selected item in the popup menu.
         */
        private void selectItem(final MenuItem newSelectedItem) {
            menu.selectItem(newSelectedItem);

            // Set the icon.
            ComboBoxSuggestion suggestion = (ComboBoxSuggestion) newSelectedItem
                    .getCommand();
            setSelectedItemIcon(suggestion.getIconUri());

            // Set the text.
            setText(suggestion.getReplacementString());
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
                debug("VComboBox.SP.LPS: run()");
                if (pagesToScroll != 0) {
                    if (!dataReceivedHandler.isWaitingForFilteringResponse()) {
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
                        // this makes sure that we don't close the popup
                        dataReceivedHandler.setNavigationCallback(() -> {
                        });
                        filterOptions(currentPage + pagesToScroll, lastFilter);
                    }
                    pagesToScroll = 0;
                }
            }

            public void scrollUp() {
                debug("VComboBox.SP.LPS: scrollUp()");
                if (pageLength > 0 && currentPage + pagesToScroll > 0) {
                    pagesToScroll--;
                    cancel();
                    schedule(200);
                }
            }

            public void scrollDown() {
                debug("VComboBox.SP.LPS: scrollDown()");
                if (pageLength > 0
                        && getTotalSuggestionsIncludingNullSelectionItem() > (currentPage
                                + pagesToScroll + 1) * pageLength) {
                    pagesToScroll++;
                    cancel();
                    schedule(200);
                }
            }
        }

        private void scroll(double deltaY) {
            boolean scrollActive = menu.isScrollActive();

            debug("VComboBox.SP: scroll() scrollActive: " + scrollActive);

            if (!scrollActive) {
                if (deltaY > 0d) {
                    lazyPageScroller.scrollDown();
                } else {
                    lazyPageScroller.scrollUp();
                }
            }
        }

        @Override
        public void onBrowserEvent(Event event) {
            debug("VComboBox.SP: onBrowserEvent()");

            if (event.getTypeInt() == Event.ONCLICK) {
                final Element target = DOM.eventGetTarget(event);
                if (target == up || target == DOM.getChild(up, 0)) {
                    lazyPageScroller.scrollUp();
                } else if (target == down || target == DOM.getChild(down, 0)) {
                    lazyPageScroller.scrollDown();
                }

            }

            /*
             * Prevent the keyboard focus from leaving the textfield by
             * preventing the default behavior of the browser. Fixes #4285.
             */
            handleMouseDownEvent(event);
        }

        @Override
        protected void onPreviewNativeEvent(NativePreviewEvent event) {
            // Check all events outside the combobox to see if they scroll the
            // page. We cannot use e.g. Window.addScrollListener() because the
            // scrolled element can be at any level on the page.

            // Normally this is only called when the popup is showing, but make
            // sure we don't accidentally process all events when not showing.
            if (!scrollPending && isShowing() && !DOM.isOrHasChild(
                    SuggestionPopup.this.getElement(),
                    Element.as(event.getNativeEvent().getEventTarget()))) {
                if (getDesiredLeftPosition() != leftPosition
                        || getDesiredTopPosition() != topPosition) {
                    updatePopupPositionOnScroll();
                }
            }

            super.onPreviewNativeEvent(event);
        }

        /**
         * Make the popup follow the position of the ComboBox when the page is
         * scrolled.
         */
        private void updatePopupPositionOnScroll() {
            if (!scrollPending) {
                AnimationScheduler.get().requestAnimationFrame(timestamp -> {
                    if (isShowing()) {
                        leftPosition = getDesiredLeftPosition();
                        topPosition = getDesiredTopPosition();
                        setPopupPosition(leftPosition, topPosition);
                    }
                    scrollPending = false;
                });
                scrollPending = true;
            }
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
            debug("VComboBox.SP: setPagingEnabled(" + paging + ")");
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
            debug("VComboBox.SP: setPosition(" + offsetWidth + ", "
                    + offsetHeight + ")");

            int top = topPosition;
            int left = getPopupLeft();

            // reset menu size and retrieve its "natural" size
            menu.setHeight("");
            if (currentPage > 0 && !hasNextPage()) {
                // fix height to avoid height change when getting to last page
                menu.fixHeightTo(pageLength);
            }

            // ignoring the parameter as in V7
            offsetHeight = getOffsetHeight();
            final int desiredHeight = offsetHeight;
            final int desiredWidth = getMainWidth();

            debug("VComboBox.SP:     desired[" + desiredWidth + ", "
                    + desiredHeight + "]");

            Element menuFirstChild = menu.getElement().getFirstChildElement();
            int naturalMenuWidth;
            if (BrowserInfo.get().isIE()
                    && BrowserInfo.get().getBrowserMajorVersion() < 10) {
                // On IE 8 & 9 visibility is set to hidden and measuring
                // elements while they are hidden yields incorrect results
                String before = menu.getElement().getParentElement().getStyle()
                        .getVisibility();
                menu.getElement().getParentElement().getStyle()
                        .setVisibility(Visibility.VISIBLE);
                naturalMenuWidth = WidgetUtil.getRequiredWidth(menuFirstChild);
                menu.getElement().getParentElement().getStyle()
                        .setProperty("visibility", before);
            } else {
                naturalMenuWidth = WidgetUtil.getRequiredWidth(menuFirstChild);
            }

            if (popupOuterPadding == -1) {
                popupOuterPadding = WidgetUtil
                        .measureHorizontalPaddingAndBorder(menu.getElement(), 2)
                        + WidgetUtil.measureHorizontalPaddingAndBorder(
                                suggestionPopup.getElement(), 0);
            }

            updateMenuWidth(desiredWidth, naturalMenuWidth);

            if (BrowserInfo.get().isIE()
                    && BrowserInfo.get().getBrowserMajorVersion() < 11) {
                // Must take margin,border,padding manually into account for
                // menu element as we measure the element child and set width to
                // the element parent

                double naturalMenuOuterWidth;
                if (BrowserInfo.get().getBrowserMajorVersion() < 10) {
                    // On IE 8 & 9 visibility is set to hidden and measuring
                    // elements while they are hidden yields incorrect results
                    String before = menu.getElement().getParentElement()
                            .getStyle().getVisibility();
                    menu.getElement().getParentElement().getStyle()
                            .setVisibility(Visibility.VISIBLE);
                    naturalMenuOuterWidth = WidgetUtil
                            .getRequiredWidthDouble(menuFirstChild)
                            + getMarginBorderPaddingWidth(menu.getElement());
                    menu.getElement().getParentElement().getStyle()
                            .setProperty("visibility", before);
                } else {
                    naturalMenuOuterWidth = WidgetUtil
                            .getRequiredWidthDouble(menuFirstChild)
                            + getMarginBorderPaddingWidth(menu.getElement());
                }

                /*
                 * IE requires us to specify the width for the container
                 * element. Otherwise it will be 100% wide
                 */
                double rootWidth = Math.max(desiredWidth - popupOuterPadding,
                        naturalMenuOuterWidth);
                getContainerElement().getStyle().setWidth(rootWidth, Unit.PX);
            }

            final int textInputHeight = VComboBox.this.getOffsetHeight();
            final int textInputTopOnPage = tb.getAbsoluteTop();
            final int viewportOffset = Document.get().getScrollTop();
            final int textInputTopInViewport = textInputTopOnPage
                    - viewportOffset;
            final int textInputBottomInViewport = textInputTopInViewport
                    + textInputHeight;

            final int spaceAboveInViewport = textInputTopInViewport;
            final int spaceBelowInViewport = Window.getClientHeight()
                    - textInputBottomInViewport;

            if (spaceBelowInViewport < offsetHeight
                    && spaceBelowInViewport < spaceAboveInViewport) {
                // popup on top of input instead
                if (offsetHeight > spaceAboveInViewport) {
                    // Shrink popup height to fit above
                    offsetHeight = spaceAboveInViewport;
                }
                top = textInputTopOnPage - offsetHeight;
            } else {
                // Show below, position calculated in showSuggestions for some
                // strange reason
                top = topPosition;
                offsetHeight = Math.min(offsetHeight, spaceBelowInViewport);
            }

            // fetch real width (mac FF bugs here due GWT popups overflow:auto )
            offsetWidth = menuFirstChild.getOffsetWidth();

            if (offsetHeight < desiredHeight) {
                int menuHeight = offsetHeight;
                if (isPagingEnabled) {
                    menuHeight -= up.getOffsetHeight() + down.getOffsetHeight()
                            + status.getOffsetHeight();
                } else {
                    final ComputedStyle menuStyle = new ComputedStyle(
                            menu.getElement());
                    final ComputedStyle popupStyle = new ComputedStyle(
                            suggestionPopup.getElement());
                    menuHeight -= menuStyle.getIntProperty("marginBottom")
                            + menuStyle.getIntProperty("marginTop")
                            + menuStyle.getIntProperty("paddingBottom")
                            + menuStyle.getIntProperty("paddingTop")
                            + popupStyle.getIntProperty("marginBottom")
                            + popupStyle.getIntProperty("marginTop")
                            + popupStyle.getIntProperty("paddingBottom")
                            + popupStyle.getIntProperty("paddingTop");
                }

                // If the available page height is really tiny then this will be
                // negative and an exception will be thrown on setHeight.
                int menuElementHeight = menu.getItemOffsetHeight();
                if (menuHeight < menuElementHeight) {
                    menuHeight = menuElementHeight;
                }

                menu.setHeight(menuHeight + "px");

                if (suggestionPopupWidth == null) {
                    final int naturalMenuWidthPlusScrollBar = naturalMenuWidth
                            + WidgetUtil.getNativeScrollbarSize();
                    if (offsetWidth < naturalMenuWidthPlusScrollBar) {
                        menu.setWidth(naturalMenuWidthPlusScrollBar + "px");
                    }
                }
            }

            if (offsetWidth + left > Window.getClientWidth()) {
                left = VComboBox.this.getAbsoluteLeft()
                        + VComboBox.this.getOffsetWidth() - offsetWidth;
                if (left < 0) {
                    left = 0;
                    menu.setWidth(Window.getClientWidth() + "px");

                }
            }

            setPopupPosition(left, top);
            menu.scrollSelectionIntoView();
        }

        /**
         * Adds in-line CSS rules to the DOM according to the
         * suggestionPopupWidth field
         *
         * @param desiredWidth
         * @param naturalMenuWidth
         */
        private void updateMenuWidth(final int desiredWidth,
                int naturalMenuWidth) {
            /**
             * Three different width modes for the suggestion pop-up:
             *
             * 1. Legacy "null"-mode: width is determined by the longest item
             * caption for each page while still maintaining minimum width of
             * (desiredWidth - popupOuterPadding)
             *
             * 2. relative to the component itself
             *
             * 3. fixed width
             */
            String width = "auto";
            if (suggestionPopupWidth == null) {
                if (naturalMenuWidth < desiredWidth) {
                    naturalMenuWidth = desiredWidth - popupOuterPadding;
                    width = desiredWidth - popupOuterPadding + "px";
                }
            } else if (isrelativeUnits(suggestionPopupWidth)) {
                float mainComponentWidth = desiredWidth - popupOuterPadding;
                // convert percentage value to fraction
                int widthInPx = Math.round(
                        mainComponentWidth * asFraction(suggestionPopupWidth));
                width = widthInPx + "px";
            } else {
                // use as fixed width CSS definition
                width = WidgetUtil.escapeAttribute(suggestionPopupWidth);
            }
            menu.setWidth(width);
        }

        /**
         * Returns the percentage value as a fraction, e.g. 42% -> 0.42
         *
         * @param percentage
         */
        private float asFraction(String percentage) {
            String trimmed = percentage.trim();
            String withoutPercentSign = trimmed.substring(0,
                    trimmed.length() - 1);
            float asFraction = Float.parseFloat(withoutPercentSign) / 100;
            return asFraction;
        }

        /**
         * @since 7.7
         * @param suggestionPopupWidth
         * @return
         */
        private boolean isrelativeUnits(String suggestionPopupWidth) {
            return suggestionPopupWidth.trim().endsWith("%");
        }

        /**
         * Was the popup just closed?
         *
         * @return true if popup was just closed
         */
        public boolean isJustClosed() {
            debug("VComboBox.SP: justClosed()");
            final long now = new Date().getTime();
            return lastAutoClosed > 0 && now - lastAutoClosed < 200;
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
                debug("VComboBox.SP: onClose(" + event.isAutoClosed() + ")");
            }
            if (event.isAutoClosed()) {
                lastAutoClosed = new Date().getTime();
            }
        }

        /**
         * Updates style names in suggestion popup to help theme building.
         *
         * @param componentState
         *            shared state of the combo box
         */
        public void updateStyleNames(AbstractComponentState componentState) {
            debug("VComboBox.SP: updateStyleNames()");
            setStyleName(
                    VComboBox.this.getStylePrimaryName() + "-suggestpopup");
            menu.setStyleName(
                    VComboBox.this.getStylePrimaryName() + "-suggestmenu");
            status.setClassName(
                    VComboBox.this.getStylePrimaryName() + "-status");
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
     * The menu where the suggestions are rendered.
     */
    public class SuggestionMenu extends MenuBar
            implements SubPartAware, LoadHandler {

        private VLazyExecutor delayedImageLoadExecutioner = new VLazyExecutor(
                100, () -> {
                    debug("VComboBox.SM: delayedImageLoadExecutioner()");
                    if (suggestionPopup.isVisible()
                            && suggestionPopup.isAttached()) {
                        setWidth("");
                        getElement().getFirstChildElement().getStyle()
                                .clearWidth();
                        suggestionPopup
                                .setPopupPositionAndShow(suggestionPopup);
                    }
                });

        private String handledNewItem = null;

        /**
         * Default constructor
         */
        SuggestionMenu() {
            super(true);
            debug("VComboBox.SM: constructor()");
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
            if (!currentSuggestions.isEmpty()) {
                final int pixels = getPreferredHeight()
                        / currentSuggestions.size() * pageItemsCount;
                return pixels + "px";
            }
            return "";
        }

        /**
         * Sets the suggestions rendered in the menu.
         *
         * @param suggestions
         *            The suggestions to be rendered in the menu
         */
        public void setSuggestions(Collection<ComboBoxSuggestion> suggestions) {
            if (enableDebug) {
                debug("VComboBox.SM: setSuggestions(" + suggestions + ")");
            }

            clearItems();
            boolean isFirstIteration = true;
            for (final ComboBoxSuggestion suggestion : suggestions) {
                final MenuItem mi = new MenuItem(suggestion.getDisplayString(),
                        true, suggestion);
                String style = suggestion.getStyle();
                if (style != null) {
                    mi.addStyleName("v-filterselect-item-" + style);
                }
                Roles.getListitemRole().set(mi.getElement());

                WidgetUtil.sinkOnloadForImages(mi.getElement());

                this.addItem(mi);

                // By default, first item on the list is always highlighted,
                // unless adding new items is allowed.
                if (isFirstIteration && !allowNewItems) {
                    selectItem(mi);
                }

                if (currentSuggestion != null && suggestion.getOptionKey()
                        .equals(currentSuggestion.getOptionKey())) {
                    // Refresh also selected caption and icon in case they have
                    // been updated on the server, e.g. just the item has been
                    // updated, but selection (from state) has stayed the same.
                    // FIXME need to update selected item caption separately, if
                    // the selected item is not in "active data range" that is
                    // being sent to the client. Then this can be removed.
                    if (currentSuggestion.getReplacementString()
                            .equals(tb.getText())) {
                        currentSuggestion = suggestion;
                        selectItem(mi);
                        setSelectedCaption(
                                currentSuggestion.getReplacementString());
                        setSelectedItemIcon(currentSuggestion.getIconUri());
                    }
                }
                isFirstIteration = false;
            }
        }

        /**
         * Create/select a suggestion based on the used entered string. This
         * method is called after filtering has completed with the given string.
         *
         * @param enteredItemValue
         *            user entered string
         */
        public void actOnEnteredValueAfterFiltering(String enteredItemValue) {
            debug("VComboBox.SM: doPostFilterSelectedItemAction()");
            final MenuItem item = getSelectedItem();
            boolean handledOnServer = handledNewItem == enteredItemValue;
            if (handledOnServer) {
                // clear value to mark it as handled
                handledNewItem = null;
            }
            // check for exact match in menu
            int p = getItems().size();
            if (p > 0) {
                for (int i = 0; i < p; i++) {
                    final MenuItem potentialExactMatch = getItems().get(i);
                    if (potentialExactMatch.getText()
                            .equals(enteredItemValue)) {
                        selectItem(potentialExactMatch);
                        // do not send a value change event if null was and
                        // stays selected
                        if (!"".equals(enteredItemValue)
                                || selectedOptionKey != null
                                        && !selectedOptionKey.isEmpty()) {
                            doItemAction(potentialExactMatch, true);
                        }
                        suggestionPopup.hide();
                        lastNewItemString = null;
                        connector.clearNewItemHandlingIfMatch(enteredItemValue);
                        return;
                    }
                }
            }

            if (!handledOnServer && "".equals(enteredItemValue)
                    && nullSelectionAllowed) {
                onNullSelected();
            } else if (!handledOnServer && allowNewItems) {
                if (!enteredItemValue.equals(lastNewItemString)) {
                    // Store last sent new item string to avoid double sends
                    lastNewItemString = enteredItemValue;
                    connector.sendNewItem(enteredItemValue);
                    // TODO try to select the new value if it matches what was
                    // sent for V7 compatibility
                }
            } else if (item != null && !"".equals(lastFilter)
                    && item.getText().toLowerCase(Locale.ROOT)
                            .contains(lastFilter.toLowerCase(Locale.ROOT))) {
                doItemAction(item, true);
            } else {
                // currentSuggestion has key="" for nullselection
                if (currentSuggestion != null
                        && !currentSuggestion.key.isEmpty()) {
                    // An item (not null) selected
                    String text = currentSuggestion.getReplacementString();
                    setText(text);
                    selectedOptionKey = currentSuggestion.key;
                } else {
                    onNullSelected();
                }
            }
            suggestionPopup.hide();

            if (handledOnServer || !allowNewItems) {
                lastNewItemString = null;
            }
        }

        private static final String SUBPART_PREFIX = "item";

        @Override
        public com.google.gwt.user.client.Element getSubPartElement(
                String subPart) {
            int index = Integer
                    .parseInt(subPart.substring(SUBPART_PREFIX.length()));

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
            debug("VComboBox.SM: onLoad()");
            // Handle icon onload events to ensure shadow is resized
            // correctly
            delayedImageLoadExecutioner.trigger();

        }

        /**
         * @deprecated use {@link SuggestionPopup#selectFirstItem()} instead.
         */
        @Deprecated
        public void selectFirstItem() {
            debug("VComboBox.SM: selectFirstItem()");
            MenuItem firstItem = getItems().get(0);
            selectItem(firstItem);
        }

        /**
         * @deprecated use {@link SuggestionPopup#selectLastItem()} instead.
         */
        @Deprecated
        public void selectLastItem() {
            debug("VComboBox.SM: selectLastItem()");
            List<MenuItem> items = getItems();
            MenuItem lastItem = items.get(items.size() - 1);
            selectItem(lastItem);
        }

        /*
         * Gets the height of one menu item.
         */
        int getItemOffsetHeight() {
            List<MenuItem> items = getItems();
            return items != null && !items.isEmpty()
                    ? items.get(0).getOffsetHeight()
                    : 0;
        }

        /*
         * Gets the width of one menu item.
         */
        int getItemOffsetWidth() {
            List<MenuItem> items = getItems();
            return items != null && !items.isEmpty()
                    ? items.get(0).getOffsetWidth()
                    : 0;
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

            return !(height == null || height.isEmpty()
                    || height.equals(preferredHeight));
        }

        /**
         * Highlight (select) an item matching the current text box content
         * without triggering its action.
         */
        public void highlightSelectedItem() {
            int p = getItems().size();
            // first check if there is a key match to handle items with
            // identical captions
            String currentKey = currentSuggestion != null
                    ? currentSuggestion.getOptionKey()
                    : "";
            for (int i = 0; i < p; i++) {
                final MenuItem potentialExactMatch = getItems().get(i);
                if (currentKey.equals(getSuggestionKey(potentialExactMatch))
                        && tb.getText().equals(potentialExactMatch.getText())) {
                    selectItem(potentialExactMatch);
                    tb.setSelectionRange(tb.getText().length(), 0);
                    return;
                }
            }
            // then check for exact string match in menu
            String text = tb.getText();
            for (int i = 0; i < p; i++) {
                final MenuItem potentialExactMatch = getItems().get(i);
                if (potentialExactMatch.getText().equals(text)) {
                    selectItem(potentialExactMatch);
                    tb.setSelectionRange(tb.getText().length(), 0);
                    return;
                }
            }
        }

        public void markNewItemsHandled(String handledNewItem) {
            this.handledNewItem = handledNewItem;
        }
    }

    private String getSuggestionKey(MenuItem item) {
        if (item != null && item.getCommand() != null) {
            return ((ComboBoxSuggestion) item.getCommand()).getOptionKey();
        }
        return "";
    }

    /**
     * TextBox variant used as input element for filter selects, which prevents
     * selecting text when disabled.
     *
     * @since 7.1.5
     */
    public class FilterSelectTextBox extends TextBox {

        /**
         * Creates a new filter select text box.
         *
         * @since 7.6.4
         */
        public FilterSelectTextBox() {
            /*-
             * Stop the browser from showing its own suggestion popup.
             *
             * Using an invalid value instead of "off" as suggested by
             * https://developer.mozilla.org/en-US/docs/Web/Security/Securing_your_site/Turning_off_form_autocompletion
             *
             * Leaving the non-standard Safari options autocapitalize and
             * autocorrect untouched since those do not interfere in the same
             * way, and they might be useful in a combo box where new items are
             * allowed.
             */
            getElement().setAttribute("autocomplete", Math.random() + "");
        }

        /**
         * Overridden to avoid selecting text when text input is disabled.
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
                WidgetUtil.setSelectionRange(getElement(), pos, length,
                        "backward");

            } else {
                /*
                 * Setting the selectionrange for an uneditable textbox leads to
                 * unwanted behavior when the width of the textbox is narrower
                 * than the width of the entry: the end of the entry is shown
                 * instead of the beginning. (see #13477)
                 *
                 * To avoid this, we set the caret to the beginning of the line.
                 */

                super.setSelectionRange(0, 0);
            }
        }
    }

    /**
     * Handler receiving notifications from the connector and updating the
     * widget state accordingly.
     *
     * This class is still subject to change and should not be considered as
     * public stable API.
     *
     * @since 8.0
     */
    public class DataReceivedHandler {

        private Runnable navigationCallback = null;
        /**
         * Set true when popupopened has been clicked. Cleared on each
         * UIDL-update. This handles the special case where are not filtering
         * yet and the selected value has changed on the server-side. See #2119
         * <p>
         * For internal use only. May be removed or replaced in the future.
         */
        private boolean popupOpenerClicked = false;
        /** For internal use only. May be removed or replaced in the future. */
        private boolean waitingForFilteringResponse = false;
        private boolean initialData = true;
        private String pendingUserInput = null;
        private boolean showPopup = false;

        /**
         * Called by the connector when new data for the last requested filter
         * is received from the server.
         */
        public void dataReceived() {
            if (initialData) {
                suggestionPopup.menu.setSuggestions(currentSuggestions);
                performSelection(serverSelectedKey, true, true);
                updateSuggestionPopupMinWidth();
                updateRootWidth();
                initialData = false;
                return;
            }

            suggestionPopup.menu.setSuggestions(currentSuggestions);
            if (!waitingForFilteringResponse && suggestionPopup.isAttached()) {
                showPopup = true;
            }
            if (showPopup) {
                suggestionPopup.showSuggestions(currentPage);
            }

            waitingForFilteringResponse = false;

            if (pendingUserInput != null) {
                boolean pendingHandled = suggestionPopup.menu.handledNewItem == pendingUserInput;
                suggestionPopup.menu
                        .actOnEnteredValueAfterFiltering(pendingUserInput);
                if (!allowNewItems || (pendingHandled
                        && suggestionPopup.menu.handledNewItem == null)) {
                    pendingUserInput = null;
                } else {
                    waitingForFilteringResponse = true;
                }
            } else if (popupOpenerClicked) {
                // make sure the current item is selected in the popup
                suggestionPopup.menu.highlightSelectedItem();
            } else {
                navigateItemAfterPageChange();
            }

            if (!showPopup) {
                suggestionPopup.hide();
            }

            popupOpenerClicked = false;
            showPopup = false;
        }

        /**
         * Perform filtering with the user entered string and when the results
         * are received, perform any action appropriate for the user input
         * (select an item or create a new one).
         *
         * @param value
         *            user input
         */
        public void reactOnInputWhenReady(String value) {
            pendingUserInput = value;
            showPopup = false;
            filterOptions(0, value);
        }

        public boolean isPending(String value) {
            return value != null && value.equals(pendingUserInput);
        }

        /*
         * This method navigates to the proper item in the combobox page. This
         * should be executed after setSuggestions() method which is called from
         * VComboBox.showSuggestions(). ShowSuggestions() method builds the page
         * content. As far as setSuggestions() method is called as deferred,
         * navigateItemAfterPageChange method should be also be called as
         * deferred. #11333
         */
        private void navigateItemAfterPageChange() {
            if (navigationCallback != null) {
                // navigationCallback is not reset here but after any server
                // request in case you are in between two requests both changing
                // the page back and forth

                // we're paging w/ arrows
                navigationCallback.run();
                navigationCallback = null;
            }
        }

        /**
         * Called by the connector any pending navigation operations should be
         * cleared.
         */
        public void clearPendingNavigation() {
            navigationCallback = null;
        }

        /**
         * Set a callback that is invoked when a page change occurs if there
         * have not been intervening requests to the server. The callback is
         * reset when any additional request is made to the server.
         *
         * @param callback
         *            method to call after filtering has completed
         */
        public void setNavigationCallback(Runnable callback) {
            showPopup = true;
            navigationCallback = callback;
        }

        /**
         * Record that the popup opener has been clicked and the popup should be
         * opened on the next request.
         *
         * This handles the special case where are not filtering yet and the
         * selected value has changed on the server-side. See #2119. The flag is
         * cleared on each server reply.
         */
        public void popupOpenerClicked() {
            popupOpenerClicked = true;
            showPopup = true;
        }

        /**
         * Cancel a pending request to perform post-filtering actions.
         */
        private void cancelPendingPostFiltering() {
            pendingUserInput = null;
            waitingForFilteringResponse = false;
        }

        /**
         * Called by the connector when it has finished handling any reply from
         * the server, regardless of what was updated.
         */
        public void serverReplyHandled() {
            popupOpenerClicked = false;

            // if (!initDone) {
            // debug("VComboBox: init done, updating widths");
            // // Calculate minimum textarea width
            // updateSuggestionPopupMinWidth();
            // updateRootWidth();
            // initDone = true;
            // }
        }

        /**
         * For internal use only - this method will be removed in the future.
         *
         * @return true if the combo box is waiting for a reply from the server
         *         with a new page of data, false otherwise
         */
        public boolean isWaitingForFilteringResponse() {
            return waitingForFilteringResponse;
        }

        /**
         * For internal use only - this method will be removed in the future.
         *
         * @return true if the combo box is waiting for initial data from the
         *         server, false otherwise
         */
        public boolean isWaitingForInitialData() {
            return initialData;
        }

        /**
         * Set a flag that filtering of options is pending a response from the
         * server.
         */
        private void startWaitingForFilteringResponse() {
            waitingForFilteringResponse = true;
        }

        /**
         * Perform selection (if appropriate) based on a reply from the server.
         * When this method is called, the suggestions have been reset if new
         * ones (different from the previous list) were received from the
         * server.
         *
         * @param selectedKey
         *            new selected key or null if none given by the server
         * @param selectedCaption
         *            new selected item caption if sent by the server or null -
         *            this is used when the selected item is not on the current
         *            page
         * @param selectedIconUri
         *            new selected item icon if sent by the server or {@ code
         *            null} to clear
         */
        public void updateSelectionFromServer(String selectedKey,
                String selectedCaption, String selectedIconUri) {
            boolean oldSuggestionTextMatchTheOldSelection = currentSuggestion != null
                    && currentSuggestion.getReplacementString()
                            .equals(tb.getText());

            serverSelectedKey = selectedKey;

            performSelection(selectedKey, oldSuggestionTextMatchTheOldSelection,
                    !isWaitingForFilteringResponse() || popupOpenerClicked);

            cancelPendingPostFiltering();

            setSelectedCaption(selectedCaption);

            setSelectedItemIcon(selectedIconUri);
        }

    }

    // TODO decide whether this should change - affects themes and v7
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
    private final HTML popupOpener = new HTML("");

    private class IconWidget extends Widget {
        IconWidget(Icon icon) {
            setElement(icon.getElement());
        }
    }

    private IconWidget selectedItemIcon;

    /** For internal use only. May be removed or replaced in the future. */
    public ComboBoxConnector connector;

    /** For internal use only. May be removed or replaced in the future. */
    public int currentPage;

    /**
     * A collection of available suggestions (options) as received from the
     * server.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public final List<ComboBoxSuggestion> currentSuggestions = new ArrayList<>();

    /** For internal use only. May be removed or replaced in the future. */
    public String serverSelectedKey;
    /** For internal use only. May be removed or replaced in the future. */
    public String selectedOptionKey;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean initDone = false;

    /** For internal use only. May be removed or replaced in the future. */
    public String lastFilter = "";

    /**
     * The current suggestion selected from the dropdown. This is one of the
     * values in currentSuggestions except when filtering, in this case
     * currentSuggestion might not be in currentSuggestions.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public ComboBoxSuggestion currentSuggestion;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean allowNewItems;

    /** Total number of suggestions, excluding null selection item. */
    private int totalSuggestions;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectionAllowed;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectItem;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean enabled;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean readonly;

    /** For internal use only. May be removed or replaced in the future. */
    public String inputPrompt = "";

    /** For internal use only. May be removed or replaced in the future. */
    public int suggestionPopupMinWidth = 0;

    public String suggestionPopupWidth = null;

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
    private String emptySelectionCaption = "";

    private final DataReceivedHandler dataReceivedHandler = new DataReceivedHandler();

    /**
     * Default constructor.
     */
    public VComboBox() {
        tb = createTextBox();
        suggestionPopup = createSuggestionPopup();

        popupOpener.addMouseDownHandler(VComboBox.this);
        Roles.getButtonRole().setAriaHiddenState(popupOpener.getElement(),
                true);
        Roles.getButtonRole().set(popupOpener.getElement());

        panel.add(tb);
        panel.add(popupOpener);
        initWidget(panel);
        Roles.getComboboxRole().set(panel.getElement());

        tb.addKeyDownHandler(this);
        tb.addKeyUpHandler(this);

        tb.addFocusHandler(this);
        tb.addBlurHandler(this);

        panel.addDomHandler(this, ClickEvent.getType());

        setStyleName(CLASSNAME);

        sinkEvents(Event.ONPASTE);
    }

    private static double getMarginBorderPaddingWidth(Element element) {
        final ComputedStyle s = new ComputedStyle(element);
        return s.getMarginWidth() + s.getBorderWidth() + s.getPaddingWidth();
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
            if (textInputEnabled && connector.isEnabled()) {
                filterOptions(currentPage);
            }
        }
    }

    /**
     * This method will create the TextBox used by the VComboBox instance. It is
     * invoked during the Constructor and should only be overridden if a custom
     * TextBox shall be used. The overriding method cannot use any instance
     * variables.
     *
     * @since 7.1.5
     * @return TextBox instance used by this VComboBox
     */
    protected TextBox createTextBox() {
        return new FilterSelectTextBox();
    }

    /**
     * This method will create the SuggestionPopup used by the VComboBox
     * instance. It is invoked during the Constructor and should only be
     * overridden if a custom SuggestionPopup shall be used. The overriding
     * method cannot use any instance variables.
     *
     * @since 7.1.5
     * @return SuggestionPopup instance used by this VComboBox
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
        return pageLength > 0
                && getTotalSuggestionsIncludingNullSelectionItem() > (currentPage
                        + 1) * pageLength;
    }

    /**
     * Filters the options at a certain page. Uses the text box input as a
     * filter and ensures the popup is opened when filtering results are
     * available.
     *
     * @param page
     *            The page which items are to be filtered
     */
    public void filterOptions(int page) {
        dataReceivedHandler.popupOpenerClicked();
        filterOptions(page, tb.getText());
    }

    /**
     * Filters the options at certain page using the given filter.
     *
     * @param page
     *            The page to filter
     * @param filter
     *            The filter to apply to the components
     */
    public void filterOptions(int page, String filter) {
        debug("VComboBox: filterOptions(" + page + ", " + filter + ")");

        if (filter.equals(lastFilter) && currentPage == page
                && suggestionPopup.isAttached()) {
            // already have the page
            dataReceivedHandler.dataReceived();
            return;
        }

        if (!filter.equals(lastFilter)) {
            // when filtering, let the server decide the page unless we've
            // set the filter to empty and explicitly said that we want to see
            // the results starting from page 0.
            if (filter.isEmpty() && page != 0) {
                // let server decide
                page = -1;
            } else {
                page = 0;
            }
        }

        dataReceivedHandler.startWaitingForFilteringResponse();
        connector.requestPage(page, filter);

        lastFilter = filter;

        // If the data was updated from cache, the page has been updated too, if
        // not, update
        if (dataReceivedHandler.isWaitingForFilteringResponse()) {
            currentPage = page;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateReadOnly() {
        debug("VComboBox: updateReadOnly()");
        tb.setReadOnly(readonly || !textInputEnabled);
    }

    public void setTextInputAllowed(boolean textInputAllowed) {
        debug("VComboBox: setTextInputAllowed()");
        // Always update styles as they might have been overwritten
        if (textInputAllowed) {
            removeStyleDependentName(STYLE_NO_INPUT);
            Roles.getTextboxRole().removeAriaReadonlyProperty(tb.getElement());
        } else {
            addStyleDependentName(STYLE_NO_INPUT);
            Roles.getTextboxRole().setAriaReadonlyProperty(tb.getElement(),
                    true);
        }

        if (textInputEnabled != textInputAllowed) {
            textInputEnabled = textInputAllowed;
            updateReadOnly();
        }
    }

    /**
     * Sets the text in the text box.
     *
     * @param text
     *            the text to set in the text box
     */
    public void setText(final String text) {
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
     * Set or reset the placeholder attribute for the text field.
     *
     * @param placeholder
     *            new placeholder string or null for none
     */
    public void setPlaceholder(String placeholder) {
        inputPrompt = placeholder;
        updatePlaceholder();
    }

    /**
     * Update placeholder visibility (hidden when read-only or disabled).
     */
    public void updatePlaceholder() {
        if (inputPrompt != null && enabled && !readonly) {
            tb.getElement().setAttribute("placeholder", inputPrompt);
        } else {
            tb.getElement().removeAttribute("placeholder");
        }
    }

    /**
     * Triggered when a suggestion is selected.
     *
     * @param suggestion
     *            The suggestion that just got selected.
     */
    public void onSuggestionSelected(ComboBoxSuggestion suggestion) {
        if (enableDebug) {
            debug("VComboBox: onSuggestionSelected(" + suggestion.caption + ": "
                    + suggestion.key + ")");
        }
        // special handling of null selection
        if (suggestion.key.isEmpty()) {
            onNullSelected();
            return;
        }

        dataReceivedHandler.cancelPendingPostFiltering();

        currentSuggestion = suggestion;
        String newKey = suggestion.getOptionKey();

        String text = suggestion.getReplacementString();
        setText(text);
        setSelectedItemIcon(suggestion.getIconUri());

        if (!newKey.equals(selectedOptionKey)) {
            selectedOptionKey = newKey;
            connector.sendSelection(selectedOptionKey);
            setSelectedCaption(text);

            // currentPage = -1; // forget the page
        }

        suggestionPopup.hide();
    }

    /**
     * Triggered when an empty value is selected and null selection is allowed.
     */
    public void onNullSelected() {
        if (enableDebug) {
            debug("VComboBox: onNullSelected()");
        }
        dataReceivedHandler.cancelPendingPostFiltering();

        currentSuggestion = null;
        setText(getEmptySelectionCaption());
        setSelectedItemIcon(null);

        if (!"".equals(selectedOptionKey) || selectedOptionKey != null) {
            selectedOptionKey = "";
            setSelectedCaption("");
            connector.sendSelection(null);
            // currentPage = 0;
        }

        updatePlaceholder();

        suggestionPopup.hide();
    }

    /**
     * Sets the icon URI of the selected item. The icon is shown on the left
     * side of the item caption text. Set the URI to null to remove the icon.
     *
     * @param iconUri
     *            The URI of the icon, or null to remove icon
     */
    public void setSelectedItemIcon(String iconUri) {

        if (selectedItemIcon != null) {
            panel.remove(selectedItemIcon);
        }
        if (iconUri == null || iconUri.isEmpty()) {
            if (selectedItemIcon != null) {
                selectedItemIcon = null;
                afterSelectedItemIconChange();
            }
        } else {
            selectedItemIcon = new IconWidget(
                    connector.getConnection().getIcon(iconUri));
            selectedItemIcon.addDomHandler(VComboBox.this,
                    ClickEvent.getType());
            selectedItemIcon.addDomHandler(VComboBox.this,
                    MouseDownEvent.getType());
            selectedItemIcon.addDomHandler(
                    event -> afterSelectedItemIconChange(),
                    LoadEvent.getType());
            panel.insert(selectedItemIcon, 0);
            afterSelectedItemIconChange();
        }
    }

    private void afterSelectedItemIconChange() {
        if (BrowserInfo.get().isWebkit()) {
            // Some browsers need a nudge to reposition the text field
            forceReflow();
        }
        updateRootWidth();
        if (selectedItemIcon != null) {
            updateSelectedIconPosition();
        }
    }

    /**
     * Perform selection based on a message from the server.
     *
     * The special case where the selected item is not on the current page is
     * handled separately by the caller.
     *
     * @param selectedKey
     *            non-empty selected item key
     * @param forceUpdateText
     *            true to force the text box value to match the suggestion text
     * @param updatePromptAndSelectionIfMatchFound
     */
    private void performSelection(String selectedKey, boolean forceUpdateText,
            boolean updatePromptAndSelectionIfMatchFound) {
        if (selectedKey == null || selectedKey.isEmpty()) {
            currentSuggestion = null; // #13217
            selectedOptionKey = null;
            setText(getEmptySelectionCaption());
        }
        // some item selected
        for (ComboBoxSuggestion suggestion : currentSuggestions) {
            String suggestionKey = suggestion.getOptionKey();
            if (!suggestionKey.equals(selectedKey)) {
                continue;
            }
            // at this point, suggestion key matches the new selection key
            if (updatePromptAndSelectionIfMatchFound) {
                if (!suggestionKey.equals(selectedOptionKey) || suggestion
                        .getReplacementString().equals(tb.getText())
                        || forceUpdateText) {
                    // Update text field if we've got a new
                    // selection
                    // Also update if we've got the same text to
                    // retain old text selection behavior
                    // OR if selected item caption is changed.
                    setText(suggestion.getReplacementString());
                    selectedOptionKey = suggestionKey;
                }
            }
            currentSuggestion = suggestion;
            // only a single item can be selected
            break;
        }
    }

    private void forceReflow() {
        WidgetUtil.setStyleTemporarily(tb.getElement(), "zoom", "1");
    }

    /**
     * Positions the icon vertically in the middle. Should be called after the
     * icon has loaded
     */
    private void updateSelectedIconPosition() {
        // Position icon vertically to middle
        int availableHeight = 0;
        availableHeight = getOffsetHeight();

        int iconHeight = WidgetUtil.getRequiredHeight(selectedItemIcon);
        int marginTop = (availableHeight - iconHeight) / 2;
        selectedItemIcon.getElement().getStyle().setMarginTop(marginTop,
                Unit.PX);
    }

    private static Set<Integer> navigationKeyCodes = new HashSet<>();
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
                debug("VComboBox: key down: " + keyCode);
            }
            if (dataReceivedHandler.isWaitingForFilteringResponse()
                    && navigationKeyCodes.contains(keyCode)
                    && (!allowNewItems || keyCode != KeyCodes.KEY_ENTER)) {
                /*
                 * Keyboard navigation events should not be handled while we are
                 * waiting for a response. This avoids flickering, disappearing
                 * items, wrongly interpreted responses and more.
                 */
                if (enableDebug) {
                    debug("Ignoring " + keyCode
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
            getLogger().severe(string);
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
            debug("VComboBox: inputFieldKeyDown(" + event.getNativeKeyCode()
                    + ")");
        }
        switch (event.getNativeKeyCode()) {
        case KeyCodes.KEY_DOWN:
        case KeyCodes.KEY_UP:
        case KeyCodes.KEY_PAGEDOWN:
        case KeyCodes.KEY_PAGEUP:
            // open popup as from gadget
            filterOptions(-1, "");
            tb.selectAll();
            dataReceivedHandler.popupOpenerClicked();
            break;
        case KeyCodes.KEY_ENTER:
            /*
             * This only handles the case when new items is allowed, a text is
             * entered, the popup opener button is clicked to close the popup
             * and enter is then pressed (see #7560).
             */
            if (!allowNewItems) {
                return;
            }

            if (currentSuggestion != null && tb.getText()
                    .equals(currentSuggestion.getReplacementString())) {
                // Retain behavior from #6686 by returning without stopping
                // propagation if there's nothing to do
                return;
            }
            dataReceivedHandler.reactOnInputWhenReady(tb.getText());
            suggestionPopup.hide();

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
            debug("VComboBox: popupKeyDown(" + event.getNativeKeyCode() + ")");
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
        case KeyCodes.KEY_ESCAPE:
            reset();
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            event.stopPropagation();
            break;
        case KeyCodes.KEY_TAB:
        case KeyCodes.KEY_ENTER:

            // queue this, may be cancelled by selection
            int selectedIndex = suggestionPopup.menu.getSelectedIndex();
            if (!allowNewItems && selectedIndex != -1) {
                onSuggestionSelected(currentSuggestions.get(selectedIndex));
            } else {
                dataReceivedHandler.reactOnInputWhenReady(tb.getText());
            }
            suggestionPopup.hide();

            event.stopPropagation();
            break;
        }
    }

    /*
     * Show the prev page.
     */
    private void selectPrevPage() {
        if (currentPage > 0) {
            dataReceivedHandler.setNavigationCallback(
                    () -> suggestionPopup.selectLastItem());
            filterOptions(currentPage - 1, lastFilter);
        }
    }

    /*
     * Show the next page.
     */
    private void selectNextPage() {
        if (hasNextPage()) {
            dataReceivedHandler.setNavigationCallback(
                    () -> suggestionPopup.selectFirstItem());
            filterOptions(currentPage + 1, lastFilter);
        }
    }

    /**
     * Triggered when a key was depressed.
     *
     * @param event
     *            The KeyUpEvent of the key depressed
     */
    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (enableDebug) {
            debug("VComboBox: onKeyUp(" + event.getNativeKeyCode() + ")");
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
            case KeyCodes.KEY_RIGHT:
            case KeyCodes.KEY_LEFT:
            case KeyCodes.KEY_PAGEDOWN:
            case KeyCodes.KEY_PAGEUP:
            case KeyCodes.KEY_ESCAPE:
            case KeyCodes.KEY_HOME:
            case KeyCodes.KEY_END:
                // NOP
                break;
            default:
                if (textInputEnabled) {
                    // when filtering, we always want to see the results on the
                    // first page first.
                    filterOptions(0);
                }
                break;
            }
        }
    }

    /**
     * Resets the ComboBox to its initial state.
     */
    private void reset() {
        debug("VComboBox: reset()");

        // just fetch selected information from state
        String text = connector.getState().selectedItemCaption;
        setText(text == null ? getEmptySelectionCaption() : text);
        setSelectedItemIcon(connector.getState().selectedItemIcon);
        selectedOptionKey = (connector.getState().selectedItemKey);
        if (selectedOptionKey == null || selectedOptionKey.isEmpty()) {
            currentSuggestion = null; // #13217
            selectedOptionKey = null;
            updatePlaceholder();
        } else {
            currentSuggestion = currentSuggestions.stream()
                    .filter(suggestion -> suggestion.getOptionKey()
                            .equals(selectedOptionKey))
                    .findAny().orElse(null);
        }

        suggestionPopup.hide();
    }

    /**
     * Listener for popupopener.
     */
    @Override
    public void onClick(ClickEvent event) {
        debug("VComboBox: onClick()");
        if (textInputEnabled && event.getNativeEvent().getEventTarget()
                .cast() == tb.getElement()) {
            // Don't process clicks on the text field if text input is enabled
            return;
        }
        if (enabled && !readonly) {
            // ask suggestionPopup if it was just closed, we are using GWT
            // Popup's auto close feature
            if (!suggestionPopup.isJustClosed()) {
                filterOptions(-1, "");
                dataReceivedHandler.popupOpenerClicked();
            }
            DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
            focus();
            tb.selectAll();
        }
    }

    /**
     * Update minimum width for combo box textarea based on input prompt and
     * suggestions.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateSuggestionPopupMinWidth() {
        debug("VComboBox: updateSuggestionPopupMinWidth()");

        // used only to calculate minimum width
        String captions = WidgetUtil.escapeHTML(inputPrompt);

        for (ComboBoxSuggestion suggestion : currentSuggestions) {
            // Collect captions so we can calculate minimum width for
            // textarea
            if (!captions.isEmpty()) {
                captions += "|";
            }
            captions += WidgetUtil
                    .escapeHTML(suggestion.getReplacementString());
        }

        // Calculate minimum textarea width
        suggestionPopupMinWidth = minWidth(captions);
    }

    /**
     * Calculate minimum width for FilterSelect textarea.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     *
     * @param captions
     *            pipe separated string listing all the captions to measure
     * @return minimum width in pixels
     */
    public native int minWidth(String captions)
    /*-{
        if (!captions || captions.length <= 0)
                return 0;
        captions = captions.split("|");
        var d = $wnd.document.createElement("div");
        var html = "";
        for (var i=0; i < captions.length; i++) {
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
     * A flag which prevents a focus event from taking place.
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
        debug("VComboBox: onFocus()");

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
        updatePlaceholder();
        addStyleDependentName("focus");

        connector.sendFocusEvent();

        connector.getConnection().getVTooltip()
                .showAssistive(connector.getTooltipInfo(getElement()));
    }

    /**
     * A flag which cancels the blur event and sets the focus back to the
     * textfield if the Browser is IE.
     */
    boolean preventNextBlurEventInIE = false;

    private String explicitSelectedCaption;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */

    @Override
    public void onBlur(BlurEvent event) {
        debug("VComboBox: onBlur()");

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

            Element focusedElement = WidgetUtil.getFocusedElement();
            if (getElement().isOrHasChild(focusedElement) || suggestionPopup
                    .getElement().isOrHasChild(focusedElement)) {

                // IF the suggestion popup or another part of the VComboBox
                // was focused, move the focus back to the textfield and prevent
                // the triggered focus event (in onFocus).
                iePreventNextFocus = true;
                tb.setFocus(true);
                return;
            }
        }

        focused = false;
        updatePlaceholder();
        removeStyleDependentName("focus");

        // Send new items when clicking out with the mouse.
        if (!readonly) {
            if (textInputEnabled && allowNewItems
                    && (currentSuggestion == null || !tb.getText().equals(
                            currentSuggestion.getReplacementString()))) {
                dataReceivedHandler.reactOnInputWhenReady(tb.getText());
            } else {
                reset();
            }
            suggestionPopup.hide();
        }

        connector.sendBlurEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.Focusable#focus()
     */

    @Override
    public void focus() {
        debug("VComboBox: focus()");
        focused = true;
        updatePlaceholder();
        tb.setFocus(true);
    }

    /**
     * Calculates the width of the select if the select has undefined width.
     * Should be called when the width changes or when the icon changes.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateRootWidth() {
        debug("VComboBox: updateRootWidth()");

        if (connector.isUndefinedWidth()) {

            /*
             * When the select has a undefined with we need to check that we are
             * only setting the text box width relative to the first page width
             * of the items. If this is not done the text box width will change
             * when the popup is used to view longer items than the text box is
             * wide.
             */
            int w = WidgetUtil.getRequiredWidth(this);

            if (dataReceivedHandler.isWaitingForInitialData()
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

                int iconWidth = selectedItemIcon == null ? 0
                        : WidgetUtil.getRequiredWidth(selectedItemIcon);
                int buttonWidth = popupOpener == null ? 0
                        : WidgetUtil.getRequiredWidth(popupOpener);

                /*
                 * Instead of setting the width of the wrapper, set the width of
                 * the combobox. Subtract the width of the icon and the
                 * popupopener
                 */

                tb.setWidth(suggestionPopupMinWidth - iconWidth - buttonWidth
                        + "px");
            }

            /*
             * Lock the textbox width to its current value if it's not already
             * locked. This can happen after setWidth("") which resets the
             * textbox width to "100%".
             */
            if (!tb.getElement().getStyle().getWidth().endsWith("px")) {
                int iconWidth = selectedItemIcon == null ? 0
                        : selectedItemIcon.getOffsetWidth();
                tb.setWidth(tb.getOffsetWidth() - iconWidth + "px");
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
        if (!width.isEmpty()) {
            tb.setWidth("100%");
        }
    }

    /**
     * Handles special behavior of the mouse down event.
     *
     * @param event
     */
    private void handleMouseDownEvent(Event event) {
        /*
         * Prevent the keyboard focus from leaving the textfield by preventing
         * the default behavior of the browser. Fixes #4285.
         */
        if (event.getTypeInt() == Event.ONMOUSEDOWN) {
            debug("VComboBox: blocking mouseDown event to avoid blur");

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
                debug("VComboBox: Going to prevent next blur event on IE");
            }
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        debug("VComboBox.onMouseDown(): blocking mouseDown event to avoid blur");

        event.preventDefault();
        event.stopPropagation();

        /*
         * In IE the above wont work, the blur event will still trigger. So, we
         * set a flag here to prevent the next blur event from happening. This
         * is not needed if do not already have focus, in that case there will
         * not be any blur event and we should not cancel the next blur.
         */
        if (BrowserInfo.get().isIE() && focused) {
            preventNextBlurEventInIE = true;
            debug("VComboBox: Going to prevent next blur event on IE");
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        suggestionPopup.hide();
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        String[] parts = subPart.split("/");
        if ("textbox".equals(parts[0])) {
            return tb.getElement();
        } else if ("button".equals(parts[0])) {
            return popupOpener.getElement();
        } else if ("popup".equals(parts[0]) && suggestionPopup.isAttached()) {
            if (parts.length == 2) {
                return suggestionPopup.menu.getSubPartElement(parts[1]);
            }
            return suggestionPopup.getElement();
        }
        return null;
    }

    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
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

    @Override
    public boolean isWorkPending() {
        return dataReceivedHandler.isWaitingForFilteringResponse()
                || suggestionPopup.lazyPageScroller.isRunning();
    }

    /**
     * Sets the caption of selected item, if "scroll to page" is disabled. This
     * method is meant for internal use and may change in future versions.
     *
     * @since 7.7
     * @param selectedCaption
     *            the caption of selected item
     */
    public void setSelectedCaption(String selectedCaption) {
        explicitSelectedCaption = selectedCaption;
        if (selectedCaption != null) {
            setText(selectedCaption);
        }
    }

    /**
     * This method is meant for internal use and may change in future versions.
     *
     * @since 7.7
     * @return the caption of selected item, if "scroll to page" is disabled
     */
    public String getSelectedCaption() {
        return explicitSelectedCaption;
    }

    /**
     * Returns a handler receiving notifications from the connector about
     * communications.
     *
     * @return the dataReceivedHandler
     */
    public DataReceivedHandler getDataReceivedHandler() {
        return dataReceivedHandler;
    }

    /**
     * Sets the number of items to show per page, or 0 for showing all items.
     *
     * @param pageLength
     *            new page length or 0 for all items
     */
    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Sets the suggestion pop-up's width as a CSS string. By using relative
     * units (e.g. "50%") it's possible to set the popup's width relative to the
     * ComboBox itself.
     *
     * @param suggestionPopupWidth
     *            new popup width as CSS string, null for old default width
     *            calculation based on items
     */
    public void setSuggestionPopupWidth(String suggestionPopupWidth) {
        this.suggestionPopupWidth = suggestionPopupWidth;
    }

    /**
     * Sets whether creation of new items when there is no match is allowed or
     * not.
     *
     * @param allowNewItems
     *            true to allow creation of new items, false to only allow
     *            selection of existing items
     */
    public void setAllowNewItems(boolean allowNewItems) {
        this.allowNewItems = allowNewItems;
    }

    /**
     * Sets the total number of suggestions.
     * <p>
     * NOTE: this excluded the possible null selection item!
     * <p>
     * NOTE: this just updates the state, but doesn't update any UI.
     *
     * @since 8.0
     * @param totalSuggestions
     *            total number of suggestions
     */
    public void setTotalSuggestions(int totalSuggestions) {
        this.totalSuggestions = totalSuggestions;
    }

    /**
     * Gets the total number of suggestions, excluding the null selection item.
     *
     * @since 8.0
     * @return total number of suggestions
     */
    public int getTotalSuggestions() {
        return totalSuggestions;
    }

    /**
     * Gets the total number of suggestions, including the possible null
     * selection item, if it should be visible.
     *
     * @return total number of suggestions with null selection items
     */
    private int getTotalSuggestionsIncludingNullSelectionItem() {
        return getTotalSuggestions()
                + (getNullSelectionItemShouldBeVisible() ? 1 : 0);
    }

    /**
     * Returns null selection item should be visible or not.
     * <p>
     * NOTE: this checks for any entered filter value, and whether the feature
     * is enabled
     *
     * @since 8.0
     * @return {@code true} if it should be visible, {@code}
     */
    public boolean getNullSelectionItemShouldBeVisible() {
        return nullSelectionAllowed && "".equals(lastFilter);
    }

    /**
     * Gets the empty selection caption.
     *
     * @since 8.0.7
     * @return the empty selection caption
     */
    public String getEmptySelectionCaption() {
        return emptySelectionCaption;
    }

    /**
     * Sets the empty selection caption for this VComboBox. The text is
     * displayed in the text input when nothing is selected.
     *
     * @param emptySelectionCaption
     *            the empty selection caption
     *
     * @since 8.0.7
     */
    public void setEmptySelectionCaption(String emptySelectionCaption) {
        this.emptySelectionCaption = emptySelectionCaption;
        if (selectedOptionKey == null) {
            setText(emptySelectionCaption);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(VComboBox.class.getName());
    }
}
