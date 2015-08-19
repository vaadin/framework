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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.Focusable;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.VTooltip;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.client.ui.dd.DDUtil;
import com.vaadin.client.ui.dd.VAbstractDropHandler;
import com.vaadin.client.ui.dd.VAcceptCallback;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VHasDropHandler;
import com.vaadin.client.ui.dd.VTransferable;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.table.TableConstants;

/**
 * VScrollTable
 * 
 * VScrollTable is a FlowPanel having two widgets in it: * TableHead component *
 * ScrollPanel
 * 
 * TableHead contains table's header and widgets + logic for resizing,
 * reordering and hiding columns.
 * 
 * ScrollPanel contains VScrollTableBody object which handles content. To save
 * some bandwidth and to improve clients responsiveness with loads of data, in
 * VScrollTableBody all rows are not necessary rendered. There are "spacers" in
 * VScrollTableBody to use the exact same space as non-rendered rows would use.
 * This way we can use seamlessly traditional scrollbars and scrolling to fetch
 * more rows instead of "paging".
 * 
 * In VScrollTable we listen to scroll events. On horizontal scrolling we also
 * update TableHeads scroll position which has its scrollbars hidden. On
 * vertical scroll events we will check if we are reaching the end of area where
 * we have rows rendered and
 * 
 * TODO implement unregistering for child components in Cells
 */
public class VScrollTable extends FlowPanel implements HasWidgets,
        ScrollHandler, VHasDropHandler, FocusHandler, BlurHandler, Focusable,
        ActionOwner, SubPartAware, DeferredWorker {

    /**
     * Simple interface for parts of the table capable of owning a context menu.
     * 
     * @since 7.2
     * @author Vaadin Ltd
     */
    private interface ContextMenuOwner {
        public void showContextMenu(Event event);
    }

    /**
     * Handles showing context menu on "long press" from a touch screen.
     * 
     * @since 7.2
     * @author Vaadin Ltd
     */
    private class TouchContextProvider {
        private static final int TOUCH_CONTEXT_MENU_TIMEOUT = 500;
        private Timer contextTouchTimeout;

        private Event touchStart;
        private int touchStartY;
        private int touchStartX;

        private ContextMenuOwner target;

        /**
         * Initializes a handler for a certain context menu owner.
         * 
         * @param target
         *            the owner of the context menu
         */
        public TouchContextProvider(ContextMenuOwner target) {
            this.target = target;
        }

        /**
         * Cancels the current context touch timeout.
         */
        public void cancel() {
            if (contextTouchTimeout != null) {
                contextTouchTimeout.cancel();
                contextTouchTimeout = null;
            }
            touchStart = null;
        }

        /**
         * A function to handle touch context events in a table.
         * 
         * @param event
         *            browser event to handle
         */
        public void handleTouchEvent(final Event event) {
            int type = event.getTypeInt();

            switch (type) {
            case Event.ONCONTEXTMENU:
                target.showContextMenu(event);
                break;
            case Event.ONTOUCHSTART:
                // save position to fields, touches in events are same
                // instance during the operation.
                touchStart = event;

                Touch touch = event.getChangedTouches().get(0);
                touchStartX = touch.getClientX();
                touchStartY = touch.getClientY();

                if (contextTouchTimeout == null) {
                    contextTouchTimeout = new Timer() {

                        @Override
                        public void run() {
                            if (touchStart != null) {
                                // Open the context menu if finger
                                // is held in place long enough.
                                target.showContextMenu(touchStart);
                                event.preventDefault();
                                touchStart = null;
                            }
                        }
                    };
                }
                contextTouchTimeout.schedule(TOUCH_CONTEXT_MENU_TIMEOUT);
                break;
            case Event.ONTOUCHCANCEL:
            case Event.ONTOUCHEND:
                cancel();
                break;
            case Event.ONTOUCHMOVE:
                if (isSignificantMove(event)) {
                    // Moved finger before the context menu timer
                    // expired, so let the browser handle the event.
                    cancel();
                }
            }
        }

        /**
         * Calculates how many pixels away the user's finger has traveled. This
         * reduces the chance of small non-intentional movements from canceling
         * the long press detection.
         * 
         * @param event
         *            the Event for which to check the move distance
         * @return true if this is considered an intentional move by the user
         */
        protected boolean isSignificantMove(Event event) {
            if (touchStart == null) {
                // no touch start
                return false;
            }

            // Calculate the distance between touch start and the current touch
            // position
            Touch touch = event.getChangedTouches().get(0);
            int deltaX = touch.getClientX() - touchStartX;
            int deltaY = touch.getClientY() - touchStartY;
            int delta = deltaX * deltaX + deltaY * deltaY;

            // Compare to the square of the significant move threshold to remove
            // the need for a square root
            if (delta > TouchScrollDelegate.SIGNIFICANT_MOVE_THRESHOLD
                    * TouchScrollDelegate.SIGNIFICANT_MOVE_THRESHOLD) {
                return true;
            }
            return false;
        }
    }

    public static final String STYLENAME = "v-table";

    public enum SelectMode {
        NONE(0), SINGLE(1), MULTI(2);
        private int id;

        private SelectMode(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static final String ROW_HEADER_COLUMN_KEY = "0";

    private static final double CACHE_RATE_DEFAULT = 2;

    /**
     * The default multi select mode where simple left clicks only selects one
     * item, CTRL+left click selects multiple items and SHIFT-left click selects
     * a range of items.
     */
    private static final int MULTISELECT_MODE_DEFAULT = 0;

    /**
     * The simple multiselect mode is what the table used to have before
     * ctrl/shift selections were added. That is that when this is set clicking
     * on an item selects/deselects the item and no ctrl/shift selections are
     * available.
     */
    private static final int MULTISELECT_MODE_SIMPLE = 1;

    /**
     * multiple of pagelength which component will cache when requesting more
     * rows
     */
    private double cache_rate = CACHE_RATE_DEFAULT;
    /**
     * fraction of pageLength which can be scrolled without making new request
     */
    private double cache_react_rate = 0.75 * cache_rate;

    public static final char ALIGN_CENTER = 'c';
    public static final char ALIGN_LEFT = 'b';
    public static final char ALIGN_RIGHT = 'e';
    private static final int CHARCODE_SPACE = 32;
    private int firstRowInViewPort = 0;
    private int pageLength = 15;
    private int lastRequestedFirstvisible = 0; // to detect "serverside scroll"
    private int firstvisibleOnLastPage = -1; // To detect if the first visible
                                             // is on the last page

    /** For internal use only. May be removed or replaced in the future. */
    public boolean showRowHeaders = false;

    private String[] columnOrder;

    protected ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    private boolean updatedReqRows = true;

    private boolean nullSelectionAllowed = true;

    private SelectMode selectMode = SelectMode.NONE;

    public final HashSet<String> selectedRowKeys = new HashSet<String>();

    /*
     * When scrolling and selecting at the same time, the selections are not in
     * sync with the server while retrieving new rows (until key is released).
     */
    private HashSet<Object> unSyncedselectionsBeforeRowFetch;

    /*
     * These are used when jumping between pages when pressing Home and End
     */

    /** For internal use only. May be removed or replaced in the future. */
    public boolean selectLastItemInNextRender = false;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean selectFirstItemInNextRender = false;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean focusFirstItemInNextRender = false;
    /** For internal use only. May be removed or replaced in the future. */
    public boolean focusLastItemInNextRender = false;

    /**
     * The currently focused row.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public VScrollTableRow focusedRow;

    /**
     * Helper to store selection range start in when using the keyboard
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public VScrollTableRow selectionRangeStart;

    /**
     * Flag for notifying when the selection has changed and should be sent to
     * the server
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean selectionChanged = false;

    /*
     * The speed (in pixels) which the scrolling scrolls vertically/horizontally
     */
    private int scrollingVelocity = 10;

    private Timer scrollingVelocityTimer = null;

    /** For internal use only. May be removed or replaced in the future. */
    public String[] bodyActionKeys;

    private boolean enableDebug = false;

    private static final boolean hasNativeTouchScrolling = BrowserInfo.get()
            .isTouchDevice()
            && !BrowserInfo.get().requiresTouchScrollDelegate();

    private Set<String> noncollapsibleColumns;

    /**
     * The last known row height used to preserve the height of a table with
     * custom row heights and a fixed page length after removing the last row
     * from the table.
     * 
     * A new VScrollTableBody instance is created every time the number of rows
     * changes causing {@link VScrollTableBody#rowHeight} to be discarded and
     * the height recalculated by {@link VScrollTableBody#getRowHeight(boolean)}
     * to avoid some rounding problems, e.g. round(2 * 19.8) / 2 = 20 but
     * round(3 * 19.8) / 3 = 19.66.
     */
    private double lastKnownRowHeight = Double.NaN;

    /**
     * Remember scroll position when getting detached to properly scroll back to
     * the location that there is data for if getting attached again.
     */
    private int detachedScrollPosition = 0;

    /**
     * Represents a select range of rows
     */
    private class SelectionRange {
        private VScrollTableRow startRow;
        private final int length;

        /**
         * Constuctor.
         */
        public SelectionRange(VScrollTableRow row1, VScrollTableRow row2) {
            VScrollTableRow endRow;
            if (row2.isBefore(row1)) {
                startRow = row2;
                endRow = row1;
            } else {
                startRow = row1;
                endRow = row2;
            }
            length = endRow.getIndex() - startRow.getIndex() + 1;
        }

        public SelectionRange(VScrollTableRow row, int length) {
            startRow = row;
            this.length = length;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */

        @Override
        public String toString() {
            return startRow.getKey() + "-" + length;
        }

        private boolean inRange(VScrollTableRow row) {
            return row.getIndex() >= startRow.getIndex()
                    && row.getIndex() < startRow.getIndex() + length;
        }

        public Collection<SelectionRange> split(VScrollTableRow row) {
            assert row.isAttached();
            ArrayList<SelectionRange> ranges = new ArrayList<SelectionRange>(2);

            int endOfFirstRange = row.getIndex() - 1;
            if (endOfFirstRange >= startRow.getIndex()) {
                // create range of first part unless its length is < 1
                ranges.add(new SelectionRange(startRow, endOfFirstRange
                        - startRow.getIndex() + 1));
            }
            int startOfSecondRange = row.getIndex() + 1;
            if (getEndIndex() >= startOfSecondRange) {
                // create range of second part unless its length is < 1
                VScrollTableRow startOfRange = scrollBody
                        .getRowByRowIndex(startOfSecondRange);
                if (startOfRange != null) {
                    ranges.add(new SelectionRange(startOfRange, getEndIndex()
                            - startOfSecondRange + 1));
                }
            }
            return ranges;
        }

        private int getEndIndex() {
            return startRow.getIndex() + length - 1;
        }

    }

    private final HashSet<SelectionRange> selectedRowRanges = new HashSet<SelectionRange>();

    /** For internal use only. May be removed or replaced in the future. */
    public boolean initializedAndAttached = false;

    /**
     * Flag to indicate if a column width recalculation is needed due update.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean headerChangedDuringUpdate = false;

    /** For internal use only. May be removed or replaced in the future. */
    public final TableHead tHead = new TableHead();

    /** For internal use only. May be removed or replaced in the future. */
    public final TableFooter tFoot = new TableFooter();

    /** Handles context menu for table body */
    private ContextMenuOwner contextMenuOwner = new ContextMenuOwner() {

        @Override
        public void showContextMenu(Event event) {
            int left = WidgetUtil.getTouchOrMouseClientX(event);
            int top = WidgetUtil.getTouchOrMouseClientY(event);
            boolean menuShown = handleBodyContextMenu(left, top);
            if (menuShown) {
                event.stopPropagation();
                event.preventDefault();
            }
        }
    };

    /** Handles touch events to display a context menu for table body */
    private TouchContextProvider touchContextProvider = new TouchContextProvider(
            contextMenuOwner);

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * Overwrites onBrowserEvent function on FocusableScrollPanel to give event
     * access to touchContextProvider. Has to be public to give TableConnector
     * access to the scrollBodyPanel field.
     * 
     * @since 7.2
     * @author Vaadin Ltd
     */
    public class FocusableScrollContextPanel extends FocusableScrollPanel {
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            touchContextProvider.handleTouchEvent(event);
        };

        public FocusableScrollContextPanel(boolean useFakeFocusElement) {
            super(useFakeFocusElement);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public final FocusableScrollContextPanel scrollBodyPanel = new FocusableScrollContextPanel(
            true);

    private KeyPressHandler navKeyPressHandler = new KeyPressHandler() {

        @Override
        public void onKeyPress(KeyPressEvent keyPressEvent) {
            // This is used for Firefox only, since Firefox auto-repeat
            // works correctly only if we use a key press handler, other
            // browsers handle it correctly when using a key down handler
            if (!BrowserInfo.get().isGecko()) {
                return;
            }

            NativeEvent event = keyPressEvent.getNativeEvent();
            if (!enabled) {
                // Cancel default keyboard events on a disabled Table
                // (prevents scrolling)
                event.preventDefault();
            } else if (hasFocus) {
                // Key code in Firefox/onKeyPress is present only for
                // special keys, otherwise 0 is returned
                int keyCode = event.getKeyCode();
                if (keyCode == 0 && event.getCharCode() == ' ') {
                    // Provide a keyCode for space to be compatible with
                    // FireFox keypress event
                    keyCode = CHARCODE_SPACE;
                }

                if (handleNavigation(keyCode,
                        event.getCtrlKey() || event.getMetaKey(),
                        event.getShiftKey())) {
                    event.preventDefault();
                }

                startScrollingVelocityTimer();
            }
        }

    };

    private KeyUpHandler navKeyUpHandler = new KeyUpHandler() {

        @Override
        public void onKeyUp(KeyUpEvent keyUpEvent) {
            NativeEvent event = keyUpEvent.getNativeEvent();
            int keyCode = event.getKeyCode();

            if (!isFocusable()) {
                cancelScrollingVelocityTimer();
            } else if (isNavigationKey(keyCode)) {
                if (keyCode == getNavigationDownKey()
                        || keyCode == getNavigationUpKey()) {
                    /*
                     * in multiselect mode the server may still have value from
                     * previous page. Clear it unless doing multiselection or
                     * just moving focus.
                     */
                    if (!event.getShiftKey() && !event.getCtrlKey()) {
                        instructServerToForgetPreviousSelections();
                    }
                    sendSelectedRows();
                }
                cancelScrollingVelocityTimer();
                navKeyDown = false;
            }
        }
    };

    private KeyDownHandler navKeyDownHandler = new KeyDownHandler() {

        @Override
        public void onKeyDown(KeyDownEvent keyDownEvent) {
            NativeEvent event = keyDownEvent.getNativeEvent();
            // This is not used for Firefox
            if (BrowserInfo.get().isGecko()) {
                return;
            }

            if (!enabled) {
                // Cancel default keyboard events on a disabled Table
                // (prevents scrolling)
                event.preventDefault();
            } else if (hasFocus) {
                if (handleNavigation(event.getKeyCode(), event.getCtrlKey()
                        || event.getMetaKey(), event.getShiftKey())) {
                    navKeyDown = true;
                    event.preventDefault();
                }

                startScrollingVelocityTimer();
            }
        }
    };

    /** For internal use only. May be removed or replaced in the future. */
    public int totalRows;

    private Set<String> collapsedColumns;

    /** For internal use only. May be removed or replaced in the future. */
    public final RowRequestHandler rowRequestHandler;

    /** For internal use only. May be removed or replaced in the future. */
    public VScrollTableBody scrollBody;

    private int firstvisible = 0;
    private boolean sortAscending;
    private String sortColumn;
    private String oldSortColumn;
    private boolean columnReordering;

    /**
     * This map contains captions and icon urls for actions like: * "33_c" ->
     * "Edit" * "33_i" -> "http://dom.com/edit.png"
     */
    private final HashMap<Object, String> actionMap = new HashMap<Object, String>();
    private String[] visibleColOrder;
    private boolean initialContentReceived = false;
    private Element scrollPositionElement;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean enabled;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean showColHeaders;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean showColFooters;

    /** flag to indicate that table body has changed */
    private boolean isNewBody = true;

    /**
     * Read from the "recalcWidths" -attribute. When it is true, the table will
     * recalculate the widths for columns - desirable in some cases. For #1983,
     * marked experimental. See also variable <code>refreshContentWidths</code>
     * in method {@link TableHead#updateCellsFromUIDL(UIDL)}.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public boolean recalcWidths = false;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean rendering = false;

    private boolean hasFocus = false;
    private int dragmode;

    private int multiselectmode;

    /** For internal use only. May be removed or replaced in the future. */
    public int tabIndex;

    private TouchScrollDelegate touchScrollDelegate;

    /** For internal use only. May be removed or replaced in the future. */
    public int lastRenderedHeight;

    /**
     * Values (serverCacheFirst+serverCacheLast) sent by server that tells which
     * rows (indexes) are in the server side cache (page buffer). -1 means
     * unknown. The server side cache row MUST MATCH the client side cache rows.
     * 
     * If the client side cache contains additional rows with e.g. buttons, it
     * will cause out of sync when such a button is pressed.
     * 
     * If the server side cache contains additional rows with e.g. buttons,
     * scrolling in the client will cause empty buttons to be rendered
     * (cached=true request for non-existing components)
     * 
     * For internal use only. May be removed or replaced in the future.
     */
    public int serverCacheFirst = -1;
    public int serverCacheLast = -1;

    /**
     * In several cases TreeTable depends on the scrollBody.lastRendered being
     * 'out of sync' while the update is being done. In those cases the sanity
     * check must be performed afterwards.
     */
    public boolean postponeSanityCheckForLastRendered;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean sizeNeedsInit = true;

    /**
     * Used to recall the position of an open context menu if we need to close
     * and reopen it during a row update.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public class ContextMenuDetails implements CloseHandler<PopupPanel> {
        public String rowKey;
        public int left;
        public int top;
        HandlerRegistration closeRegistration;

        public ContextMenuDetails(VContextMenu menu, String rowKey, int left,
                int top) {
            this.rowKey = rowKey;
            this.left = left;
            this.top = top;
            closeRegistration = menu.addCloseHandler(this);
        }

        @Override
        public void onClose(CloseEvent<PopupPanel> event) {
            contextMenu = null;
            closeRegistration.removeHandler();
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public ContextMenuDetails contextMenu = null;

    private boolean hadScrollBars = false;

    private HandlerRegistration addCloseHandler;

    /**
     * Changes to manage mouseDown and mouseUp
     */
    /**
     * The element where the last mouse down event was registered.
     */
    private Element lastMouseDownTarget;

    /**
     * Set to true by {@link #mouseUpPreviewHandler} if it gets a mouseup at the
     * same element as {@link #lastMouseDownTarget}.
     */
    private boolean mouseUpPreviewMatched = false;

    private HandlerRegistration mouseUpEventPreviewRegistration;

    /**
     * Previews events after a mousedown to detect where the following mouseup
     * hits.
     */
    private final NativePreviewHandler mouseUpPreviewHandler = new NativePreviewHandler() {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONMOUSEUP) {
                mouseUpEventPreviewRegistration.removeHandler();

                // Event's reported target not always correct if event
                // capture is in use
                Element elementUnderMouse = WidgetUtil
                        .getElementUnderMouse(event.getNativeEvent());
                if (lastMouseDownTarget != null
                        && lastMouseDownTarget.isOrHasChild(elementUnderMouse)) {
                    mouseUpPreviewMatched = true;
                } else {
                    getLogger().log(
                            Level.FINEST,
                            "Ignoring mouseup from " + elementUnderMouse
                                    + " when mousedown was on "
                                    + lastMouseDownTarget);
                }
            }
        }
    };

    public VScrollTable() {
        setMultiSelectMode(MULTISELECT_MODE_DEFAULT);

        scrollBodyPanel.addFocusHandler(this);
        scrollBodyPanel.addBlurHandler(this);

        scrollBodyPanel.addScrollHandler(this);

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            scrollBodyPanel.addKeyPressHandler(navKeyPressHandler);
        } else {
            scrollBodyPanel.addKeyDownHandler(navKeyDownHandler);
        }
        scrollBodyPanel.addKeyUpHandler(navKeyUpHandler);

        scrollBodyPanel.sinkEvents(Event.TOUCHEVENTS | Event.ONCONTEXTMENU);

        setStyleName(STYLENAME);

        add(tHead);
        add(scrollBodyPanel);
        add(tFoot);

        rowRequestHandler = new RowRequestHandler();
    }

    @Override
    public void setStyleName(String style) {
        updateStyleNames(style, false);
    }

    @Override
    public void setStylePrimaryName(String style) {
        updateStyleNames(style, true);
    }

    private void updateStyleNames(String newStyle, boolean isPrimary) {
        scrollBodyPanel
                .removeStyleName(getStylePrimaryName() + "-body-wrapper");
        scrollBodyPanel.removeStyleName(getStylePrimaryName() + "-body");

        if (scrollBody != null) {
            scrollBody.removeStyleName(getStylePrimaryName()
                    + "-body-noselection");
        }

        if (isPrimary) {
            super.setStylePrimaryName(newStyle);
        } else {
            super.setStyleName(newStyle);
        }

        scrollBodyPanel.addStyleName(getStylePrimaryName() + "-body-wrapper");
        scrollBodyPanel.addStyleName(getStylePrimaryName() + "-body");

        tHead.updateStyleNames(getStylePrimaryName());
        tFoot.updateStyleNames(getStylePrimaryName());

        if (scrollBody != null) {
            scrollBody.updateStyleNames(getStylePrimaryName());
        }
    }

    public void init(ApplicationConnection client) {
        this.client = client;
        // Add a handler to clear saved context menu details when the menu
        // closes. See #8526.
        addCloseHandler = client.getContextMenu().addCloseHandler(
                new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        contextMenu = null;
                    }
                });
    }

    /**
     * Handles a context menu event on table body.
     * 
     * @param left
     *            left position of the context menu
     * @param top
     *            top position of the context menu
     * @return true if a context menu was shown, otherwise false
     */
    private boolean handleBodyContextMenu(int left, int top) {
        if (enabled && bodyActionKeys != null) {
            top += Window.getScrollTop();
            left += Window.getScrollLeft();
            client.getContextMenu().showAt(this, left, top);
            return true;
        }
        return false;
    }

    /**
     * Fires a column resize event which sends the resize information to the
     * server.
     * 
     * @param columnId
     *            The columnId of the column which was resized
     * @param originalWidth
     *            The width in pixels of the column before the resize event
     * @param newWidth
     *            The width in pixels of the column after the resize event
     */
    private void fireColumnResizeEvent(String columnId, int originalWidth,
            int newWidth) {
        client.updateVariable(paintableId, "columnResizeEventColumn", columnId,
                false);
        client.updateVariable(paintableId, "columnResizeEventPrev",
                originalWidth, false);
        client.updateVariable(paintableId, "columnResizeEventCurr", newWidth,
                immediate);

    }

    /**
     * Non-immediate variable update of column widths for a collection of
     * columns.
     * 
     * @param columns
     *            the columns to trigger the events for.
     */
    private void sendColumnWidthUpdates(Collection<HeaderCell> columns) {
        String[] newSizes = new String[columns.size()];
        int ix = 0;
        for (HeaderCell cell : columns) {
            newSizes[ix++] = cell.getColKey() + ":" + cell.getWidth();
        }
        client.updateVariable(paintableId, "columnWidthUpdates", newSizes,
                false);
    }

    /**
     * Moves the focus one step down
     * 
     * @return Returns true if succeeded
     */
    private boolean moveFocusDown() {
        return moveFocusDown(0);
    }

    /**
     * Moves the focus down by 1+offset rows
     * 
     * @return Returns true if succeeded, else false if the selection could not
     *         be move downwards
     */
    private boolean moveFocusDown(int offset) {
        if (isSelectable()) {
            if (focusedRow == null && scrollBody.iterator().hasNext()) {
                // FIXME should focus first visible from top, not first rendered
                // ??
                return setRowFocus((VScrollTableRow) scrollBody.iterator()
                        .next());
            } else {
                VScrollTableRow next = getNextRow(focusedRow, offset);
                if (next != null) {
                    return setRowFocus(next);
                }
            }
        }

        return false;
    }

    /**
     * Moves the selection one step up
     * 
     * @return Returns true if succeeded
     */
    private boolean moveFocusUp() {
        return moveFocusUp(0);
    }

    /**
     * Moves the focus row upwards
     * 
     * @return Returns true if succeeded, else false if the selection could not
     *         be move upwards
     * 
     */
    private boolean moveFocusUp(int offset) {
        if (isSelectable()) {
            if (focusedRow == null && scrollBody.iterator().hasNext()) {
                // FIXME logic is exactly the same as in moveFocusDown, should
                // be the opposite??
                return setRowFocus((VScrollTableRow) scrollBody.iterator()
                        .next());
            } else {
                VScrollTableRow prev = getPreviousRow(focusedRow, offset);
                if (prev != null) {
                    return setRowFocus(prev);
                } else {
                    VConsole.log("no previous available");
                }
            }
        }

        return false;
    }

    /**
     * Selects a row where the current selection head is
     * 
     * @param ctrlSelect
     *            Is the selection a ctrl+selection
     * @param shiftSelect
     *            Is the selection a shift+selection
     * @return Returns truw
     */
    private void selectFocusedRow(boolean ctrlSelect, boolean shiftSelect) {
        if (focusedRow != null) {
            // Arrows moves the selection and clears previous selections
            if (isSelectable() && !ctrlSelect && !shiftSelect) {
                deselectAll();
                focusedRow.toggleSelection();
                selectionRangeStart = focusedRow;
            } else if (isSelectable() && ctrlSelect && !shiftSelect) {
                // Ctrl+arrows moves selection head
                selectionRangeStart = focusedRow;
                // No selection, only selection head is moved
            } else if (isMultiSelectModeAny() && !ctrlSelect && shiftSelect) {
                // Shift+arrows selection selects a range
                focusedRow.toggleShiftSelection(shiftSelect);
            }
        }
    }

    /**
     * Sends the selection to the server if changed since the last update/visit.
     */
    protected void sendSelectedRows() {
        sendSelectedRows(immediate);
    }

    private void updateFirstVisibleAndSendSelectedRows() {
        updateFirstVisibleRow();
        sendSelectedRows(immediate);
    }

    /**
     * Sends the selection to the server if it has been changed since the last
     * update/visit.
     * 
     * @param immediately
     *            set to true to immediately send the rows
     */
    protected void sendSelectedRows(boolean immediately) {
        // Don't send anything if selection has not changed
        if (!selectionChanged) {
            return;
        }

        // Reset selection changed flag
        selectionChanged = false;

        // Note: changing the immediateness of this might require changes to
        // "clickEvent" immediateness also.
        if (isMultiSelectModeDefault()) {
            // Convert ranges to a set of strings
            Set<String> ranges = new HashSet<String>();
            for (SelectionRange range : selectedRowRanges) {
                ranges.add(range.toString());
            }

            // Send the selected row ranges
            client.updateVariable(paintableId, "selectedRanges",
                    ranges.toArray(new String[selectedRowRanges.size()]), false);
            selectedRowRanges.clear();

            // clean selectedRowKeys so that they don't contain excess values
            for (Iterator<String> iterator = selectedRowKeys.iterator(); iterator
                    .hasNext();) {
                String key = iterator.next();
                VScrollTableRow renderedRowByKey = getRenderedRowByKey(key);
                if (renderedRowByKey != null) {
                    for (SelectionRange range : selectedRowRanges) {
                        if (range.inRange(renderedRowByKey)) {
                            iterator.remove();
                        }
                    }
                } else {
                    // orphaned selected key, must be in a range, ignore
                    iterator.remove();
                }

            }
        }

        // Send the selected rows
        client.updateVariable(paintableId, "selected",
                selectedRowKeys.toArray(new String[selectedRowKeys.size()]),
                immediately);

    }

    /**
     * Get the key that moves the selection head upwards. By default it is the
     * up arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationUpKey() {
        return KeyCodes.KEY_UP;
    }

    /**
     * Get the key that moves the selection head downwards. By default it is the
     * down arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationDownKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Get the key that scrolls to the left in the table. By default it is the
     * left arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationLeftKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * Get the key that scroll to the right on the table. By default it is the
     * right arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationRightKey() {
        return KeyCodes.KEY_RIGHT;
    }

    /**
     * Get the key that selects an item in the table. By default it is the space
     * bar key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return
     */
    protected int getNavigationSelectKey() {
        return CHARCODE_SPACE;
    }

    /**
     * Get the key the moves the selection one page up in the table. By default
     * this is the Page Up key but by overriding this you can change the key to
     * whatever you want.
     * 
     * @return
     */
    protected int getNavigationPageUpKey() {
        return KeyCodes.KEY_PAGEUP;
    }

    /**
     * Get the key the moves the selection one page down in the table. By
     * default this is the Page Down key but by overriding this you can change
     * the key to whatever you want.
     * 
     * @return
     */
    protected int getNavigationPageDownKey() {
        return KeyCodes.KEY_PAGEDOWN;
    }

    /**
     * Get the key the moves the selection to the beginning of the table. By
     * default this is the Home key but by overriding this you can change the
     * key to whatever you want.
     * 
     * @return
     */
    protected int getNavigationStartKey() {
        return KeyCodes.KEY_HOME;
    }

    /**
     * Get the key the moves the selection to the end of the table. By default
     * this is the End key but by overriding this you can change the key to
     * whatever you want.
     * 
     * @return
     */
    protected int getNavigationEndKey() {
        return KeyCodes.KEY_END;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void initializeRows(UIDL uidl, UIDL rowData) {
        if (scrollBody != null) {
            scrollBody.removeFromParent();
        }

        // Without this call the scroll position is messed up in IE even after
        // the lazy scroller has set the scroll position to the first visible
        // item
        int pos = scrollBodyPanel.getScrollPosition();

        // Reset first row in view port so client requests correct last row.
        if (pos == 0) {
            firstRowInViewPort = 0;
        }

        scrollBody = createScrollBody();

        scrollBody.renderInitialRows(rowData, uidl.getIntAttribute("firstrow"),
                uidl.getIntAttribute("rows"));
        scrollBodyPanel.add(scrollBody);

        // New body starts scrolled to the left, make sure the header and footer
        // are also scrolled to the left
        tHead.setHorizontalScrollPosition(0);
        tFoot.setHorizontalScrollPosition(0);

        initialContentReceived = true;
        sizeNeedsInit = true;
        scrollBody.restoreRowVisibility();
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateColumnProperties(UIDL uidl) {
        updateColumnOrder(uidl);

        updateCollapsedColumns(uidl);

        UIDL vc = uidl.getChildByTagName("visiblecolumns");
        if (vc != null) {
            tHead.updateCellsFromUIDL(vc);
            tFoot.updateCellsFromUIDL(vc);
        }

        updateHeader(uidl.getStringArrayAttribute("vcolorder"));
        updateFooter(uidl.getStringArrayAttribute("vcolorder"));
        if (uidl.hasVariable("noncollapsiblecolumns")) {
            noncollapsibleColumns = uidl
                    .getStringArrayVariableAsSet("noncollapsiblecolumns");
        }
    }

    private void updateCollapsedColumns(UIDL uidl) {
        if (uidl.hasVariable("collapsedcolumns")) {
            tHead.setColumnCollapsingAllowed(true);
            collapsedColumns = uidl
                    .getStringArrayVariableAsSet("collapsedcolumns");
        } else {
            tHead.setColumnCollapsingAllowed(false);
        }
    }

    private void updateColumnOrder(UIDL uidl) {
        if (uidl.hasVariable("columnorder")) {
            columnReordering = true;
            columnOrder = uidl.getStringArrayVariable("columnorder");
        } else {
            columnReordering = false;
            columnOrder = null;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public boolean selectSelectedRows(UIDL uidl) {
        boolean keyboardSelectionOverRowFetchInProgress = false;

        if (uidl.hasVariable("selected")) {
            final Set<String> selectedKeys = uidl
                    .getStringArrayVariableAsSet("selected");
            // Do not update focus if there is a single selected row
            // that is the same as the previous selection. This prevents
            // unwanted scrolling (#18247).
            boolean rowsUnSelected = removeUnselectedRowKeys(selectedKeys);
            boolean updateFocus = rowsUnSelected || selectedRowKeys.size() == 0
                    || focusedRow == null;
            if (scrollBody != null) {
                Iterator<Widget> iterator = scrollBody.iterator();
                while (iterator.hasNext()) {
                    /*
                     * Make the focus reflect to the server side state unless we
                     * are currently selecting multiple rows with keyboard.
                     */
                    VScrollTableRow row = (VScrollTableRow) iterator.next();
                    boolean selected = selectedKeys.contains(row.getKey());
                    if (!selected
                            && unSyncedselectionsBeforeRowFetch != null
                            && unSyncedselectionsBeforeRowFetch.contains(row
                                    .getKey())) {
                        selected = true;
                        keyboardSelectionOverRowFetchInProgress = true;
                    }
                    if (selected && selectedKeys.size() == 1 && updateFocus) {
                        /*
                         * If a single item is selected, move focus to the
                         * selected row. (#10522)
                         */
                        setRowFocus(row);
                    }

                    if (selected != row.isSelected()) {
                        row.toggleSelection();

                        if (!isSingleSelectMode() && !selected) {
                            // Update selection range in case a row is
                            // unselected from the middle of a range - #8076
                            removeRowFromUnsentSelectionRanges(row);
                        }
                    }
                }

            }
        }
        unSyncedselectionsBeforeRowFetch = null;
        return keyboardSelectionOverRowFetchInProgress;
    }

    private boolean removeUnselectedRowKeys(final Set<String> selectedKeys) {
        List<String> unselectedKeys = new ArrayList<String>(0);
        for (String key : selectedRowKeys) {
            if (!selectedKeys.contains(key)) {
                unselectedKeys.add(key);
            }
        }
        return selectedRowKeys.removeAll(unselectedKeys);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateSortingProperties(UIDL uidl) {
        oldSortColumn = sortColumn;
        if (uidl.hasVariable("sortascending")) {
            sortAscending = uidl.getBooleanVariable("sortascending");
            sortColumn = uidl.getStringVariable("sortcolumn");
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void resizeSortedColumnForSortIndicator() {
        // Force recalculation of the captionContainer element inside the header
        // cell to accomodate for the size of the sort arrow.
        HeaderCell sortedHeader = tHead.getHeaderCell(sortColumn);
        if (sortedHeader != null) {
            // Mark header as sorted now. Any earlier marking would lead to
            // columns with wrong sizes
            sortedHeader.setSorted(true);
            tHead.resizeCaptionContainer(sortedHeader);
        }
        // Also recalculate the width of the captionContainer element in the
        // previously sorted header, since this now has more room.
        HeaderCell oldSortedHeader = tHead.getHeaderCell(oldSortColumn);
        if (oldSortedHeader != null) {
            tHead.resizeCaptionContainer(oldSortedHeader);
        }
    }

    private boolean lazyScrollerIsActive;

    private void disableLazyScroller() {
        lazyScrollerIsActive = false;
        scrollBodyPanel.getElement().getStyle().clearOverflowX();
        scrollBodyPanel.getElement().getStyle().clearOverflowY();
    }

    private void enableLazyScroller() {
        Scheduler.get().scheduleDeferred(lazyScroller);
        lazyScrollerIsActive = true;
        // prevent scrolling to jump in IE11
        scrollBodyPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
        scrollBodyPanel.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
    }

    private boolean isLazyScrollerActive() {
        return lazyScrollerIsActive;
    }

    private ScheduledCommand lazyScroller = new ScheduledCommand() {

        @Override
        public void execute() {
            if (firstvisible >= 0) {
                firstRowInViewPort = firstvisible;
                if (firstvisibleOnLastPage > -1) {
                    scrollBodyPanel
                            .setScrollPosition(measureRowHeightOffset(firstvisibleOnLastPage));
                } else {
                    scrollBodyPanel
                            .setScrollPosition(measureRowHeightOffset(firstvisible));
                }
            }
            disableLazyScroller();
        }
    };

    /** For internal use only. May be removed or replaced in the future. */
    public void updateFirstVisibleAndScrollIfNeeded(UIDL uidl) {
        firstvisible = uidl.hasVariable("firstvisible") ? uidl
                .getIntVariable("firstvisible") : 0;
        firstvisibleOnLastPage = uidl.hasVariable("firstvisibleonlastpage") ? uidl
                .getIntVariable("firstvisibleonlastpage") : -1;
        if (firstvisible != lastRequestedFirstvisible && scrollBody != null) {

            // Update lastRequestedFirstvisible right away here
            // (don't rely on update in the timer which could be cancelled).
            lastRequestedFirstvisible = firstRowInViewPort;

            // Only scroll if the first visible changes from the server side.
            // Else we might unintentionally scroll even when the scroll
            // position has not changed.
            enableLazyScroller();
        }
    }

    protected int measureRowHeightOffset(int rowIx) {
        return (int) (rowIx * scrollBody.getRowHeight());
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updatePageLength(UIDL uidl) {
        int oldPageLength = pageLength;
        if (uidl.hasAttribute("pagelength")) {
            pageLength = uidl.getIntAttribute("pagelength");
        } else {
            // pagelength is "0" meaning scrolling is turned off
            pageLength = totalRows;
        }

        if (oldPageLength != pageLength && initializedAndAttached) {
            // page length changed, need to update size
            sizeNeedsInit = true;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateSelectionProperties(UIDL uidl,
            AbstractComponentState state, boolean readOnly) {
        setMultiSelectMode(uidl.hasAttribute("multiselectmode") ? uidl
                .getIntAttribute("multiselectmode") : MULTISELECT_MODE_DEFAULT);

        nullSelectionAllowed = uidl.hasAttribute("nsa") ? uidl
                .getBooleanAttribute("nsa") : true;

        if (uidl.hasAttribute("selectmode")) {
            if (readOnly) {
                selectMode = SelectMode.NONE;
            } else if (uidl.getStringAttribute("selectmode").equals("multi")) {
                selectMode = SelectMode.MULTI;
            } else if (uidl.getStringAttribute("selectmode").equals("single")) {
                selectMode = SelectMode.SINGLE;
            } else {
                selectMode = SelectMode.NONE;
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateDragMode(UIDL uidl) {
        dragmode = uidl.hasAttribute("dragmode") ? uidl
                .getIntAttribute("dragmode") : 0;
        if (BrowserInfo.get().isIE()) {
            if (dragmode > 0) {
                getElement().setPropertyJSO("onselectstart",
                        getPreventTextSelectionIEHack());
            } else {
                getElement().setPropertyJSO("onselectstart", null);
            }
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateTotalRows(UIDL uidl) {
        int newTotalRows = uidl.getIntAttribute("totalrows");
        if (newTotalRows != getTotalRows()) {
            if (scrollBody != null) {
                if (getTotalRows() == 0) {
                    tHead.clear();
                    tFoot.clear();
                }
                initializedAndAttached = false;
                initialContentReceived = false;
                isNewBody = true;
            }
            setTotalRows(newTotalRows);
        }
    }

    protected void setTotalRows(int newTotalRows) {
        totalRows = newTotalRows;
    }

    public int getTotalRows() {
        return totalRows;
    }

    /**
     * Returns the extra space that is given to the header column when column
     * width is determined by header text.
     * 
     * @return extra space in pixels
     */
    private int getHeaderPadding() {
        return scrollBody.getCellExtraWidth();
    }

    /**
     * This method exists for the needs of {@link VTreeTable} only. Not part of
     * the official API, <b>extend at your own risk</b>. May be removed or
     * replaced in the future.
     * 
     * @return index of TreeTable's hierarchy column, or -1 if not applicable
     */
    protected int getHierarchyColumnIndex() {
        return -1;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     */
    public void updateMaxIndent() {
        int oldIndent = scrollBody.getMaxIndent();
        scrollBody.calculateMaxIndent();
        if (oldIndent != scrollBody.getMaxIndent()) {
            // indent updated, headers might need adjusting
            triggerLazyColumnAdjustment(true);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void focusRowFromBody() {
        if (selectedRowKeys.size() == 1) {
            // try to focus a row currently selected and in viewport
            String selectedRowKey = selectedRowKeys.iterator().next();
            if (selectedRowKey != null) {
                VScrollTableRow renderedRow = getRenderedRowByKey(selectedRowKey);
                if (renderedRow == null || !renderedRow.isInViewPort()) {
                    setRowFocus(scrollBody.getRowByRowIndex(firstRowInViewPort));
                } else {
                    setRowFocus(renderedRow);
                }
            }
        } else {
            // multiselect mode
            setRowFocus(scrollBody.getRowByRowIndex(firstRowInViewPort));
        }
    }

    protected VScrollTableBody createScrollBody() {
        return new VScrollTableBody();
    }

    /**
     * Selects the last row visible in the table
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param focusOnly
     *            Should the focus only be moved to the last row
     */
    public void selectLastRenderedRowInViewPort(boolean focusOnly) {
        int index = firstRowInViewPort + getFullyVisibleRowCount();
        VScrollTableRow lastRowInViewport = scrollBody.getRowByRowIndex(index);
        if (lastRowInViewport == null) {
            // this should not happen in normal situations (white space at the
            // end of viewport). Select the last rendered as a fallback.
            lastRowInViewport = scrollBody.getRowByRowIndex(scrollBody
                    .getLastRendered());
            if (lastRowInViewport == null) {
                return; // empty table
            }
        }
        setRowFocus(lastRowInViewport);
        if (!focusOnly) {
            selectFocusedRow(false, multiselectPending);
            sendSelectedRows();
        }
    }

    /**
     * Selects the first row visible in the table
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param focusOnly
     *            Should the focus only be moved to the first row
     */
    public void selectFirstRenderedRowInViewPort(boolean focusOnly) {
        int index = firstRowInViewPort;
        VScrollTableRow firstInViewport = scrollBody.getRowByRowIndex(index);
        if (firstInViewport == null) {
            // this should not happen in normal situations
            return;
        }
        setRowFocus(firstInViewport);
        if (!focusOnly) {
            selectFocusedRow(false, multiselectPending);
            sendSelectedRows();
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setCacheRateFromUIDL(UIDL uidl) {
        setCacheRate(uidl.hasAttribute("cr") ? uidl.getDoubleAttribute("cr")
                : CACHE_RATE_DEFAULT);
    }

    private void setCacheRate(double d) {
        if (cache_rate != d) {
            cache_rate = d;
            cache_react_rate = 0.75 * d;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateActionMap(UIDL mainUidl) {
        UIDL actionsUidl = mainUidl.getChildByTagName("actions");
        if (actionsUidl == null) {
            return;
        }

        final Iterator<?> it = actionsUidl.getChildIterator();
        while (it.hasNext()) {
            final UIDL action = (UIDL) it.next();
            final String key = action.getStringAttribute("key");
            final String caption = action.getStringAttribute("caption");
            actionMap.put(key + "_c", caption);
            if (action.hasAttribute("icon")) {
                // TODO need some uri handling ??
                actionMap.put(key + "_i", action.getStringAttribute("icon"));
            } else {
                actionMap.remove(key + "_i");
            }
        }

    }

    public String getActionCaption(String actionKey) {
        return actionMap.get(actionKey + "_c");
    }

    public String getActionIcon(String actionKey) {
        return client.translateVaadinUri(actionMap.get(actionKey + "_i"));
    }

    private void updateHeader(String[] strings) {
        if (strings == null) {
            return;
        }

        int visibleCols = strings.length;
        int colIndex = 0;
        if (showRowHeaders) {
            tHead.enableColumn(ROW_HEADER_COLUMN_KEY, colIndex);
            visibleCols++;
            visibleColOrder = new String[visibleCols];
            visibleColOrder[colIndex] = ROW_HEADER_COLUMN_KEY;
            colIndex++;
        } else {
            visibleColOrder = new String[visibleCols];
            tHead.removeCell(ROW_HEADER_COLUMN_KEY);
        }

        int i;
        for (i = 0; i < strings.length; i++) {
            final String cid = strings[i];
            visibleColOrder[colIndex] = cid;
            tHead.enableColumn(cid, colIndex);
            colIndex++;
        }

        tHead.setVisible(showColHeaders);
        setContainerHeight();

    }

    /**
     * Updates footers.
     * <p>
     * Update headers whould be called before this method is called!
     * </p>
     * 
     * @param strings
     */
    private void updateFooter(String[] strings) {
        if (strings == null) {
            return;
        }

        // Add dummy column if row headers are present
        int colIndex = 0;
        if (showRowHeaders) {
            tFoot.enableColumn(ROW_HEADER_COLUMN_KEY, colIndex);
            colIndex++;
        } else {
            tFoot.removeCell(ROW_HEADER_COLUMN_KEY);
        }

        int i;
        for (i = 0; i < strings.length; i++) {
            final String cid = strings[i];
            tFoot.enableColumn(cid, colIndex);
            colIndex++;
        }

        tFoot.setVisible(showColFooters);
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param uidl
     *            which contains row data
     * @param firstRow
     *            first row in data set
     * @param reqRows
     *            amount of rows in data set
     */
    public void updateBody(UIDL uidl, int firstRow, int reqRows) {
        int oldIndent = scrollBody.getMaxIndent();
        if (uidl == null || reqRows < 1) {
            // container is empty, remove possibly existing rows
            if (firstRow <= 0) {
                postponeSanityCheckForLastRendered = true;
                while (scrollBody.getLastRendered() > scrollBody
                        .getFirstRendered()) {
                    scrollBody.unlinkRow(false);
                }
                postponeSanityCheckForLastRendered = false;
                scrollBody.unlinkRow(false);
            }
            return;
        }

        scrollBody.renderRows(uidl, firstRow, reqRows);

        discardRowsOutsideCacheWindow();
        scrollBody.calculateMaxIndent();
        if (oldIndent != scrollBody.getMaxIndent()) {
            // indent updated, headers might need adjusting
            headerChangedDuringUpdate = true;
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateRowsInBody(UIDL partialRowUpdates) {
        if (partialRowUpdates == null) {
            return;
        }
        int firstRowIx = partialRowUpdates.getIntAttribute("firsturowix");
        int count = partialRowUpdates.getIntAttribute("numurows");
        scrollBody.unlinkRows(firstRowIx, count);
        scrollBody.insertRows(partialRowUpdates, firstRowIx, count);
    }

    /**
     * Updates the internal cache by unlinking rows that fall outside of the
     * caching window.
     */
    protected void discardRowsOutsideCacheWindow() {
        int firstRowToKeep = (int) (firstRowInViewPort - pageLength
                * cache_rate);
        int lastRowToKeep = (int) (firstRowInViewPort + pageLength + pageLength
                * cache_rate);
        // sanity checks:
        if (firstRowToKeep < 0) {
            firstRowToKeep = 0;
        }
        if (lastRowToKeep > totalRows) {
            lastRowToKeep = totalRows - 1;
        }
        debug("Client side calculated cache rows to keep: " + firstRowToKeep
                + "-" + lastRowToKeep);

        if (serverCacheFirst != -1) {
            firstRowToKeep = serverCacheFirst;
            lastRowToKeep = serverCacheLast;
            debug("Server cache rows that override: " + serverCacheFirst + "-"
                    + serverCacheLast);
            if (firstRowToKeep < scrollBody.getFirstRendered()
                    || lastRowToKeep > scrollBody.getLastRendered()) {
                debug("*** Server wants us to keep " + serverCacheFirst + "-"
                        + serverCacheLast + " but we only have rows "
                        + scrollBody.getFirstRendered() + "-"
                        + scrollBody.getLastRendered() + " rendered!");
            }
        }
        discardRowsOutsideOf(firstRowToKeep, lastRowToKeep);

        scrollBody.fixSpacers();

        scrollBody.restoreRowVisibility();
    }

    private void discardRowsOutsideOf(int optimalFirstRow, int optimalLastRow) {
        /*
         * firstDiscarded and lastDiscarded are only calculated for debug
         * purposes
         */
        int firstDiscarded = -1, lastDiscarded = -1;
        boolean cont = true;
        while (cont && scrollBody.getLastRendered() > optimalFirstRow
                && scrollBody.getFirstRendered() < optimalFirstRow) {
            if (firstDiscarded == -1) {
                firstDiscarded = scrollBody.getFirstRendered();
            }

            // removing row from start
            cont = scrollBody.unlinkRow(true);
        }
        if (firstDiscarded != -1) {
            lastDiscarded = scrollBody.getFirstRendered() - 1;
            debug("Discarded rows " + firstDiscarded + "-" + lastDiscarded);
        }
        firstDiscarded = lastDiscarded = -1;

        cont = true;
        while (cont && scrollBody.getLastRendered() > optimalLastRow) {
            if (lastDiscarded == -1) {
                lastDiscarded = scrollBody.getLastRendered();
            }

            // removing row from the end
            cont = scrollBody.unlinkRow(false);
        }
        if (lastDiscarded != -1) {
            firstDiscarded = scrollBody.getLastRendered() + 1;
            debug("Discarded rows " + firstDiscarded + "-" + lastDiscarded);
        }

        debug("Now in cache: " + scrollBody.getFirstRendered() + "-"
                + scrollBody.getLastRendered());
    }

    /**
     * Inserts rows in the table body or removes them from the table body based
     * on the commands in the UIDL.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     * 
     * @param partialRowAdditions
     *            the UIDL containing row updates.
     */
    public void addAndRemoveRows(UIDL partialRowAdditions) {
        if (partialRowAdditions == null) {
            return;
        }
        if (partialRowAdditions.hasAttribute("hide")) {
            scrollBody.unlinkAndReindexRows(
                    partialRowAdditions.getIntAttribute("firstprowix"),
                    partialRowAdditions.getIntAttribute("numprows"));
            scrollBody.ensureCacheFilled();
        } else {
            if (partialRowAdditions.hasAttribute("delbelow")) {
                scrollBody.insertRowsDeleteBelow(partialRowAdditions,
                        partialRowAdditions.getIntAttribute("firstprowix"),
                        partialRowAdditions.getIntAttribute("numprows"));
            } else {
                scrollBody.insertAndReindexRows(partialRowAdditions,
                        partialRowAdditions.getIntAttribute("firstprowix"),
                        partialRowAdditions.getIntAttribute("numprows"));
            }
        }

        discardRowsOutsideCacheWindow();
    }

    /**
     * Gives correct column index for given column key ("cid" in UIDL).
     * 
     * @param colKey
     * @return column index of visible columns, -1 if column not visible
     */
    private int getColIndexByKey(String colKey) {
        // return 0 if asked for rowHeaders
        if (ROW_HEADER_COLUMN_KEY.equals(colKey)) {
            return 0;
        }
        for (int i = 0; i < visibleColOrder.length; i++) {
            if (visibleColOrder[i].equals(colKey)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isMultiSelectModeSimple() {
        return selectMode == SelectMode.MULTI
                && multiselectmode == MULTISELECT_MODE_SIMPLE;
    }

    private boolean isSingleSelectMode() {
        return selectMode == SelectMode.SINGLE;
    }

    private boolean isMultiSelectModeAny() {
        return selectMode == SelectMode.MULTI;
    }

    private boolean isMultiSelectModeDefault() {
        return selectMode == SelectMode.MULTI
                && multiselectmode == MULTISELECT_MODE_DEFAULT;
    }

    private void setMultiSelectMode(int multiselectmode) {
        if (BrowserInfo.get().isTouchDevice()) {
            // Always use the simple mode for touch devices that do not have
            // shift/ctrl keys
            this.multiselectmode = MULTISELECT_MODE_SIMPLE;
        } else {
            this.multiselectmode = multiselectmode;
        }

    }

    /** For internal use only. May be removed or replaced in the future. */
    public boolean isSelectable() {
        return selectMode.getId() > SelectMode.NONE.getId();
    }

    private boolean isCollapsedColumn(String colKey) {
        if (collapsedColumns == null) {
            return false;
        }
        if (collapsedColumns.contains(colKey)) {
            return true;
        }
        return false;
    }

    private String getColKeyByIndex(int index) {
        return tHead.getHeaderCell(index).getColKey();
    }

    /**
     * Note: not part of the official API, extend at your own risk. May be
     * removed or replaced in the future.
     * 
     * Sets the indicated column's width for headers and scrollBody alike.
     * 
     * @param colIndex
     *            index of the modified column
     * @param w
     *            new width (may be subject to modifications if doesn't meet
     *            minimum requirements)
     * @param isDefinedWidth
     *            disables expand ratio if set true
     */
    protected void setColWidth(int colIndex, int w, boolean isDefinedWidth) {
        final HeaderCell hcell = tHead.getHeaderCell(colIndex);

        // Make sure that the column grows to accommodate the sort indicator if
        // necessary.
        // get min width with no indent or padding
        int minWidth = hcell.getMinWidth(false, false);
        if (w < minWidth) {
            w = minWidth;
        }

        // Set header column width WITHOUT INDENT
        hcell.setWidth(w, isDefinedWidth);

        // Set footer column width likewise
        FooterCell fcell = tFoot.getFooterCell(colIndex);
        fcell.setWidth(w, isDefinedWidth);

        // Ensure indicators have been taken into account
        tHead.resizeCaptionContainer(hcell);

        // Make sure that the body column grows to accommodate the indent if
        // necessary.
        // get min width with indent, no padding
        minWidth = hcell.getMinWidth(true, false);
        if (w < minWidth) {
            w = minWidth;
        }

        // Set body column width
        scrollBody.setColWidth(colIndex, w);
    }

    private int getColWidth(String colKey) {
        return tHead.getHeaderCell(colKey).getWidthWithIndent();
    }

    /**
     * Get a rendered row by its key
     * 
     * @param key
     *            The key to search with
     * @return
     */
    public VScrollTableRow getRenderedRowByKey(String key) {
        if (scrollBody != null) {
            final Iterator<Widget> it = scrollBody.iterator();
            VScrollTableRow r = null;
            while (it.hasNext()) {
                r = (VScrollTableRow) it.next();
                if (r.getKey().equals(key)) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Returns the next row to the given row
     * 
     * @param row
     *            The row to calculate from
     * 
     * @return The next row or null if no row exists
     */
    private VScrollTableRow getNextRow(VScrollTableRow row, int offset) {
        final Iterator<Widget> it = scrollBody.iterator();
        VScrollTableRow r = null;
        while (it.hasNext()) {
            r = (VScrollTableRow) it.next();
            if (r == row) {
                r = null;
                while (offset >= 0 && it.hasNext()) {
                    r = (VScrollTableRow) it.next();
                    offset--;
                }
                return r;
            }
        }

        return null;
    }

    /**
     * Returns the previous row from the given row
     * 
     * @param row
     *            The row to calculate from
     * @return The previous row or null if no row exists
     */
    private VScrollTableRow getPreviousRow(VScrollTableRow row, int offset) {
        final Iterator<Widget> it = scrollBody.iterator();
        final Iterator<Widget> offsetIt = scrollBody.iterator();
        VScrollTableRow r = null;
        VScrollTableRow prev = null;
        while (it.hasNext()) {
            r = (VScrollTableRow) it.next();
            if (offset < 0) {
                prev = (VScrollTableRow) offsetIt.next();
            }
            if (r == row) {
                return prev;
            }
            offset--;
        }

        return null;
    }

    protected void reOrderColumn(String columnKey, int newIndex) {

        final int oldIndex = getColIndexByKey(columnKey);

        // Change header order
        tHead.moveCell(oldIndex, newIndex);

        // Change body order
        scrollBody.moveCol(oldIndex, newIndex);

        // Change footer order
        tFoot.moveCell(oldIndex, newIndex);

        /*
         * Build new columnOrder and update it to server Note that columnOrder
         * also contains collapsed columns so we cannot directly build it from
         * cells vector Loop the old columnOrder and append in order to new
         * array unless on moved columnKey. On new index also put the moved key
         * i == index on columnOrder, j == index on newOrder
         */
        final String oldKeyOnNewIndex = visibleColOrder[newIndex];
        if (showRowHeaders) {
            newIndex--; // columnOrder don't have rowHeader
        }
        // add back hidden rows,
        for (int i = 0; i < columnOrder.length; i++) {
            if (columnOrder[i].equals(oldKeyOnNewIndex)) {
                break; // break loop at target
            }
            if (isCollapsedColumn(columnOrder[i])) {
                newIndex++;
            }
        }
        // finally we can build the new columnOrder for server
        final String[] newOrder = new String[columnOrder.length];
        for (int i = 0, j = 0; j < newOrder.length; i++) {
            if (j == newIndex) {
                newOrder[j] = columnKey;
                j++;
            }
            if (i == columnOrder.length) {
                break;
            }
            if (columnOrder[i].equals(columnKey)) {
                continue;
            }
            newOrder[j] = columnOrder[i];
            j++;
        }
        columnOrder = newOrder;
        // also update visibleColumnOrder
        int i = showRowHeaders ? 1 : 0;
        for (int j = 0; j < newOrder.length; j++) {
            final String cid = newOrder[j];
            if (!isCollapsedColumn(cid)) {
                visibleColOrder[i++] = cid;
            }
        }
        client.updateVariable(paintableId, "columnorder", columnOrder, false);
        if (client.hasEventListeners(this,
                TableConstants.COLUMN_REORDER_EVENT_ID)) {
            client.sendPendingVariableChanges();
        }
    }

    @Override
    protected void onDetach() {
        detachedScrollPosition = scrollBodyPanel.getScrollPosition();
        rowRequestHandler.cancel();
        super.onDetach();
        // ensure that scrollPosElement will be detached
        if (scrollPositionElement != null) {
            final Element parent = DOM.getParent(scrollPositionElement);
            if (parent != null) {
                DOM.removeChild(parent, scrollPositionElement);
            }
        }
    }

    @Override
    public void onAttach() {
        super.onAttach();
        scrollBodyPanel.setScrollPosition(detachedScrollPosition);
    }

    /**
     * Run only once when component is attached and received its initial
     * content. This function:
     * 
     * * Syncs headers and bodys "natural widths and saves the values.
     * 
     * * Sets proper width and height
     * 
     * * Makes deferred request to get some cache rows
     * 
     * For internal use only. May be removed or replaced in the future.
     */
    public void sizeInit() {
        sizeNeedsInit = false;

        scrollBody.setContainerHeight();

        /*
         * We will use browsers table rendering algorithm to find proper column
         * widths. If content and header take less space than available, we will
         * divide extra space relatively to each column which has not width set.
         * 
         * Overflow pixels are added to last column.
         */

        Iterator<Widget> headCells = tHead.iterator();
        Iterator<Widget> footCells = tFoot.iterator();
        int i = 0;
        int totalExplicitColumnsWidths = 0;
        int total = 0;
        float expandRatioDivider = 0;

        final int[] widths = new int[tHead.visibleCells.size()];

        tHead.enableBrowserIntelligence();
        tFoot.enableBrowserIntelligence();

        int hierarchyColumnIndent = scrollBody != null ? scrollBody
                .getMaxIndent() : 0;
        HeaderCell hierarchyHeaderWithExpandRatio = null;

        // first loop: collect natural widths
        while (headCells.hasNext()) {
            final HeaderCell hCell = (HeaderCell) headCells.next();
            final FooterCell fCell = (FooterCell) footCells.next();
            boolean needsIndent = hierarchyColumnIndent > 0
                    && hCell.isHierarchyColumn();
            hCell.saveNaturalColumnWidthIfNotSaved(i);
            fCell.saveNaturalColumnWidthIfNotSaved(i);
            int w = hCell.getWidth();
            if (hCell.isDefinedWidth()) {
                // server has defined column width explicitly
                if (needsIndent && w < hierarchyColumnIndent) {
                    // hierarchy indent overrides explicitly set width
                    w = hierarchyColumnIndent;
                }
                totalExplicitColumnsWidths += w;
            } else {
                if (hCell.getExpandRatio() > 0) {
                    expandRatioDivider += hCell.getExpandRatio();
                    w = 0;
                    if (needsIndent && w < hierarchyColumnIndent) {
                        hierarchyHeaderWithExpandRatio = hCell;
                        // don't add to widths here, because will be included in
                        // the expand ratio space if there's enough of it
                    }
                } else {
                    // get and store greater of header width and column width,
                    // and store it as a minimum natural column width (these
                    // already contain the indent if any)
                    int headerWidth = hCell.getNaturalColumnWidth(i);
                    int footerWidth = fCell.getNaturalColumnWidth(i);
                    w = headerWidth > footerWidth ? headerWidth : footerWidth;
                }
                if (w != 0) {
                    hCell.setNaturalMinimumColumnWidth(w);
                    fCell.setNaturalMinimumColumnWidth(w);
                }
            }
            widths[i] = w;
            total += w;
            i++;
        }
        if (hierarchyHeaderWithExpandRatio != null) {
            total += hierarchyColumnIndent;
        }

        tHead.disableBrowserIntelligence();
        tFoot.disableBrowserIntelligence();

        boolean willHaveScrollbarz = willHaveScrollbars();

        // fix "natural" width if width not set
        if (isDynamicWidth()) {
            int w = total;
            w += scrollBody.getCellExtraWidth() * visibleColOrder.length;
            if (willHaveScrollbarz) {
                w += WidgetUtil.getNativeScrollbarSize();
            }
            setContentWidth(w);
        }

        int availW = scrollBody.getAvailableWidth();
        if (BrowserInfo.get().isIE()) {
            // Hey IE, are you really sure about this?
            availW = scrollBody.getAvailableWidth();
        }
        availW -= scrollBody.getCellExtraWidth() * visibleColOrder.length;

        if (willHaveScrollbarz) {
            availW -= WidgetUtil.getNativeScrollbarSize();
        }

        // TODO refactor this code to be the same as in resize timer

        if (availW > total) {
            // natural size is smaller than available space
            int extraSpace = availW - total;
            if (hierarchyHeaderWithExpandRatio != null) {
                /*
                 * add the indent's space back to ensure each column gets an
                 * even share according to the expand ratios (note: if the
                 * allocated space isn't enough for the hierarchy column it
                 * shall be treated like a defined width column and the indent
                 * space gets removed from the extra space again)
                 */
                extraSpace += hierarchyColumnIndent;
            }
            final int totalWidthR = total - totalExplicitColumnsWidths;
            int checksum = 0;

            if (extraSpace == 1) {
                // We cannot divide one single pixel so we give it the first
                // undefined column
                // no need to worry about indent here
                headCells = tHead.iterator();
                i = 0;
                checksum = availW;
                while (headCells.hasNext()) {
                    HeaderCell hc = (HeaderCell) headCells.next();
                    if (!hc.isDefinedWidth()) {
                        widths[i]++;
                        break;
                    }
                    i++;
                }

            } else if (expandRatioDivider > 0) {
                boolean setIndentToHierarchyHeader = false;
                if (hierarchyHeaderWithExpandRatio != null) {
                    // ensure first that the hierarchyColumn gets at least the
                    // space allocated for indent
                    final int newSpace = Math
                            .round((extraSpace * (hierarchyHeaderWithExpandRatio
                                    .getExpandRatio() / expandRatioDivider)));
                    if (newSpace < hierarchyColumnIndent) {
                        // not enough space for indent, remove indent from the
                        // extraSpace again and handle hierarchy column's header
                        // separately
                        setIndentToHierarchyHeader = true;
                        extraSpace -= hierarchyColumnIndent;
                    }
                }

                // visible columns have some active expand ratios, excess
                // space is divided according to them
                headCells = tHead.iterator();
                i = 0;
                while (headCells.hasNext()) {
                    HeaderCell hCell = (HeaderCell) headCells.next();
                    if (hCell.getExpandRatio() > 0) {
                        int w = widths[i];
                        if (setIndentToHierarchyHeader
                                && hierarchyHeaderWithExpandRatio.equals(hCell)) {
                            // hierarchy column's header is no longer part of
                            // the expansion divide and only gets indent
                            w += hierarchyColumnIndent;
                        } else {
                            final int newSpace = Math
                                    .round((extraSpace * (hCell
                                            .getExpandRatio() / expandRatioDivider)));
                            w += newSpace;
                        }
                        widths[i] = w;
                    }
                    checksum += widths[i];
                    i++;
                }
            } else if (totalWidthR > 0) {
                // no expand ratios defined, we will share extra space
                // relatively to "natural widths" among those without
                // explicit width
                // no need to worry about indent here, it's already included
                headCells = tHead.iterator();
                i = 0;
                while (headCells.hasNext()) {
                    HeaderCell hCell = (HeaderCell) headCells.next();
                    if (!hCell.isDefinedWidth()) {
                        int w = widths[i];
                        final int newSpace = Math.round((float) extraSpace
                                * (float) w / totalWidthR);
                        w += newSpace;
                        widths[i] = w;
                    }
                    checksum += widths[i];
                    i++;
                }
            }

            if (extraSpace > 0 && checksum != availW) {
                /*
                 * There might be in some cases a rounding error of 1px when
                 * extra space is divided so if there is one then we give the
                 * first undefined column 1 more pixel
                 */
                headCells = tHead.iterator();
                i = 0;
                while (headCells.hasNext()) {
                    HeaderCell hc = (HeaderCell) headCells.next();
                    if (!hc.isDefinedWidth()) {
                        widths[i] += availW - checksum;
                        break;
                    }
                    i++;
                }
            }

        } else {
            // body's size will be more than available and scrollbar will appear
        }

        // last loop: set possibly modified values or reset if new tBody
        i = 0;
        headCells = tHead.iterator();
        while (headCells.hasNext()) {
            final HeaderCell hCell = (HeaderCell) headCells.next();
            if (isNewBody || hCell.getWidth() == -1) {
                final int w = widths[i];
                setColWidth(i, w, false);
            }
            i++;
        }

        initializedAndAttached = true;

        updatePageLength();

        /*
         * Fix "natural" height if height is not set. This must be after width
         * fixing so the components' widths have been adjusted.
         */
        if (isDynamicHeight()) {
            /*
             * We must force an update of the row height as this point as it
             * might have been (incorrectly) calculated earlier
             */

            /*
             * TreeTable updates stuff in a funky order, so we must set the
             * height as zero here before doing the real update to make it
             * realize that there is no content,
             */
            if (pageLength == totalRows && pageLength == 0) {
                scrollBody.setHeight("0px");
            }

            int bodyHeight;
            if (pageLength == totalRows) {
                /*
                 * A hack to support variable height rows when paging is off.
                 * Generally this is not supported by scrolltable. We want to
                 * show all rows so the bodyHeight should be equal to the table
                 * height.
                 */
                // int bodyHeight = scrollBody.getOffsetHeight();
                bodyHeight = scrollBody.getRequiredHeight();
            } else {
                bodyHeight = (int) Math.round(scrollBody.getRowHeight(true)
                        * pageLength);
            }
            boolean needsSpaceForHorizontalSrollbar = (total > availW);
            if (needsSpaceForHorizontalSrollbar) {
                bodyHeight += WidgetUtil.getNativeScrollbarSize();
            }
            scrollBodyPanel.setHeight(bodyHeight + "px");
            WidgetUtil.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
        }

        isNewBody = false;

        if (firstvisible > 0) {
            enableLazyScroller();
        }

        if (enabled) {
            // Do we need cache rows
            if (scrollBody.getLastRendered() + 1 < firstRowInViewPort
                    + pageLength + (int) cache_react_rate * pageLength) {
                if (totalRows - 1 > scrollBody.getLastRendered()) {
                    // fetch cache rows
                    int firstInNewSet = scrollBody.getLastRendered() + 1;
                    int lastInNewSet = (int) (firstRowInViewPort + pageLength + cache_rate
                            * pageLength);
                    if (lastInNewSet > totalRows - 1) {
                        lastInNewSet = totalRows - 1;
                    }
                    rowRequestHandler.triggerRowFetch(firstInNewSet,
                            lastInNewSet - firstInNewSet + 1, 1);
                }
            }
        }

        /*
         * Ensures the column alignments are correct at initial loading. <br/>
         * (child components widths are correct)
         */
        WidgetUtil.runWebkitOverflowAutoFixDeferred(scrollBodyPanel
                .getElement());

        hadScrollBars = willHaveScrollbarz;
    }

    /**
     * Note: this method is not part of official API although declared as
     * protected. Extend at your own risk.
     * 
     * @return true if content area will have scrollbars visible.
     */
    protected boolean willHaveScrollbars() {
        if (isDynamicHeight()) {
            if (pageLength < totalRows) {
                return true;
            }
        } else {
            int fakeheight = (int) Math.round(scrollBody.getRowHeight()
                    * totalRows);
            int availableHeight = scrollBodyPanel.getElement().getPropertyInt(
                    "clientHeight");
            if (fakeheight > availableHeight) {
                return true;
            }
        }
        return false;
    }

    private void announceScrollPosition() {
        if (scrollPositionElement == null) {
            scrollPositionElement = DOM.createDiv();
            scrollPositionElement.setClassName(getStylePrimaryName()
                    + "-scrollposition");
            scrollPositionElement.getStyle().setPosition(Position.ABSOLUTE);
            scrollPositionElement.getStyle().setDisplay(Display.NONE);
            getElement().appendChild(scrollPositionElement);
        }

        Style style = scrollPositionElement.getStyle();
        style.setMarginLeft(getElement().getOffsetWidth() / 2 - 80, Unit.PX);
        style.setMarginTop(-scrollBodyPanel.getOffsetHeight(), Unit.PX);

        // indexes go from 1-totalRows, as rowheaders in index-mode indicate
        int last = (firstRowInViewPort + pageLength);
        if (last > totalRows) {
            last = totalRows;
        }
        scrollPositionElement.setInnerHTML("<span>" + (firstRowInViewPort + 1)
                + " &ndash; " + (last) + "..." + "</span>");
        style.setDisplay(Display.BLOCK);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void hideScrollPositionAnnotation() {
        if (scrollPositionElement != null) {
            scrollPositionElement.getStyle().setDisplay(Display.NONE);
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public boolean isScrollPositionVisible() {
        return scrollPositionElement != null
                && !scrollPositionElement.getStyle().getDisplay()
                        .equals(Display.NONE.toString());
    }

    /** For internal use only. May be removed or replaced in the future. */
    public class RowRequestHandler extends Timer {

        private int reqFirstRow = 0;
        private int reqRows = 0;
        private boolean isRequestHandlerRunning = false;

        public void triggerRowFetch(int first, int rows) {
            setReqFirstRow(first);
            setReqRows(rows);
            deferRowFetch();
        }

        public void triggerRowFetch(int first, int rows, int delay) {
            setReqFirstRow(first);
            setReqRows(rows);
            deferRowFetch(delay);
        }

        public void deferRowFetch() {
            deferRowFetch(250);
        }

        public boolean isRequestHandlerRunning() {
            return isRequestHandlerRunning;
        }

        public void deferRowFetch(int msec) {
            isRequestHandlerRunning = true;
            if (reqRows > 0 && reqFirstRow < totalRows) {
                schedule(msec);

                // tell scroll position to user if currently "visible" rows are
                // not rendered
                if (totalRows > pageLength
                        && ((firstRowInViewPort + pageLength > scrollBody
                                .getLastRendered()) || (firstRowInViewPort < scrollBody
                                .getFirstRendered()))) {
                    announceScrollPosition();
                } else {
                    hideScrollPositionAnnotation();
                }
            }
        }

        public int getReqFirstRow() {
            return reqFirstRow;
        }

        public void setReqFirstRow(int reqFirstRow) {
            if (reqFirstRow < 0) {
                this.reqFirstRow = 0;
            } else if (reqFirstRow >= totalRows) {
                this.reqFirstRow = totalRows - 1;
            } else {
                this.reqFirstRow = reqFirstRow;
            }
        }

        public void setReqRows(int reqRows) {
            if (reqRows < 0) {
                this.reqRows = 0;
            } else if (reqFirstRow + reqRows > totalRows) {
                this.reqRows = totalRows - reqFirstRow;
            } else {
                this.reqRows = reqRows;
            }
        }

        @Override
        public void run() {

            if (client.hasActiveRequest() || navKeyDown) {
                // if client connection is busy, don't bother loading it more
                VConsole.log("Postponed rowfetch");
                schedule(250);
            } else if (allRenderedRowsAreNew() && !updatedReqRows) {

                /*
                 * If all rows are new, there might have been a server-side call
                 * to Table.setCurrentPageFirstItemIndex(int) In this case,
                 * scrolling event takes way too late, and all the rows from
                 * previous viewport to this one were requested.
                 * 
                 * This should prevent requesting unneeded rows by updating
                 * reqFirstRow and reqRows before needing them. See (#14135)
                 */

                setReqFirstRow((firstRowInViewPort - (int) (pageLength * cache_rate)));
                int last = firstRowInViewPort + (int) (cache_rate * pageLength)
                        + pageLength - 1;
                if (last >= totalRows) {
                    last = totalRows - 1;
                }
                setReqRows(last - getReqFirstRow() + 1);
                updatedReqRows = true;
                schedule(250);

            } else {

                int firstRendered = scrollBody.getFirstRendered();
                int lastRendered = scrollBody.getLastRendered();
                if (lastRendered > totalRows) {
                    lastRendered = totalRows - 1;
                }
                boolean rendered = firstRendered >= 0 && lastRendered >= 0;

                int firstToBeRendered = firstRendered;

                if (reqFirstRow < firstToBeRendered) {
                    firstToBeRendered = reqFirstRow;
                } else if (firstRowInViewPort - (int) (cache_rate * pageLength) > firstToBeRendered) {
                    firstToBeRendered = firstRowInViewPort
                            - (int) (cache_rate * pageLength);
                    if (firstToBeRendered < 0) {
                        firstToBeRendered = 0;
                    }
                } else if (rendered && firstRendered + 1 < reqFirstRow
                        && lastRendered + 1 < reqFirstRow) {
                    // requested rows must fall within the requested rendering
                    // area
                    firstToBeRendered = reqFirstRow;
                }
                if (firstToBeRendered + reqRows < firstRendered) {
                    // must increase the required row count accordingly,
                    // otherwise may leave a gap and the rows beyond will get
                    // removed
                    setReqRows(firstRendered - firstToBeRendered);
                }

                int lastToBeRendered = lastRendered;
                int lastReqRow = reqFirstRow + reqRows - 1;

                if (lastReqRow > lastToBeRendered) {
                    lastToBeRendered = lastReqRow;
                } else if (firstRowInViewPort + pageLength + pageLength
                        * cache_rate < lastToBeRendered) {
                    lastToBeRendered = (firstRowInViewPort + pageLength + (int) (pageLength * cache_rate));
                    if (lastToBeRendered >= totalRows) {
                        lastToBeRendered = totalRows - 1;
                    }
                    // due Safari 3.1 bug (see #2607), verify reqrows, original
                    // problem unknown, but this should catch the issue
                    if (lastReqRow > lastToBeRendered) {
                        setReqRows(lastToBeRendered - reqFirstRow);
                    }
                } else if (rendered && lastRendered - 1 > lastReqRow
                        && firstRendered - 1 > lastReqRow) {
                    // requested rows must fall within the requested rendering
                    // area
                    lastToBeRendered = lastReqRow;
                }

                if (lastToBeRendered > totalRows) {
                    lastToBeRendered = totalRows - 1;
                }
                if (reqFirstRow < firstToBeRendered
                        || (reqFirstRow > firstToBeRendered && (reqFirstRow < firstRendered || reqFirstRow > lastRendered + 1))) {
                    setReqFirstRow(firstToBeRendered);
                }
                if (lastRendered < lastToBeRendered
                        && lastRendered + reqRows < lastToBeRendered) {
                    // must increase the required row count accordingly,
                    // otherwise may leave a gap and the rows after will get
                    // removed
                    setReqRows(lastToBeRendered - lastRendered);
                } else if (lastToBeRendered >= firstRendered
                        && reqFirstRow + reqRows < firstRendered) {
                    setReqRows(lastToBeRendered - lastRendered);
                }

                client.updateVariable(paintableId, "firstToBeRendered",
                        firstToBeRendered, false);
                client.updateVariable(paintableId, "lastToBeRendered",
                        lastToBeRendered, false);

                // don't request server to update page first index in case it
                // has not been changed
                if (firstRowInViewPort != firstvisible) {
                    // remember which firstvisible we requested, in case the
                    // server has a differing opinion
                    lastRequestedFirstvisible = firstRowInViewPort;
                    client.updateVariable(paintableId, "firstvisible",
                            firstRowInViewPort, false);
                }

                client.updateVariable(paintableId, "reqfirstrow", reqFirstRow,
                        false);
                client.updateVariable(paintableId, "reqrows", reqRows, true);

                if (selectionChanged) {
                    unSyncedselectionsBeforeRowFetch = new HashSet<Object>(
                            selectedRowKeys);
                }
                isRequestHandlerRunning = false;
            }
        }

        /**
         * Sends request to refresh content at this position.
         */
        public void refreshContent() {
            isRequestHandlerRunning = true;
            int first = (int) (firstRowInViewPort - pageLength * cache_rate);
            int reqRows = (int) (2 * pageLength * cache_rate + pageLength);
            if (first < 0) {
                reqRows = reqRows + first;
                first = 0;
            }
            setReqFirstRow(first);
            setReqRows(reqRows);
            run();
        }
    }

    public class HeaderCell extends Widget {

        Element td = DOM.createTD();

        Element captionContainer = DOM.createDiv();

        Element sortIndicator = DOM.createDiv();

        Element colResizeWidget = DOM.createDiv();

        Element floatingCopyOfHeaderCell;

        private boolean sortable = false;
        private final String cid;

        private boolean dragging;
        private Integer currentDragX = null; // is used to resolve #14796

        private int dragStartX;
        private int colIndex;
        private int originalWidth;

        private boolean isResizing;

        private int headerX;

        private boolean moved;

        private int closestSlot;

        private int width = -1;

        private int naturalWidth = -1;

        private char align = ALIGN_LEFT;

        boolean definedWidth = false;

        private float expandRatio = 0;

        private boolean sorted;

        public void setSortable(boolean b) {
            sortable = b;
        }

        /**
         * Makes room for the sorting indicator in case the column that the
         * header cell belongs to is sorted. This is done by resizing the width
         * of the caption container element by the correct amount
         */
        public void resizeCaptionContainer(int rightSpacing) {
            int captionContainerWidth = width
                    - colResizeWidget.getOffsetWidth() - rightSpacing;

            if (td.getClassName().contains("-asc")
                    || td.getClassName().contains("-desc")) {
                // Leave room for the sort indicator
                captionContainerWidth -= sortIndicator.getOffsetWidth();
            }

            if (captionContainerWidth < 0) {
                rightSpacing += captionContainerWidth;
                captionContainerWidth = 0;
            }

            captionContainer.getStyle().setPropertyPx("width",
                    captionContainerWidth);

            // Apply/Remove spacing if defined
            if (rightSpacing > 0) {
                colResizeWidget.getStyle().setMarginLeft(rightSpacing, Unit.PX);
            } else {
                colResizeWidget.getStyle().clearMarginLeft();
            }
        }

        public void setNaturalMinimumColumnWidth(int w) {
            naturalWidth = w;
        }

        public HeaderCell(String colId, String headerText) {
            cid = colId;

            setText(headerText);

            td.appendChild(colResizeWidget);

            // ensure no clipping initially (problem on column additions)
            captionContainer.getStyle().setOverflow(Overflow.VISIBLE);

            td.appendChild(sortIndicator);
            td.appendChild(captionContainer);

            DOM.sinkEvents(td, Event.MOUSEEVENTS | Event.ONDBLCLICK
                    | Event.ONCONTEXTMENU | Event.TOUCHEVENTS);

            setElement(td);

            setAlign(ALIGN_LEFT);
        }

        protected void updateStyleNames(String primaryStyleName) {
            colResizeWidget.setClassName(primaryStyleName + "-resizer");
            sortIndicator.setClassName(primaryStyleName + "-sort-indicator");
            captionContainer.setClassName(primaryStyleName
                    + "-caption-container");
            if (sorted) {
                if (sortAscending) {
                    setStyleName(primaryStyleName + "-header-cell-asc");
                } else {
                    setStyleName(primaryStyleName + "-header-cell-desc");
                }
            } else {
                setStyleName(primaryStyleName + "-header-cell");
            }

            final String ALIGN_PREFIX = primaryStyleName
                    + "-caption-container-align-";

            switch (align) {
            case ALIGN_CENTER:
                captionContainer.addClassName(ALIGN_PREFIX + "center");
                break;
            case ALIGN_RIGHT:
                captionContainer.addClassName(ALIGN_PREFIX + "right");
                break;
            default:
                captionContainer.addClassName(ALIGN_PREFIX + "left");
                break;
            }

        }

        public void disableAutoWidthCalculation() {
            definedWidth = true;
            expandRatio = 0;
        }

        /**
         * Sets width to the header cell. This width should not include any
         * possible indent modifications that are present in
         * {@link VScrollTableBody#getMaxIndent()}.
         * 
         * @param w
         *            required width of the cell sans indentations
         * @param ensureDefinedWidth
         *            disables expand ratio if required
         */
        public void setWidth(int w, boolean ensureDefinedWidth) {
            if (ensureDefinedWidth) {
                definedWidth = true;
                // on column resize expand ratio becomes zero
                expandRatio = 0;
            }
            if (width == -1) {
                // go to default mode, clip content if necessary
                captionContainer.getStyle().clearOverflow();
            }
            width = w;
            if (w == -1) {
                captionContainer.getStyle().clearWidth();
                setWidth("");
            } else {
                tHead.resizeCaptionContainer(this);

                /*
                 * if we already have tBody, set the header width properly, if
                 * not defer it. IE will fail with complex float in table header
                 * unless TD width is not explicitly set.
                 */
                if (scrollBody != null) {
                    int maxIndent = scrollBody.getMaxIndent();
                    if (w < maxIndent && isHierarchyColumn()) {
                        w = maxIndent;
                    }
                    int tdWidth = w + scrollBody.getCellExtraWidth();
                    setWidth(tdWidth + "px");
                } else {
                    Scheduler.get().scheduleDeferred(new Command() {

                        @Override
                        public void execute() {
                            int maxIndent = scrollBody.getMaxIndent();
                            int tdWidth = width;
                            if (tdWidth < maxIndent && isHierarchyColumn()) {
                                tdWidth = maxIndent;
                            }
                            tdWidth += scrollBody.getCellExtraWidth();
                            setWidth(tdWidth + "px");
                        }
                    });
                }
            }
        }

        public void setUndefinedWidth() {
            definedWidth = false;
            if (!isResizing) {
                setWidth(-1, false);
            }
        }

        private void setUndefinedWidthFlagOnly() {
            definedWidth = false;
        }

        /**
         * Detects if width is fixed by developer on server side or resized to
         * current width by user.
         * 
         * @return true if defined, false if "natural" width
         */
        public boolean isDefinedWidth() {
            return definedWidth && width >= 0;
        }

        /**
         * This method exists for the needs of {@link VTreeTable} only.
         * 
         * Returns the pixels width of the header cell. This includes the
         * indent, if applicable.
         * 
         * @return The width in pixels
         */
        protected int getWidthWithIndent() {
            if (scrollBody != null && isHierarchyColumn()) {
                int maxIndent = scrollBody.getMaxIndent();
                if (maxIndent > width) {
                    return maxIndent;
                }
            }
            return width;
        }

        /**
         * Returns the pixels width of the header cell.
         * 
         * @return The width in pixels
         */
        public int getWidth() {
            return width;
        }

        /**
         * This method exists for the needs of {@link VTreeTable} only.
         * 
         * @return <code>true</code> if this is hierarcyColumn's header cell,
         *         <code>false</code> otherwise
         */
        private boolean isHierarchyColumn() {
            int hierarchyColumnIndex = getHierarchyColumnIndex();
            return hierarchyColumnIndex >= 0
                    && tHead.visibleCells.indexOf(this) == hierarchyColumnIndex;
        }

        public void setText(String headerText) {
            DOM.setInnerHTML(captionContainer, headerText);
        }

        public String getColKey() {
            return cid;
        }

        private void setSorted(boolean sorted) {
            this.sorted = sorted;
            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        /**
         * Handle column reordering.
         */

        @Override
        public void onBrowserEvent(Event event) {
            if (enabled && event != null) {
                if (isResizing
                        || event.getEventTarget().cast() == colResizeWidget) {
                    if (dragging
                            && (event.getTypeInt() == Event.ONMOUSEUP || event
                                    .getTypeInt() == Event.ONTOUCHEND)) {
                        // Handle releasing column header on spacer #5318
                        handleCaptionEvent(event);
                    } else {
                        onResizeEvent(event);
                    }
                } else {
                    /*
                     * Ensure focus before handling caption event. Otherwise
                     * variables changed from caption event may be before
                     * variables from other components that fire variables when
                     * they lose focus.
                     */
                    if (event.getTypeInt() == Event.ONMOUSEDOWN
                            || event.getTypeInt() == Event.ONTOUCHSTART) {
                        scrollBodyPanel.setFocus(true);
                    }
                    handleCaptionEvent(event);
                    boolean stopPropagation = true;
                    if (event.getTypeInt() == Event.ONCONTEXTMENU
                            && !client.hasEventListeners(VScrollTable.this,
                                    TableConstants.HEADER_CLICK_EVENT_ID)) {
                        // Prevent showing the browser's context menu only when
                        // there is a header click listener.
                        stopPropagation = false;
                    }
                    if (stopPropagation) {
                        event.stopPropagation();
                        event.preventDefault();
                    }
                }
            }
        }

        private void createFloatingCopy() {
            floatingCopyOfHeaderCell = DOM.createDiv();
            DOM.setInnerHTML(floatingCopyOfHeaderCell, DOM.getInnerHTML(td));
            floatingCopyOfHeaderCell = DOM
                    .getChild(floatingCopyOfHeaderCell, 2);
            // #12714 the shown "ghost element" should be inside
            // v-overlay-container, and it should contain the same styles as the
            // table to enable theming (except v-table & v-widget).
            String stylePrimaryName = VScrollTable.this.getStylePrimaryName();
            StringBuilder sb = new StringBuilder();
            for (String s : VScrollTable.this.getStyleName().split(" ")) {
                if (!s.equals(StyleConstants.UI_WIDGET)) {
                    sb.append(s);
                    if (s.equals(stylePrimaryName)) {
                        sb.append("-header-drag ");
                    } else {
                        sb.append(" ");
                    }
                }
            }
            floatingCopyOfHeaderCell.setClassName(sb.toString().trim());
            // otherwise might wrap or be cut if narrow column
            floatingCopyOfHeaderCell.getStyle().setProperty("width", "auto");
            updateFloatingCopysPosition(DOM.getAbsoluteLeft(td),
                    DOM.getAbsoluteTop(td));
            DOM.appendChild(VOverlay.getOverlayContainer(client),
                    floatingCopyOfHeaderCell);
        }

        private void updateFloatingCopysPosition(int x, int y) {
            x -= DOM.getElementPropertyInt(floatingCopyOfHeaderCell,
                    "offsetWidth") / 2;
            floatingCopyOfHeaderCell.getStyle().setLeft(x, Unit.PX);
            if (y > 0) {
                floatingCopyOfHeaderCell.getStyle().setTop(y + 7, Unit.PX);
            }
        }

        private void hideFloatingCopy() {
            floatingCopyOfHeaderCell.removeFromParent();
            floatingCopyOfHeaderCell = null;
        }

        /**
         * Fires a header click event after the user has clicked a column header
         * cell
         * 
         * @param event
         *            The click event
         */
        private void fireHeaderClickedEvent(Event event) {
            if (client.hasEventListeners(VScrollTable.this,
                    TableConstants.HEADER_CLICK_EVENT_ID)) {
                MouseEventDetails details = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event);
                client.updateVariable(paintableId, "headerClickEvent",
                        details.toString(), false);
                client.updateVariable(paintableId, "headerClickCID", cid, true);
            }
        }

        protected void handleCaptionEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONTOUCHSTART:
            case Event.ONMOUSEDOWN:
                if (columnReordering
                        && WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                    if (event.getTypeInt() == Event.ONTOUCHSTART) {
                        /*
                         * prevent using this event in e.g. scrolling
                         */
                        event.stopPropagation();
                    }
                    dragging = true;
                    currentDragX = WidgetUtil.getTouchOrMouseClientX(event);
                    moved = false;
                    colIndex = getColIndexByKey(cid);
                    DOM.setCapture(getElement());
                    headerX = tHead.getAbsoluteLeft();
                    event.preventDefault(); // prevent selecting text &&
                                            // generated touch events
                }
                break;
            case Event.ONMOUSEUP:
            case Event.ONTOUCHEND:
            case Event.ONTOUCHCANCEL:
                if (columnReordering
                        && WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                    dragging = false;
                    currentDragX = null;
                    DOM.releaseCapture(getElement());

                    if (WidgetUtil.isTouchEvent(event)) {
                        /*
                         * Prevent using in e.g. scrolling and prevent generated
                         * events.
                         */
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    if (moved) {
                        hideFloatingCopy();
                        tHead.removeSlotFocus();
                        if (closestSlot != colIndex
                                && closestSlot != (colIndex + 1)) {
                            if (closestSlot > colIndex) {
                                reOrderColumn(cid, closestSlot - 1);
                            } else {
                                reOrderColumn(cid, closestSlot);
                            }
                        }
                        moved = false;
                        break;
                    }
                }

                if (!moved) {
                    // mouse event was a click to header -> sort column
                    if (sortable
                            && WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                        if (sortColumn.equals(cid)) {
                            // just toggle order
                            client.updateVariable(paintableId, "sortascending",
                                    !sortAscending, false);
                        } else {
                            // set table sorted by this column
                            client.updateVariable(paintableId, "sortcolumn",
                                    cid, false);
                        }
                        // get also cache columns at the same request
                        scrollBodyPanel.setScrollPosition(0);
                        firstvisible = 0;
                        rowRequestHandler.setReqFirstRow(0);
                        rowRequestHandler.setReqRows((int) (2 * pageLength
                                * cache_rate + pageLength));
                        rowRequestHandler.deferRowFetch(); // some validation +
                                                           // defer 250ms
                        rowRequestHandler.cancel(); // instead of waiting
                        rowRequestHandler.run(); // run immediately
                    }
                    fireHeaderClickedEvent(event);
                    if (WidgetUtil.isTouchEvent(event)) {
                        /*
                         * Prevent using in e.g. scrolling and prevent generated
                         * events.
                         */
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    break;
                }
                break;
            case Event.ONDBLCLICK:
                fireHeaderClickedEvent(event);
                break;
            case Event.ONTOUCHMOVE:
            case Event.ONMOUSEMOVE:
                // only start the drag if the mouse / touch has moved a minimum
                // distance in x-axis (the same idea as in #13381)
                int currentX = WidgetUtil.getTouchOrMouseClientX(event);

                if (currentDragX == null
                        || Math.abs(currentDragX - currentX) > VDragAndDropManager.MINIMUM_DISTANCE_TO_START_DRAG) {
                    if (dragging
                            && WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                        if (event.getTypeInt() == Event.ONTOUCHMOVE) {
                            /*
                             * prevent using this event in e.g. scrolling
                             */
                            event.stopPropagation();
                        }
                        if (!moved) {
                            createFloatingCopy();
                            moved = true;
                        }

                        final int clientX = WidgetUtil
                                .getTouchOrMouseClientX(event);
                        final int x = clientX
                                + tHead.hTableWrapper.getScrollLeft();
                        int slotX = headerX;
                        closestSlot = colIndex;
                        int closestDistance = -1;
                        int start = 0;
                        if (showRowHeaders) {
                            start++;
                        }
                        final int visibleCellCount = tHead
                                .getVisibleCellCount();
                        for (int i = start; i <= visibleCellCount; i++) {
                            if (i > 0) {
                                final String colKey = getColKeyByIndex(i - 1);
                                // getColWidth only returns the internal width
                                // without padding, not the offset width of the
                                // whole td (#10890)
                                slotX += getColWidth(colKey)
                                        + scrollBody.getCellExtraWidth();
                            }
                            final int dist = Math.abs(x - slotX);
                            if (closestDistance == -1 || dist < closestDistance) {
                                closestDistance = dist;
                                closestSlot = i;
                            }
                        }
                        tHead.focusSlot(closestSlot);

                        updateFloatingCopysPosition(clientX, -1);
                    }
                }
                break;
            default:
                break;
            }
        }

        private void onResizeEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                if (!WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                    return;
                }
                isResizing = true;
                DOM.setCapture(getElement());
                dragStartX = DOM.eventGetClientX(event);
                colIndex = getColIndexByKey(cid);
                originalWidth = getWidthWithIndent();
                DOM.eventPreventDefault(event);
                break;
            case Event.ONMOUSEUP:
                if (!WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                    return;
                }
                isResizing = false;
                DOM.releaseCapture(getElement());
                tHead.disableAutoColumnWidthCalculation(this);

                // Ensure last header cell is taking into account possible
                // column selector
                HeaderCell lastCell = tHead.getHeaderCell(tHead
                        .getVisibleCellCount() - 1);
                tHead.resizeCaptionContainer(lastCell);
                triggerLazyColumnAdjustment(true);

                fireColumnResizeEvent(cid, originalWidth, getColWidth(cid));
                break;
            case Event.ONMOUSEMOVE:
                if (!WidgetUtil.isTouchEventOrLeftMouseButton(event)) {
                    return;
                }
                if (isResizing) {
                    final int deltaX = DOM.eventGetClientX(event) - dragStartX;
                    if (deltaX == 0) {
                        return;
                    }
                    tHead.disableAutoColumnWidthCalculation(this);

                    int newWidth = originalWidth + deltaX;
                    // get min width with indent, no padding
                    int minWidth = getMinWidth(true, false);
                    if (newWidth < minWidth) {
                        // already includes indent if any
                        newWidth = minWidth;
                    }
                    setColWidth(colIndex, newWidth, true);
                    triggerLazyColumnAdjustment(false);
                    forceRealignColumnHeaders();
                }
                break;
            default:
                break;
            }
        }

        /**
         * Returns the smallest possible cell width in pixels.
         * 
         * @param includeIndent
         *            - width should include hierarchy column indent if
         *            applicable (VTreeTable only)
         * @param includeCellExtraWidth
         *            - width should include paddings etc.
         * @return
         */
        private int getMinWidth(boolean includeIndent,
                boolean includeCellExtraWidth) {
            int minWidth = sortIndicator.getOffsetWidth();
            if (scrollBody != null) {
                // check the need for indent before adding paddings etc.
                if (includeIndent && isHierarchyColumn()) {
                    int maxIndent = scrollBody.getMaxIndent();
                    if (minWidth < maxIndent) {
                        minWidth = maxIndent;
                    }
                }
                if (includeCellExtraWidth) {
                    minWidth += scrollBody.getCellExtraWidth();
                }
            }
            return minWidth;
        }

        public int getMinWidth() {
            // get min width with padding, no indent
            return getMinWidth(false, true);
        }

        public String getCaption() {
            return DOM.getInnerText(captionContainer);
        }

        public boolean isEnabled() {
            return getParent() != null;
        }

        public void setAlign(char c) {
            align = c;
            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        public char getAlign() {
            return align;
        }

        /**
         * Saves natural column width if it hasn't been saved already.
         * 
         * @param columnIndex
         * @since 7.3.9
         */
        protected void saveNaturalColumnWidthIfNotSaved(int columnIndex) {
            if (naturalWidth < 0) {
                // This is recently revealed column. Try to detect a proper
                // value (greater of header and data columns)

                int hw = captionContainer.getOffsetWidth() + getHeaderPadding();
                if (BrowserInfo.get().isGecko()) {
                    hw += sortIndicator.getOffsetWidth();
                }
                if (columnIndex < 0) {
                    columnIndex = 0;
                    for (Iterator<Widget> it = tHead.iterator(); it.hasNext(); columnIndex++) {
                        if (it.next() == this) {
                            break;
                        }
                    }
                }
                final int cw = scrollBody.getColWidth(columnIndex);
                naturalWidth = (hw > cw ? hw : cw);
            }
        }

        /**
         * Detects the natural minimum width for the column of this header cell.
         * If column is resized by user or the width is defined by server the
         * actual width is returned. Else the natural min width is returned.
         * 
         * @param columnIndex
         *            column index hint, if -1 (unknown) it will be detected
         * 
         * @return
         */
        public int getNaturalColumnWidth(int columnIndex) {
            final int iw = columnIndex == getHierarchyColumnIndex() ? scrollBody
                    .getMaxIndent() : 0;
            saveNaturalColumnWidthIfNotSaved(columnIndex);
            if (isDefinedWidth()) {
                if (iw > width) {
                    return iw;
                }
                return width;
            } else {
                if (iw > naturalWidth) {
                    // indent is temporary value, naturalWidth shouldn't be
                    // updated
                    return iw;
                } else {
                    return naturalWidth;
                }
            }
        }

        public void setExpandRatio(float floatAttribute) {
            if (floatAttribute != expandRatio) {
                triggerLazyColumnAdjustment(false);
            }
            expandRatio = floatAttribute;
        }

        public float getExpandRatio() {
            return expandRatio;
        }

        public boolean isSorted() {
            return sorted;
        }
    }

    /**
     * HeaderCell that is header cell for row headers.
     * 
     * Reordering disabled and clicking on it resets sorting.
     */
    public class RowHeadersHeaderCell extends HeaderCell {

        RowHeadersHeaderCell() {
            super(ROW_HEADER_COLUMN_KEY, "");
            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        @Override
        protected void updateStyleNames(String primaryStyleName) {
            super.updateStyleNames(primaryStyleName);
            setStyleName(primaryStyleName + "-header-cell-rowheader");
        }

        @Override
        protected void handleCaptionEvent(Event event) {
            // NOP: RowHeaders cannot be reordered
            // TODO It'd be nice to reset sorting here
        }
    }

    public class TableHead extends Panel implements ActionOwner {

        private static final int WRAPPER_WIDTH = 900000;

        ArrayList<Widget> visibleCells = new ArrayList<Widget>();

        HashMap<String, HeaderCell> availableCells = new HashMap<String, HeaderCell>();

        Element div = DOM.createDiv();
        Element hTableWrapper = DOM.createDiv();
        Element hTableContainer = DOM.createDiv();
        Element table = DOM.createTable();
        Element headerTableBody = DOM.createTBody();
        Element tr = DOM.createTR();

        private final Element columnSelector = DOM.createDiv();

        private int focusedSlot = -1;

        public TableHead() {
            if (BrowserInfo.get().isIE()) {
                table.setPropertyInt("cellSpacing", 0);
            }

            hTableWrapper.getStyle().setOverflow(Overflow.HIDDEN);
            columnSelector.getStyle().setDisplay(Display.NONE);

            DOM.appendChild(table, headerTableBody);
            DOM.appendChild(headerTableBody, tr);
            DOM.appendChild(hTableContainer, table);
            DOM.appendChild(hTableWrapper, hTableContainer);
            DOM.appendChild(div, hTableWrapper);
            DOM.appendChild(div, columnSelector);
            setElement(div);

            DOM.sinkEvents(columnSelector, Event.ONCLICK);

            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersHeaderCell());
        }

        protected void updateStyleNames(String primaryStyleName) {
            hTableWrapper.setClassName(primaryStyleName + "-header");
            columnSelector.setClassName(primaryStyleName + "-column-selector");
            setStyleName(primaryStyleName + "-header-wrap");
            for (HeaderCell c : availableCells.values()) {
                c.updateStyleNames(primaryStyleName);
            }
        }

        public void resizeCaptionContainer(HeaderCell cell) {
            HeaderCell lastcell = getHeaderCell(visibleCells.size() - 1);
            int columnSelectorOffset = columnSelector.getOffsetWidth();

            if (cell == lastcell && columnSelectorOffset > 0
                    && !hasVerticalScrollbar()) {

                // Measure column widths
                int columnTotalWidth = 0;
                for (Widget w : visibleCells) {
                    int cellExtraWidth = w.getOffsetWidth();
                    if (scrollBody != null
                            && visibleCells.indexOf(w) == getHierarchyColumnIndex()
                            && cellExtraWidth < scrollBody.getMaxIndent()) {
                        // indent must be taken into consideration even if it
                        // hasn't been applied yet
                        columnTotalWidth += scrollBody.getMaxIndent();
                    } else {
                        columnTotalWidth += cellExtraWidth;
                    }
                }

                int divOffset = div.getOffsetWidth();
                if (columnTotalWidth >= divOffset - columnSelectorOffset) {
                    /*
                     * Ensure column caption is visible when placed under the
                     * column selector widget by shifting and resizing the
                     * caption.
                     */
                    int offset = 0;
                    int diff = divOffset - columnTotalWidth;
                    if (diff < columnSelectorOffset && diff > 0) {
                        /*
                         * If the difference is less than the column selectors
                         * width then just offset by the difference
                         */
                        offset = columnSelectorOffset - diff;
                    } else {
                        // Else offset by the whole column selector
                        offset = columnSelectorOffset;
                    }
                    lastcell.resizeCaptionContainer(offset);
                } else {
                    cell.resizeCaptionContainer(0);
                }
            } else {
                cell.resizeCaptionContainer(0);
            }
        }

        @Override
        public void clear() {
            for (String cid : availableCells.keySet()) {
                removeCell(cid);
            }
            availableCells.clear();
            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersHeaderCell());
        }

        public void updateCellsFromUIDL(UIDL uidl) {
            Iterator<?> it = uidl.getChildIterator();
            HashSet<String> updated = new HashSet<String>();
            boolean refreshContentWidths = initializedAndAttached
                    && hadScrollBars != willHaveScrollbars();
            while (it.hasNext()) {
                final UIDL col = (UIDL) it.next();
                final String cid = col.getStringAttribute("cid");
                updated.add(cid);

                String caption = buildCaptionHtmlSnippet(col);
                HeaderCell c = getHeaderCell(cid);
                if (c == null) {
                    c = new HeaderCell(cid, caption);
                    availableCells.put(cid, c);
                    if (initializedAndAttached) {
                        // we will need a column width recalculation
                        initializedAndAttached = false;
                        initialContentReceived = false;
                        isNewBody = true;
                    }
                } else {
                    c.setText(caption);
                    if (BrowserInfo.get().isIE10()) {
                        // IE10 can some times define min-height to include
                        // padding when setting the text...
                        // See https://dev.vaadin.com/ticket/15169
                        WidgetUtil.forceIERedraw(c.getElement());
                    }
                }

                c.setSorted(false);
                if (col.hasAttribute("sortable")) {
                    c.setSortable(true);
                } else {
                    c.setSortable(false);
                }

                if (col.hasAttribute("align")) {
                    c.setAlign(col.getStringAttribute("align").charAt(0));
                } else {
                    c.setAlign(ALIGN_LEFT);

                }
                if (col.hasAttribute("width") && !c.isResizing) {
                    // Make sure to accomodate for the sort indicator if
                    // necessary.
                    int width = col.getIntAttribute("width");
                    int widthWithoutAddedIndent = width;

                    // get min width with indent, no padding
                    int minWidth = c.getMinWidth(true, false);
                    if (width < minWidth) {
                        width = minWidth;
                    }
                    if (scrollBody != null && width != c.getWidthWithIndent()) {
                        // Do a more thorough update if a column is resized from
                        // the server *after* the header has been properly
                        // initialized
                        final int newWidth = width;
                        Scheduler.get().scheduleFinally(new ScheduledCommand() {

                            @Override
                            public void execute() {
                                final int colIx = getColIndexByKey(cid);
                                setColWidth(colIx, newWidth, true);
                            }
                        });
                        refreshContentWidths = true;
                    } else {
                        // get min width with no indent or padding
                        minWidth = c.getMinWidth(false, false);
                        if (widthWithoutAddedIndent < minWidth) {
                            widthWithoutAddedIndent = minWidth;
                        }
                        // save min width without indent
                        c.setWidth(widthWithoutAddedIndent, true);
                    }
                } else if (col.hasAttribute("er")) {
                    c.setExpandRatio(col.getFloatAttribute("er"));
                    c.setUndefinedWidthFlagOnly();
                } else if (recalcWidths) {
                    c.setUndefinedWidth();

                } else {
                    boolean hadExpandRatio = c.getExpandRatio() > 0;
                    boolean hadDefinedWidth = c.isDefinedWidth();
                    if (hadExpandRatio || hadDefinedWidth) {
                        // Someone has removed a expand width or the defined
                        // width on the server side (setting it to -1), make the
                        // column undefined again and measure columns again.
                        c.setUndefinedWidth();
                        c.setExpandRatio(0);
                        refreshContentWidths = true;
                    }
                }

                if (col.hasAttribute("collapsed")) {
                    // ensure header is properly removed from parent (case when
                    // collapsing happens via servers side api)
                    if (c.isAttached()) {
                        c.removeFromParent();
                        headerChangedDuringUpdate = true;
                    }
                }
            }

            if (refreshContentWidths) {
                // Recalculate the column sizings if any column has changed
                Scheduler.get().scheduleFinally(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        triggerLazyColumnAdjustment(true);
                    }
                });
            }

            // check for orphaned header cells
            for (Iterator<String> cit = availableCells.keySet().iterator(); cit
                    .hasNext();) {
                String cid = cit.next();
                if (!updated.contains(cid)) {
                    removeCell(cid);
                    cit.remove();
                    // we will need a column width recalculation, since columns
                    // with expand ratios should expand to fill the void.
                    initializedAndAttached = false;
                    initialContentReceived = false;
                    isNewBody = true;
                }
            }
        }

        public void enableColumn(String cid, int index) {
            final HeaderCell c = getHeaderCell(cid);
            if (!c.isEnabled() || getHeaderCell(index) != c) {
                setHeaderCell(index, c);
                if (initializedAndAttached) {
                    headerChangedDuringUpdate = true;
                }
            }
        }

        public int getVisibleCellCount() {
            return visibleCells.size();
        }

        public void setHorizontalScrollPosition(int scrollLeft) {
            hTableWrapper.setScrollLeft(scrollLeft);
        }

        public void setColumnCollapsingAllowed(boolean cc) {
            if (cc) {
                columnSelector.getStyle().setDisplay(Display.BLOCK);
            } else {
                columnSelector.getStyle().setDisplay(Display.NONE);
            }
        }

        public void disableBrowserIntelligence() {
            hTableContainer.getStyle().setWidth(WRAPPER_WIDTH, Unit.PX);
        }

        public void enableBrowserIntelligence() {
            hTableContainer.getStyle().clearWidth();
        }

        public void setHeaderCell(int index, HeaderCell cell) {
            if (cell.isEnabled()) {
                // we're moving the cell
                DOM.removeChild(tr, cell.getElement());
                orphan(cell);
                visibleCells.remove(cell);
            }
            if (index < visibleCells.size()) {
                // insert to right slot
                DOM.insertChild(tr, cell.getElement(), index);
                adopt(cell);
                visibleCells.add(index, cell);
            } else if (index == visibleCells.size()) {
                // simply append
                DOM.appendChild(tr, cell.getElement());
                adopt(cell);
                visibleCells.add(cell);
            } else {
                throw new RuntimeException(
                        "Header cells must be appended in order");
            }
        }

        public HeaderCell getHeaderCell(int index) {
            if (index >= 0 && index < visibleCells.size()) {
                return (HeaderCell) visibleCells.get(index);
            } else {
                return null;
            }
        }

        /**
         * Get's HeaderCell by it's column Key.
         * 
         * Note that this returns HeaderCell even if it is currently collapsed.
         * 
         * @param cid
         *            Column key of accessed HeaderCell
         * @return HeaderCell
         */
        public HeaderCell getHeaderCell(String cid) {
            return availableCells.get(cid);
        }

        public void moveCell(int oldIndex, int newIndex) {
            final HeaderCell hCell = getHeaderCell(oldIndex);
            final Element cell = hCell.getElement();

            visibleCells.remove(oldIndex);
            DOM.removeChild(tr, cell);

            DOM.insertChild(tr, cell, newIndex);
            visibleCells.add(newIndex, hCell);
        }

        @Override
        public Iterator<Widget> iterator() {
            return visibleCells.iterator();
        }

        @Override
        public boolean remove(Widget w) {
            if (visibleCells.contains(w)) {
                visibleCells.remove(w);
                orphan(w);
                DOM.removeChild(DOM.getParent(w.getElement()), w.getElement());
                return true;
            }
            return false;
        }

        public void removeCell(String colKey) {
            final HeaderCell c = getHeaderCell(colKey);
            remove(c);
        }

        private void focusSlot(int index) {
            removeSlotFocus();
            if (index > 0) {
                Element child = tr.getChild(index - 1).getFirstChild().cast();
                child.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-resizer");
                child.addClassName(VScrollTable.this.getStylePrimaryName()
                        + "-focus-slot-right");
            } else {
                Element child = tr.getChild(index).getFirstChild().cast();
                child.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-resizer");
                child.addClassName(VScrollTable.this.getStylePrimaryName()
                        + "-focus-slot-left");
            }
            focusedSlot = index;
        }

        private void removeSlotFocus() {
            if (focusedSlot < 0) {
                return;
            }
            if (focusedSlot == 0) {
                Element child = tr.getChild(focusedSlot).getFirstChild().cast();
                child.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-resizer");
            } else if (focusedSlot > 0) {
                Element child = tr.getChild(focusedSlot - 1).getFirstChild()
                        .cast();
                child.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-resizer");
            }
            focusedSlot = -1;
        }

        @Override
        public void onBrowserEvent(Event event) {
            if (enabled) {
                if (event.getEventTarget().cast() == columnSelector) {
                    final int left = DOM.getAbsoluteLeft(columnSelector);
                    final int top = DOM.getAbsoluteTop(columnSelector)
                            + DOM.getElementPropertyInt(columnSelector,
                                    "offsetHeight");
                    client.getContextMenu().showAt(this, left, top);
                }
            }
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            if (client != null) {
                client.getContextMenu().ensureHidden(this);
            }
        }

        class VisibleColumnAction extends Action {

            String colKey;
            private boolean collapsed;
            private boolean noncollapsible = false;
            private VScrollTableRow currentlyFocusedRow;

            public VisibleColumnAction(String colKey) {
                super(VScrollTable.TableHead.this);
                this.colKey = colKey;
                caption = tHead.getHeaderCell(colKey).getCaption();
                currentlyFocusedRow = focusedRow;
            }

            @Override
            public void execute() {
                if (noncollapsible) {
                    return;
                }
                client.getContextMenu().hide();
                // toggle selected column
                if (collapsedColumns.contains(colKey)) {
                    collapsedColumns.remove(colKey);
                } else {
                    tHead.removeCell(colKey);
                    collapsedColumns.add(colKey);
                    triggerLazyColumnAdjustment(true);
                }

                // update variable to server
                client.updateVariable(paintableId, "collapsedcolumns",
                        collapsedColumns.toArray(new String[collapsedColumns
                                .size()]), false);
                // let rowRequestHandler determine proper rows
                rowRequestHandler.refreshContent();
                lazyRevertFocusToRow(currentlyFocusedRow);
            }

            public void setCollapsed(boolean b) {
                collapsed = b;
            }

            public void setNoncollapsible(boolean b) {
                noncollapsible = b;
            }

            /**
             * Override default method to distinguish on/off columns
             */

            @Override
            public String getHTML() {
                final StringBuffer buf = new StringBuffer();
                buf.append("<span class=\"");
                if (collapsed) {
                    buf.append("v-off");
                } else {
                    buf.append("v-on");
                }
                if (noncollapsible) {
                    buf.append(" v-disabled");
                }
                buf.append("\">");

                buf.append(super.getHTML());
                buf.append("</span>");

                return buf.toString();
            }

        }

        /*
         * Returns columns as Action array for column select popup
         */

        @Override
        public Action[] getActions() {
            Object[] cols;
            if (columnReordering && columnOrder != null) {
                cols = columnOrder;
            } else {
                // if columnReordering is disabled, we need different way to get
                // all available columns
                cols = visibleColOrder;
                cols = new Object[visibleColOrder.length
                        + collapsedColumns.size()];
                int i;
                for (i = 0; i < visibleColOrder.length; i++) {
                    cols[i] = visibleColOrder[i];
                }
                for (final Iterator<String> it = collapsedColumns.iterator(); it
                        .hasNext();) {
                    cols[i++] = it.next();
                }
            }
            final Action[] actions = new Action[cols.length];

            for (int i = 0; i < cols.length; i++) {
                final String cid = (String) cols[i];
                final HeaderCell c = getHeaderCell(cid);
                final VisibleColumnAction a = new VisibleColumnAction(
                        c.getColKey());
                a.setCaption(c.getCaption());
                if (!c.isEnabled()) {
                    a.setCollapsed(true);
                }
                if (noncollapsibleColumns.contains(cid)) {
                    a.setNoncollapsible(true);
                }
                actions[i] = a;
            }
            return actions;
        }

        @Override
        public ApplicationConnection getClient() {
            return client;
        }

        @Override
        public String getPaintableId() {
            return paintableId;
        }

        /**
         * Returns column alignments for visible columns
         */
        public char[] getColumnAlignments() {
            final Iterator<Widget> it = visibleCells.iterator();
            final char[] aligns = new char[visibleCells.size()];
            int colIndex = 0;
            while (it.hasNext()) {
                aligns[colIndex++] = ((HeaderCell) it.next()).getAlign();
            }
            return aligns;
        }

        /**
         * Disables the automatic calculation of all column widths by forcing
         * the widths to be "defined" thus turning off expand ratios and such.
         */
        public void disableAutoColumnWidthCalculation(HeaderCell source) {
            for (HeaderCell cell : availableCells.values()) {
                cell.disableAutoWidthCalculation();
            }
            // fire column resize events for all columns but the source of the
            // resize action, since an event will fire separately for this.
            ArrayList<HeaderCell> columns = new ArrayList<HeaderCell>(
                    availableCells.values());
            columns.remove(source);
            sendColumnWidthUpdates(columns);
            forceRealignColumnHeaders();
        }
    }

    /**
     * A cell in the footer
     */
    public class FooterCell extends Widget {
        private final Element td = DOM.createTD();
        private final Element captionContainer = DOM.createDiv();
        private char align = ALIGN_LEFT;
        private int width = -1;
        private float expandRatio = 0;
        private final String cid;
        boolean definedWidth = false;
        private int naturalWidth = -1;

        public FooterCell(String colId, String headerText) {
            cid = colId;

            setText(headerText);

            // ensure no clipping initially (problem on column additions)
            captionContainer.getStyle().setOverflow(Overflow.VISIBLE);

            DOM.sinkEvents(captionContainer, Event.MOUSEEVENTS);

            DOM.appendChild(td, captionContainer);

            DOM.sinkEvents(td, Event.MOUSEEVENTS | Event.ONDBLCLICK
                    | Event.ONCONTEXTMENU);

            setElement(td);

            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        protected void updateStyleNames(String primaryStyleName) {
            captionContainer.setClassName(primaryStyleName
                    + "-footer-container");
        }

        /**
         * Sets the text of the footer
         * 
         * @param footerText
         *            The text in the footer
         */
        public void setText(String footerText) {
            if (footerText == null || footerText.equals("")) {
                footerText = "&nbsp;";
            }

            DOM.setInnerHTML(captionContainer, footerText);
        }

        /**
         * Set alignment of the text in the cell
         * 
         * @param c
         *            The alignment which can be ALIGN_CENTER, ALIGN_LEFT,
         *            ALIGN_RIGHT
         */
        public void setAlign(char c) {
            if (align != c) {
                switch (c) {
                case ALIGN_CENTER:
                    captionContainer.getStyle().setTextAlign(TextAlign.CENTER);
                    break;
                case ALIGN_RIGHT:
                    captionContainer.getStyle().setTextAlign(TextAlign.RIGHT);
                    break;
                default:
                    captionContainer.getStyle().setTextAlign(TextAlign.LEFT);
                    break;
                }
            }
            align = c;
        }

        /**
         * Get the alignment of the text int the cell
         * 
         * @return Returns either ALIGN_CENTER, ALIGN_LEFT or ALIGN_RIGHT
         */
        public char getAlign() {
            return align;
        }

        /**
         * Sets the width of the cell. This width should not include any
         * possible indent modifications that are present in
         * {@link VScrollTableBody#getMaxIndent()}.
         * 
         * @param w
         *            The width of the cell
         * @param ensureDefinedWidth
         *            Ensures that the given width is not recalculated
         */
        public void setWidth(int w, boolean ensureDefinedWidth) {

            if (ensureDefinedWidth) {
                definedWidth = true;
                // on column resize expand ratio becomes zero
                expandRatio = 0;
            }
            if (width == w) {
                return;
            }
            if (width == -1) {
                // go to default mode, clip content if necessary
                captionContainer.getStyle().clearOverflow();
            }
            width = w;
            if (w == -1) {
                captionContainer.getStyle().clearWidth();
                setWidth("");
            } else {
                /*
                 * Reduce width with one pixel for the right border since the
                 * footers does not have any spacers between them.
                 */
                final int borderWidths = 1;

                // Set the container width (check for negative value)
                captionContainer.getStyle().setPropertyPx("width",
                        Math.max(w - borderWidths, 0));

                /*
                 * if we already have tBody, set the header width properly, if
                 * not defer it. IE will fail with complex float in table header
                 * unless TD width is not explicitly set.
                 */
                if (scrollBody != null) {
                    int maxIndent = scrollBody.getMaxIndent();
                    if (w < maxIndent
                            && tFoot.visibleCells.indexOf(this) == getHierarchyColumnIndex()) {
                        // ensure there's room for the indent
                        w = maxIndent;
                    }
                    int tdWidth = w + scrollBody.getCellExtraWidth()
                            - borderWidths;
                    setWidth(Math.max(tdWidth, 0) + "px");
                } else {
                    Scheduler.get().scheduleDeferred(new Command() {

                        @Override
                        public void execute() {
                            int tdWidth = width;
                            int maxIndent = scrollBody.getMaxIndent();
                            if (tdWidth < maxIndent
                                    && tFoot.visibleCells.indexOf(this) == getHierarchyColumnIndex()) {
                                // ensure there's room for the indent
                                tdWidth = maxIndent;
                            }
                            tdWidth += scrollBody.getCellExtraWidth()
                                    - borderWidths;
                            setWidth(Math.max(tdWidth, 0) + "px");
                        }
                    });
                }
            }
        }

        /**
         * Sets the width to undefined
         */
        public void setUndefinedWidth() {
            definedWidth = false;
            setWidth(-1, false);
        }

        /**
         * Detects if width is fixed by developer on server side or resized to
         * current width by user.
         * 
         * @return true if defined, false if "natural" width
         */
        public boolean isDefinedWidth() {
            return definedWidth && width >= 0;
        }

        /**
         * Returns the pixels width of the footer cell.
         * 
         * @return The width in pixels
         */
        public int getWidth() {
            return width;
        }

        /**
         * Sets the expand ratio of the cell
         * 
         * @param floatAttribute
         *            The expand ratio
         */
        public void setExpandRatio(float floatAttribute) {
            expandRatio = floatAttribute;
        }

        /**
         * Returns the expand ratio of the cell
         * 
         * @return The expand ratio
         */
        public float getExpandRatio() {
            return expandRatio;
        }

        /**
         * Is the cell enabled?
         * 
         * @return True if enabled else False
         */
        public boolean isEnabled() {
            return getParent() != null;
        }

        /**
         * Handle column clicking
         */

        @Override
        public void onBrowserEvent(Event event) {
            if (enabled && event != null) {
                handleCaptionEvent(event);

                if (DOM.eventGetType(event) == Event.ONMOUSEUP) {
                    scrollBodyPanel.setFocus(true);
                }
                boolean stopPropagation = true;
                if (event.getTypeInt() == Event.ONCONTEXTMENU
                        && !client.hasEventListeners(VScrollTable.this,
                                TableConstants.FOOTER_CLICK_EVENT_ID)) {
                    // Show browser context menu if a footer click listener is
                    // not present
                    stopPropagation = false;
                }
                if (stopPropagation) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        }

        /**
         * Handles a event on the captions
         * 
         * @param event
         *            The event to handle
         */
        protected void handleCaptionEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEUP
                    || event.getTypeInt() == Event.ONDBLCLICK) {
                fireFooterClickedEvent(event);
            }
        }

        /**
         * Fires a footer click event after the user has clicked a column footer
         * cell
         * 
         * @param event
         *            The click event
         */
        private void fireFooterClickedEvent(Event event) {
            if (client.hasEventListeners(VScrollTable.this,
                    TableConstants.FOOTER_CLICK_EVENT_ID)) {
                MouseEventDetails details = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event);
                client.updateVariable(paintableId, "footerClickEvent",
                        details.toString(), false);
                client.updateVariable(paintableId, "footerClickCID", cid, true);
            }
        }

        /**
         * Returns the column key of the column
         * 
         * @return The column key
         */
        public String getColKey() {
            return cid;
        }

        /**
         * Saves natural column width if it hasn't been saved already.
         * 
         * @param columnIndex
         * @since 7.3.9
         */
        protected void saveNaturalColumnWidthIfNotSaved(int columnIndex) {
            if (naturalWidth < 0) {
                // This is recently revealed column. Try to detect a proper
                // value (greater of header and data cols)

                final int hw = ((Element) getElement().getLastChild())
                        .getOffsetWidth() + getHeaderPadding();
                if (columnIndex < 0) {
                    columnIndex = 0;
                    for (Iterator<Widget> it = tHead.iterator(); it.hasNext(); columnIndex++) {
                        if (it.next() == this) {
                            break;
                        }
                    }
                }
                final int cw = scrollBody.getColWidth(columnIndex);
                naturalWidth = (hw > cw ? hw : cw);
            }
        }

        /**
         * Detects the natural minimum width for the column of this header cell.
         * If column is resized by user or the width is defined by server the
         * actual width is returned. Else the natural min width is returned.
         * 
         * @param columnIndex
         *            column index hint, if -1 (unknown) it will be detected
         * 
         * @return
         */
        public int getNaturalColumnWidth(int columnIndex) {
            final int iw = columnIndex == getHierarchyColumnIndex() ? scrollBody
                    .getMaxIndent() : 0;
            saveNaturalColumnWidthIfNotSaved(columnIndex);
            if (isDefinedWidth()) {
                if (iw > width) {
                    return iw;
                }
                return width;
            } else {
                if (iw > naturalWidth) {
                    return iw;
                } else {
                    return naturalWidth;
                }
            }
        }

        public void setNaturalMinimumColumnWidth(int w) {
            naturalWidth = w;
        }
    }

    /**
     * HeaderCell that is header cell for row headers.
     * 
     * Reordering disabled and clicking on it resets sorting.
     */
    public class RowHeadersFooterCell extends FooterCell {

        RowHeadersFooterCell() {
            super(ROW_HEADER_COLUMN_KEY, "");
        }

        @Override
        protected void handleCaptionEvent(Event event) {
            // NOP: RowHeaders cannot be reordered
            // TODO It'd be nice to reset sorting here
        }
    }

    /**
     * The footer of the table which can be seen in the bottom of the Table.
     */
    public class TableFooter extends Panel {

        private static final int WRAPPER_WIDTH = 900000;

        ArrayList<Widget> visibleCells = new ArrayList<Widget>();
        HashMap<String, FooterCell> availableCells = new HashMap<String, FooterCell>();

        Element div = DOM.createDiv();
        Element hTableWrapper = DOM.createDiv();
        Element hTableContainer = DOM.createDiv();
        Element table = DOM.createTable();
        Element headerTableBody = DOM.createTBody();
        Element tr = DOM.createTR();

        public TableFooter() {

            hTableWrapper.getStyle().setOverflow(Overflow.HIDDEN);

            DOM.appendChild(table, headerTableBody);
            DOM.appendChild(headerTableBody, tr);
            DOM.appendChild(hTableContainer, table);
            DOM.appendChild(hTableWrapper, hTableContainer);
            DOM.appendChild(div, hTableWrapper);
            setElement(div);

            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersFooterCell());

            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        protected void updateStyleNames(String primaryStyleName) {
            hTableWrapper.setClassName(primaryStyleName + "-footer");
            setStyleName(primaryStyleName + "-footer-wrap");
            for (FooterCell c : availableCells.values()) {
                c.updateStyleNames(primaryStyleName);
            }
        }

        @Override
        public void clear() {
            for (String cid : availableCells.keySet()) {
                removeCell(cid);
            }
            availableCells.clear();
            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersFooterCell());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.Panel#remove(com.google.gwt.user.client
         * .ui.Widget)
         */

        @Override
        public boolean remove(Widget w) {
            if (visibleCells.contains(w)) {
                visibleCells.remove(w);
                orphan(w);
                DOM.removeChild(DOM.getParent(w.getElement()), w.getElement());
                return true;
            }
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
         */

        @Override
        public Iterator<Widget> iterator() {
            return visibleCells.iterator();
        }

        /**
         * Gets a footer cell which represents the given columnId
         * 
         * @param cid
         *            The columnId
         * 
         * @return The cell
         */
        public FooterCell getFooterCell(String cid) {
            return availableCells.get(cid);
        }

        /**
         * Gets a footer cell by using a column index
         * 
         * @param index
         *            The index of the column
         * @return The Cell
         */
        public FooterCell getFooterCell(int index) {
            if (index < visibleCells.size()) {
                return (FooterCell) visibleCells.get(index);
            } else {
                return null;
            }
        }

        /**
         * Updates the cells contents when updateUIDL request is received
         * 
         * @param uidl
         *            The UIDL
         */
        public void updateCellsFromUIDL(UIDL uidl) {
            Iterator<?> columnIterator = uidl.getChildIterator();
            HashSet<String> updated = new HashSet<String>();
            while (columnIterator.hasNext()) {
                final UIDL col = (UIDL) columnIterator.next();
                final String cid = col.getStringAttribute("cid");
                updated.add(cid);

                String caption = col.hasAttribute("fcaption") ? col
                        .getStringAttribute("fcaption") : "";
                FooterCell c = getFooterCell(cid);
                if (c == null) {
                    c = new FooterCell(cid, caption);
                    availableCells.put(cid, c);
                    if (initializedAndAttached) {
                        // we will need a column width recalculation
                        initializedAndAttached = false;
                        initialContentReceived = false;
                        isNewBody = true;
                    }
                } else {
                    c.setText(caption);
                }

                if (col.hasAttribute("align")) {
                    c.setAlign(col.getStringAttribute("align").charAt(0));
                } else {
                    c.setAlign(ALIGN_LEFT);

                }
                if (col.hasAttribute("width")) {
                    if (scrollBody == null || isNewBody) {
                        // Already updated by setColWidth called from
                        // TableHeads.updateCellsFromUIDL in case of a server
                        // side resize
                        final int width = col.getIntAttribute("width");
                        c.setWidth(width, true);
                    }
                } else if (recalcWidths) {
                    c.setUndefinedWidth();
                }
                if (col.hasAttribute("er")) {
                    c.setExpandRatio(col.getFloatAttribute("er"));
                }
                if (col.hasAttribute("collapsed")) {
                    // ensure header is properly removed from parent (case when
                    // collapsing happens via servers side api)
                    if (c.isAttached()) {
                        c.removeFromParent();
                        headerChangedDuringUpdate = true;
                    }
                }
            }

            // check for orphaned header cells
            for (Iterator<String> cit = availableCells.keySet().iterator(); cit
                    .hasNext();) {
                String cid = cit.next();
                if (!updated.contains(cid)) {
                    removeCell(cid);
                    cit.remove();
                }
            }
        }

        /**
         * Set a footer cell for a specified column index
         * 
         * @param index
         *            The index
         * @param cell
         *            The footer cell
         */
        public void setFooterCell(int index, FooterCell cell) {
            if (cell.isEnabled()) {
                // we're moving the cell
                DOM.removeChild(tr, cell.getElement());
                orphan(cell);
                visibleCells.remove(cell);
            }
            if (index < visibleCells.size()) {
                // insert to right slot
                DOM.insertChild(tr, cell.getElement(), index);
                adopt(cell);
                visibleCells.add(index, cell);
            } else if (index == visibleCells.size()) {
                // simply append
                DOM.appendChild(tr, cell.getElement());
                adopt(cell);
                visibleCells.add(cell);
            } else {
                throw new RuntimeException(
                        "Header cells must be appended in order");
            }
        }

        /**
         * Remove a cell by using the columnId
         * 
         * @param colKey
         *            The columnId to remove
         */
        public void removeCell(String colKey) {
            final FooterCell c = getFooterCell(colKey);
            remove(c);
        }

        /**
         * Enable a column (Sets the footer cell)
         * 
         * @param cid
         *            The columnId
         * @param index
         *            The index of the column
         */
        public void enableColumn(String cid, int index) {
            final FooterCell c = getFooterCell(cid);
            if (!c.isEnabled() || getFooterCell(index) != c) {
                setFooterCell(index, c);
                if (initializedAndAttached) {
                    headerChangedDuringUpdate = true;
                }
            }
        }

        /**
         * Disable browser measurement of the table width
         */
        public void disableBrowserIntelligence() {
            hTableContainer.getStyle().setWidth(WRAPPER_WIDTH, Unit.PX);
        }

        /**
         * Enable browser measurement of the table width
         */
        public void enableBrowserIntelligence() {
            hTableContainer.getStyle().clearWidth();
        }

        /**
         * Set the horizontal position in the cell in the footer. This is done
         * when a horizontal scrollbar is present.
         * 
         * @param scrollLeft
         *            The value of the leftScroll
         */
        public void setHorizontalScrollPosition(int scrollLeft) {
            hTableWrapper.setScrollLeft(scrollLeft);
        }

        /**
         * Swap cells when the column are dragged
         * 
         * @param oldIndex
         *            The old index of the cell
         * @param newIndex
         *            The new index of the cell
         */
        public void moveCell(int oldIndex, int newIndex) {
            final FooterCell hCell = getFooterCell(oldIndex);
            final Element cell = hCell.getElement();

            visibleCells.remove(oldIndex);
            DOM.removeChild(tr, cell);

            DOM.insertChild(tr, cell, newIndex);
            visibleCells.add(newIndex, hCell);
        }
    }

    /**
     * This Panel can only contain VScrollTableRow type of widgets. This
     * "simulates" very large table, keeping spacers which take room of
     * unrendered rows.
     * 
     */
    public class VScrollTableBody extends Panel {

        public static final int DEFAULT_ROW_HEIGHT = 24;

        private double rowHeight = -1;

        private final LinkedList<Widget> renderedRows = new LinkedList<Widget>();

        /**
         * Due some optimizations row height measuring is deferred and initial
         * set of rows is rendered detached. Flag set on when table body has
         * been attached in dom and rowheight has been measured.
         */
        private boolean tBodyMeasurementsDone = false;

        Element preSpacer = DOM.createDiv();
        Element postSpacer = DOM.createDiv();

        Element container = DOM.createDiv();

        TableSectionElement tBodyElement = Document.get().createTBodyElement();
        Element table = DOM.createTable();

        private int firstRendered;
        private int lastRendered;

        private char[] aligns;

        protected VScrollTableBody() {
            constructDOM();
            setElement(container);
        }

        public void setLastRendered(int lastRendered) {
            if (totalRows >= 0 && lastRendered > totalRows) {
                VConsole.log("setLastRendered: " + this.lastRendered + " -> "
                        + lastRendered);
                this.lastRendered = totalRows - 1;
            } else {
                this.lastRendered = lastRendered;
            }
        }

        public int getLastRendered() {

            return lastRendered;
        }

        public int getFirstRendered() {

            return firstRendered;
        }

        public VScrollTableRow getRowByRowIndex(int indexInTable) {
            int internalIndex = indexInTable - firstRendered;
            if (internalIndex >= 0 && internalIndex < renderedRows.size()) {
                return (VScrollTableRow) renderedRows.get(internalIndex);
            } else {
                return null;
            }
        }

        /**
         * @return the height of scrollable body, subpixels ceiled.
         */
        public int getRequiredHeight() {
            return preSpacer.getOffsetHeight() + postSpacer.getOffsetHeight()
                    + WidgetUtil.getRequiredHeight(table);
        }

        private void constructDOM() {
            if (BrowserInfo.get().isIE()) {
                table.setPropertyInt("cellSpacing", 0);
            }

            table.appendChild(tBodyElement);
            DOM.appendChild(container, preSpacer);
            DOM.appendChild(container, table);
            DOM.appendChild(container, postSpacer);
            if (BrowserInfo.get().requiresTouchScrollDelegate()) {
                NodeList<Node> childNodes = container.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Element item = (Element) childNodes.getItem(i);
                    item.getStyle().setProperty("webkitTransform",
                            "translate3d(0,0,0)");
                }
            }
            updateStyleNames(VScrollTable.this.getStylePrimaryName());
        }

        protected void updateStyleNames(String primaryStyleName) {
            table.setClassName(primaryStyleName + "-table");
            preSpacer.setClassName(primaryStyleName + "-row-spacer");
            postSpacer.setClassName(primaryStyleName + "-row-spacer");
            for (Widget w : renderedRows) {
                VScrollTableRow row = (VScrollTableRow) w;
                row.updateStyleNames(primaryStyleName);
            }
        }

        public int getAvailableWidth() {
            int availW = scrollBodyPanel.getOffsetWidth() - getBorderWidth();
            return availW;
        }

        public void renderInitialRows(UIDL rowData, int firstIndex, int rows) {
            firstRendered = firstIndex;
            setLastRendered(firstIndex + rows - 1);
            final Iterator<?> it = rowData.getChildIterator();
            aligns = tHead.getColumnAlignments();
            while (it.hasNext()) {
                final VScrollTableRow row = createRow((UIDL) it.next(), aligns);
                addRow(row);
            }
            if (isAttached()) {
                fixSpacers();
            }
        }

        public void renderRows(UIDL rowData, int firstIndex, int rows) {
            // FIXME REVIEW
            aligns = tHead.getColumnAlignments();
            final Iterator<?> it = rowData.getChildIterator();
            if (firstIndex == lastRendered + 1) {
                while (it.hasNext()) {
                    final VScrollTableRow row = prepareRow((UIDL) it.next());
                    addRow(row);
                    setLastRendered(lastRendered + 1);
                }
                fixSpacers();
            } else if (firstIndex + rows == firstRendered) {
                final VScrollTableRow[] rowArray = new VScrollTableRow[rows];
                int i = rows;

                while (it.hasNext()) {
                    i--;
                    rowArray[i] = prepareRow((UIDL) it.next());
                }

                for (i = 0; i < rows; i++) {
                    addRowBeforeFirstRendered(rowArray[i]);
                    firstRendered--;
                }
            } else {
                // completely new set of rows

                // there can't be sanity checks for last rendered within this
                // while loop regardless of what has been set previously, so
                // change it temporarily to true and then return the original
                // value
                boolean temp = postponeSanityCheckForLastRendered;
                postponeSanityCheckForLastRendered = true;
                while (lastRendered + 1 > firstRendered) {
                    unlinkRow(false);
                }
                postponeSanityCheckForLastRendered = temp;
                VScrollTableRow row = prepareRow((UIDL) it.next());
                firstRendered = firstIndex;
                setLastRendered(firstIndex - 1);
                addRow(row);
                setLastRendered(lastRendered + 1);
                setContainerHeight();
                fixSpacers();

                while (it.hasNext()) {
                    addRow(prepareRow((UIDL) it.next()));
                    setLastRendered(lastRendered + 1);
                }

                fixSpacers();
            }

            // this may be a new set of rows due content change,
            // ensure we have proper cache rows
            ensureCacheFilled();
        }

        /**
         * Ensure we have the correct set of rows on client side, e.g. if the
         * content on the server side has changed, or the client scroll position
         * has changed since the last request.
         */
        protected void ensureCacheFilled() {

            /**
             * Fixes cache issue #13576 where unnecessary rows are fetched
             */
            if (isLazyScrollerActive()) {
                return;
            }

            int reactFirstRow = (int) (firstRowInViewPort - pageLength
                    * cache_react_rate);
            int reactLastRow = (int) (firstRowInViewPort + pageLength + pageLength
                    * cache_react_rate);
            if (reactFirstRow < 0) {
                reactFirstRow = 0;
            }
            if (reactLastRow >= totalRows) {
                reactLastRow = totalRows - 1;
            }
            if (lastRendered < reactFirstRow || firstRendered > reactLastRow) {
                /*
                 * #8040 - scroll position is completely changed since the
                 * latest request, so request a new set of rows.
                 * 
                 * TODO: We should probably check whether the fetched rows match
                 * the current scroll position right when they arrive, so as to
                 * not waste time rendering a set of rows that will never be
                 * visible...
                 */
                rowRequestHandler.triggerRowFetch(reactFirstRow, reactLastRow
                        - reactFirstRow + 1, 1);
            } else if (lastRendered < reactLastRow) {
                // get some cache rows below visible area
                rowRequestHandler.triggerRowFetch(lastRendered + 1,
                        reactLastRow - lastRendered, 1);
            } else if (firstRendered > reactFirstRow) {
                /*
                 * Branch for fetching cache above visible area.
                 * 
                 * If cache needed for both before and after visible area, this
                 * will be rendered after-cache is received and rendered. So in
                 * some rare situations the table may make two cache visits to
                 * server.
                 */
                rowRequestHandler.triggerRowFetch(reactFirstRow, firstRendered
                        - reactFirstRow, 1);
            }
        }

        /**
         * Inserts rows as provided in the rowData starting at firstIndex.
         * 
         * @param rowData
         * @param firstIndex
         * @param rows
         *            the number of rows
         * @return a list of the rows added.
         */
        protected List<VScrollTableRow> insertRows(UIDL rowData,
                int firstIndex, int rows) {
            aligns = tHead.getColumnAlignments();
            final Iterator<?> it = rowData.getChildIterator();
            List<VScrollTableRow> insertedRows = new ArrayList<VScrollTableRow>();

            if (firstIndex == lastRendered + 1) {
                while (it.hasNext()) {
                    final VScrollTableRow row = prepareRow((UIDL) it.next());
                    addRow(row);
                    insertedRows.add(row);
                    if (postponeSanityCheckForLastRendered) {
                        lastRendered++;
                    } else {
                        setLastRendered(lastRendered + 1);
                    }
                }
                fixSpacers();
            } else if (firstIndex + rows == firstRendered) {
                final VScrollTableRow[] rowArray = new VScrollTableRow[rows];
                int i = rows;
                while (it.hasNext()) {
                    i--;
                    rowArray[i] = prepareRow((UIDL) it.next());
                }
                for (i = 0; i < rows; i++) {
                    addRowBeforeFirstRendered(rowArray[i]);
                    insertedRows.add(rowArray[i]);
                    firstRendered--;
                }
            } else {
                // insert in the middle
                int ix = firstIndex;
                while (it.hasNext()) {
                    VScrollTableRow row = prepareRow((UIDL) it.next());
                    insertRowAt(row, ix);
                    insertedRows.add(row);
                    if (postponeSanityCheckForLastRendered) {
                        lastRendered++;
                    } else {
                        setLastRendered(lastRendered + 1);
                    }
                    ix++;
                }
                fixSpacers();
            }
            return insertedRows;
        }

        protected List<VScrollTableRow> insertAndReindexRows(UIDL rowData,
                int firstIndex, int rows) {
            List<VScrollTableRow> inserted = insertRows(rowData, firstIndex,
                    rows);
            int actualIxOfFirstRowAfterInserted = firstIndex + rows
                    - firstRendered;
            for (int ix = actualIxOfFirstRowAfterInserted; ix < renderedRows
                    .size(); ix++) {
                VScrollTableRow r = (VScrollTableRow) renderedRows.get(ix);
                r.setIndex(r.getIndex() + rows);
            }
            setContainerHeight();
            return inserted;
        }

        protected void insertRowsDeleteBelow(UIDL rowData, int firstIndex,
                int rows) {
            unlinkAllRowsStartingAt(firstIndex);
            insertRows(rowData, firstIndex, rows);
            setContainerHeight();
        }

        /**
         * This method is used to instantiate new rows for this table. It
         * automatically sets correct widths to rows cells and assigns correct
         * client reference for child widgets.
         * 
         * This method can be called only after table has been initialized
         * 
         * @param uidl
         */
        private VScrollTableRow prepareRow(UIDL uidl) {
            final VScrollTableRow row = createRow(uidl, aligns);
            row.initCellWidths();
            return row;
        }

        protected VScrollTableRow createRow(UIDL uidl, char[] aligns2) {
            if (uidl.hasAttribute("gen_html")) {
                // This is a generated row.
                return new VScrollTableGeneratedRow(uidl, aligns2);
            }
            return new VScrollTableRow(uidl, aligns2);
        }

        private void addRowBeforeFirstRendered(VScrollTableRow row) {
            row.setIndex(firstRendered - 1);
            if (row.isSelected()) {
                row.addStyleName("v-selected");
            }
            tBodyElement.insertBefore(row.getElement(),
                    tBodyElement.getFirstChild());
            adopt(row);
            renderedRows.add(0, row);
        }

        private void addRow(VScrollTableRow row) {
            row.setIndex(firstRendered + renderedRows.size());
            if (row.isSelected()) {
                row.addStyleName("v-selected");
            }
            tBodyElement.appendChild(row.getElement());
            // Add to renderedRows before adopt so iterator() will return also
            // this row if called in an attach handler (#9264)
            renderedRows.add(row);
            adopt(row);
        }

        private void insertRowAt(VScrollTableRow row, int index) {
            row.setIndex(index);
            if (row.isSelected()) {
                row.addStyleName("v-selected");
            }
            if (index > 0) {
                VScrollTableRow sibling = getRowByRowIndex(index - 1);
                tBodyElement
                        .insertAfter(row.getElement(), sibling.getElement());
            } else {
                VScrollTableRow sibling = getRowByRowIndex(index);
                tBodyElement.insertBefore(row.getElement(),
                        sibling.getElement());
            }
            adopt(row);
            int actualIx = index - firstRendered;
            renderedRows.add(actualIx, row);
        }

        @Override
        public Iterator<Widget> iterator() {
            return renderedRows.iterator();
        }

        /**
         * @return false if couldn't remove row
         */
        protected boolean unlinkRow(boolean fromBeginning) {
            if (lastRendered - firstRendered < 0) {
                return false;
            }
            int actualIx;
            if (fromBeginning) {
                actualIx = 0;
                firstRendered++;
            } else {
                actualIx = renderedRows.size() - 1;
                if (postponeSanityCheckForLastRendered) {
                    --lastRendered;
                } else {
                    setLastRendered(lastRendered - 1);
                }
            }
            if (actualIx >= 0) {
                unlinkRowAtActualIndex(actualIx);
                fixSpacers();
                return true;
            }
            return false;
        }

        protected void unlinkRows(int firstIndex, int count) {
            if (count < 1) {
                return;
            }
            if (firstRendered > firstIndex
                    && firstRendered < firstIndex + count) {
                count = count - (firstRendered - firstIndex);
                firstIndex = firstRendered;
            }
            int lastIndex = firstIndex + count - 1;
            if (lastRendered < lastIndex) {
                lastIndex = lastRendered;
            }
            for (int ix = lastIndex; ix >= firstIndex; ix--) {
                unlinkRowAtActualIndex(actualIndex(ix));
                if (postponeSanityCheckForLastRendered) {
                    // partialUpdate handles sanity check later
                    lastRendered--;
                } else {
                    setLastRendered(lastRendered - 1);
                }
            }
            fixSpacers();
        }

        protected void unlinkAndReindexRows(int firstIndex, int count) {
            unlinkRows(firstIndex, count);
            int actualFirstIx = firstIndex - firstRendered;
            for (int ix = actualFirstIx; ix < renderedRows.size(); ix++) {
                VScrollTableRow r = (VScrollTableRow) renderedRows.get(ix);
                r.setIndex(r.getIndex() - count);
            }
            setContainerHeight();
        }

        protected void unlinkAllRowsStartingAt(int index) {
            if (firstRendered > index) {
                index = firstRendered;
            }
            for (int ix = renderedRows.size() - 1; ix >= index; ix--) {
                unlinkRowAtActualIndex(actualIndex(ix));
                setLastRendered(lastRendered - 1);
            }
            fixSpacers();
        }

        private int actualIndex(int index) {
            return index - firstRendered;
        }

        private void unlinkRowAtActualIndex(int index) {
            final VScrollTableRow toBeRemoved = (VScrollTableRow) renderedRows
                    .get(index);
            tBodyElement.removeChild(toBeRemoved.getElement());
            orphan(toBeRemoved);
            renderedRows.remove(index);
        }

        @Override
        public boolean remove(Widget w) {
            throw new UnsupportedOperationException();
        }

        /**
         * Fix container blocks height according to totalRows to avoid
         * "bouncing" when scrolling
         */
        private void setContainerHeight() {
            fixSpacers();
            container.getStyle().setHeight(measureRowHeightOffset(totalRows),
                    Unit.PX);
        }

        private void fixSpacers() {
            int prepx = measureRowHeightOffset(firstRendered);
            if (prepx < 0) {
                prepx = 0;
            }
            preSpacer.getStyle().setPropertyPx("height", prepx);
            int postpx;
            if (pageLength == 0 && totalRows == pageLength) {
                /*
                 * TreeTable depends on having lastRendered out of sync in some
                 * situations, which makes this method miss the special
                 * situation in which one row worth of post spacer to be added
                 * if there are no rows in the table. #9203
                 */
                postpx = measureRowHeightOffset(1);
            } else {
                postpx = measureRowHeightOffset(totalRows - 1)
                        - measureRowHeightOffset(lastRendered);
            }

            if (postpx < 0) {
                postpx = 0;
            }
            postSpacer.getStyle().setPropertyPx("height", postpx);
        }

        public double getRowHeight() {
            return getRowHeight(false);
        }

        public double getRowHeight(boolean forceUpdate) {
            if (tBodyMeasurementsDone && !forceUpdate) {
                return rowHeight;
            } else {
                if (tBodyElement.getRows().getLength() > 0) {
                    int tableHeight = getTableHeight();
                    int rowCount = tBodyElement.getRows().getLength();
                    rowHeight = tableHeight / (double) rowCount;
                } else {
                    // Special cases if we can't just measure the current rows
                    if (!Double.isNaN(lastKnownRowHeight)) {
                        // Use previous value if available
                        if (BrowserInfo.get().isIE()) {
                            /*
                             * IE needs to reflow the table element at this
                             * point to work correctly (e.g.
                             * com.vaadin.tests.components.table.
                             * ContainerSizeChange) - the other code paths
                             * already trigger reflows, but here it must be done
                             * explicitly.
                             */
                            getTableHeight();
                        }
                        rowHeight = lastKnownRowHeight;
                    } else if (isAttached()) {
                        // measure row height by adding a dummy row
                        VScrollTableRow scrollTableRow = new VScrollTableRow();
                        tBodyElement.appendChild(scrollTableRow.getElement());
                        getRowHeight(forceUpdate);
                        tBodyElement.removeChild(scrollTableRow.getElement());
                    } else {
                        // TODO investigate if this can never happen anymore
                        return DEFAULT_ROW_HEIGHT;
                    }
                }
                lastKnownRowHeight = rowHeight;
                tBodyMeasurementsDone = true;
                return rowHeight;
            }
        }

        public int getTableHeight() {
            return table.getOffsetHeight();
        }

        /**
         * Returns the width available for column content.
         * 
         * @param columnIndex
         * @return
         */
        public int getColWidth(int columnIndex) {
            if (tBodyMeasurementsDone) {
                if (renderedRows.isEmpty()) {
                    // no rows yet rendered
                    return 0;
                }
                for (Widget row : renderedRows) {
                    if (!(row instanceof VScrollTableGeneratedRow)) {
                        TableRowElement tr = row.getElement().cast();
                        Element wrapperdiv = tr.getCells().getItem(columnIndex)
                                .getFirstChildElement().cast();
                        return wrapperdiv.getOffsetWidth();
                    }
                }
                return 0;
            } else {
                return 0;
            }
        }

        /**
         * Sets the content width of a column.
         * 
         * Due IE limitation, we must set the width to a wrapper elements inside
         * table cells (with overflow hidden, which does not work on td
         * elements).
         * 
         * To get this work properly crossplatform, we will also set the width
         * of td.
         * 
         * @param colIndex
         * @param w
         */
        public void setColWidth(int colIndex, int w) {
            for (Widget row : renderedRows) {
                ((VScrollTableRow) row).setCellWidth(colIndex, w);
            }
        }

        private int cellExtraWidth = -1;

        /**
         * Method to return the space used for cell paddings + border.
         */
        private int getCellExtraWidth() {
            if (cellExtraWidth < 0) {
                detectExtrawidth();
            }
            return cellExtraWidth;
        }

        /**
         * This method exists for the needs of {@link VTreeTable} only. May be
         * removed or replaced in the future.</br> </br> Returns the maximum
         * indent of the hierarcyColumn, if applicable.
         * 
         * @see {@link VScrollTable#getHierarchyColumnIndex()}
         * 
         * @return maximum indent in pixels
         */
        protected int getMaxIndent() {
            return 0;
        }

        /**
         * This method exists for the needs of {@link VTreeTable} only. May be
         * removed or replaced in the future.</br> </br> Calculates the maximum
         * indent of the hierarcyColumn, if applicable.
         */
        protected void calculateMaxIndent() {
            // NOP
        }

        private void detectExtrawidth() {
            NodeList<TableRowElement> rows = tBodyElement.getRows();
            if (rows.getLength() == 0) {
                /* need to temporary add empty row and detect */
                VScrollTableRow scrollTableRow = new VScrollTableRow();
                scrollTableRow.updateStyleNames(VScrollTable.this
                        .getStylePrimaryName());
                tBodyElement.appendChild(scrollTableRow.getElement());
                detectExtrawidth();
                tBodyElement.removeChild(scrollTableRow.getElement());
            } else {
                boolean noCells = false;
                TableRowElement item = rows.getItem(0);
                TableCellElement firstTD = item.getCells().getItem(0);
                if (firstTD == null) {
                    // content is currently empty, we need to add a fake cell
                    // for measuring
                    noCells = true;
                    VScrollTableRow next = (VScrollTableRow) iterator().next();
                    boolean sorted = tHead.getHeaderCell(0) != null ? tHead
                            .getHeaderCell(0).isSorted() : false;
                    next.addCell(null, "", ALIGN_LEFT, "", true, sorted);
                    firstTD = item.getCells().getItem(0);
                }
                com.google.gwt.dom.client.Element wrapper = firstTD
                        .getFirstChildElement();
                cellExtraWidth = firstTD.getOffsetWidth()
                        - wrapper.getOffsetWidth();
                if (noCells) {
                    firstTD.getParentElement().removeChild(firstTD);
                }
            }
        }

        public void moveCol(int oldIndex, int newIndex) {

            // loop all rows and move given index to its new place
            final Iterator<?> rows = iterator();
            while (rows.hasNext()) {
                final VScrollTableRow row = (VScrollTableRow) rows.next();

                final Element td = DOM.getChild(row.getElement(), oldIndex);
                if (td != null) {
                    DOM.removeChild(row.getElement(), td);

                    DOM.insertChild(row.getElement(), td, newIndex);
                }
            }

        }

        /**
         * Restore row visibility which is set to "none" when the row is
         * rendered (due a performance optimization).
         */
        private void restoreRowVisibility() {
            for (Widget row : renderedRows) {
                row.getElement().getStyle().setProperty("visibility", "");
            }
        }

        public int indexOf(Widget row) {
            int relIx = -1;
            for (int ix = 0; ix < renderedRows.size(); ix++) {
                if (renderedRows.get(ix) == row) {
                    relIx = ix;
                    break;
                }
            }
            if (relIx >= 0) {
                return firstRendered + relIx;
            }
            return -1;
        }

        public class VScrollTableRow extends Panel implements ActionOwner,
                ContextMenuOwner {

            private static final int TOUCHSCROLL_TIMEOUT = 100;
            private static final int DRAGMODE_MULTIROW = 2;
            protected ArrayList<Widget> childWidgets = new ArrayList<Widget>();
            private boolean selected = false;
            protected final int rowKey;

            private String[] actionKeys = null;
            private final TableRowElement rowElement;
            private int index;
            private Event touchStart;

            private static final int TOUCH_CONTEXT_MENU_TIMEOUT = 500;
            private Timer contextTouchTimeout;
            private Timer dragTouchTimeout;
            private int touchStartY;
            private int touchStartX;

            private TouchContextProvider touchContextProvider = new TouchContextProvider(
                    this);

            private TooltipInfo tooltipInfo = null;
            private Map<TableCellElement, TooltipInfo> cellToolTips = new HashMap<TableCellElement, TooltipInfo>();
            private boolean isDragging = false;
            private String rowStyle = null;
            protected boolean applyZeroWidthFix = true;

            private VScrollTableRow(int rowKey) {
                this.rowKey = rowKey;
                rowElement = Document.get().createTRElement();
                setElement(rowElement);
                DOM.sinkEvents(getElement(), Event.MOUSEEVENTS
                        | Event.TOUCHEVENTS | Event.ONDBLCLICK
                        | Event.ONCONTEXTMENU | VTooltip.TOOLTIP_EVENTS);
            }

            public VScrollTableRow(UIDL uidl, char[] aligns) {
                this(uidl.getIntAttribute("key"));

                /*
                 * Rendering the rows as hidden improves Firefox and Safari
                 * performance drastically.
                 */
                getElement().getStyle().setProperty("visibility", "hidden");

                rowStyle = uidl.getStringAttribute("rowstyle");
                updateStyleNames(VScrollTable.this.getStylePrimaryName());

                String rowDescription = uidl.getStringAttribute("rowdescr");
                if (rowDescription != null && !rowDescription.equals("")) {
                    tooltipInfo = new TooltipInfo(rowDescription, null, this);
                } else {
                    tooltipInfo = null;
                }

                tHead.getColumnAlignments();
                int col = 0;
                int visibleColumnIndex = -1;

                // row header
                if (showRowHeaders) {
                    boolean sorted = tHead.getHeaderCell(col).isSorted();
                    addCell(uidl, buildCaptionHtmlSnippet(uidl), aligns[col++],
                            "rowheader", true, sorted);
                    visibleColumnIndex++;
                }

                if (uidl.hasAttribute("al")) {
                    actionKeys = uidl.getStringArrayAttribute("al");
                }

                addCellsFromUIDL(uidl, aligns, col, visibleColumnIndex);

                if (uidl.hasAttribute("selected") && !isSelected()) {
                    toggleSelection();
                }
            }

            protected void updateStyleNames(String primaryStyleName) {

                if (getStylePrimaryName().contains("odd")) {
                    setStyleName(primaryStyleName + "-row-odd");
                } else {
                    setStyleName(primaryStyleName + "-row");
                }

                if (rowStyle != null) {
                    addStyleName(primaryStyleName + "-row-" + rowStyle);
                }

                for (int i = 0; i < rowElement.getChildCount(); i++) {
                    TableCellElement cell = (TableCellElement) rowElement
                            .getChild(i);
                    updateCellStyleNames(cell, primaryStyleName);
                }
            }

            public TooltipInfo getTooltipInfo() {
                return tooltipInfo;
            }

            /**
             * Add a dummy row, used for measurements if Table is empty.
             */
            public VScrollTableRow() {
                this(0);
                addCell(null, "_", 'b', "", true, false);
            }

            protected void initCellWidths() {
                final int cells = tHead.getVisibleCellCount();
                for (int i = 0; i < cells; i++) {
                    int w = VScrollTable.this.getColWidth(getColKeyByIndex(i));
                    if (w < 0) {
                        w = 0;
                    }
                    setCellWidth(i, w);
                }
            }

            protected void setCellWidth(int cellIx, int width) {
                final Element cell = DOM.getChild(getElement(), cellIx);
                Style wrapperStyle = cell.getFirstChildElement().getStyle();
                int wrapperWidth = width;
                if (BrowserInfo.get().isWebkit()
                        || BrowserInfo.get().isOpera10()) {
                    /*
                     * Some versions of Webkit and Opera ignore the width
                     * definition of zero width table cells. Instead, use 1px
                     * and compensate with a negative margin.
                     */
                    if (applyZeroWidthFix && width == 0) {
                        wrapperWidth = 1;
                        wrapperStyle.setMarginRight(-1, Unit.PX);
                    } else {
                        wrapperStyle.clearMarginRight();
                    }
                }
                wrapperStyle.setPropertyPx("width", wrapperWidth);
                cell.getStyle().setPropertyPx("width", width);
            }

            protected void addCellsFromUIDL(UIDL uidl, char[] aligns, int col,
                    int visibleColumnIndex) {
                final Iterator<?> cells = uidl.getChildIterator();
                while (cells.hasNext()) {
                    final Object cell = cells.next();
                    visibleColumnIndex++;

                    String columnId = visibleColOrder[visibleColumnIndex];

                    String style = "";
                    if (uidl.hasAttribute("style-" + columnId)) {
                        style = uidl.getStringAttribute("style-" + columnId);
                    }

                    String description = null;
                    if (uidl.hasAttribute("descr-" + columnId)) {
                        description = uidl.getStringAttribute("descr-"
                                + columnId);
                    }

                    boolean sorted = tHead.getHeaderCell(col).isSorted();
                    if (cell instanceof String) {
                        addCell(uidl, cell.toString(), aligns[col++], style,
                                isRenderHtmlInCells(), sorted, description);
                    } else {
                        final ComponentConnector cellContent = client
                                .getPaintable((UIDL) cell);

                        addCell(uidl, cellContent.getWidget(), aligns[col++],
                                style, sorted, description);
                    }
                }
            }

            /**
             * Overriding this and returning true causes all text cells to be
             * rendered as HTML.
             * 
             * @return always returns false in the default implementation
             */
            protected boolean isRenderHtmlInCells() {
                return false;
            }

            /**
             * Detects whether row is visible in tables viewport.
             * 
             * @return
             */
            public boolean isInViewPort() {
                int absoluteTop = getAbsoluteTop();
                int absoluteBottom = absoluteTop + getOffsetHeight();
                int viewPortTop = scrollBodyPanel.getAbsoluteTop();
                int viewPortBottom = viewPortTop
                        + scrollBodyPanel.getOffsetHeight();
                return absoluteBottom > viewPortTop
                        && absoluteTop < viewPortBottom;
            }

            /**
             * Makes a check based on indexes whether the row is before the
             * compared row.
             * 
             * @param row1
             * @return true if this rows index is smaller than in the row1
             */
            public boolean isBefore(VScrollTableRow row1) {
                return getIndex() < row1.getIndex();
            }

            /**
             * Sets the index of the row in the whole table. Currently used just
             * to set even/odd classname
             * 
             * @param indexInWholeTable
             */
            private void setIndex(int indexInWholeTable) {
                index = indexInWholeTable;
                boolean isOdd = indexInWholeTable % 2 == 0;
                // Inverted logic to be backwards compatible with earlier 6.4.
                // It is very strange because rows 1,3,5 are considered "even"
                // and 2,4,6 "odd".
                //
                // First remove any old styles so that both styles aren't
                // applied when indexes are updated.
                String primaryStyleName = getStylePrimaryName();
                if (primaryStyleName != null && !primaryStyleName.equals("")) {
                    removeStyleName(getStylePrimaryName());
                }
                if (!isOdd) {
                    addStyleName(VScrollTable.this.getStylePrimaryName()
                            + "-row-odd");
                } else {
                    addStyleName(VScrollTable.this.getStylePrimaryName()
                            + "-row");
                }
            }

            public int getIndex() {
                return index;
            }

            @Override
            protected void onDetach() {
                super.onDetach();
                client.getContextMenu().ensureHidden(this);
            }

            public String getKey() {
                return String.valueOf(rowKey);
            }

            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean sorted) {
                addCell(rowUidl, text, align, style, textIsHTML, sorted, null);
            }

            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean sorted,
                    String description) {
                // String only content is optimized by not using Label widget
                final TableCellElement td = DOM.createTD().cast();
                initCellWithText(text, align, style, textIsHTML, sorted,
                        description, td);
            }

            protected void initCellWithText(String text, char align,
                    String style, boolean textIsHTML, boolean sorted,
                    String description, final TableCellElement td) {
                final Element container = DOM.createDiv();
                container.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-cell-wrapper");

                td.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-cell-content");

                if (style != null && !style.equals("")) {
                    td.addClassName(VScrollTable.this.getStylePrimaryName()
                            + "-cell-content-" + style);
                }

                if (sorted) {
                    td.addClassName(VScrollTable.this.getStylePrimaryName()
                            + "-cell-content-sorted");
                }

                if (textIsHTML) {
                    container.setInnerHTML(text);
                } else {
                    container.setInnerText(text);
                }
                setAlign(align, container);
                setTooltip(td, description);

                td.appendChild(container);
                getElement().appendChild(td);
            }

            protected void updateCellStyleNames(TableCellElement td,
                    String primaryStyleName) {
                Element container = td.getFirstChild().cast();
                container.setClassName(primaryStyleName + "-cell-wrapper");

                /*
                 * Replace old primary style name with new one
                 */
                String className = td.getClassName();
                String oldPrimaryName = className.split("-cell-content")[0];
                td.setClassName(className.replaceAll(oldPrimaryName,
                        primaryStyleName));
            }

            public void addCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean sorted, String description) {
                final TableCellElement td = DOM.createTD().cast();
                initCellWithWidget(w, align, style, sorted, td);
                setTooltip(td, description);
            }

            private void setTooltip(TableCellElement td, String description) {
                if (description != null && !description.equals("")) {
                    TooltipInfo info = new TooltipInfo(description, null, this);
                    cellToolTips.put(td, info);
                } else {
                    cellToolTips.remove(td);
                }

            }

            private void setAlign(char align, final Element container) {
                switch (align) {
                case ALIGN_CENTER:
                    container.getStyle().setProperty("textAlign", "center");
                    break;
                case ALIGN_LEFT:
                    container.getStyle().setProperty("textAlign", "left");
                    break;
                case ALIGN_RIGHT:
                default:
                    container.getStyle().setProperty("textAlign", "right");
                    break;
                }
            }

            protected void initCellWithWidget(Widget w, char align,
                    String style, boolean sorted, final TableCellElement td) {
                final Element container = DOM.createDiv();
                String className = VScrollTable.this.getStylePrimaryName()
                        + "-cell-content";
                if (style != null && !style.equals("")) {
                    className += " " + VScrollTable.this.getStylePrimaryName()
                            + "-cell-content-" + style;
                }
                if (sorted) {
                    className += " " + VScrollTable.this.getStylePrimaryName()
                            + "-cell-content-sorted";
                }
                td.setClassName(className);
                container.setClassName(VScrollTable.this.getStylePrimaryName()
                        + "-cell-wrapper");
                setAlign(align, container);
                td.appendChild(container);
                getElement().appendChild(td);
                // ensure widget not attached to another element (possible tBody
                // change)
                w.removeFromParent();
                container.appendChild(w.getElement());
                adopt(w);
                childWidgets.add(w);
            }

            @Override
            public Iterator<Widget> iterator() {
                return childWidgets.iterator();
            }

            @Override
            public boolean remove(Widget w) {
                if (childWidgets.contains(w)) {
                    orphan(w);
                    DOM.removeChild(DOM.getParent(w.getElement()),
                            w.getElement());
                    childWidgets.remove(w);
                    return true;
                } else {
                    return false;
                }
            }

            /**
             * If there are registered click listeners, sends a click event and
             * returns true. Otherwise, does nothing and returns false.
             * 
             * @param event
             * @param targetTdOrTr
             * @param immediate
             *            Whether the event is sent immediately
             * @return Whether a click event was sent
             */
            private boolean handleClickEvent(Event event, Element targetTdOrTr,
                    boolean immediate) {
                if (!client.hasEventListeners(VScrollTable.this,
                        TableConstants.ITEM_CLICK_EVENT_ID)) {
                    // Don't send an event if nobody is listening
                    return false;
                }

                // This row was clicked
                client.updateVariable(paintableId, "clickedKey", "" + rowKey,
                        false);

                if (getElement() == targetTdOrTr.getParentElement()) {
                    // A specific column was clicked
                    int childIndex = DOM.getChildIndex(getElement(),
                            targetTdOrTr);
                    String colKey = null;
                    colKey = tHead.getHeaderCell(childIndex).getColKey();
                    client.updateVariable(paintableId, "clickedColKey", colKey,
                            false);
                }

                MouseEventDetails details = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event);

                client.updateVariable(paintableId, "clickEvent",
                        details.toString(), immediate);

                return true;
            }

            public TooltipInfo getTooltip(
                    com.google.gwt.dom.client.Element target) {

                TooltipInfo info = null;
                final Element targetTdOrTr = getTdOrTr(target);
                if (targetTdOrTr != null
                        && "td".equals(targetTdOrTr.getTagName().toLowerCase())) {
                    TableCellElement td = (TableCellElement) targetTdOrTr
                            .cast();
                    info = cellToolTips.get(td);
                }

                if (info == null) {
                    info = tooltipInfo;
                }

                return info;
            }

            private Element getTdOrTr(Element target) {
                Element thisTrElement = getElement();
                if (target == thisTrElement) {
                    // This was a on the TR element
                    return target;
                }

                // Iterate upwards until we find the TR element
                Element element = target;
                while (element != null
                        && element.getParentElement() != thisTrElement) {
                    element = element.getParentElement();
                }
                return element;
            }

            /**
             * Special handler for touch devices that support native scrolling
             * 
             * @return Whether the event was handled by this method.
             */
            private boolean handleTouchEvent(final Event event) {

                boolean touchEventHandled = false;

                if (enabled && hasNativeTouchScrolling) {
                    touchContextProvider.handleTouchEvent(event);

                    final Element targetTdOrTr = getEventTargetTdOrTr(event);
                    final int type = event.getTypeInt();

                    switch (type) {
                    case Event.ONTOUCHSTART:
                        touchEventHandled = true;
                        touchStart = event;
                        isDragging = false;
                        Touch touch = event.getChangedTouches().get(0);
                        // save position to fields, touches in events are same
                        // instance during the operation.
                        touchStartX = touch.getClientX();
                        touchStartY = touch.getClientY();

                        if (dragmode != 0) {
                            if (dragTouchTimeout == null) {
                                dragTouchTimeout = new Timer() {

                                    @Override
                                    public void run() {
                                        if (touchStart != null) {
                                            // Start a drag if a finger is held
                                            // in place long enough, then moved
                                            isDragging = true;
                                        }
                                    }
                                };
                            }
                            dragTouchTimeout.schedule(TOUCHSCROLL_TIMEOUT);
                        }

                        if (actionKeys != null) {
                            if (contextTouchTimeout == null) {
                                contextTouchTimeout = new Timer() {

                                    @Override
                                    public void run() {
                                        if (touchStart != null) {
                                            // Open the context menu if finger
                                            // is held in place long enough.
                                            showContextMenu(touchStart);
                                            event.preventDefault();
                                            touchStart = null;
                                        }
                                    }
                                };
                            }
                            contextTouchTimeout
                                    .schedule(TOUCH_CONTEXT_MENU_TIMEOUT);
                            event.stopPropagation();
                        }
                        break;
                    case Event.ONTOUCHMOVE:
                        touchEventHandled = true;
                        if (isSignificantMove(event)) {
                            if (contextTouchTimeout != null) {
                                // Moved finger before the context menu timer
                                // expired, so let the browser handle this as a
                                // scroll.
                                contextTouchTimeout.cancel();
                                contextTouchTimeout = null;
                            }
                            if (!isDragging && dragTouchTimeout != null) {
                                // Moved finger before the drag timer expired,
                                // so let the browser handle this as a scroll.
                                dragTouchTimeout.cancel();
                                dragTouchTimeout = null;
                            }

                            if (dragmode != 0 && touchStart != null
                                    && isDragging) {
                                event.preventDefault();
                                event.stopPropagation();
                                startRowDrag(touchStart, type, targetTdOrTr);
                            }
                            touchStart = null;
                        }
                        break;
                    case Event.ONTOUCHEND:
                    case Event.ONTOUCHCANCEL:
                        touchEventHandled = true;
                        if (contextTouchTimeout != null) {
                            contextTouchTimeout.cancel();
                        }
                        if (dragTouchTimeout != null) {
                            dragTouchTimeout.cancel();
                        }
                        if (touchStart != null) {
                            if (!BrowserInfo.get().isAndroid()) {
                                event.preventDefault();
                                WidgetUtil.simulateClickFromTouchEvent(
                                        touchStart, this);
                            }
                            event.stopPropagation();
                            touchStart = null;
                        }
                        isDragging = false;
                        break;
                    }
                }
                return touchEventHandled;
            }

            /*
             * React on click that occur on content cells only
             */

            @Override
            public void onBrowserEvent(final Event event) {

                final boolean touchEventHandled = handleTouchEvent(event);

                if (enabled && !touchEventHandled) {
                    final int type = event.getTypeInt();
                    final Element targetTdOrTr = getEventTargetTdOrTr(event);
                    if (type == Event.ONCONTEXTMENU) {
                        showContextMenu(event);
                        if (enabled
                                && (actionKeys != null || client
                                        .hasEventListeners(
                                                VScrollTable.this,
                                                TableConstants.ITEM_CLICK_EVENT_ID))) {
                            /*
                             * Prevent browser context menu only if there are
                             * action handlers or item click listeners
                             * registered
                             */
                            event.stopPropagation();
                            event.preventDefault();
                        }
                        return;
                    }

                    boolean targetCellOrRowFound = targetTdOrTr != null;

                    switch (type) {
                    case Event.ONDBLCLICK:
                        if (targetCellOrRowFound) {
                            handleClickEvent(event, targetTdOrTr, true);
                        }
                        break;
                    case Event.ONMOUSEUP:
                        /*
                         * Only fire a click if the mouseup hits the same
                         * element as the corresponding mousedown. This is first
                         * checked in the event preview but we can't fire the
                         * event there as the event might get canceled before it
                         * gets here.
                         */
                        if (mouseUpPreviewMatched
                                && lastMouseDownTarget != null
                                && lastMouseDownTarget == getElementTdOrTr(WidgetUtil
                                        .getElementUnderMouse(event))) {
                            // "Click" with left, right or middle button

                            if (targetCellOrRowFound) {
                                /*
                                 * Queue here, send at the same time as the
                                 * corresponding value change event - see #7127
                                 */
                                boolean clickEventSent = handleClickEvent(
                                        event, targetTdOrTr, false);

                                if (event.getButton() == Event.BUTTON_LEFT
                                        && isSelectable()) {

                                    // Ctrl+Shift click
                                    if ((event.getCtrlKey() || event
                                            .getMetaKey())
                                            && event.getShiftKey()
                                            && isMultiSelectModeDefault()) {
                                        toggleShiftSelection(false);
                                        setRowFocus(this);

                                        // Ctrl click
                                    } else if ((event.getCtrlKey() || event
                                            .getMetaKey())
                                            && isMultiSelectModeDefault()) {
                                        boolean wasSelected = isSelected();
                                        toggleSelection();
                                        setRowFocus(this);
                                        /*
                                         * next possible range select must start
                                         * on this row
                                         */
                                        selectionRangeStart = this;
                                        if (wasSelected) {
                                            removeRowFromUnsentSelectionRanges(this);
                                        }

                                    } else if ((event.getCtrlKey() || event
                                            .getMetaKey())
                                            && isSingleSelectMode()) {
                                        // Ctrl (or meta) click (Single
                                        // selection)
                                        if (!isSelected()
                                                || (isSelected() && nullSelectionAllowed)) {

                                            if (!isSelected()) {
                                                deselectAll();
                                            }

                                            toggleSelection();
                                            setRowFocus(this);
                                        }

                                    } else if (event.getShiftKey()
                                            && isMultiSelectModeDefault()) {
                                        // Shift click
                                        toggleShiftSelection(true);

                                    } else {
                                        // click
                                        boolean currentlyJustThisRowSelected = selectedRowKeys
                                                .size() == 1
                                                && selectedRowKeys
                                                        .contains(getKey());

                                        if (!currentlyJustThisRowSelected) {
                                            if (isSingleSelectMode()
                                                    || isMultiSelectModeDefault()) {
                                                /*
                                                 * For default multi select mode
                                                 * (ctrl/shift) and for single
                                                 * select mode we need to clear
                                                 * the previous selection before
                                                 * selecting a new one when the
                                                 * user clicks on a row. Only in
                                                 * multiselect/simple mode the
                                                 * old selection should remain
                                                 * after a normal click.
                                                 */
                                                deselectAll();
                                            }
                                            toggleSelection();
                                        } else if ((isSingleSelectMode() || isMultiSelectModeSimple())
                                                && nullSelectionAllowed) {
                                            toggleSelection();
                                        }/*
                                          * else NOP to avoid excessive server
                                          * visits (selection is removed with
                                          * CTRL/META click)
                                          */

                                        selectionRangeStart = this;
                                        setRowFocus(this);
                                    }

                                    // Remove IE text selection hack
                                    if (BrowserInfo.get().isIE()) {
                                        ((Element) event.getEventTarget()
                                                .cast()).setPropertyJSO(
                                                "onselectstart", null);
                                    }
                                    // Queue value change
                                    sendSelectedRows(false);
                                }
                                /*
                                 * Send queued click and value change events if
                                 * any If a click event is sent, send value
                                 * change with it regardless of the immediate
                                 * flag, see #7127
                                 */
                                if (immediate || clickEventSent) {
                                    client.sendPendingVariableChanges();
                                }
                            }
                        }
                        mouseUpPreviewMatched = false;
                        lastMouseDownTarget = null;
                        break;
                    case Event.ONTOUCHEND:
                    case Event.ONTOUCHCANCEL:
                        if (touchStart != null) {
                            /*
                             * Touch has not been handled as neither context or
                             * drag start, handle it as a click.
                             */
                            WidgetUtil.simulateClickFromTouchEvent(touchStart,
                                    this);
                            touchStart = null;
                        }
                        touchContextProvider.cancel();
                        break;
                    case Event.ONTOUCHMOVE:
                        if (isSignificantMove(event)) {
                            /*
                             * TODO figure out scroll delegate don't eat events
                             * if row is selected. Null check for active
                             * delegate is as a workaround.
                             */
                            if (dragmode != 0
                                    && touchStart != null
                                    && (TouchScrollDelegate
                                            .getActiveScrollDelegate() == null)) {
                                startRowDrag(touchStart, type, targetTdOrTr);
                            }
                            touchContextProvider.cancel();
                            /*
                             * Avoid clicks and drags by clearing touch start
                             * flag.
                             */
                            touchStart = null;
                        }

                        break;
                    case Event.ONTOUCHSTART:
                        touchStart = event;
                        Touch touch = event.getChangedTouches().get(0);
                        // save position to fields, touches in events are same
                        // instance during the operation.
                        touchStartX = touch.getClientX();
                        touchStartY = touch.getClientY();
                        /*
                         * Prevent simulated mouse events.
                         */
                        touchStart.preventDefault();
                        if (dragmode != 0 || actionKeys != null) {
                            new Timer() {

                                @Override
                                public void run() {
                                    TouchScrollDelegate activeScrollDelegate = TouchScrollDelegate
                                            .getActiveScrollDelegate();
                                    /*
                                     * If there's a scroll delegate, check if
                                     * we're actually scrolling and handle it.
                                     * If no delegate, do nothing here and let
                                     * the row handle potential drag'n'drop or
                                     * context menu.
                                     */
                                    if (activeScrollDelegate != null) {
                                        if (activeScrollDelegate.isMoved()) {
                                            /*
                                             * Prevent the row from handling
                                             * touch move/end events (the
                                             * delegate handles those) and from
                                             * doing drag'n'drop or opening a
                                             * context menu.
                                             */
                                            touchStart = null;
                                        } else {
                                            /*
                                             * Scrolling hasn't started, so
                                             * cancel delegate and let the row
                                             * handle potential drag'n'drop or
                                             * context menu.
                                             */
                                            activeScrollDelegate
                                                    .stopScrolling();
                                        }
                                    }
                                }
                            }.schedule(TOUCHSCROLL_TIMEOUT);

                            if (contextTouchTimeout == null
                                    && actionKeys != null) {
                                contextTouchTimeout = new Timer() {

                                    @Override
                                    public void run() {
                                        if (touchStart != null) {
                                            showContextMenu(touchStart);
                                            touchStart = null;
                                        }
                                    }
                                };
                            }
                            if (contextTouchTimeout != null) {
                                contextTouchTimeout.cancel();
                                contextTouchTimeout
                                        .schedule(TOUCH_CONTEXT_MENU_TIMEOUT);
                            }
                        }
                        break;
                    case Event.ONMOUSEDOWN:
                        /*
                         * When getting a mousedown event, we must detect where
                         * the corresponding mouseup event if it's on a
                         * different part of the page.
                         */
                        lastMouseDownTarget = getElementTdOrTr(WidgetUtil
                                .getElementUnderMouse(event));
                        mouseUpPreviewMatched = false;
                        mouseUpEventPreviewRegistration = Event
                                .addNativePreviewHandler(mouseUpPreviewHandler);

                        if (targetCellOrRowFound) {
                            setRowFocus(this);
                            ensureFocus();
                            if (dragmode != 0
                                    && (event.getButton() == NativeEvent.BUTTON_LEFT)) {
                                startRowDrag(event, type, targetTdOrTr);

                            } else if (event.getCtrlKey()
                                    || event.getShiftKey()
                                    || event.getMetaKey()
                                    && isMultiSelectModeDefault()) {

                                // Prevent default text selection in Firefox
                                event.preventDefault();

                                // Prevent default text selection in IE
                                if (BrowserInfo.get().isIE()) {
                                    ((Element) event.getEventTarget().cast())
                                            .setPropertyJSO(
                                                    "onselectstart",
                                                    getPreventTextSelectionIEHack());
                                }

                                event.stopPropagation();
                            }
                        }
                        break;
                    case Event.ONMOUSEOUT:
                        break;
                    default:
                        break;
                    }
                }
                super.onBrowserEvent(event);
            }

            private boolean isSignificantMove(Event event) {
                if (touchStart == null) {
                    // no touch start
                    return false;
                }
                /*
                 * TODO calculate based on real distance instead of separate
                 * axis checks
                 */
                Touch touch = event.getChangedTouches().get(0);
                if (Math.abs(touch.getClientX() - touchStartX) > TouchScrollDelegate.SIGNIFICANT_MOVE_THRESHOLD) {
                    return true;
                }
                if (Math.abs(touch.getClientY() - touchStartY) > TouchScrollDelegate.SIGNIFICANT_MOVE_THRESHOLD) {
                    return true;
                }
                return false;
            }

            /**
             * Checks if the row represented by the row key has been selected
             * 
             * @param key
             *            The generated row key
             */
            private boolean rowKeyIsSelected(int rowKey) {
                // Check single selections
                if (selectedRowKeys.contains("" + rowKey)) {
                    return true;
                }

                // Check range selections
                for (SelectionRange r : selectedRowRanges) {
                    if (r.inRange(getRenderedRowByKey("" + rowKey))) {
                        return true;
                    }
                }
                return false;
            }

            protected void startRowDrag(Event event, final int type,
                    Element targetTdOrTr) {
                VTransferable transferable = new VTransferable();
                transferable.setDragSource(ConnectorMap.get(client)
                        .getConnector(VScrollTable.this));
                transferable.setData("itemId", "" + rowKey);
                NodeList<TableCellElement> cells = rowElement.getCells();
                for (int i = 0; i < cells.getLength(); i++) {
                    if (cells.getItem(i).isOrHasChild(targetTdOrTr)) {
                        HeaderCell headerCell = tHead.getHeaderCell(i);
                        transferable.setData("propertyId", headerCell.cid);
                        break;
                    }
                }

                VDragEvent ev = VDragAndDropManager.get().startDrag(
                        transferable, event, true);
                if (dragmode == DRAGMODE_MULTIROW && isMultiSelectModeAny()
                        && rowKeyIsSelected(rowKey)) {

                    // Create a drag image of ALL rows
                    ev.createDragImage(scrollBody.tBodyElement, true);

                    // Hide rows which are not selected
                    Element dragImage = ev.getDragImage();
                    int i = 0;
                    for (Iterator<Widget> iterator = scrollBody.iterator(); iterator
                            .hasNext();) {
                        VScrollTableRow next = (VScrollTableRow) iterator
                                .next();

                        Element child = (Element) dragImage.getChild(i++);

                        if (!rowKeyIsSelected(next.rowKey)) {
                            child.getStyle().setVisibility(Visibility.HIDDEN);
                        }
                    }
                } else {
                    ev.createDragImage(getElement(), true);
                }
                if (type == Event.ONMOUSEDOWN) {
                    event.preventDefault();
                }
                event.stopPropagation();
            }

            /**
             * Finds the TD that the event interacts with. Returns null if the
             * target of the event should not be handled. If the event target is
             * the row directly this method returns the TR element instead of
             * the TD.
             * 
             * @param event
             * @return TD or TR element that the event targets (the actual event
             *         target is this element or a child of it)
             */
            private Element getEventTargetTdOrTr(Event event) {
                final Element eventTarget = event.getEventTarget().cast();
                return getElementTdOrTr(eventTarget);
            }

            private Element getElementTdOrTr(Element element) {

                Widget widget = WidgetUtil.findWidget(element, null);

                if (widget != this) {
                    /*
                     * This is a workaround to make Labels, read only TextFields
                     * and Embedded in a Table clickable (see #2688). It is
                     * really not a fix as it does not work with a custom read
                     * only components (not extending VLabel/VEmbedded).
                     */
                    while (widget != null && widget.getParent() != this) {
                        widget = widget.getParent();
                    }

                    if (!(widget instanceof VLabel)
                            && !(widget instanceof VEmbedded)
                            && !(widget instanceof VTextField && ((VTextField) widget)
                                    .isReadOnly())) {
                        return null;
                    }
                }
                return getTdOrTr(element);
            }

            @Override
            public void showContextMenu(Event event) {
                if (enabled && actionKeys != null) {
                    // Show context menu if there are registered action handlers
                    int left = WidgetUtil.getTouchOrMouseClientX(event)
                            + Window.getScrollLeft();
                    int top = WidgetUtil.getTouchOrMouseClientY(event)
                            + Window.getScrollTop();
                    showContextMenu(left, top);
                }
            }

            public void showContextMenu(int left, int top) {
                VContextMenu menu = client.getContextMenu();
                contextMenu = new ContextMenuDetails(menu, getKey(), left, top);
                menu.showAt(this, left, top);
            }

            /**
             * Has the row been selected?
             * 
             * @return Returns true if selected, else false
             */
            public boolean isSelected() {
                return selected;
            }

            /**
             * Toggle the selection of the row
             */
            public void toggleSelection() {
                selected = !selected;
                selectionChanged = true;
                if (selected) {
                    selectedRowKeys.add(String.valueOf(rowKey));
                    addStyleName("v-selected");
                } else {
                    removeStyleName("v-selected");
                    selectedRowKeys.remove(String.valueOf(rowKey));
                }
            }

            /**
             * Is called when a user clicks an item when holding SHIFT key down.
             * This will select a new range from the last focused row
             * 
             * @param deselectPrevious
             *            Should the previous selected range be deselected
             */
            private void toggleShiftSelection(boolean deselectPrevious) {

                /*
                 * Ensures that we are in multiselect mode and that we have a
                 * previous selection which was not a deselection
                 */
                if (isSingleSelectMode()) {
                    // No previous selection found
                    deselectAll();
                    toggleSelection();
                    return;
                }

                // Set the selectable range
                VScrollTableRow endRow = this;
                VScrollTableRow startRow = selectionRangeStart;
                if (startRow == null) {
                    startRow = focusedRow;
                    selectionRangeStart = focusedRow;
                    // If start row is null then we have a multipage selection
                    // from above
                    if (startRow == null) {
                        startRow = (VScrollTableRow) scrollBody.iterator()
                                .next();
                        setRowFocus(endRow);
                    }
                } else if (!startRow.isSelected()) {
                    // The start row is no longer selected (probably removed)
                    // and so we select from above
                    startRow = (VScrollTableRow) scrollBody.iterator().next();
                    setRowFocus(endRow);
                }

                // Deselect previous items if so desired
                if (deselectPrevious) {
                    deselectAll();
                }

                // we'll ensure GUI state from top down even though selection
                // was the opposite way
                if (!startRow.isBefore(endRow)) {
                    VScrollTableRow tmp = startRow;
                    startRow = endRow;
                    endRow = tmp;
                }
                SelectionRange range = new SelectionRange(startRow, endRow);

                for (Widget w : scrollBody) {
                    VScrollTableRow row = (VScrollTableRow) w;
                    if (range.inRange(row)) {
                        if (!row.isSelected()) {
                            row.toggleSelection();
                        }
                        selectedRowKeys.add(row.getKey());
                    }
                }

                // Add range
                if (startRow != endRow) {
                    selectedRowRanges.add(range);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.vaadin.client.ui.IActionOwner#getActions ()
             */

            @Override
            public Action[] getActions() {
                if (actionKeys == null) {
                    return new Action[] {};
                }
                final Action[] actions = new Action[actionKeys.length];
                for (int i = 0; i < actions.length; i++) {
                    final String actionKey = actionKeys[i];
                    final TreeAction a = new TreeAction(this,
                            String.valueOf(rowKey), actionKey) {

                        @Override
                        public void execute() {
                            super.execute();
                            lazyRevertFocusToRow(VScrollTableRow.this);
                        }
                    };
                    a.setCaption(getActionCaption(actionKey));
                    a.setIconUrl(getActionIcon(actionKey));
                    actions[i] = a;
                }
                return actions;
            }

            @Override
            public ApplicationConnection getClient() {
                return client;
            }

            @Override
            public String getPaintableId() {
                return paintableId;
            }

            private int getColIndexOf(Widget child) {
                com.google.gwt.dom.client.Element widgetCell = child
                        .getElement().getParentElement().getParentElement();
                NodeList<TableCellElement> cells = rowElement.getCells();
                for (int i = 0; i < cells.getLength(); i++) {
                    if (cells.getItem(i) == widgetCell) {
                        return i;
                    }
                }
                return -1;
            }

            public Widget getWidgetForPaintable() {
                return this;
            }
        }

        protected class VScrollTableGeneratedRow extends VScrollTableRow {

            private boolean spanColumns;
            private boolean htmlContentAllowed;

            public VScrollTableGeneratedRow(UIDL uidl, char[] aligns) {
                super(uidl, aligns);
                addStyleName("v-table-generated-row");
            }

            public boolean isSpanColumns() {
                return spanColumns;
            }

            @Override
            protected void initCellWidths() {
                if (spanColumns) {
                    setSpannedColumnWidthAfterDOMFullyInited();
                } else {
                    super.initCellWidths();
                }
            }

            private void setSpannedColumnWidthAfterDOMFullyInited() {
                // Defer setting width on spanned columns to make sure that
                // they are added to the DOM before trying to calculate
                // widths.
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        if (showRowHeaders) {
                            setCellWidth(0, tHead.getHeaderCell(0)
                                    .getWidthWithIndent());
                            calcAndSetSpanWidthOnCell(1);
                        } else {
                            calcAndSetSpanWidthOnCell(0);
                        }
                    }
                });
            }

            @Override
            protected boolean isRenderHtmlInCells() {
                return htmlContentAllowed;
            }

            @Override
            protected void addCellsFromUIDL(UIDL uidl, char[] aligns, int col,
                    int visibleColumnIndex) {
                htmlContentAllowed = uidl.getBooleanAttribute("gen_html");
                spanColumns = uidl.getBooleanAttribute("gen_span");

                final Iterator<?> cells = uidl.getChildIterator();
                if (spanColumns) {
                    int colCount = uidl.getChildCount();
                    if (cells.hasNext()) {
                        final Object cell = cells.next();
                        if (cell instanceof String) {
                            addSpannedCell(uidl, cell.toString(), aligns[0],
                                    "", htmlContentAllowed, false, null,
                                    colCount);
                        } else {
                            addSpannedCell(uidl, (Widget) cell, aligns[0], "",
                                    false, colCount);
                        }
                    }
                } else {
                    super.addCellsFromUIDL(uidl, aligns, col,
                            visibleColumnIndex);
                }
            }

            private void addSpannedCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean sorted, int colCount) {
                TableCellElement td = DOM.createTD().cast();
                td.setColSpan(colCount);
                initCellWithWidget(w, align, style, sorted, td);
            }

            private void addSpannedCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean sorted,
                    String description, int colCount) {
                // String only content is optimized by not using Label widget
                final TableCellElement td = DOM.createTD().cast();
                td.setColSpan(colCount);
                initCellWithText(text, align, style, textIsHTML, sorted,
                        description, td);
            }

            @Override
            protected void setCellWidth(int cellIx, int width) {
                if (isSpanColumns()) {
                    if (showRowHeaders) {
                        if (cellIx == 0) {
                            super.setCellWidth(0, width);
                        } else {
                            // We need to recalculate the spanning TDs width for
                            // every cellIx in order to support column resizing.
                            calcAndSetSpanWidthOnCell(1);
                        }
                    } else {
                        // Same as above.
                        calcAndSetSpanWidthOnCell(0);
                    }
                } else {
                    super.setCellWidth(cellIx, width);
                }
            }

            private void calcAndSetSpanWidthOnCell(final int cellIx) {
                int spanWidth = 0;
                for (int ix = (showRowHeaders ? 1 : 0); ix < tHead
                        .getVisibleCellCount(); ix++) {
                    spanWidth += tHead.getHeaderCell(ix).getOffsetWidth();
                }
                WidgetUtil.setWidthExcludingPaddingAndBorder(
                        (Element) getElement().getChild(cellIx), spanWidth, 13,
                        false);
            }
        }

        /**
         * Ensure the component has a focus.
         * 
         * TODO the current implementation simply always calls focus for the
         * component. In case the Table at some point implements focus/blur
         * listeners, this method needs to be evolved to conditionally call
         * focus only if not currently focused.
         */
        protected void ensureFocus() {
            if (!hasFocus) {
                scrollBodyPanel.setFocus(true);
            }

        }

    }

    /**
     * Deselects all items
     */
    public void deselectAll() {
        for (Widget w : scrollBody) {
            VScrollTableRow row = (VScrollTableRow) w;
            if (row.isSelected()) {
                row.toggleSelection();
            }
        }
        // still ensure all selects are removed from (not necessary rendered)
        selectedRowKeys.clear();
        selectedRowRanges.clear();
        // also notify server that it clears all previous selections (the client
        // side does not know about the invisible ones)
        instructServerToForgetPreviousSelections();
    }

    /**
     * Used in multiselect mode when the client side knows that all selections
     * are in the next request.
     */
    private void instructServerToForgetPreviousSelections() {
        client.updateVariable(paintableId, "clearSelections", true, false);
    }

    /**
     * Determines the pagelength when the table height is fixed.
     */
    public void updatePageLength() {
        // Only update if visible and enabled
        if (!isVisible() || !enabled) {
            return;
        }

        if (scrollBody == null) {
            return;
        }

        if (isDynamicHeight()) {
            return;
        }

        int rowHeight = (int) Math.round(scrollBody.getRowHeight());
        int bodyH = scrollBodyPanel.getOffsetHeight();
        int rowsAtOnce = bodyH / rowHeight;
        boolean anotherPartlyVisible = ((bodyH % rowHeight) != 0);
        if (anotherPartlyVisible) {
            rowsAtOnce++;
        }
        if (pageLength != rowsAtOnce) {
            pageLength = rowsAtOnce;
            client.updateVariable(paintableId, "pagelength", pageLength, false);

            if (!rendering) {
                int currentlyVisible = scrollBody.getLastRendered()
                        - scrollBody.getFirstRendered();
                if (currentlyVisible < pageLength
                        && currentlyVisible < totalRows) {
                    // shake scrollpanel to fill empty space
                    scrollBodyPanel.setScrollPosition(scrollTop + 1);
                    scrollBodyPanel.setScrollPosition(scrollTop - 1);
                }

                sizeNeedsInit = true;
            }
        }

    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateWidth() {
        if (!isVisible()) {
            /*
             * Do not update size when the table is hidden as all column widths
             * will be set to zero and they won't be recalculated when the table
             * is set visible again (until the size changes again)
             */
            return;
        }

        if (!isDynamicWidth()) {
            int innerPixels = getOffsetWidth() - getBorderWidth();
            if (innerPixels < 0) {
                innerPixels = 0;
            }
            setContentWidth(innerPixels);

            // readjust undefined width columns
            triggerLazyColumnAdjustment(false);

        } else {

            sizeNeedsInit = true;

            // readjust undefined width columns
            triggerLazyColumnAdjustment(false);
        }

        /*
         * setting width may affect wheter the component has scrollbars -> needs
         * scrolling or not
         */
        setProperTabIndex();
    }

    private static final int LAZY_COLUMN_ADJUST_TIMEOUT = 300;

    private final Timer lazyAdjustColumnWidths = new Timer() {
        /**
         * Check for column widths, and available width, to see if we can fix
         * column widths "optimally". Doing this lazily to avoid expensive
         * calculation when resizing is not yet finished.
         */

        @Override
        public void run() {
            if (scrollBody == null) {
                // Try again later if we get here before scrollBody has been
                // initalized
                triggerLazyColumnAdjustment(false);
                return;
            }

            Iterator<Widget> headCells = tHead.iterator();
            int usedMinimumWidth = 0;
            int totalExplicitColumnsWidths = 0;
            float expandRatioDivider = 0;
            int colIndex = 0;

            int hierarchyColumnIndent = scrollBody.getMaxIndent();
            int hierarchyColumnIndex = getHierarchyColumnIndex();
            HeaderCell hierarchyHeaderInNeedOfFurtherHandling = null;

            while (headCells.hasNext()) {
                final HeaderCell hCell = (HeaderCell) headCells.next();
                boolean hasIndent = hierarchyColumnIndent > 0
                        && hCell.isHierarchyColumn();
                if (hCell.isDefinedWidth()) {
                    // get width without indent to find out whether adjustments
                    // are needed (requires special handling further ahead)
                    int w = hCell.getWidth();
                    if (hasIndent && w < hierarchyColumnIndent) {
                        // enforce indent if necessary
                        w = hierarchyColumnIndent;
                        hierarchyHeaderInNeedOfFurtherHandling = hCell;
                    }
                    totalExplicitColumnsWidths += w;
                    usedMinimumWidth += w;
                } else {
                    // natural width already includes indent if any
                    int naturalColumnWidth = hCell
                            .getNaturalColumnWidth(colIndex);
                    /*
                     * TODO If there is extra width, expand ratios are for
                     * additional extra widths, not for absolute column widths.
                     * Should be fixed in sizeInit(), too.
                     */
                    if (hCell.getExpandRatio() > 0) {
                        naturalColumnWidth = 0;
                    }
                    usedMinimumWidth += naturalColumnWidth;
                    expandRatioDivider += hCell.getExpandRatio();
                    if (hasIndent) {
                        hierarchyHeaderInNeedOfFurtherHandling = hCell;
                    }
                }
                colIndex++;
            }

            int availW = scrollBody.getAvailableWidth();
            // Hey IE, are you really sure about this?
            availW = scrollBody.getAvailableWidth();
            int visibleCellCount = tHead.getVisibleCellCount();
            int totalExtraWidth = scrollBody.getCellExtraWidth()
                    * visibleCellCount;
            if (willHaveScrollbars()) {
                totalExtraWidth += WidgetUtil.getNativeScrollbarSize();
                // if there will be vertical scrollbar, let's enable it
                scrollBodyPanel.getElement().getStyle().clearOverflowY();
            } else {
                // if there is no need for vertical scrollbar, let's disable it
                // this is necessary since sometimes the browsers insist showing
                // the scrollbar even if the content would fit perfectly
                scrollBodyPanel.getElement().getStyle()
                        .setOverflowY(Overflow.HIDDEN);
            }

            availW -= totalExtraWidth;
            int forceScrollBodyWidth = -1;

            int extraSpace = availW - usedMinimumWidth;
            if (extraSpace < 0) {
                if (getTotalRows() == 0) {
                    /*
                     * Too wide header combined with no rows in the table.
                     * 
                     * No horizontal scrollbars would be displayed because
                     * there's no rows that grows too wide causing the
                     * scrollBody container div to overflow. Must explicitely
                     * force a width to a scrollbar. (see #9187)
                     */
                    forceScrollBodyWidth = usedMinimumWidth + totalExtraWidth;
                }
                extraSpace = 0;
                // if there will be horizontal scrollbar, let's enable it
                scrollBodyPanel.getElement().getStyle().clearOverflowX();
            } else {
                // if there is no need for horizontal scrollbar, let's disable
                // it
                // this is necessary since sometimes the browsers insist showing
                // the scrollbar even if the content would fit perfectly
                scrollBodyPanel.getElement().getStyle()
                        .setOverflowX(Overflow.HIDDEN);
            }

            if (forceScrollBodyWidth > 0) {
                scrollBody.container.getStyle().setWidth(forceScrollBodyWidth,
                        Unit.PX);
            } else {
                // Clear width that might have been set to force horizontal
                // scrolling if there are no rows
                scrollBody.container.getStyle().clearWidth();
            }

            int totalUndefinedNaturalWidths = usedMinimumWidth
                    - totalExplicitColumnsWidths;

            if (hierarchyHeaderInNeedOfFurtherHandling != null
                    && !hierarchyHeaderInNeedOfFurtherHandling.isDefinedWidth()) {
                // ensure the cell gets enough space for the indent
                int w = hierarchyHeaderInNeedOfFurtherHandling
                        .getNaturalColumnWidth(hierarchyColumnIndex);
                int newSpace = Math.round(w + (float) extraSpace * (float) w
                        / totalUndefinedNaturalWidths);
                if (newSpace >= hierarchyColumnIndent) {
                    // no special handling required
                    hierarchyHeaderInNeedOfFurtherHandling = null;
                } else {
                    // treat as a defined width column of indent's width
                    totalExplicitColumnsWidths += hierarchyColumnIndent;
                    usedMinimumWidth -= w - hierarchyColumnIndent;
                    totalUndefinedNaturalWidths = usedMinimumWidth
                            - totalExplicitColumnsWidths;
                    expandRatioDivider += hierarchyHeaderInNeedOfFurtherHandling
                            .getExpandRatio();
                    extraSpace = Math.max(availW - usedMinimumWidth, 0);
                }
            }

            // we have some space that can be divided optimally
            HeaderCell hCell;
            colIndex = 0;
            headCells = tHead.iterator();
            int checksum = 0;
            while (headCells.hasNext()) {
                hCell = (HeaderCell) headCells.next();
                if (hCell.isResizing) {
                    continue;
                }
                if (!hCell.isDefinedWidth()) {
                    int w = hCell.getNaturalColumnWidth(colIndex);
                    int newSpace;
                    if (expandRatioDivider > 0) {
                        // divide excess space by expand ratios
                        if (hCell.getExpandRatio() > 0) {
                            w = 0;
                        }
                        newSpace = Math.round((w + extraSpace
                                * hCell.getExpandRatio() / expandRatioDivider));
                    } else {
                        if (hierarchyHeaderInNeedOfFurtherHandling == hCell) {
                            // still exists, so needs exactly the indent's width
                            newSpace = hierarchyColumnIndent;
                        } else if (totalUndefinedNaturalWidths != 0) {
                            // divide relatively to natural column widths
                            newSpace = Math.round(w + (float) extraSpace
                                    * (float) w / totalUndefinedNaturalWidths);
                        } else {
                            newSpace = w;
                        }
                    }
                    checksum += newSpace;
                    setColWidth(colIndex, newSpace, false);

                } else {
                    if (hierarchyHeaderInNeedOfFurtherHandling == hCell) {
                        // defined with enforced into indent width
                        checksum += hierarchyColumnIndent;
                        setColWidth(colIndex, hierarchyColumnIndent, false);
                    } else {
                        int cellWidth = hCell.getWidthWithIndent();
                        checksum += cellWidth;
                        if (hCell.isHierarchyColumn()) {
                            // update in case the indent has changed
                            // (not detectable earlier)
                            setColWidth(colIndex, cellWidth, true);
                        }
                    }
                }
                colIndex++;
            }

            if (extraSpace > 0 && checksum != availW) {
                /*
                 * There might be in some cases a rounding error of 1px when
                 * extra space is divided so if there is one then we give the
                 * first undefined column 1 more pixel
                 */
                headCells = tHead.iterator();
                colIndex = 0;
                while (headCells.hasNext()) {
                    HeaderCell hc = (HeaderCell) headCells.next();
                    if (!hc.isResizing && !hc.isDefinedWidth()) {
                        setColWidth(colIndex, hc.getWidthWithIndent() + availW
                                - checksum, false);
                        break;
                    }
                    colIndex++;
                }
            }

            if (isDynamicHeight() && totalRows == pageLength) {
                // fix body height (may vary if lazy loading is offhorizontal
                // scrollbar appears/disappears)
                int bodyHeight = scrollBody.getRequiredHeight();
                boolean needsSpaceForHorizontalScrollbar = (availW < usedMinimumWidth);
                if (needsSpaceForHorizontalScrollbar) {
                    bodyHeight += WidgetUtil.getNativeScrollbarSize();
                }
                int heightBefore = getOffsetHeight();
                scrollBodyPanel.setHeight(bodyHeight + "px");

                if (heightBefore != getOffsetHeight()) {
                    Util.notifyParentOfSizeChange(VScrollTable.this, rendering);
                }
            }

            forceRealignColumnHeaders();
        }

    };

    private void forceRealignColumnHeaders() {
        if (BrowserInfo.get().isIE()) {
            /*
             * IE does not fire onscroll event if scroll position is reverted to
             * 0 due to the content element size growth. Ensure headers are in
             * sync with content manually. Safe to use null event as we don't
             * actually use the event object in listener.
             */
            onScroll(null);
        }
    }

    /**
     * helper to set pixel size of head and body part
     * 
     * @param pixels
     */
    private void setContentWidth(int pixels) {
        tHead.setWidth(pixels + "px");
        scrollBodyPanel.setWidth(pixels + "px");
        tFoot.setWidth(pixels + "px");
    }

    private int borderWidth = -1;

    /**
     * @return border left + border right
     */
    private int getBorderWidth() {
        if (borderWidth < 0) {
            borderWidth = WidgetUtil.measureHorizontalPaddingAndBorder(
                    scrollBodyPanel.getElement(), 2);
            if (borderWidth < 0) {
                borderWidth = 0;
            }
        }
        return borderWidth;
    }

    /**
     * Ensures scrollable area is properly sized. This method is used when fixed
     * size is used.
     */
    private int containerHeight;

    private void setContainerHeight() {
        if (!isDynamicHeight()) {

            /*
             * Android 2.3 cannot measure the height of the inline-block
             * properly, and will return the wrong offset height. So for android
             * 2.3 we set the element to a block element while measuring and
             * then restore it which yields the correct result. #11331
             */
            if (BrowserInfo.get().isAndroid23()) {
                getElement().getStyle().setDisplay(Display.BLOCK);
            }

            containerHeight = getOffsetHeight();
            containerHeight -= showColHeaders ? tHead.getOffsetHeight() : 0;
            containerHeight -= tFoot.getOffsetHeight();
            containerHeight -= getContentAreaBorderHeight();
            if (containerHeight < 0) {
                containerHeight = 0;
            }

            scrollBodyPanel.setHeight(containerHeight + "px");

            if (BrowserInfo.get().isAndroid23()) {
                getElement().getStyle().clearDisplay();
            }
        }
    }

    private int contentAreaBorderHeight = -1;
    private int scrollLeft;
    private int scrollTop;

    /** For internal use only. May be removed or replaced in the future. */
    public VScrollTableDropHandler dropHandler;

    private boolean navKeyDown;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean multiselectPending;

    /**
     * @return border top + border bottom of the scrollable area of table
     */
    private int getContentAreaBorderHeight() {
        if (contentAreaBorderHeight < 0) {

            scrollBodyPanel.getElement().getStyle()
                    .setOverflow(Overflow.HIDDEN);
            int oh = scrollBodyPanel.getOffsetHeight();
            int ch = scrollBodyPanel.getElement()
                    .getPropertyInt("clientHeight");
            contentAreaBorderHeight = oh - ch;
            scrollBodyPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
        }
        return contentAreaBorderHeight;
    }

    @Override
    public void setHeight(String height) {
        if (height.length() == 0
                && getElement().getStyle().getHeight().length() != 0) {
            /*
             * Changing from defined to undefined size -> should do a size init
             * to take page length into account again
             */
            sizeNeedsInit = true;
        }
        super.setHeight(height);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateHeight() {
        setContainerHeight();

        if (initializedAndAttached) {
            updatePageLength();
        }

        triggerLazyColumnAdjustment(false);

        /*
         * setting height may affect wheter the component has scrollbars ->
         * needs scrolling or not
         */
        setProperTabIndex();

    }

    /*
     * Overridden due Table might not survive of visibility change (scroll pos
     * lost). Example ITabPanel just set contained components invisible and back
     * when changing tabs.
     */

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() != visible) {
            super.setVisible(visible);
            if (initializedAndAttached) {
                if (visible) {
                    Scheduler.get().scheduleDeferred(new Command() {

                        @Override
                        public void execute() {
                            scrollBodyPanel
                                    .setScrollPosition(measureRowHeightOffset(firstRowInViewPort));
                        }
                    });
                }
            }
        }
    }

    /**
     * Helper function to build html snippet for column or row headers
     * 
     * @param uidl
     *            possibly with values caption and icon
     * @return html snippet containing possibly an icon + caption text
     */
    protected String buildCaptionHtmlSnippet(UIDL uidl) {
        String s = uidl.hasAttribute("caption") ? uidl
                .getStringAttribute("caption") : "";
        if (uidl.hasAttribute("icon")) {
            Icon icon = client.getIcon(uidl.getStringAttribute("icon"));
            icon.setAlternateText("icon");
            s = icon.getElement().getString() + s;
        }
        return s;
    }

    // Updates first visible row for the case we cannot wait
    // for onScroll
    private void updateFirstVisibleRow() {
        scrollTop = scrollBodyPanel.getScrollPosition();
        firstRowInViewPort = calcFirstRowInViewPort();
        int maxFirstRow = totalRows - pageLength;
        if (firstRowInViewPort > maxFirstRow && maxFirstRow >= 0) {
            firstRowInViewPort = maxFirstRow;
        }
        lastRequestedFirstvisible = firstRowInViewPort;
        client.updateVariable(paintableId, "firstvisible", firstRowInViewPort,
                false);
    }

    /**
     * This method has logic which rows needs to be requested from server when
     * user scrolls
     */

    @Override
    public void onScroll(ScrollEvent event) {
        // Do not handle scroll events while there is scroll initiated from
        // server side which is not yet executed (#11454)
        if (isLazyScrollerActive()) {
            return;
        }

        scrollLeft = scrollBodyPanel.getElement().getScrollLeft();
        scrollTop = scrollBodyPanel.getScrollPosition();
        /*
         * #6970 - IE sometimes fires scroll events for a detached table.
         * 
         * FIXME initializedAndAttached should probably be renamed - its name
         * doesn't seem to reflect its semantics. onDetach() doesn't set it to
         * false, and changing that might break something else, so we need to
         * check isAttached() separately.
         */
        if (!initializedAndAttached || !isAttached()) {
            return;
        }
        if (!enabled) {
            scrollBodyPanel
                    .setScrollPosition(measureRowHeightOffset(firstRowInViewPort));
            return;
        }

        rowRequestHandler.cancel();

        if (BrowserInfo.get().isSafari() && event != null && scrollTop == 0) {
            // due to the webkitoverflowworkaround, top may sometimes report 0
            // for webkit, although it really is not. Expecting to have the
            // correct
            // value available soon.
            Scheduler.get().scheduleDeferred(new Command() {

                @Override
                public void execute() {
                    onScroll(null);
                }
            });
            return;
        }

        // fix headers horizontal scrolling
        tHead.setHorizontalScrollPosition(scrollLeft);

        // fix footers horizontal scrolling
        tFoot.setHorizontalScrollPosition(scrollLeft);

        if (totalRows == 0) {
            // No rows, no need to fetch new rows
            return;
        }

        firstRowInViewPort = calcFirstRowInViewPort();
        int maxFirstRow = totalRows - pageLength;
        if (firstRowInViewPort > maxFirstRow && maxFirstRow >= 0) {
            firstRowInViewPort = maxFirstRow;
        }

        int postLimit = (int) (firstRowInViewPort + (pageLength - 1) + pageLength
                * cache_react_rate);
        if (postLimit > totalRows - 1) {
            postLimit = totalRows - 1;
        }
        int preLimit = (int) (firstRowInViewPort - pageLength
                * cache_react_rate);
        if (preLimit < 0) {
            preLimit = 0;
        }
        final int lastRendered = scrollBody.getLastRendered();
        final int firstRendered = scrollBody.getFirstRendered();

        if (postLimit <= lastRendered && preLimit >= firstRendered) {
            // we're within no-react area, no need to request more rows
            // remember which firstvisible we requested, in case the server has
            // a differing opinion
            lastRequestedFirstvisible = firstRowInViewPort;
            client.updateVariable(paintableId, "firstvisible",
                    firstRowInViewPort, false);
            return;
        }

        if (allRenderedRowsAreNew()) {
            // need a totally new set of rows
            rowRequestHandler
                    .setReqFirstRow((firstRowInViewPort - (int) (pageLength * cache_rate)));
            int last = firstRowInViewPort + (int) (cache_rate * pageLength)
                    + pageLength - 1;
            if (last >= totalRows) {
                last = totalRows - 1;
            }
            rowRequestHandler.setReqRows(last
                    - rowRequestHandler.getReqFirstRow() + 1);
            updatedReqRows = false;
            rowRequestHandler.deferRowFetch();
            return;
        }
        if (preLimit < firstRendered) {
            // need some rows to the beginning of the rendered area

            rowRequestHandler
                    .setReqFirstRow((int) (firstRowInViewPort - pageLength
                            * cache_rate));
            rowRequestHandler.setReqRows(firstRendered
                    - rowRequestHandler.getReqFirstRow());
            rowRequestHandler.deferRowFetch();

            return;
        }
        if (postLimit > lastRendered) {
            // need some rows to the end of the rendered area
            int reqRows = (int) ((firstRowInViewPort + pageLength + pageLength
                    * cache_rate) - lastRendered);
            rowRequestHandler.triggerRowFetch(lastRendered + 1, reqRows);
        }
    }

    private boolean allRenderedRowsAreNew() {
        int firstRowInViewPort = calcFirstRowInViewPort();
        int firstRendered = scrollBody.getFirstRendered();
        int lastRendered = scrollBody.getLastRendered();
        return (firstRowInViewPort - pageLength * cache_rate > lastRendered || firstRowInViewPort
                + pageLength + pageLength * cache_rate < firstRendered);
    }

    protected int calcFirstRowInViewPort() {
        return (int) Math.ceil(scrollTop / scrollBody.getRowHeight());
    }

    @Override
    public VScrollTableDropHandler getDropHandler() {
        return dropHandler;
    }

    private static class TableDDDetails {
        int overkey = -1;
        VerticalDropLocation dropLocation;
        String colkey;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TableDDDetails) {
                TableDDDetails other = (TableDDDetails) obj;
                return dropLocation == other.dropLocation
                        && overkey == other.overkey
                        && ((colkey != null && colkey.equals(other.colkey)) || (colkey == null && other.colkey == null));
            }
            return false;
        }

        //
        // public int hashCode() {
        // return overkey;
        // }
    }

    public class VScrollTableDropHandler extends VAbstractDropHandler {

        private static final String ROWSTYLEBASE = "v-table-row-drag-";
        private TableDDDetails dropDetails;
        private TableDDDetails lastEmphasized;

        @Override
        public void dragEnter(VDragEvent drag) {
            updateDropDetails(drag);
            super.dragEnter(drag);
        }

        private void updateDropDetails(VDragEvent drag) {
            dropDetails = new TableDDDetails();
            Element elementOver = drag.getElementOver();

            Class<? extends Widget> clazz = getRowClass();
            VScrollTableRow row = null;
            if (clazz != null) {
                row = WidgetUtil.findWidget(elementOver, clazz);
            }
            if (row != null) {
                dropDetails.overkey = row.rowKey;
                Element tr = row.getElement();
                Element element = elementOver;
                while (element != null && element.getParentElement() != tr) {
                    element = element.getParentElement();
                }
                int childIndex = DOM.getChildIndex(tr, element);
                dropDetails.colkey = tHead.getHeaderCell(childIndex)
                        .getColKey();
                dropDetails.dropLocation = DDUtil.getVerticalDropLocation(
                        row.getElement(), drag.getCurrentGwtEvent(), 0.2);
            }

            drag.getDropDetails().put("itemIdOver", dropDetails.overkey + "");
            drag.getDropDetails().put(
                    "detail",
                    dropDetails.dropLocation != null ? dropDetails.dropLocation
                            .toString() : null);

        }

        private Class<? extends Widget> getRowClass() {
            // get the row type this way to make dd work in derived
            // implementations
            Iterator<Widget> iterator = scrollBody.iterator();
            if (iterator.hasNext()) {
                return iterator.next().getClass();
            } else {
                return null;
            }
        }

        @Override
        public void dragOver(VDragEvent drag) {
            TableDDDetails oldDetails = dropDetails;
            updateDropDetails(drag);
            if (!oldDetails.equals(dropDetails)) {
                deEmphasis();
                final TableDDDetails newDetails = dropDetails;
                VAcceptCallback cb = new VAcceptCallback() {

                    @Override
                    public void accepted(VDragEvent event) {
                        if (newDetails.equals(dropDetails)) {
                            dragAccepted(event);
                        }
                        /*
                         * Else new target slot already defined, ignore
                         */
                    }
                };
                validate(cb, drag);
            }
        }

        @Override
        public void dragLeave(VDragEvent drag) {
            deEmphasis();
            super.dragLeave(drag);
        }

        @Override
        public boolean drop(VDragEvent drag) {
            deEmphasis();
            return super.drop(drag);
        }

        private void deEmphasis() {
            UIObject.setStyleName(getElement(),
                    getStylePrimaryName() + "-drag", false);
            if (lastEmphasized == null) {
                return;
            }
            for (Widget w : scrollBody.renderedRows) {
                VScrollTableRow row = (VScrollTableRow) w;
                if (lastEmphasized != null
                        && row.rowKey == lastEmphasized.overkey) {
                    String stylename = ROWSTYLEBASE
                            + lastEmphasized.dropLocation.toString()
                                    .toLowerCase();
                    VScrollTableRow.setStyleName(row.getElement(), stylename,
                            false);
                    lastEmphasized = null;
                    return;
                }
            }
        }

        /**
         * TODO needs different drop modes ?? (on cells, on rows), now only
         * supports rows
         */
        private void emphasis(TableDDDetails details) {
            deEmphasis();
            UIObject.setStyleName(getElement(),
                    getStylePrimaryName() + "-drag", true);
            // iterate old and new emphasized row
            for (Widget w : scrollBody.renderedRows) {
                VScrollTableRow row = (VScrollTableRow) w;
                if (details != null && details.overkey == row.rowKey) {
                    String stylename = ROWSTYLEBASE
                            + details.dropLocation.toString().toLowerCase();
                    VScrollTableRow.setStyleName(row.getElement(), stylename,
                            true);
                    lastEmphasized = details;
                    return;
                }
            }
        }

        @Override
        protected void dragAccepted(VDragEvent drag) {
            emphasis(dropDetails);
        }

        @Override
        public ComponentConnector getConnector() {
            return ConnectorMap.get(client).getConnector(VScrollTable.this);
        }

        @Override
        public ApplicationConnection getApplicationConnection() {
            return client;
        }

    }

    protected VScrollTableRow getFocusedRow() {
        return focusedRow;
    }

    /**
     * Moves the selection head to a specific row
     * 
     * @param row
     *            The row to where the selection head should move
     * @return Returns true if focus was moved successfully, else false
     */
    public boolean setRowFocus(VScrollTableRow row) {

        if (!isSelectable()) {
            return false;
        }

        // Remove previous selection
        if (focusedRow != null && focusedRow != row) {
            focusedRow.removeStyleName(getStylePrimaryName() + "-focus");
        }

        if (row != null) {
            // Apply focus style to new selection
            row.addStyleName(getStylePrimaryName() + "-focus");

            /*
             * Trying to set focus on already focused row
             */
            if (row == focusedRow) {
                return false;
            }

            // Set new focused row
            focusedRow = row;

            if (hasFocus) {
                ensureRowIsVisible(row);
            }

            return true;
        }

        return false;
    }

    /**
     * Ensures that the row is visible
     * 
     * @param row
     *            The row to ensure is visible
     */
    private void ensureRowIsVisible(VScrollTableRow row) {
        if (BrowserInfo.get().isTouchDevice()) {
            // Skip due to android devices that have broken scrolltop will may
            // get odd scrolling here.
            return;
        }
        /*
         * FIXME The next line doesn't always do what expected, because if the
         * row is not in the DOM it won't scroll to it.
         */
        WidgetUtil.scrollIntoViewVertically(row.getElement());
    }

    /**
     * Handles the keyboard events handled by the table
     * 
     * @param event
     *            The keyboard event received
     * @return true iff the navigation event was handled
     */
    protected boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (keycode == KeyCodes.KEY_TAB || keycode == KeyCodes.KEY_SHIFT) {
            // Do not handle tab key
            return false;
        }

        // Down navigation
        if (!isSelectable() && keycode == getNavigationDownKey()) {
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition() + scrollingVelocity);
            return true;
        } else if (keycode == getNavigationDownKey()) {
            if (isMultiSelectModeAny() && moveFocusDown()) {
                selectFocusedRow(ctrl, shift);

            } else if (isSingleSelectMode() && !shift && moveFocusDown()) {
                selectFocusedRow(ctrl, shift);
            }
            return true;
        }

        // Up navigation
        if (!isSelectable() && keycode == getNavigationUpKey()) {
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition() - scrollingVelocity);
            return true;
        } else if (keycode == getNavigationUpKey()) {
            if (isMultiSelectModeAny() && moveFocusUp()) {
                selectFocusedRow(ctrl, shift);
            } else if (isSingleSelectMode() && !shift && moveFocusUp()) {
                selectFocusedRow(ctrl, shift);
            }
            return true;
        }

        if (keycode == getNavigationLeftKey()) {
            // Left navigation
            scrollBodyPanel.setHorizontalScrollPosition(scrollBodyPanel
                    .getHorizontalScrollPosition() - scrollingVelocity);
            return true;

        } else if (keycode == getNavigationRightKey()) {
            // Right navigation
            scrollBodyPanel.setHorizontalScrollPosition(scrollBodyPanel
                    .getHorizontalScrollPosition() + scrollingVelocity);
            return true;
        }

        // Select navigation
        if (isSelectable() && keycode == getNavigationSelectKey()) {
            if (isSingleSelectMode()) {
                boolean wasSelected = focusedRow.isSelected();
                deselectAll();
                if (!wasSelected || !nullSelectionAllowed) {
                    focusedRow.toggleSelection();
                }
            } else {
                focusedRow.toggleSelection();
                removeRowFromUnsentSelectionRanges(focusedRow);
            }

            sendSelectedRows();
            return true;
        }

        // Page Down navigation
        if (keycode == getNavigationPageDownKey()) {
            if (isSelectable()) {
                /*
                 * If selectable we plagiate MSW behaviour: first scroll to the
                 * end of current view. If at the end, scroll down one page
                 * length and keep the selected row in the bottom part of
                 * visible area.
                 */
                if (!isFocusAtTheEndOfTable()) {
                    VScrollTableRow lastVisibleRowInViewPort = scrollBody
                            .getRowByRowIndex(firstRowInViewPort
                                    + getFullyVisibleRowCount() - 1);
                    if (lastVisibleRowInViewPort != null
                            && lastVisibleRowInViewPort != focusedRow) {
                        // focused row is not at the end of the table, move
                        // focus and select the last visible row
                        setRowFocus(lastVisibleRowInViewPort);
                        selectFocusedRow(ctrl, shift);
                        updateFirstVisibleAndSendSelectedRows();
                    } else {
                        int indexOfToBeFocused = focusedRow.getIndex()
                                + getFullyVisibleRowCount();
                        if (indexOfToBeFocused >= totalRows) {
                            indexOfToBeFocused = totalRows - 1;
                        }
                        VScrollTableRow toBeFocusedRow = scrollBody
                                .getRowByRowIndex(indexOfToBeFocused);

                        if (toBeFocusedRow != null) {
                            /*
                             * if the next focused row is rendered
                             */
                            setRowFocus(toBeFocusedRow);
                            selectFocusedRow(ctrl, shift);
                            // TODO needs scrollintoview ?
                            updateFirstVisibleAndSendSelectedRows();
                        } else {
                            // scroll down by pixels and return, to wait for
                            // new rows, then select the last item in the
                            // viewport
                            selectLastItemInNextRender = true;
                            multiselectPending = shift;
                            scrollByPagelength(1);
                        }
                    }
                }
            } else {
                /* No selections, go page down by scrolling */
                scrollByPagelength(1);
            }
            return true;
        }

        // Page Up navigation
        if (keycode == getNavigationPageUpKey()) {
            if (isSelectable()) {
                /*
                 * If selectable we plagiate MSW behaviour: first scroll to the
                 * end of current view. If at the end, scroll down one page
                 * length and keep the selected row in the bottom part of
                 * visible area.
                 */
                if (!isFocusAtTheBeginningOfTable()) {
                    VScrollTableRow firstVisibleRowInViewPort = scrollBody
                            .getRowByRowIndex(firstRowInViewPort);
                    if (firstVisibleRowInViewPort != null
                            && firstVisibleRowInViewPort != focusedRow) {
                        // focus is not at the beginning of the table, move
                        // focus and select the first visible row
                        setRowFocus(firstVisibleRowInViewPort);
                        selectFocusedRow(ctrl, shift);
                        updateFirstVisibleAndSendSelectedRows();
                    } else {
                        int indexOfToBeFocused = focusedRow.getIndex()
                                - getFullyVisibleRowCount();
                        if (indexOfToBeFocused < 0) {
                            indexOfToBeFocused = 0;
                        }
                        VScrollTableRow toBeFocusedRow = scrollBody
                                .getRowByRowIndex(indexOfToBeFocused);

                        if (toBeFocusedRow != null) { // if the next focused row
                                                      // is rendered
                            setRowFocus(toBeFocusedRow);
                            selectFocusedRow(ctrl, shift);
                            // TODO needs scrollintoview ?
                            updateFirstVisibleAndSendSelectedRows();
                        } else {
                            // unless waiting for the next rowset already
                            // scroll down by pixels and return, to wait for
                            // new rows, then select the last item in the
                            // viewport
                            selectFirstItemInNextRender = true;
                            multiselectPending = shift;
                            scrollByPagelength(-1);
                        }
                    }
                }
            } else {
                /* No selections, go page up by scrolling */
                scrollByPagelength(-1);
            }

            return true;
        }

        // Goto start navigation
        if (keycode == getNavigationStartKey()) {
            scrollBodyPanel.setScrollPosition(0);
            if (isSelectable()) {
                if (focusedRow != null && focusedRow.getIndex() == 0) {
                    return false;
                } else {
                    VScrollTableRow rowByRowIndex = (VScrollTableRow) scrollBody
                            .iterator().next();
                    if (rowByRowIndex.getIndex() == 0) {
                        setRowFocus(rowByRowIndex);
                        selectFocusedRow(ctrl, shift);
                        updateFirstVisibleAndSendSelectedRows();
                    } else {
                        // first row of table will come in next row fetch
                        if (ctrl) {
                            focusFirstItemInNextRender = true;
                        } else {
                            selectFirstItemInNextRender = true;
                            multiselectPending = shift;
                        }
                    }
                }
            }
            return true;
        }

        // Goto end navigation
        if (keycode == getNavigationEndKey()) {
            scrollBodyPanel.setScrollPosition(scrollBody.getOffsetHeight());
            if (isSelectable()) {
                final int lastRendered = scrollBody.getLastRendered();
                if (lastRendered + 1 == totalRows) {
                    VScrollTableRow rowByRowIndex = scrollBody
                            .getRowByRowIndex(lastRendered);
                    if (focusedRow != rowByRowIndex) {
                        setRowFocus(rowByRowIndex);
                        selectFocusedRow(ctrl, shift);
                        updateFirstVisibleAndSendSelectedRows();
                    }
                } else {
                    if (ctrl) {
                        focusLastItemInNextRender = true;
                    } else {
                        selectLastItemInNextRender = true;
                        multiselectPending = shift;
                    }
                }
            }
            return true;
        }

        return false;
    }

    private boolean isFocusAtTheBeginningOfTable() {
        return focusedRow.getIndex() == 0;
    }

    private boolean isFocusAtTheEndOfTable() {
        return focusedRow.getIndex() + 1 >= totalRows;
    }

    private int getFullyVisibleRowCount() {
        return (int) (scrollBodyPanel.getOffsetHeight() / scrollBody
                .getRowHeight());
    }

    private void scrollByPagelength(int i) {
        int pixels = i * scrollBodyPanel.getOffsetHeight();
        int newPixels = scrollBodyPanel.getScrollPosition() + pixels;
        if (newPixels < 0) {
            newPixels = 0;
        } // else if too high, NOP (all know browsers accept illegally big
          // values here)
        scrollBodyPanel.setScrollPosition(newPixels);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */

    @Override
    public void onFocus(FocusEvent event) {
        if (isFocusable()) {
            hasFocus = true;

            // Focus a row if no row is in focus
            if (focusedRow == null) {
                focusRowFromBody();
            } else {
                setRowFocus(focusedRow);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */

    @Override
    public void onBlur(BlurEvent event) {
        hasFocus = false;
        navKeyDown = false;

        if (BrowserInfo.get().isIE()) {
            /*
             * IE sometimes moves focus to a clicked table cell... (#7965)
             * ...and sometimes it sends blur events even though the focus
             * handler is still active. (#10464)
             */
            Element focusedElement = WidgetUtil.getFocusedElement();
            if (Util.getConnectorForElement(client, getParent(), focusedElement) == this
                    && focusedElement != null
                    && focusedElement != scrollBodyPanel.getFocusElement()) {
                /*
                 * Steal focus back to the focus handler if it was moved to some
                 * other part of the table. Avoid stealing focus in other cases.
                 */
                focus();
                return;
            }
        }

        if (isFocusable()) {
            // Unfocus any row
            setRowFocus(null);
        }
    }

    /**
     * Removes a key from a range if the key is found in a selected range
     * 
     * @param key
     *            The key to remove
     */
    private void removeRowFromUnsentSelectionRanges(VScrollTableRow row) {
        Collection<SelectionRange> newRanges = null;
        for (Iterator<SelectionRange> iterator = selectedRowRanges.iterator(); iterator
                .hasNext();) {
            SelectionRange range = iterator.next();
            if (range.inRange(row)) {
                // Split the range if given row is in range
                Collection<SelectionRange> splitranges = range.split(row);
                if (newRanges == null) {
                    newRanges = new ArrayList<SelectionRange>();
                }
                newRanges.addAll(splitranges);
                iterator.remove();
            }
        }
        if (newRanges != null) {
            selectedRowRanges.addAll(newRanges);
        }
    }

    /**
     * Can the Table be focused?
     * 
     * @return True if the table can be focused, else false
     */
    public boolean isFocusable() {
        if (scrollBody != null && enabled) {
            return !(!hasHorizontalScrollbar() && !hasVerticalScrollbar() && !isSelectable());
        }
        return false;
    }

    private boolean hasHorizontalScrollbar() {
        return scrollBody.getOffsetWidth() > scrollBodyPanel.getOffsetWidth();
    }

    private boolean hasVerticalScrollbar() {
        return scrollBody.getOffsetHeight() > scrollBodyPanel.getOffsetHeight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.Focusable#focus()
     */

    @Override
    public void focus() {
        if (isFocusable()) {
            scrollBodyPanel.focus();
        }
    }

    /**
     * Sets the proper tabIndex for scrollBodyPanel (the focusable elemen in the
     * component).
     * <p>
     * If the component has no explicit tabIndex a zero is given (default
     * tabbing order based on dom hierarchy) or -1 if the component does not
     * need to gain focus. The component needs no focus if it has no scrollabars
     * (not scrollable) and not selectable. Note that in the future shortcut
     * actions may need focus.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void setProperTabIndex() {
        int storedScrollTop = 0;
        int storedScrollLeft = 0;

        if (BrowserInfo.get().getOperaVersion() >= 11) {
            // Workaround for Opera scroll bug when changing tabIndex (#6222)
            storedScrollTop = scrollBodyPanel.getScrollPosition();
            storedScrollLeft = scrollBodyPanel.getHorizontalScrollPosition();
        }

        if (tabIndex == 0 && !isFocusable()) {
            scrollBodyPanel.setTabIndex(-1);
        } else {
            scrollBodyPanel.setTabIndex(tabIndex);
        }

        if (BrowserInfo.get().getOperaVersion() >= 11) {
            // Workaround for Opera scroll bug when changing tabIndex (#6222)
            scrollBodyPanel.setScrollPosition(storedScrollTop);
            scrollBodyPanel.setHorizontalScrollPosition(storedScrollLeft);
        }
    }

    public void startScrollingVelocityTimer() {
        if (scrollingVelocityTimer == null) {
            scrollingVelocityTimer = new Timer() {

                @Override
                public void run() {
                    scrollingVelocity++;
                }
            };
            scrollingVelocityTimer.scheduleRepeating(100);
        }
    }

    public void cancelScrollingVelocityTimer() {
        if (scrollingVelocityTimer != null) {
            // Remove velocityTimer if it exists and the Table is disabled
            scrollingVelocityTimer.cancel();
            scrollingVelocityTimer = null;
            scrollingVelocity = 10;
        }
    }

    /**
     * 
     * @param keyCode
     * @return true if the given keyCode is used by the table for navigation
     */
    private boolean isNavigationKey(int keyCode) {
        return keyCode == getNavigationUpKey()
                || keyCode == getNavigationLeftKey()
                || keyCode == getNavigationRightKey()
                || keyCode == getNavigationDownKey()
                || keyCode == getNavigationPageUpKey()
                || keyCode == getNavigationPageDownKey()
                || keyCode == getNavigationEndKey()
                || keyCode == getNavigationStartKey();
    }

    public void lazyRevertFocusToRow(final VScrollTableRow currentlyFocusedRow) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                if (currentlyFocusedRow != null) {
                    setRowFocus(currentlyFocusedRow);
                } else {
                    VConsole.log("no row?");
                    focusRowFromBody();
                }
                scrollBody.ensureFocus();
            }
        });
    }

    @Override
    public Action[] getActions() {
        if (bodyActionKeys == null) {
            return new Action[] {};
        }
        final Action[] actions = new Action[bodyActionKeys.length];
        for (int i = 0; i < actions.length; i++) {
            final String actionKey = bodyActionKeys[i];
            Action bodyAction = new TreeAction(this, null, actionKey);
            bodyAction.setCaption(getActionCaption(actionKey));
            bodyAction.setIconUrl(getActionIcon(actionKey));
            actions[i] = bodyAction;
        }
        return actions;
    }

    @Override
    public ApplicationConnection getClient() {
        return client;
    }

    @Override
    public String getPaintableId() {
        return paintableId;
    }

    /**
     * Add this to the element mouse down event by using element.setPropertyJSO
     * ("onselectstart",applyDisableTextSelectionIEHack()); Remove it then again
     * when the mouse is depressed in the mouse up event.
     * 
     * @return Returns the JSO preventing text selection
     */
    private static native JavaScriptObject getPreventTextSelectionIEHack()
    /*-{
            return function(){ return false; };
    }-*/;

    public void triggerLazyColumnAdjustment(boolean now) {
        lazyAdjustColumnWidths.cancel();
        if (now) {
            lazyAdjustColumnWidths.run();
        } else {
            lazyAdjustColumnWidths.schedule(LAZY_COLUMN_ADJUST_TIMEOUT);
        }
    }

    private boolean isDynamicWidth() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        return paintable.isUndefinedWidth();
    }

    private boolean isDynamicHeight() {
        ComponentConnector paintable = ConnectorMap.get(client).getConnector(
                this);
        if (paintable == null) {
            // This should be refactored. As isDynamicHeight can be called from
            // a timer it is possible that the connector has been unregistered
            // when this method is called, causing getConnector to return null.
            return false;
        }
        return paintable.isUndefinedHeight();
    }

    private void debug(String msg) {
        if (enableDebug) {
            VConsole.error(msg);
        }
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

    private static final String SUBPART_HEADER = "header";
    private static final String SUBPART_FOOTER = "footer";
    private static final String SUBPART_ROW = "row";
    private static final String SUBPART_COL = "col";
    /**
     * Matches header[ix] - used for extracting the index of the targeted header
     * cell
     */
    private static final RegExp SUBPART_HEADER_REGEXP = RegExp
            .compile(SUBPART_HEADER + "\\[(\\d+)\\]");
    /**
     * Matches footer[ix] - used for extracting the index of the targeted footer
     * cell
     */
    private static final RegExp SUBPART_FOOTER_REGEXP = RegExp
            .compile(SUBPART_FOOTER + "\\[(\\d+)\\]");
    /** Matches row[ix] - used for extracting the index of the targeted row */
    private static final RegExp SUBPART_ROW_REGEXP = RegExp.compile(SUBPART_ROW
            + "\\[(\\d+)]");
    /** Matches col[ix] - used for extracting the index of the targeted column */
    private static final RegExp SUBPART_ROW_COL_REGEXP = RegExp
            .compile(SUBPART_ROW + "\\[(\\d+)\\]/" + SUBPART_COL
                    + "\\[(\\d+)\\]");

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if (SUBPART_ROW_COL_REGEXP.test(subPart)) {
            MatchResult result = SUBPART_ROW_COL_REGEXP.exec(subPart);
            int rowIx = Integer.valueOf(result.getGroup(1));
            int colIx = Integer.valueOf(result.getGroup(2));
            VScrollTableRow row = scrollBody.getRowByRowIndex(rowIx);
            if (row != null) {
                Element rowElement = row.getElement();
                if (colIx < rowElement.getChildCount()) {
                    return rowElement.getChild(colIx).getFirstChild().cast();
                }
            }

        } else if (SUBPART_ROW_REGEXP.test(subPart)) {
            MatchResult result = SUBPART_ROW_REGEXP.exec(subPart);
            int rowIx = Integer.valueOf(result.getGroup(1));
            VScrollTableRow row = scrollBody.getRowByRowIndex(rowIx);
            if (row != null) {
                return row.getElement();
            }

        } else if (SUBPART_HEADER_REGEXP.test(subPart)) {
            MatchResult result = SUBPART_HEADER_REGEXP.exec(subPart);
            int headerIx = Integer.valueOf(result.getGroup(1));
            HeaderCell headerCell = tHead.getHeaderCell(headerIx);
            if (headerCell != null) {
                return headerCell.getElement();
            }

        } else if (SUBPART_FOOTER_REGEXP.test(subPart)) {
            MatchResult result = SUBPART_FOOTER_REGEXP.exec(subPart);
            int footerIx = Integer.valueOf(result.getGroup(1));
            FooterCell footerCell = tFoot.getFooterCell(footerIx);
            if (footerCell != null) {
                return footerCell.getElement();
            }
        }
        // Nothing found.
        return null;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        Widget widget = WidgetUtil.findWidget(subElement, null);
        if (widget instanceof HeaderCell) {
            return SUBPART_HEADER + "[" + tHead.visibleCells.indexOf(widget)
                    + "]";
        } else if (widget instanceof FooterCell) {
            return SUBPART_FOOTER + "[" + tFoot.visibleCells.indexOf(widget)
                    + "]";
        } else if (widget instanceof VScrollTableRow) {
            // a cell in a row
            VScrollTableRow row = (VScrollTableRow) widget;
            int rowIx = scrollBody.indexOf(row);
            if (rowIx >= 0) {
                int colIx = -1;
                for (int ix = 0; ix < row.getElement().getChildCount(); ix++) {
                    if (row.getElement().getChild(ix).isOrHasChild(subElement)) {
                        colIx = ix;
                        break;
                    }
                }
                if (colIx >= 0) {
                    return SUBPART_ROW + "[" + rowIx + "]/" + SUBPART_COL + "["
                            + colIx + "]";
                }
                return SUBPART_ROW + "[" + rowIx + "]";
            }
        }
        // Nothing found.
        return null;
    }

    /**
     * @since 7.2.6
     */
    public void onUnregister() {
        if (addCloseHandler != null) {
            addCloseHandler.removeHandler();
        }
    }

    /*
     * Return true if component need to perform some work and false otherwise.
     */
    @Override
    public boolean isWorkPending() {
        return lazyAdjustColumnWidths.isRunning();
    }

    private static Logger getLogger() {
        return Logger.getLogger(VScrollTable.class.getName());
    }
}
