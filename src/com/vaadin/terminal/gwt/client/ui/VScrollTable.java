/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
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
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.terminal.gwt.client.ui.dd.DDUtil;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.VDragAndDropManager;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;
import com.vaadin.terminal.gwt.client.ui.dd.VTransferable;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

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
public class VScrollTable extends FlowPanel implements Table, ScrollHandler,
        VHasDropHandler, FocusHandler, BlurHandler, Focusable, ActionOwner {

    private static final String ROW_HEADER_COLUMN_KEY = "0";

    public static final String CLASSNAME = "v-table";
    public static final String CLASSNAME_SELECTION_FOCUS = CLASSNAME + "-focus";

    public static final String ITEM_CLICK_EVENT_ID = "itemClick";
    public static final String HEADER_CLICK_EVENT_ID = "handleHeaderClick";
    public static final String FOOTER_CLICK_EVENT_ID = "handleFooterClick";
    public static final String COLUMN_RESIZE_EVENT_ID = "columnResize";
    public static final String COLUMN_REORDER_EVENT_ID = "columnReorder";

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
     * fraction of pageLenght which can be scrolled without making new request
     */
    private double cache_react_rate = 0.75 * cache_rate;

    public static final char ALIGN_CENTER = 'c';
    public static final char ALIGN_LEFT = 'b';
    public static final char ALIGN_RIGHT = 'e';
    private static final int CHARCODE_SPACE = 32;
    private int firstRowInViewPort = 0;
    private int pageLength = 15;
    private int lastRequestedFirstvisible = 0; // to detect "serverside scroll"

    protected boolean showRowHeaders = false;

    private String[] columnOrder;

    protected ApplicationConnection client;
    protected String paintableId;

    private boolean immediate;
    private boolean nullSelectionAllowed = true;

    private int selectMode = Table.SELECT_MODE_NONE;

    private final HashSet<String> selectedRowKeys = new HashSet<String>();

    /*
     * When scrolling and selecting at the same time, the selections are not in
     * sync with the server while retrieving new rows (until key is released).
     */
    private HashSet<Object> unSyncedselectionsBeforeRowFetch;

    /*
     * These are used when jumping between pages when pressing Home and End
     */
    private boolean selectLastItemInNextRender = false;
    private boolean selectFirstItemInNextRender = false;
    private boolean focusFirstItemInNextRender = false;
    private boolean focusLastItemInNextRender = false;

    /*
     * The currently focused row
     */
    private VScrollTableRow focusedRow;

    /*
     * Helper to store selection range start in when using the keyboard
     */
    private VScrollTableRow selectionRangeStart;

    /*
     * Flag for notifying when the selection has changed and should be sent to
     * the server
     */
    private boolean selectionChanged = false;

    /*
     * The speed (in pixels) which the scrolling scrolls vertically/horizontally
     */
    private int scrollingVelocity = 10;

    private Timer scrollingVelocityTimer = null;

    private String[] bodyActionKeys;

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
            if (!(endOfFirstRange - startRow.getIndex() < 0)) {
                // create range of first part unless its length is < 1
                VScrollTableRow endOfRange = scrollBody
                        .getRowByRowIndex(endOfFirstRange);
                ranges.add(new SelectionRange(startRow, endOfRange));
            }
            int startOfSecondRange = row.getIndex() + 1;
            if (!(getEndIndex() - startOfSecondRange < 0)) {
                // create range of second part unless its length is < 1
                VScrollTableRow startOfRange = scrollBody
                        .getRowByRowIndex(startOfSecondRange);
                ranges.add(new SelectionRange(startOfRange, getEndIndex()
                        - startOfSecondRange + 1));
            }
            return ranges;
        }

        private int getEndIndex() {
            return startRow.getIndex() + length - 1;
        }

    };

    private final HashSet<SelectionRange> selectedRowRanges = new HashSet<SelectionRange>();

    private boolean initializedAndAttached = false;

    /**
     * Flag to indicate if a column width recalculation is needed due update.
     */
    private boolean headerChangedDuringUpdate = false;

    private final TableHead tHead = new TableHead();

    private final TableFooter tFoot = new TableFooter();

    private final FocusableScrollPanel scrollBodyPanel = new FocusableScrollPanel(
            true);

    private KeyPressHandler navKeyPressHandler = new KeyPressHandler() {
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
    private int totalRows;

    private Set<String> collapsedColumns;

    private final RowRequestHandler rowRequestHandler;
    private VScrollTableBody scrollBody;
    private int firstvisible = 0;
    private boolean sortAscending;
    private String sortColumn;
    private boolean columnReordering;

    /**
     * This map contains captions and icon urls for actions like: * "33_c" ->
     * "Edit" * "33_i" -> "http://dom.com/edit.png"
     */
    private final HashMap<Object, String> actionMap = new HashMap<Object, String>();
    private String[] visibleColOrder;
    private boolean initialContentReceived = false;
    private Element scrollPositionElement;
    private boolean enabled;
    private boolean showColHeaders;
    private boolean showColFooters;

    /** flag to indicate that table body has changed */
    private boolean isNewBody = true;

    /*
     * Read from the "recalcWidths" -attribute. When it is true, the table will
     * recalculate the widths for columns - desirable in some cases. For #1983,
     * marked experimental.
     */
    boolean recalcWidths = false;

    private final ArrayList<Panel> lazyUnregistryBag = new ArrayList<Panel>();
    private String height;
    private String width = "";
    private boolean rendering = false;
    private boolean hasFocus = false;
    private int dragmode;

    private int multiselectmode = BrowserInfo.get().isTouchDevice() ? MULTISELECT_MODE_SIMPLE
            : MULTISELECT_MODE_DEFAULT;;
    private int tabIndex;
    private TouchScrollDelegate touchScrollDelegate;

    public VScrollTable() {
        scrollBodyPanel.setStyleName(CLASSNAME + "-body-wrapper");
        scrollBodyPanel.addFocusHandler(this);
        scrollBodyPanel.addBlurHandler(this);

        scrollBodyPanel.addScrollHandler(this);
        scrollBodyPanel.setStyleName(CLASSNAME + "-body");

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

        scrollBodyPanel.sinkEvents(Event.TOUCHEVENTS);
        scrollBodyPanel.addDomHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                getTouchScrollDelegate().onTouchStart(event);
            }
        }, TouchStartEvent.getType());

        scrollBodyPanel.sinkEvents(Event.ONCONTEXTMENU);
        scrollBodyPanel.addDomHandler(new ContextMenuHandler() {
            public void onContextMenu(ContextMenuEvent event) {
                handleBodyContextMenu(event);
            }
        }, ContextMenuEvent.getType());

        setStyleName(CLASSNAME);

        add(tHead);
        add(scrollBodyPanel);
        add(tFoot);

        rowRequestHandler = new RowRequestHandler();
    }

    protected TouchScrollDelegate getTouchScrollDelegate() {
        if (touchScrollDelegate == null) {
            touchScrollDelegate = new TouchScrollDelegate(
                    scrollBodyPanel.getElement());
        }
        return touchScrollDelegate;

    }

    private void handleBodyContextMenu(ContextMenuEvent event) {
        if (enabled && bodyActionKeys != null) {
            int left = Util.getTouchOrMouseClientX(event.getNativeEvent());
            int top = Util.getTouchOrMouseClientY(event.getNativeEvent());
            top += Window.getScrollTop();
            left += Window.getScrollLeft();
            client.getContextMenu().showAt(this, left, top);
        }
        event.stopPropagation();
        event.preventDefault();
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
            }

            // Ctrl+arrows moves selection head
            else if (isSelectable() && ctrlSelect && !shiftSelect) {
                selectionRangeStart = focusedRow;
                // No selection, only selection head is moved
            }

            // Shift+arrows selection selects a range
            else if (selectMode == SELECT_MODE_MULTI && !ctrlSelect
                    && shiftSelect) {
                focusedRow.toggleShiftSelection(shiftSelect);
            }
        }
    }

    /**
     * Sends the selection to the server if changed since the last update/visit.
     */
    protected void sendSelectedRows() {
        // Don't send anything if selection has not changed
        if (!selectionChanged) {
            return;
        }

        // Reset selection changed flag
        selectionChanged = false;

        // Note: changing the immediateness of this might require changes to
        // "clickEvent" immediateness also.
        if (multiselectmode == MULTISELECT_MODE_DEFAULT) {
            // Convert ranges to a set of strings
            Set<String> ranges = new HashSet<String>();
            for (SelectionRange range : selectedRowRanges) {
                ranges.add(range.toString());
            }

            // Send the selected row ranges
            client.updateVariable(paintableId, "selectedRanges",
                    ranges.toArray(new String[selectedRowRanges.size()]), false);

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
                immediate);

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        rendering = true;

        /*
         * We need to do this before updateComponent since updateComponent calls
         * this.setHeight() which will calculate a new body height depending on
         * the space available.
         */
        if (uidl.hasAttribute("colfooters")) {
            showColFooters = uidl.getBooleanAttribute("colfooters");
        }

        tFoot.setVisible(showColFooters);

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }

        // we may have pending cache row fetch, cancel it. See #2136
        rowRequestHandler.cancel();

        enabled = !uidl.hasAttribute("disabled");

        if (BrowserInfo.get().isIE8() && !enabled) {
            /*
             * The disabled shim will not cover the table body if it is relative
             * in IE8. See #7324
             */
            scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.STATIC);
        } else if (BrowserInfo.get().isIE8()) {
            scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.RELATIVE);
        }

        this.client = client;
        paintableId = uidl.getStringAttribute("id");
        immediate = uidl.getBooleanAttribute("immediate");
        final int newTotalRows = uidl.getIntAttribute("totalrows");
        if (newTotalRows != totalRows) {
            if (scrollBody != null) {
                if (totalRows == 0) {
                    tHead.clear();
                    tFoot.clear();
                }
                initializedAndAttached = false;
                initialContentReceived = false;
                isNewBody = true;
            }
            totalRows = newTotalRows;
        }

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

        tabIndex = uidl.hasAttribute("tabindex") ? uidl
                .getIntAttribute("tabindex") : 0;

        if (!BrowserInfo.get().isTouchDevice()) {
            multiselectmode = uidl.hasAttribute("multiselectmode") ? uidl
                    .getIntAttribute("multiselectmode")
                    : MULTISELECT_MODE_DEFAULT;
        }

        if (uidl.hasAttribute("alb")) {
            bodyActionKeys = uidl.getStringArrayAttribute("alb");
        } else {
            // Need to clear the actions if the action handlers have been
            // removed
            bodyActionKeys = null;
        }

        setCacheRate(uidl.hasAttribute("cr") ? uidl.getDoubleAttribute("cr")
                : CACHE_RATE_DEFAULT);

        recalcWidths = uidl.hasAttribute("recalcWidths");
        if (recalcWidths) {
            tHead.clear();
            tFoot.clear();
        }

        int oldPageLength = pageLength;
        if (uidl.hasAttribute("pagelength")) {
            pageLength = uidl.getIntAttribute("pagelength");
        } else {
            // pagelenght is "0" meaning scrolling is turned off
            pageLength = totalRows;
        }

        if (oldPageLength != pageLength && initializedAndAttached) {
            // page length changed, need to update size
            sizeInit();
        }

        firstvisible = uidl.hasVariable("firstvisible") ? uidl
                .getIntVariable("firstvisible") : 0;
        if (firstvisible != lastRequestedFirstvisible && scrollBody != null) {
            // received 'surprising' firstvisible from server: scroll there
            firstRowInViewPort = firstvisible;
            scrollBodyPanel.setScrollPosition((int) (firstvisible * scrollBody
                    .getRowHeight()));
        }

        showRowHeaders = uidl.getBooleanAttribute("rowheaders");
        showColHeaders = uidl.getBooleanAttribute("colheaders");

        nullSelectionAllowed = uidl.hasAttribute("nsa") ? uidl
                .getBooleanAttribute("nsa") : true;

        String oldSortColumn = sortColumn;
        if (uidl.hasVariable("sortascending")) {
            sortAscending = uidl.getBooleanVariable("sortascending");
            sortColumn = uidl.getStringVariable("sortcolumn");
        }

        boolean keyboardSelectionOverRowFetchInProgress = false;

        if (uidl.hasVariable("selected")) {
            final Set<String> selectedKeys = uidl
                    .getStringArrayVariableAsSet("selected");
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
                    if (selected != row.isSelected()) {
                        row.toggleSelection();
                    }
                }
            }
        }
        unSyncedselectionsBeforeRowFetch = null;

        if (uidl.hasAttribute("selectmode")) {
            if (uidl.getBooleanAttribute("readonly")) {
                selectMode = Table.SELECT_MODE_NONE;
            } else if (uidl.getStringAttribute("selectmode").equals("multi")) {
                selectMode = Table.SELECT_MODE_MULTI;
            } else if (uidl.getStringAttribute("selectmode").equals("single")) {
                selectMode = Table.SELECT_MODE_SINGLE;
            } else {
                selectMode = Table.SELECT_MODE_NONE;
            }
        }

        if (uidl.hasVariable("columnorder")) {
            columnReordering = true;
            columnOrder = uidl.getStringArrayVariable("columnorder");
        } else {
            columnReordering = false;
            columnOrder = null;
        }

        if (uidl.hasVariable("collapsedcolumns")) {
            tHead.setColumnCollapsingAllowed(true);
            collapsedColumns = uidl
                    .getStringArrayVariableAsSet("collapsedcolumns");
        } else {
            tHead.setColumnCollapsingAllowed(false);
        }

        UIDL rowData = null;
        UIDL ac = null;
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL c = (UIDL) it.next();
            if (c.getTag().equals("rows")) {
                rowData = c;
            } else if (c.getTag().equals("actions")) {
                updateActionMap(c);
            } else if (c.getTag().equals("visiblecolumns")) {
                tHead.updateCellsFromUIDL(c);
                tFoot.updateCellsFromUIDL(c);
            } else if (c.getTag().equals("-ac")) {
                ac = c;
            }
        }
        if (ac == null) {
            if (dropHandler != null) {
                // remove dropHandler if not present anymore
                dropHandler = null;
            }
        } else {
            if (dropHandler == null) {
                dropHandler = new VScrollTableDropHandler();
            }
            dropHandler.updateAcceptRules(ac);
        }
        updateHeader(uidl.getStringArrayAttribute("vcolorder"));

        updateFooter(uidl.getStringArrayAttribute("vcolorder"));

        if (!recalcWidths && initializedAndAttached) {
            updateBody(rowData, uidl.getIntAttribute("firstrow"),
                    uidl.getIntAttribute("rows"));
            if (headerChangedDuringUpdate) {
                lazyAdjustColumnWidths.schedule(1);
            } else {
                // webkits may still bug with their disturbing scrollbar bug,
                // See #3457
                // run overflow fix for scrollable area
                Scheduler.get().scheduleDeferred(new Command() {
                    public void execute() {
                        Util.runWebkitOverflowAutoFix(scrollBodyPanel
                                .getElement());
                    }
                });
            }
        } else {
            if (scrollBody != null) {
                scrollBody.removeFromParent();
                lazyUnregistryBag.add(scrollBody);
            }
            scrollBody = createScrollBody();

            scrollBody.renderInitialRows(rowData,
                    uidl.getIntAttribute("firstrow"),
                    uidl.getIntAttribute("rows"));
            scrollBodyPanel.add(scrollBody);
            initialContentReceived = true;
            if (isAttached()) {
                sizeInit();
            }
            scrollBody.restoreRowVisibility();
        }

        if (selectMode == Table.SELECT_MODE_NONE) {
            scrollBody.addStyleName(CLASSNAME + "-body-noselection");
        } else {
            scrollBody.removeStyleName(CLASSNAME + "-body-noselection");
        }

        hideScrollPositionAnnotation();
        purgeUnregistryBag();

        // selection is no in sync with server, avoid excessive server visits by
        // clearing to flag used during the normal operation
        if (!keyboardSelectionOverRowFetchInProgress) {
            selectionChanged = false;
        }

        /*
         * This is called when the Home or page up button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (selectFirstItemInNextRender || focusFirstItemInNextRender) {
            selectFirstRenderedRowInViewPort(focusFirstItemInNextRender);
            selectFirstItemInNextRender = focusFirstItemInNextRender = false;
        }

        /*
         * This is called when the page down or end button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (selectLastItemInNextRender || focusLastItemInNextRender) {
            selectLastRenderedRowInViewPort(focusLastItemInNextRender);
            selectLastItemInNextRender = focusLastItemInNextRender = false;
        }
        multiselectPending = false;

        if (focusedRow != null) {
            if (!focusedRow.isAttached()) {
                // focused row has been orphaned, can't focus
                focusRowFromBody();
            }
        }

        setProperTabIndex();

        // Force recalculation of the captionContainer element inside the header
        // cell to accomodate for the size of the sort arrow.
        HeaderCell sortedHeader = tHead.getHeaderCell(sortColumn);
        if (sortedHeader != null) {
            tHead.resizeCaptionContainer(sortedHeader);
        }
        // Also recalculate the width of the captionContainer element in the
        // previously sorted header, since this now has more room.
        HeaderCell oldSortedHeader = tHead.getHeaderCell(oldSortColumn);
        if (oldSortedHeader != null) {
            tHead.resizeCaptionContainer(oldSortedHeader);
        }

        rendering = false;
        headerChangedDuringUpdate = false;

    }

    private void focusRowFromBody() {
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
     * 
     * @param focusOnly
     *            Should the focus only be moved to the last row
     */
    private void selectLastRenderedRowInViewPort(boolean focusOnly) {
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
     * 
     * @param focusOnly
     *            Should the focus only be moved to the first row
     */
    private void selectFirstRenderedRowInViewPort(boolean focusOnly) {
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

    private void setCacheRate(double d) {
        if (cache_rate != d) {
            cache_rate = d;
            cache_react_rate = 0.75 * d;
        }
    }

    /**
     * Unregisters Paintables in "trashed" HasWidgets (IScrollTableBodys or
     * IScrollTableRows). This is done lazily as Table must survive from
     * "subtreecaching" logic.
     */
    private void purgeUnregistryBag() {
        for (Iterator<Panel> iterator = lazyUnregistryBag.iterator(); iterator
                .hasNext();) {
            client.unregisterChildPaintables(iterator.next());
        }
        lazyUnregistryBag.clear();
    }

    private void updateActionMap(UIDL c) {
        final Iterator<?> it = c.getChildIterator();
        while (it.hasNext()) {
            final UIDL action = (UIDL) it.next();
            final String key = action.getStringAttribute("key");
            final String caption = action.getStringAttribute("caption");
            actionMap.put(key + "_c", caption);
            if (action.hasAttribute("icon")) {
                // TODO need some uri handling ??
                actionMap.put(key + "_i", client.translateVaadinUri(action
                        .getStringAttribute("icon")));
            } else {
                actionMap.remove(key + "_i");
            }
        }

    }

    public String getActionCaption(String actionKey) {
        return actionMap.get(actionKey + "_c");
    }

    public String getActionIcon(String actionKey) {
        return actionMap.get(actionKey + "_i");
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
     * @param uidl
     *            which contains row data
     * @param firstRow
     *            first row in data set
     * @param reqRows
     *            amount of rows in data set
     */
    private void updateBody(UIDL uidl, int firstRow, int reqRows) {
        if (uidl == null || reqRows < 1) {
            // container is empty, remove possibly existing rows
            if (firstRow < 0) {
                while (scrollBody.getLastRendered() > scrollBody.firstRendered) {
                    scrollBody.unlinkRow(false);
                }
                scrollBody.unlinkRow(false);
            }
            return;
        }

        scrollBody.renderRows(uidl, firstRow, reqRows);

        final int optimalFirstRow = (int) (firstRowInViewPort - pageLength
                * cache_rate);
        boolean cont = true;
        while (cont && scrollBody.getLastRendered() > optimalFirstRow
                && scrollBody.getFirstRendered() < optimalFirstRow) {
            // removing row from start
            cont = scrollBody.unlinkRow(true);
        }
        final int optimalLastRow = (int) (firstRowInViewPort + pageLength + pageLength
                * cache_rate);
        cont = true;
        while (cont && scrollBody.getLastRendered() > optimalLastRow) {
            // removing row from the end
            cont = scrollBody.unlinkRow(false);
        }
        scrollBody.fixSpacers();

        scrollBody.restoreRowVisibility();
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

    protected boolean isSelectable() {
        return selectMode > Table.SELECT_MODE_NONE;
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

    private void setColWidth(int colIndex, int w, boolean isDefinedWidth) {
        final HeaderCell hcell = tHead.getHeaderCell(colIndex);

        // Make sure that the column grows to accommodate the sort indicator if
        // necessary.
        if (w < hcell.getMinWidth()) {
            w = hcell.getMinWidth();
        }

        // Set header column width
        hcell.setWidth(w, isDefinedWidth);

        // Ensure indicators have been taken into account
        tHead.resizeCaptionContainer(hcell);

        // Set body column width
        scrollBody.setColWidth(colIndex, w);

        // Set footer column width
        FooterCell fcell = tFoot.getFooterCell(colIndex);
        fcell.setWidth(w, isDefinedWidth);
    }

    private int getColWidth(String colKey) {
        return tHead.getHeaderCell(colKey).getWidth();
    }

    /**
     * Get a rendered row by its key
     * 
     * @param key
     *            The key to search with
     * @return
     */
    private VScrollTableRow getRenderedRowByKey(String key) {
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
        if (client.hasEventListeners(this, COLUMN_REORDER_EVENT_ID)) {
            client.sendPendingVariableChanges();
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (initialContentReceived) {
            sizeInit();
        }
    }

    @Override
    protected void onDetach() {
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

    /**
     * Run only once when component is attached and received its initial
     * content. This function : * Syncs headers and bodys "natural widths and
     * saves the values. * Sets proper width and height * Makes deferred request
     * to get some cache rows
     */
    private void sizeInit() {
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

        // first loop: collect natural widths
        while (headCells.hasNext()) {
            final HeaderCell hCell = (HeaderCell) headCells.next();
            final FooterCell fCell = (FooterCell) footCells.next();
            int w = hCell.getWidth();
            if (hCell.isDefinedWidth()) {
                // server has defined column width explicitly
                totalExplicitColumnsWidths += w;
            } else {
                if (hCell.getExpandRatio() > 0) {
                    expandRatioDivider += hCell.getExpandRatio();
                    w = 0;
                } else {
                    // get and store greater of header width and column width,
                    // and
                    // store it as a minimumn natural col width
                    int headerWidth = hCell.getNaturalColumnWidth(i);
                    int footerWidth = fCell.getNaturalColumnWidth(i);
                    w = headerWidth > footerWidth ? headerWidth : footerWidth;
                }
                hCell.setNaturalMinimumColumnWidth(w);
                fCell.setNaturalMinimumColumnWidth(w);
            }
            widths[i] = w;
            total += w;
            i++;
        }

        tHead.disableBrowserIntelligence();
        tFoot.disableBrowserIntelligence();

        boolean willHaveScrollbarz = willHaveScrollbars();

        // fix "natural" width if width not set
        if (width == null || "".equals(width)) {
            int w = total;
            w += scrollBody.getCellExtraWidth() * visibleColOrder.length;
            if (willHaveScrollbarz) {
                w += Util.getNativeScrollbarSize();
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
            availW -= Util.getNativeScrollbarSize();
        }

        // TODO refactor this code to be the same as in resize timer
        boolean needsReLayout = false;

        if (availW > total) {
            // natural size is smaller than available space
            final int extraSpace = availW - total;
            final int totalWidthR = total - totalExplicitColumnsWidths;
            int checksum = 0;
            needsReLayout = true;

            if (extraSpace == 1) {
                // We cannot divide one single pixel so we give it the first
                // undefined column
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
                // visible columns have some active expand ratios, excess
                // space is divided according to them
                headCells = tHead.iterator();
                i = 0;
                while (headCells.hasNext()) {
                    HeaderCell hCell = (HeaderCell) headCells.next();
                    if (hCell.getExpandRatio() > 0) {
                        int w = widths[i];
                        final int newSpace = Math.round((extraSpace * (hCell
                                .getExpandRatio() / expandRatioDivider)));
                        w += newSpace;
                        widths[i] = w;
                    }
                    checksum += widths[i];
                    i++;
                }
            } else if (totalWidthR > 0) {
                // no expand ratios defined, we will share extra space
                // relatively to "natural widths" among those without
                // explicit width
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
            // bodys size will be more than available and scrollbar will appear
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

        if (needsReLayout) {
            scrollBody.reLayoutComponents();
        }

        updatePageLength();

        /*
         * Fix "natural" height if height is not set. This must be after width
         * fixing so the components' widths have been adjusted.
         */
        if (height == null || "".equals(height)) {
            /*
             * We must force an update of the row height as this point as it
             * might have been (incorrectly) calculated earlier
             */

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
                bodyHeight += Util.getNativeScrollbarSize();
            }
            scrollBodyPanel.setHeight(bodyHeight + "px");
            Util.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
        }

        isNewBody = false;

        if (firstvisible > 0) {
            // Deferred due some Firefox oddities. IE & Safari could survive
            // without
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    scrollBodyPanel
                            .setScrollPosition((int) (firstvisible * scrollBody
                                    .getRowHeight()));
                    firstRowInViewPort = firstvisible;
                }
            });
        }

        if (enabled) {
            // Do we need cache rows
            if (scrollBody.getLastRendered() + 1 < firstRowInViewPort
                    + pageLength + (int) cache_react_rate * pageLength) {
                if (totalRows - 1 > scrollBody.getLastRendered()) {
                    // fetch cache rows
                    int firstInNewSet = scrollBody.getLastRendered() + 1;
                    rowRequestHandler.setReqFirstRow(firstInNewSet);
                    int lastInNewSet = (int) (firstRowInViewPort + pageLength + cache_rate
                            * pageLength);
                    if (lastInNewSet > totalRows - 1) {
                        lastInNewSet = totalRows - 1;
                    }
                    rowRequestHandler.setReqRows(lastInNewSet - firstInNewSet
                            + 1);
                    rowRequestHandler.deferRowFetch(1);
                }
            }
        }

        /*
         * Ensures the column alignments are correct at initial loading. <br/>
         * (child components widths are correct)
         */
        scrollBody.reLayoutComponents();
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                Util.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
            }
        });
    }

    /**
     * Note, this method is not official api although declared as protected.
     * Extend at you own risk.
     * 
     * @return true if content area will have scrollbars visible.
     */
    protected boolean willHaveScrollbars() {
        if (!(height != null && !height.equals(""))) {
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
            scrollPositionElement.setClassName(CLASSNAME + "-scrollposition");
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

    private void hideScrollPositionAnnotation() {
        if (scrollPositionElement != null) {
            DOM.setStyleAttribute(scrollPositionElement, "display", "none");
        }
    }

    private class RowRequestHandler extends Timer {

        private int reqFirstRow = 0;
        private int reqRows = 0;

        public void deferRowFetch() {
            deferRowFetch(250);
        }

        public void deferRowFetch(int msec) {
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

        public void setReqFirstRow(int reqFirstRow) {
            if (reqFirstRow < 0) {
                reqFirstRow = 0;
            } else if (reqFirstRow >= totalRows) {
                reqFirstRow = totalRows - 1;
            }
            this.reqFirstRow = reqFirstRow;
        }

        public void setReqRows(int reqRows) {
            this.reqRows = reqRows;
        }

        @Override
        public void run() {
            if (client.hasActiveRequest() || navKeyDown) {
                // if client connection is busy, don't bother loading it more
                VConsole.log("Postponed rowfetch");
                schedule(250);
            } else {

                int firstToBeRendered = scrollBody.firstRendered;
                if (reqFirstRow < firstToBeRendered) {
                    firstToBeRendered = reqFirstRow;
                } else if (firstRowInViewPort - (int) (cache_rate * pageLength) > firstToBeRendered) {
                    firstToBeRendered = firstRowInViewPort
                            - (int) (cache_rate * pageLength);
                    if (firstToBeRendered < 0) {
                        firstToBeRendered = 0;
                    }
                }

                int lastToBeRendered = scrollBody.lastRendered;

                if (reqFirstRow + reqRows - 1 > lastToBeRendered) {
                    lastToBeRendered = reqFirstRow + reqRows - 1;
                } else if (firstRowInViewPort + pageLength + pageLength
                        * cache_rate < lastToBeRendered) {
                    lastToBeRendered = (firstRowInViewPort + pageLength + (int) (pageLength * cache_rate));
                    if (lastToBeRendered >= totalRows) {
                        lastToBeRendered = totalRows - 1;
                    }
                    // due Safari 3.1 bug (see #2607), verify reqrows, original
                    // problem unknown, but this should catch the issue
                    if (reqFirstRow + reqRows - 1 > lastToBeRendered) {
                        reqRows = lastToBeRendered - reqFirstRow;
                    }
                }

                client.updateVariable(paintableId, "firstToBeRendered",
                        firstToBeRendered, false);

                client.updateVariable(paintableId, "lastToBeRendered",
                        lastToBeRendered, false);
                // remember which firstvisible we requested, in case the server
                // has
                // a differing opinion
                lastRequestedFirstvisible = firstRowInViewPort;
                client.updateVariable(paintableId, "firstvisible",
                        firstRowInViewPort, false);
                client.updateVariable(paintableId, "reqfirstrow", reqFirstRow,
                        false);
                client.updateVariable(paintableId, "reqrows", reqRows, true);

                if (selectionChanged) {
                    unSyncedselectionsBeforeRowFetch = new HashSet<Object>(
                            selectedRowKeys);
                }
            }
        }

        public int getReqFirstRow() {
            return reqFirstRow;
        }

        /**
         * Sends request to refresh content at this position.
         */
        public void refreshContent() {
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
            if (BrowserInfo.get().isIE6() || td.getClassName().contains("-asc")
                    || td.getClassName().contains("-desc")) {
                /*
                 * Room for the sort indicator is made by subtracting the styled
                 * margin and width of the resizer from the width of the caption
                 * container.
                 */
                int captionContainerWidth = width
                        - sortIndicator.getOffsetWidth()
                        - colResizeWidget.getOffsetWidth() - rightSpacing;
                captionContainer.getStyle().setPropertyPx("width",
                        captionContainerWidth);
            } else {
                /*
                 * Set the caption container element as wide as possible when
                 * the sorting indicator is not visible.
                 */
                captionContainer.getStyle().setPropertyPx("width",
                        width - rightSpacing);
            }

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

            DOM.setElementProperty(colResizeWidget, "className", CLASSNAME
                    + "-resizer");

            setText(headerText);

            DOM.appendChild(td, colResizeWidget);

            DOM.setElementProperty(sortIndicator, "className", CLASSNAME
                    + "-sort-indicator");
            DOM.appendChild(td, sortIndicator);

            DOM.setElementProperty(captionContainer, "className", CLASSNAME
                    + "-caption-container");

            // ensure no clipping initially (problem on column additions)
            DOM.setStyleAttribute(captionContainer, "overflow", "visible");

            DOM.appendChild(td, captionContainer);

            DOM.sinkEvents(td, Event.MOUSEEVENTS | Event.TOUCHEVENTS);

            setElement(td);

            setAlign(ALIGN_LEFT);
        }

        public void disableAutoWidthCalculation() {
            definedWidth = true;
            expandRatio = 0;
        }

        public void setWidth(int w, boolean ensureDefinedWidth) {
            if (ensureDefinedWidth) {
                definedWidth = true;
                // on column resize expand ratio becomes zero
                expandRatio = 0;
            }
            if (width == -1) {
                // go to default mode, clip content if necessary
                DOM.setStyleAttribute(captionContainer, "overflow", "");
            }
            width = w;
            if (w == -1) {
                DOM.setStyleAttribute(captionContainer, "width", "");
                setWidth("");
            } else {
                tHead.resizeCaptionContainer(this);

                /*
                 * if we already have tBody, set the header width properly, if
                 * not defer it. IE will fail with complex float in table header
                 * unless TD width is not explicitly set.
                 */
                if (scrollBody != null) {
                    int tdWidth = width + scrollBody.getCellExtraWidth();
                    setWidth(tdWidth + "px");
                } else {
                    Scheduler.get().scheduleDeferred(new Command() {
                        public void execute() {
                            int tdWidth = width
                                    + scrollBody.getCellExtraWidth();
                            setWidth(tdWidth + "px");
                        }
                    });
                }
            }
        }

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
            return definedWidth;
        }

        public int getWidth() {
            return width;
        }

        public void setText(String headerText) {
            DOM.setInnerHTML(captionContainer, headerText);
        }

        public String getColKey() {
            return cid;
        }

        private void setSorted(boolean sorted) {
            this.sorted = sorted;
            if (sorted) {
                if (sortAscending) {
                    this.setStyleName(CLASSNAME + "-header-cell-asc");
                } else {
                    this.setStyleName(CLASSNAME + "-header-cell-desc");
                }
            } else {
                this.setStyleName(CLASSNAME + "-header-cell");
            }
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
                    event.stopPropagation();
                    event.preventDefault();
                }
            }
        }

        private void createFloatingCopy() {
            floatingCopyOfHeaderCell = DOM.createDiv();
            DOM.setInnerHTML(floatingCopyOfHeaderCell, DOM.getInnerHTML(td));
            floatingCopyOfHeaderCell = DOM
                    .getChild(floatingCopyOfHeaderCell, 1);
            DOM.setElementProperty(floatingCopyOfHeaderCell, "className",
                    CLASSNAME + "-header-drag");
            updateFloatingCopysPosition(DOM.getAbsoluteLeft(td),
                    DOM.getAbsoluteTop(td));
            DOM.appendChild(RootPanel.get().getElement(),
                    floatingCopyOfHeaderCell);
        }

        private void updateFloatingCopysPosition(int x, int y) {
            x -= DOM.getElementPropertyInt(floatingCopyOfHeaderCell,
                    "offsetWidth") / 2;
            DOM.setStyleAttribute(floatingCopyOfHeaderCell, "left", x + "px");
            if (y > 0) {
                DOM.setStyleAttribute(floatingCopyOfHeaderCell, "top", (y + 7)
                        + "px");
            }
        }

        private void hideFloatingCopy() {
            DOM.removeChild(RootPanel.get().getElement(),
                    floatingCopyOfHeaderCell);
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
                    HEADER_CLICK_EVENT_ID)) {
                MouseEventDetails details = new MouseEventDetails(event);
                client.updateVariable(paintableId, "headerClickEvent",
                        details.toString(), false);
                client.updateVariable(paintableId, "headerClickCID", cid, true);
            }
        }

        protected void handleCaptionEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONTOUCHSTART:
            case Event.ONMOUSEDOWN:
                if (columnReordering) {
                    if (event.getTypeInt() == Event.ONTOUCHSTART) {
                        /*
                         * prevent using this event in e.g. scrolling
                         */
                        event.stopPropagation();
                    }
                    dragging = true;
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
                if (columnReordering) {
                    dragging = false;
                    DOM.releaseCapture(getElement());
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
                    }
                    if (Util.isTouchEvent(event)) {
                        /*
                         * Prevent using in e.g. scrolling and prevent generated
                         * events.
                         */
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }

                if (!moved) {
                    // mouse event was a click to header -> sort column
                    if (sortable) {
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
                    if (Util.isTouchEvent(event)) {
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
            case Event.ONTOUCHMOVE:
            case Event.ONMOUSEMOVE:
                if (dragging) {
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

                    final int clientX = Util.getTouchOrMouseClientX(event);
                    final int x = clientX + tHead.hTableWrapper.getScrollLeft();
                    int slotX = headerX;
                    closestSlot = colIndex;
                    int closestDistance = -1;
                    int start = 0;
                    if (showRowHeaders) {
                        start++;
                    }
                    final int visibleCellCount = tHead.getVisibleCellCount();
                    for (int i = start; i <= visibleCellCount; i++) {
                        if (i > 0) {
                            final String colKey = getColKeyByIndex(i - 1);
                            slotX += getColWidth(colKey);
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
                break;
            default:
                break;
            }
        }

        private void onResizeEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                isResizing = true;
                DOM.setCapture(getElement());
                dragStartX = DOM.eventGetClientX(event);
                colIndex = getColIndexByKey(cid);
                originalWidth = getWidth();
                DOM.eventPreventDefault(event);
                break;
            case Event.ONMOUSEUP:
                isResizing = false;
                DOM.releaseCapture(getElement());
                tHead.disableAutoColumnWidthCalculation(this);

                // Ensure last header cell is taking into account possible
                // column selector
                HeaderCell lastCell = tHead.getHeaderCell(tHead
                        .getVisibleCellCount() - 1);
                tHead.resizeCaptionContainer(lastCell);

                fireColumnResizeEvent(cid, originalWidth, getColWidth(cid));
                break;
            case Event.ONMOUSEMOVE:
                if (isResizing) {
                    final int deltaX = DOM.eventGetClientX(event) - dragStartX;
                    if (deltaX == 0) {
                        return;
                    }

                    int newWidth = originalWidth + deltaX;
                    if (newWidth < getMinWidth()) {
                        newWidth = getMinWidth();
                    }
                    setColWidth(colIndex, newWidth, true);
                    forceRealignColumnHeaders();
                }
                break;
            default:
                break;
            }
        }

        public int getMinWidth() {
            int cellExtraWidth = 0;
            if (scrollBody != null) {
                cellExtraWidth += scrollBody.getCellExtraWidth();
            }
            return cellExtraWidth + sortIndicator.getOffsetWidth();
        }

        public String getCaption() {
            return DOM.getInnerText(captionContainer);
        }

        public boolean isEnabled() {
            return getParent() != null;
        }

        public void setAlign(char c) {
            final String ALIGN_PREFIX = CLASSNAME + "-caption-container-align-";
            if (align != c) {
                captionContainer.removeClassName(ALIGN_PREFIX + "center");
                captionContainer.removeClassName(ALIGN_PREFIX + "right");
                captionContainer.removeClassName(ALIGN_PREFIX + "left");
                switch (c) {
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
            align = c;
        }

        public char getAlign() {
            return align;
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
            if (isDefinedWidth()) {
                return width;
            } else {
                if (naturalWidth < 0) {
                    // This is recently revealed column. Try to detect a proper
                    // value (greater of header and data
                    // cols)

                    int hw = captionContainer.getOffsetWidth()
                            + scrollBody.getCellExtraWidth();
                    if (BrowserInfo.get().isGecko()
                            || BrowserInfo.get().isIE7()) {
                        hw += sortIndicator.getOffsetWidth();
                    }
                    if (columnIndex < 0) {
                        columnIndex = 0;
                        for (Iterator<Widget> it = tHead.iterator(); it
                                .hasNext(); columnIndex++) {
                            if (it.next() == this) {
                                break;
                            }
                        }
                    }
                    final int cw = scrollBody.getColWidth(columnIndex);
                    naturalWidth = (hw > cw ? hw : cw);
                }
                return naturalWidth;
            }
        }

        public void setExpandRatio(float floatAttribute) {
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
            this.setStyleName(CLASSNAME + "-header-cell-rowheader");
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

            DOM.setStyleAttribute(hTableWrapper, "overflow", "hidden");
            DOM.setElementProperty(hTableWrapper, "className", CLASSNAME
                    + "-header");

            // TODO move styles to CSS
            DOM.setElementProperty(columnSelector, "className", CLASSNAME
                    + "-column-selector");
            DOM.setStyleAttribute(columnSelector, "display", "none");

            DOM.appendChild(table, headerTableBody);
            DOM.appendChild(headerTableBody, tr);
            DOM.appendChild(hTableContainer, table);
            DOM.appendChild(hTableWrapper, hTableContainer);
            DOM.appendChild(div, hTableWrapper);
            DOM.appendChild(div, columnSelector);
            setElement(div);

            setStyleName(CLASSNAME + "-header-wrap");

            DOM.sinkEvents(columnSelector, Event.ONCLICK);

            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersHeaderCell());
        }

        public void resizeCaptionContainer(HeaderCell cell) {
            HeaderCell lastcell = getHeaderCell(visibleCells.size() - 1);

            // Measure column widths
            int columnTotalWidth = 0;
            for (Widget w : visibleCells) {
                columnTotalWidth += w.getOffsetWidth();
            }

            if (cell == lastcell
                    && columnSelector.getOffsetWidth() > 0
                    && columnTotalWidth >= div.getOffsetWidth()
                            - columnSelector.getOffsetWidth()
                    && !hasVerticalScrollbar()) {
                // Ensure column caption is visible when placed under the column
                // selector widget by shifting and resizing the caption.
                int offset = 0;
                int diff = div.getOffsetWidth() - columnTotalWidth;
                if (diff < columnSelector.getOffsetWidth() && diff > 0) {
                    // If the difference is less than the column selectors width
                    // then just offset by the
                    // difference
                    offset = columnSelector.getOffsetWidth() - diff;
                } else {
                    // Else offset by the whole column selector
                    offset = columnSelector.getOffsetWidth();
                }
                lastcell.resizeCaptionContainer(offset);
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
                }

                if (col.hasAttribute("sortable")) {
                    c.setSortable(true);
                    if (cid.equals(sortColumn)) {
                        c.setSorted(true);
                    } else {
                        c.setSorted(false);
                    }
                } else {
                    c.setSortable(false);
                }

                if (col.hasAttribute("align")) {
                    c.setAlign(col.getStringAttribute("align").charAt(0));
                }
                if (col.hasAttribute("width")) {
                    final String widthStr = col.getStringAttribute("width");
                    // Make sure to accomodate for the sort indicator if
                    // necessary.
                    int width = Integer.parseInt(widthStr);
                    if (width < c.getMinWidth()) {
                        width = c.getMinWidth();
                    }
                    c.setWidth(width, true);
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
            if (BrowserInfo.get().isIE6()) {
                hTableWrapper.getStyle().setPosition(Position.RELATIVE);
                hTableWrapper.getStyle().setLeft(-scrollLeft, Unit.PX);
            } else {
                hTableWrapper.setScrollLeft(scrollLeft);
            }
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
                DOM.setElementProperty(
                        DOM.getFirstChild(DOM.getChild(tr, index - 1)),
                        "className", CLASSNAME + "-resizer " + CLASSNAME
                                + "-focus-slot-right");
            } else {
                DOM.setElementProperty(
                        DOM.getFirstChild(DOM.getChild(tr, index)),
                        "className", CLASSNAME + "-resizer " + CLASSNAME
                                + "-focus-slot-left");
            }
            focusedSlot = index;
        }

        private void removeSlotFocus() {
            if (focusedSlot < 0) {
                return;
            }
            if (focusedSlot == 0) {
                DOM.setElementProperty(
                        DOM.getFirstChild(DOM.getChild(tr, focusedSlot)),
                        "className", CLASSNAME + "-resizer");
            } else if (focusedSlot > 0) {
                DOM.setElementProperty(
                        DOM.getFirstChild(DOM.getChild(tr, focusedSlot - 1)),
                        "className", CLASSNAME + "-resizer");
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
            private VScrollTableRow currentlyFocusedRow;

            public VisibleColumnAction(String colKey) {
                super(VScrollTable.TableHead.this);
                this.colKey = colKey;
                caption = tHead.getHeaderCell(colKey).getCaption();
                currentlyFocusedRow = focusedRow;
            }

            @Override
            public void execute() {
                client.getContextMenu().hide();
                // toggle selected column
                if (collapsedColumns.contains(colKey)) {
                    collapsedColumns.remove(colKey);
                } else {
                    tHead.removeCell(colKey);
                    collapsedColumns.add(colKey);
                    lazyAdjustColumnWidths.schedule(1);
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

            /**
             * Override default method to distinguish on/off columns
             */
            @Override
            public String getHTML() {
                final StringBuffer buf = new StringBuffer();
                if (collapsed) {
                    buf.append("<span class=\"v-off\">");
                } else {
                    buf.append("<span class=\"v-on\">");
                }
                buf.append(super.getHTML());
                buf.append("</span>");

                return buf.toString();
            }

        }

        /*
         * Returns columns as Action array for column select popup
         */
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
                actions[i] = a;
            }
            return actions;
        }

        public ApplicationConnection getClient() {
            return client;
        }

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

            DOM.setElementProperty(captionContainer, "className", CLASSNAME
                    + "-footer-container");

            // ensure no clipping initially (problem on column additions)
            DOM.setStyleAttribute(captionContainer, "overflow", "visible");

            DOM.sinkEvents(captionContainer, Event.MOUSEEVENTS);

            DOM.appendChild(td, captionContainer);

            DOM.sinkEvents(td, Event.MOUSEEVENTS);

            setElement(td);
        }

        /**
         * Sets the text of the footer
         * 
         * @param footerText
         *            The text in the footer
         */
        public void setText(String footerText) {
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
                    DOM.setStyleAttribute(captionContainer, "textAlign",
                            "center");
                    break;
                case ALIGN_RIGHT:
                    DOM.setStyleAttribute(captionContainer, "textAlign",
                            "right");
                    break;
                default:
                    DOM.setStyleAttribute(captionContainer, "textAlign", "");
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
         * Sets the width of the cell
         * 
         * @param w
         *            The width of the cell
         * @param ensureDefinedWidth
         *            Ensures the the given width is not recalculated
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
                DOM.setStyleAttribute(captionContainer, "overflow", "");
            }
            width = w;
            if (w == -1) {
                DOM.setStyleAttribute(captionContainer, "width", "");
                setWidth("");
            } else {

                /*
                 * Reduce width with one pixel for the right border since the
                 * footers does not have any spacers between them.
                 */
                int borderWidths = 1;

                // Set the container width (check for negative value)
                if (w - borderWidths >= 0) {
                    captionContainer.getStyle().setPropertyPx("width",
                            w - borderWidths);
                } else {
                    captionContainer.getStyle().setPropertyPx("width", 0);
                }

                /*
                 * if we already have tBody, set the header width properly, if
                 * not defer it. IE will fail with complex float in table header
                 * unless TD width is not explicitly set.
                 */
                if (scrollBody != null) {
                    /*
                     * Reduce with one since footer does not have any spacers,
                     * instead a 1 pixel border.
                     */
                    int tdWidth = width + scrollBody.getCellExtraWidth()
                            - borderWidths;
                    setWidth(tdWidth + "px");
                } else {
                    Scheduler.get().scheduleDeferred(new Command() {
                        public void execute() {
                            int borderWidths = 1;
                            int tdWidth = width
                                    + scrollBody.getCellExtraWidth()
                                    - borderWidths;
                            setWidth(tdWidth + "px");
                        }
                    });
                }
            }
        }

        /**
         * Sets the width to undefined
         */
        public void setUndefinedWidth() {
            setWidth(-1, false);
        }

        /**
         * Detects if width is fixed by developer on server side or resized to
         * current width by user.
         * 
         * @return true if defined, false if "natural" width
         */
        public boolean isDefinedWidth() {
            return definedWidth;
        }

        /**
         * Returns the pixels width of the footer cell
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
         * Returns the expand ration of the cell
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
                event.stopPropagation();
                event.preventDefault();
            }
        }

        /**
         * Handles a event on the captions
         * 
         * @param event
         *            The event to handle
         */
        protected void handleCaptionEvent(Event event) {
            if (DOM.eventGetType(event) == Event.ONMOUSEUP) {
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
                    FOOTER_CLICK_EVENT_ID)) {
                MouseEventDetails details = new MouseEventDetails(event);
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
            if (isDefinedWidth()) {
                return width;
            } else {
                if (naturalWidth < 0) {
                    // This is recently revealed column. Try to detect a proper
                    // value (greater of header and data
                    // cols)

                    final int hw = ((Element) getElement().getLastChild())
                            .getOffsetWidth() + scrollBody.getCellExtraWidth();
                    if (columnIndex < 0) {
                        columnIndex = 0;
                        for (Iterator<Widget> it = tHead.iterator(); it
                                .hasNext(); columnIndex++) {
                            if (it.next() == this) {
                                break;
                            }
                        }
                    }
                    final int cw = scrollBody.getColWidth(columnIndex);
                    naturalWidth = (hw > cw ? hw : cw);
                }
                return naturalWidth;
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

            DOM.setStyleAttribute(hTableWrapper, "overflow", "hidden");
            DOM.setElementProperty(hTableWrapper, "className", CLASSNAME
                    + "-footer");

            DOM.appendChild(table, headerTableBody);
            DOM.appendChild(headerTableBody, tr);
            DOM.appendChild(hTableContainer, table);
            DOM.appendChild(hTableWrapper, hTableContainer);
            DOM.appendChild(div, hTableWrapper);
            setElement(div);

            setStyleName(CLASSNAME + "-footer-wrap");

            availableCells.put(ROW_HEADER_COLUMN_KEY,
                    new RowHeadersFooterCell());
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
                }
                if (col.hasAttribute("width")) {
                    final String width = col.getStringAttribute("width");
                    c.setWidth(Integer.parseInt(width), true);
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
            DOM.setStyleAttribute(hTableContainer, "width", WRAPPER_WIDTH
                    + "px");
        }

        /**
         * Enable browser measurement of the table width
         */
        public void enableBrowserIntelligence() {
            DOM.setStyleAttribute(hTableContainer, "width", "");
        }

        /**
         * Set the horizontal position in the cell in the footer. This is done
         * when a horizontal scrollbar is present.
         * 
         * @param scrollLeft
         *            The value of the leftScroll
         */
        public void setHorizontalScrollPosition(int scrollLeft) {
            if (BrowserInfo.get().isIE6()) {
                hTableWrapper.getStyle().setProperty("position", "relative");
                hTableWrapper.getStyle().setPropertyPx("left", -scrollLeft);
            } else {
                hTableWrapper.setScrollLeft(scrollLeft);
            }
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

        private final List<Widget> renderedRows = new ArrayList<Widget>();

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
                    + Util.getRequiredHeight(table);
        }

        private void constructDOM() {
            DOM.setElementProperty(table, "className", CLASSNAME + "-table");
            if (BrowserInfo.get().isIE()) {
                table.setPropertyInt("cellSpacing", 0);
            }
            DOM.setElementProperty(preSpacer, "className", CLASSNAME
                    + "-row-spacer");
            DOM.setElementProperty(postSpacer, "className", CLASSNAME
                    + "-row-spacer");

            table.appendChild(tBodyElement);
            DOM.appendChild(container, preSpacer);
            DOM.appendChild(container, table);
            DOM.appendChild(container, postSpacer);

        }

        public int getAvailableWidth() {
            int availW = scrollBodyPanel.getOffsetWidth() - getBorderWidth();
            return availW;
        }

        public void renderInitialRows(UIDL rowData, int firstIndex, int rows) {
            firstRendered = firstIndex;
            lastRendered = firstIndex + rows - 1;
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
                    lastRendered++;
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
                while (lastRendered + 1 > firstRendered) {
                    unlinkRow(false);
                }
                final VScrollTableRow row = prepareRow((UIDL) it.next());
                firstRendered = firstIndex;
                lastRendered = firstIndex - 1;
                addRow(row);
                lastRendered++;
                setContainerHeight();
                fixSpacers();
                while (it.hasNext()) {
                    addRow(prepareRow((UIDL) it.next()));
                    lastRendered++;
                }
                fixSpacers();
            }
            // this may be a new set of rows due content change,
            // ensure we have proper cache rows
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
            if (lastRendered < reactLastRow) {
                // get some cache rows below visible area
                rowRequestHandler.setReqFirstRow(lastRendered + 1);
                rowRequestHandler.setReqRows(reactLastRow - lastRendered);
                rowRequestHandler.deferRowFetch(1);
            } else if (scrollBody.getFirstRendered() > reactFirstRow) {
                /*
                 * Branch for fetching cache above visible area.
                 * 
                 * If cache needed for both before and after visible area, this
                 * will be rendered after-cache is reveived and rendered. So in
                 * some rare situations table may take two cache visits to
                 * server.
                 */
                rowRequestHandler.setReqFirstRow(reactFirstRow);
                rowRequestHandler.setReqRows(firstRendered - reactFirstRow);
                rowRequestHandler.deferRowFetch(1);
            }
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
            final int cells = DOM.getChildCount(row.getElement());
            for (int i = 0; i < cells; i++) {
                final Element cell = DOM.getChild(row.getElement(), i);
                int w = VScrollTable.this.getColWidth(getColKeyByIndex(i));
                if (w < 0) {
                    w = 0;
                }
                cell.getFirstChildElement().getStyle()
                        .setPropertyPx("width", w);
                cell.getStyle().setPropertyPx("width", w);
            }
            return row;
        }

        protected VScrollTableRow createRow(UIDL uidl, char[] aligns2) {
            return new VScrollTableRow(uidl, aligns);
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
            adopt(row);
            renderedRows.add(row);
        }

        public Iterator<Widget> iterator() {
            return renderedRows.iterator();
        }

        /**
         * @return false if couldn't remove row
         */
        public boolean unlinkRow(boolean fromBeginning) {
            if (lastRendered - firstRendered < 0) {
                return false;
            }
            int index;
            if (fromBeginning) {
                index = 0;
                firstRendered++;
            } else {
                index = renderedRows.size() - 1;
                lastRendered--;
            }
            if (index >= 0) {
                final VScrollTableRow toBeRemoved = (VScrollTableRow) renderedRows
                        .get(index);
                lazyUnregistryBag.add(toBeRemoved);
                tBodyElement.removeChild(toBeRemoved.getElement());
                orphan(toBeRemoved);
                renderedRows.remove(index);
                fixSpacers();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean remove(Widget w) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onAttach() {
            super.onAttach();
            setContainerHeight();
        }

        /**
         * Fix container blocks height according to totalRows to avoid
         * "bouncing" when scrolling
         */
        private void setContainerHeight() {
            fixSpacers();
            DOM.setStyleAttribute(container, "height", totalRows
                    * getRowHeight() + "px");
        }

        private void fixSpacers() {
            int prepx = (int) Math.round(getRowHeight() * firstRendered);
            if (prepx < 0) {
                prepx = 0;
            }
            preSpacer.getStyle().setPropertyPx("height", prepx);
            int postpx = (int) (getRowHeight() * (totalRows - 1 - lastRendered));
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
                    if (isAttached()) {
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
                NodeList<TableRowElement> rows = tBodyElement.getRows();
                if (rows.getLength() == 0) {
                    // no rows yet rendered
                    return 0;
                } else {
                    com.google.gwt.dom.client.Element wrapperdiv = rows
                            .getItem(0).getCells().getItem(columnIndex)
                            .getFirstChildElement();
                    return wrapperdiv.getOffsetWidth();
                }
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
            NodeList<TableRowElement> rows2 = tBodyElement.getRows();
            final int rows = rows2.getLength();
            for (int i = 0; i < rows; i++) {
                TableRowElement row = rows2.getItem(i);
                TableCellElement cell = row.getCells().getItem(colIndex);
                cell.getFirstChildElement().getStyle()
                        .setPropertyPx("width", w);
                cell.getStyle().setPropertyPx("width", w);
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

        private void detectExtrawidth() {
            NodeList<TableRowElement> rows = tBodyElement.getRows();
            if (rows.getLength() == 0) {
                /* need to temporary add empty row and detect */
                VScrollTableRow scrollTableRow = new VScrollTableRow();
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
                    next.addCell(null, "", ALIGN_LEFT, "", true, tHead
                            .getHeaderCell(0).isSorted());
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

        private void reLayoutComponents() {
            for (Widget w : this) {
                VScrollTableRow r = (VScrollTableRow) w;
                for (Widget widget : r) {
                    client.handleComponentRelativeSize(widget);
                }
            }
        }

        public int getLastRendered() {
            return lastRendered;
        }

        public int getFirstRendered() {
            return firstRendered;
        }

        public void moveCol(int oldIndex, int newIndex) {

            // loop all rows and move given index to its new place
            final Iterator<?> rows = iterator();
            while (rows.hasNext()) {
                final VScrollTableRow row = (VScrollTableRow) rows.next();

                final Element td = DOM.getChild(row.getElement(), oldIndex);
                DOM.removeChild(row.getElement(), td);

                DOM.insertChild(row.getElement(), td, newIndex);

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

        public class VScrollTableRow extends Panel implements ActionOwner,
                Container {

            private static final int TOUCHSCROLL_TIMEOUT = 70;
            private static final int DRAGMODE_MULTIROW = 2;
            protected ArrayList<Widget> childWidgets = new ArrayList<Widget>();
            private boolean selected = false;
            protected final int rowKey;
            private List<UIDL> pendingComponentPaints;

            private String[] actionKeys = null;
            private final TableRowElement rowElement;
            private boolean mDown;
            private int index;
            private Event touchStart;
            private static final String ROW_CLASSNAME_EVEN = CLASSNAME + "-row";
            private static final String ROW_CLASSNAME_ODD = CLASSNAME
                    + "-row-odd";
            private static final int TOUCH_CONTEXT_MENU_TIMEOUT = 500;
            private Timer contextTouchTimeout;
            private int touchStartY;
            private int touchStartX;

            private VScrollTableRow(int rowKey) {
                this.rowKey = rowKey;
                rowElement = Document.get().createTRElement();
                setElement(rowElement);
                DOM.sinkEvents(getElement(), Event.MOUSEEVENTS
                        | Event.TOUCHEVENTS | Event.ONDBLCLICK
                        | Event.ONCONTEXTMENU);
            }

            /**
             * Detects whether row is visible in tables viewport.
             * 
             * @return
             */
            public boolean isInViewPort() {
                int absoluteTop = getAbsoluteTop();
                int scrollPosition = scrollBodyPanel.getScrollPosition();
                if (absoluteTop < scrollPosition) {
                    return false;
                }
                int maxVisible = scrollPosition
                        + scrollBodyPanel.getOffsetHeight() - getOffsetHeight();
                if (absoluteTop > maxVisible) {
                    return false;
                }
                return true;
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
                if (!isOdd) {
                    addStyleName(ROW_CLASSNAME_ODD);
                } else {
                    addStyleName(ROW_CLASSNAME_EVEN);
                }
            }

            public int getIndex() {
                return index;
            }

            private void paintComponent(Paintable p, UIDL uidl) {
                if (isAttached()) {
                    p.updateFromUIDL(uidl, client);
                } else {
                    if (pendingComponentPaints == null) {
                        pendingComponentPaints = new LinkedList<UIDL>();
                    }
                    pendingComponentPaints.add(uidl);
                }
            }

            @Override
            protected void onAttach() {
                super.onAttach();
                if (pendingComponentPaints != null) {
                    for (UIDL uidl : pendingComponentPaints) {
                        Paintable paintable = client.getPaintable(uidl);
                        paintable.updateFromUIDL(uidl, client);
                    }
                }
            }

            @Override
            protected void onDetach() {
                super.onDetach();
                client.getContextMenu().ensureHidden(this);
            }

            public String getKey() {
                return String.valueOf(rowKey);
            }

            public VScrollTableRow(UIDL uidl, char[] aligns) {
                this(uidl.getIntAttribute("key"));

                /*
                 * Rendering the rows as hidden improves Firefox and Safari
                 * performance drastically.
                 */
                getElement().getStyle().setProperty("visibility", "hidden");

                String rowStyle = uidl.getStringAttribute("rowstyle");
                if (rowStyle != null) {
                    addStyleName(CLASSNAME + "-row-" + rowStyle);
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

                final Iterator<?> cells = uidl.getChildIterator();
                while (cells.hasNext()) {
                    final Object cell = cells.next();
                    visibleColumnIndex++;

                    String columnId = visibleColOrder[visibleColumnIndex];

                    String style = "";
                    if (uidl.hasAttribute("style-" + columnId)) {
                        style = uidl.getStringAttribute("style-" + columnId);
                    }

                    boolean sorted = tHead.getHeaderCell(col).isSorted();
                    if (cell instanceof String) {
                        addCell(uidl, cell.toString(), aligns[col++], style,
                                false, sorted);
                    } else {
                        final Paintable cellContent = client
                                .getPaintable((UIDL) cell);

                        addCell(uidl, (Widget) cellContent, aligns[col++],
                                style, sorted);
                        paintComponent(cellContent, (UIDL) cell);
                    }
                }
                if (uidl.hasAttribute("selected") && !isSelected()) {
                    toggleSelection();
                }
            }

            /**
             * Add a dummy row, used for measurements if Table is empty.
             */
            public VScrollTableRow() {
                this(0);
                addStyleName(CLASSNAME + "-row");
                addCell(null, "_", 'b', "", true, false);
            }

            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean sorted) {
                // String only content is optimized by not using Label widget
                final Element td = DOM.createTD();
                final Element container = DOM.createDiv();
                String className = CLASSNAME + "-cell-content";
                if (style != null && !style.equals("")) {
                    className += " " + CLASSNAME + "-cell-content-" + style;
                }
                if (sorted) {
                    className += " " + CLASSNAME + "-cell-content-sorted";
                }
                td.setClassName(className);
                container.setClassName(CLASSNAME + "-cell-wrapper");
                if (textIsHTML) {
                    container.setInnerHTML(text);
                } else {
                    container.setInnerText(text);
                }
                if (align != ALIGN_LEFT) {
                    switch (align) {
                    case ALIGN_CENTER:
                        container.getStyle().setProperty("textAlign", "center");
                        break;
                    case ALIGN_RIGHT:
                    default:
                        container.getStyle().setProperty("textAlign", "right");
                        break;
                    }
                }
                td.appendChild(container);
                getElement().appendChild(td);
            }

            public void addCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean sorted) {
                final Element td = DOM.createTD();
                final Element container = DOM.createDiv();
                String className = CLASSNAME + "-cell-content";
                if (style != null && !style.equals("")) {
                    className += " " + CLASSNAME + "-cell-content-" + style;
                }
                if (sorted) {
                    className += " " + CLASSNAME + "-cell-content-sorted";
                }
                td.setClassName(className);
                container.setClassName(CLASSNAME + "-cell-wrapper");
                // TODO most components work with this, but not all (e.g.
                // Select)
                // Old comment: make widget cells respect align.
                // text-align:center for IE, margin: auto for others
                if (align != ALIGN_LEFT) {
                    switch (align) {
                    case ALIGN_CENTER:
                        container.getStyle().setProperty("textAlign", "center");
                        break;
                    case ALIGN_RIGHT:
                    default:
                        container.getStyle().setProperty("textAlign", "right");
                        break;
                    }
                }
                td.appendChild(container);
                getElement().appendChild(td);
                // ensure widget not attached to another element (possible tBody
                // change)
                w.removeFromParent();
                container.appendChild(w.getElement());
                adopt(w);
                childWidgets.add(w);
            }

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

            private void handleClickEvent(Event event, Element targetTdOrTr) {
                if (client.hasEventListeners(VScrollTable.this,
                        ITEM_CLICK_EVENT_ID)) {
                    boolean doubleClick = (DOM.eventGetType(event) == Event.ONDBLCLICK);

                    /* This row was clicked */
                    client.updateVariable(paintableId, "clickedKey", ""
                            + rowKey, false);

                    if (getElement() == targetTdOrTr.getParentElement()) {
                        /* A specific column was clicked */
                        int childIndex = DOM.getChildIndex(getElement(),
                                targetTdOrTr);
                        String colKey = null;
                        colKey = tHead.getHeaderCell(childIndex).getColKey();
                        client.updateVariable(paintableId, "clickedColKey",
                                colKey, false);
                    }

                    MouseEventDetails details = new MouseEventDetails(event);

                    boolean imm = true;
                    if (immediate && event.getButton() == Event.BUTTON_LEFT
                            && !doubleClick && isSelectable() && !isSelected()) {
                        /*
                         * A left click when the table is selectable and in
                         * immediate mode on a row that is not currently
                         * selected will cause a selection event to be fired
                         * after this click event. By making the click event
                         * non-immediate we avoid sending two separate messages
                         * to the server.
                         */
                        imm = false;
                    }

                    client.updateVariable(paintableId, "clickEvent",
                            details.toString(), imm);
                }
            }

            /*
             * React on click that occur on content cells only
             */
            @Override
            public void onBrowserEvent(final Event event) {
                if (enabled) {
                    final int type = event.getTypeInt();
                    final Element targetTdOrTr = getEventTargetTdOrTr(event);
                    if (type == Event.ONCONTEXTMENU) {
                        showContextMenu(event);
                        event.stopPropagation();
                        return;
                    }

                    boolean targetCellOrRowFound = targetTdOrTr != null;
                    switch (type) {
                    case Event.ONDBLCLICK:
                        if (targetCellOrRowFound) {
                            handleClickEvent(event, targetTdOrTr);
                        }
                        break;
                    case Event.ONMOUSEUP:
                        if (targetCellOrRowFound) {
                            mDown = false;
                            handleClickEvent(event, targetTdOrTr);
                            if (event.getButton() == Event.BUTTON_LEFT
                                    && isSelectable()) {

                                // Ctrl+Shift click
                                if ((event.getCtrlKey() || event.getMetaKey())
                                        && event.getShiftKey()
                                        && selectMode == SELECT_MODE_MULTI
                                        && multiselectmode == MULTISELECT_MODE_DEFAULT) {
                                    toggleShiftSelection(false);
                                    setRowFocus(this);

                                    // Ctrl click
                                } else if ((event.getCtrlKey() || event
                                        .getMetaKey())
                                        && selectMode == SELECT_MODE_MULTI
                                        && multiselectmode == MULTISELECT_MODE_DEFAULT) {
                                    boolean wasSelected = isSelected();
                                    toggleSelection();
                                    setRowFocus(this);
                                    /*
                                     * next possible range select must start on
                                     * this row
                                     */
                                    selectionRangeStart = this;
                                    if (wasSelected) {
                                        removeRowFromUnsentSelectionRanges(this);
                                    }

                                    // Ctrl click (Single selection)
                                } else if ((event.getCtrlKey() || event
                                        .getMetaKey()
                                        && selectMode == SELECT_MODE_SINGLE)) {
                                    if (!isSelected()
                                            || (isSelected() && nullSelectionAllowed)) {

                                        if (!isSelected()) {
                                            deselectAll();
                                        }

                                        toggleSelection();
                                        setRowFocus(this);
                                    }

                                    // Shift click
                                } else if (event.getShiftKey()
                                        && selectMode == SELECT_MODE_MULTI
                                        && multiselectmode == MULTISELECT_MODE_DEFAULT) {
                                    toggleShiftSelection(true);

                                    // click
                                } else {
                                    boolean currentlyJustThisRowSelected = selectedRowKeys
                                            .size() == 1
                                            && selectedRowKeys
                                                    .contains(getKey());

                                    if (!currentlyJustThisRowSelected) {
                                        if (multiselectmode == MULTISELECT_MODE_DEFAULT) {
                                            deselectAll();
                                        }
                                        toggleSelection();
                                    } else if ((selectMode == SELECT_MODE_SINGLE || multiselectmode == MULTISELECT_MODE_SIMPLE)
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
                                    ((Element) event.getEventTarget().cast())
                                            .setPropertyJSO("onselectstart",
                                                    null);
                                }
                                sendSelectedRows();
                            }
                        }
                        break;
                    case Event.ONTOUCHEND:
                    case Event.ONTOUCHCANCEL:
                        if (touchStart != null) {
                            /*
                             * Touch has not been handled as neither context or
                             * drag start, handle it as a click.
                             */
                            Util.simulateClickFromTouchEvent(touchStart, this);
                            touchStart = null;
                        }
                        if (contextTouchTimeout != null) {
                            contextTouchTimeout.cancel();
                        }
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
                            if (contextTouchTimeout != null) {
                                contextTouchTimeout.cancel();
                            }
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
                        // isntance during the operation.
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
                                    if (activeScrollDelegate != null
                                            && !activeScrollDelegate.isMoved()) {
                                        /*
                                         * scrolling hasn't started. Cancel
                                         * scrolling and let row handle this as
                                         * drag start or context menu.
                                         */
                                        activeScrollDelegate.stopScrolling();
                                    } else {
                                        /*
                                         * Scrolled or scrolling, clear touch
                                         * start to indicate that row shouldn't
                                         * handle touch move/end events.
                                         */
                                        touchStart = null;
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
                            contextTouchTimeout.cancel();
                            contextTouchTimeout
                                    .schedule(TOUCH_CONTEXT_MENU_TIMEOUT);
                        }
                        break;
                    case Event.ONMOUSEDOWN:
                        if (targetCellOrRowFound) {
                            setRowFocus(this);
                            ensureFocus();
                            if (dragmode != 0
                                    && (event.getButton() == NativeEvent.BUTTON_LEFT)) {
                                startRowDrag(event, type, targetTdOrTr);

                            } else if (event.getCtrlKey()
                                    || event.getShiftKey()
                                    || event.getMetaKey()
                                    && selectMode == SELECT_MODE_MULTI
                                    && multiselectmode == MULTISELECT_MODE_DEFAULT) {

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
                        if (targetCellOrRowFound) {
                            mDown = false;
                        }
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

            protected void startRowDrag(Event event, final int type,
                    Element targetTdOrTr) {
                mDown = true;
                VTransferable transferable = new VTransferable();
                transferable.setDragSource(VScrollTable.this);
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
                if (dragmode == DRAGMODE_MULTIROW
                        && selectMode == SELECT_MODE_MULTI
                        && selectedRowKeys.contains("" + rowKey)) {
                    ev.createDragImage(
                            (Element) scrollBody.tBodyElement.cast(), true);
                    Element dragImage = ev.getDragImage();
                    int i = 0;
                    for (Iterator<Widget> iterator = scrollBody.iterator(); iterator
                            .hasNext();) {
                        VScrollTableRow next = (VScrollTableRow) iterator
                                .next();
                        Element child = (Element) dragImage.getChild(i++);
                        if (!selectedRowKeys.contains("" + next.rowKey)) {
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
                Widget widget = Util.findWidget(eventTarget, null);
                final Element thisTrElement = getElement();

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
                if (eventTarget == thisTrElement) {
                    // This was a click on the TR element
                    return thisTrElement;
                }

                // Iterate upwards until we find the TR element
                Element element = eventTarget;
                while (element != null
                        && element.getParentElement().cast() != thisTrElement) {
                    element = element.getParentElement().cast();
                }
                return element;
            }

            public void showContextMenu(Event event) {
                if (enabled && actionKeys != null) {
                    int left = Util.getTouchOrMouseClientX(event);
                    int top = Util.getTouchOrMouseClientY(event);
                    top += Window.getScrollTop();
                    left += Window.getScrollLeft();
                    client.getContextMenu().showAt(this, left, top);
                }
                event.stopPropagation();
                event.preventDefault();
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
                if (selectMode == SELECT_MODE_SINGLE) {
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
                    // If start row is null then we have a multipage selection
                    // from
                    // above
                    if (startRow == null) {
                        startRow = (VScrollTableRow) scrollBody.iterator()
                                .next();
                        setRowFocus(endRow);
                    }
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
             * @see com.vaadin.terminal.gwt.client.ui.IActionOwner#getActions ()
             */
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

            public ApplicationConnection getClient() {
                return client;
            }

            public String getPaintableId() {
                return paintableId;
            }

            public RenderSpace getAllocatedSpace(Widget child) {
                int w = 0;
                int i = getColIndexOf(child);
                HeaderCell headerCell = tHead.getHeaderCell(i);
                if (headerCell != null) {
                    if (initializedAndAttached) {
                        w = headerCell.getWidth();
                    } else {
                        // header offset width is not absolutely correct value,
                        // but a best guess (expecting similar content in all
                        // columns ->
                        // if one component is relative width so are others)
                        w = headerCell.getOffsetWidth() - getCellExtraWidth();
                    }
                }
                return new RenderSpace(w, 0) {
                    @Override
                    public int getHeight() {
                        return (int) getRowHeight();
                    }
                };
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

            public boolean hasChildComponent(Widget component) {
                return childWidgets.contains(component);
            }

            public void replaceChildComponent(Widget oldComponent,
                    Widget newComponent) {
                com.google.gwt.dom.client.Element parentElement = oldComponent
                        .getElement().getParentElement();
                int index = childWidgets.indexOf(oldComponent);
                oldComponent.removeFromParent();

                parentElement.appendChild(newComponent.getElement());
                childWidgets.add(index, newComponent);
                adopt(newComponent);

            }

            public boolean requestLayout(Set<Paintable> children) {
                // row size should never change and system wouldn't event
                // survive as this is a kind of fake paitable
                return true;
            }

            public void updateCaption(Paintable component, UIDL uidl) {
                // NOP, not rendered
            }

            public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
                // Should never be called,
                // Component container interface faked here to get layouts
                // render properly
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

        if (height == null || height.equals("")) {
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
                int currentlyVisible = scrollBody.lastRendered
                        - scrollBody.firstRendered;
                if (currentlyVisible < pageLength
                        && currentlyVisible < totalRows) {
                    // shake scrollpanel to fill empty space
                    scrollBodyPanel.setScrollPosition(scrollTop + 1);
                    scrollBodyPanel.setScrollPosition(scrollTop - 1);
                }
            }
        }

    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        this.width = width;
        if (width != null && !"".equals(width)) {
            super.setWidth(width);
            int innerPixels = getOffsetWidth() - getBorderWidth();
            if (innerPixels < 0) {
                innerPixels = 0;
            }
            setContentWidth(innerPixels);

            // readjust undefined width columns
            lazyAdjustColumnWidths.cancel();
            lazyAdjustColumnWidths.schedule(LAZY_COLUMN_ADJUST_TIMEOUT);

        } else {
            // Undefined width
            super.setWidth("");

            // Readjust size of table
            sizeInit();

            // readjust undefined width columns
            lazyAdjustColumnWidths.cancel();
            lazyAdjustColumnWidths.schedule(LAZY_COLUMN_ADJUST_TIMEOUT);
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

            Iterator<Widget> headCells = tHead.iterator();
            int usedMinimumWidth = 0;
            int totalExplicitColumnsWidths = 0;
            float expandRatioDivider = 0;
            int colIndex = 0;
            while (headCells.hasNext()) {
                final HeaderCell hCell = (HeaderCell) headCells.next();
                if (hCell.isDefinedWidth()) {
                    totalExplicitColumnsWidths += hCell.getWidth();
                    usedMinimumWidth += hCell.getWidth();
                } else {
                    usedMinimumWidth += hCell.getNaturalColumnWidth(colIndex);
                    expandRatioDivider += hCell.getExpandRatio();
                }
                colIndex++;
            }

            int availW = scrollBody.getAvailableWidth();
            // Hey IE, are you really sure about this?
            availW = scrollBody.getAvailableWidth();
            int visibleCellCount = tHead.getVisibleCellCount();
            availW -= scrollBody.getCellExtraWidth() * visibleCellCount;
            if (willHaveScrollbars()) {
                availW -= Util.getNativeScrollbarSize();
            }

            int extraSpace = availW - usedMinimumWidth;
            if (extraSpace < 0) {
                extraSpace = 0;
            }

            int totalUndefinedNaturalWidths = usedMinimumWidth
                    - totalExplicitColumnsWidths;

            // we have some space that can be divided optimally
            HeaderCell hCell;
            colIndex = 0;
            headCells = tHead.iterator();
            int checksum = 0;
            while (headCells.hasNext()) {
                hCell = (HeaderCell) headCells.next();
                if (!hCell.isDefinedWidth()) {
                    int w = hCell.getNaturalColumnWidth(colIndex);
                    int newSpace;
                    if (expandRatioDivider > 0) {
                        // divide excess space by expand ratios
                        newSpace = Math.round((w + extraSpace
                                * hCell.getExpandRatio() / expandRatioDivider));
                    } else {
                        if (totalUndefinedNaturalWidths != 0) {
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
                    checksum += hCell.getWidth();
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
                    if (!hc.isDefinedWidth()) {
                        setColWidth(colIndex,
                                hc.getWidth() + availW - checksum, false);
                        break;
                    }
                    colIndex++;
                }
            }

            if ((height == null || "".equals(height))
                    && totalRows == pageLength) {
                // fix body height (may vary if lazy loading is offhorizontal
                // scrollbar appears/disappears)
                int bodyHeight = scrollBody.getRequiredHeight();
                boolean needsSpaceForHorizontalScrollbar = (availW < usedMinimumWidth);
                if (needsSpaceForHorizontalScrollbar) {
                    bodyHeight += Util.getNativeScrollbarSize();
                }
                int heightBefore = getOffsetHeight();
                scrollBodyPanel.setHeight(bodyHeight + "px");
                if (heightBefore != getOffsetHeight()) {
                    Util.notifyParentOfSizeChange(VScrollTable.this, false);
                }
            }
            scrollBody.reLayoutComponents();
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    Util.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
                }
            });

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
            borderWidth = Util.measureHorizontalPaddingAndBorder(
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
        if (height != null && !"".equals(height)) {
            containerHeight = getOffsetHeight();
            containerHeight -= showColHeaders ? tHead.getOffsetHeight() : 0;
            containerHeight -= tFoot.getOffsetHeight();
            containerHeight -= getContentAreaBorderHeight();
            if (containerHeight < 0) {
                containerHeight = 0;
            }
            scrollBodyPanel.setHeight(containerHeight + "px");
        }
    }

    private int contentAreaBorderHeight = -1;
    private int scrollLeft;
    private int scrollTop;
    private VScrollTableDropHandler dropHandler;
    private boolean navKeyDown;
    private boolean multiselectPending;

    /**
     * @return border top + border bottom of the scrollable area of table
     */
    private int getContentAreaBorderHeight() {
        if (contentAreaBorderHeight < 0) {
            if (BrowserInfo.get().isIE7() || BrowserInfo.get().isIE6()) {
                contentAreaBorderHeight = Util
                        .measureVerticalBorder(scrollBodyPanel.getElement());
            } else {
                DOM.setStyleAttribute(scrollBodyPanel.getElement(), "overflow",
                        "hidden");
                int oh = scrollBodyPanel.getOffsetHeight();
                int ch = scrollBodyPanel.getElement().getPropertyInt(
                        "clientHeight");
                contentAreaBorderHeight = oh - ch;
                DOM.setStyleAttribute(scrollBodyPanel.getElement(), "overflow",
                        "auto");
            }
        }
        return contentAreaBorderHeight;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
        super.setHeight(height);
        setContainerHeight();
        if (initializedAndAttached) {
            updatePageLength();
        }
        if (!rendering) {
            // Webkit may sometimes get an odd rendering bug (white space
            // between header and body), see bug #3875. Running
            // overflow hack here to shake body element a bit.
            Util.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
        }

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
                        public void execute() {
                            scrollBodyPanel
                                    .setScrollPosition((int) (firstRowInViewPort * scrollBody
                                            .getRowHeight()));
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
            s = "<img src=\""
                    + client.translateVaadinUri(uidl.getStringAttribute("icon"))
                    + "\" alt=\"icon\" class=\"v-icon\">" + s;
        }
        return s;
    }

    /**
     * This method has logic which rows needs to be requested from server when
     * user scrolls
     */
    public void onScroll(ScrollEvent event) {
        scrollLeft = scrollBodyPanel.getElement().getScrollLeft();
        scrollTop = scrollBodyPanel.getScrollPosition();
        if (!initializedAndAttached) {
            return;
        }
        if (!enabled) {
            scrollBodyPanel
                    .setScrollPosition((int) (firstRowInViewPort * scrollBody
                            .getRowHeight()));
            return;
        }

        rowRequestHandler.cancel();

        if (BrowserInfo.get().isSafari() && event != null && scrollTop == 0) {
            // due to the webkitoverflowworkaround, top may sometimes report 0
            // for webkit, although it really is not. Expecting to have the
            // correct
            // value available soon.
            Scheduler.get().scheduleDeferred(new Command() {
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

        firstRowInViewPort = (int) Math.ceil(scrollTop
                / scrollBody.getRowHeight());
        if (firstRowInViewPort > totalRows - pageLength) {
            firstRowInViewPort = totalRows - pageLength;
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
            // remember which firstvisible we requested, in case the server has
            // a differing opinion
            lastRequestedFirstvisible = firstRowInViewPort;
            client.updateVariable(paintableId, "firstvisible",
                    firstRowInViewPort, false);
            return; // scrolled withing "non-react area"
        }

        if (firstRowInViewPort - pageLength * cache_rate > lastRendered
                || firstRowInViewPort + pageLength + pageLength * cache_rate < firstRendered) {
            // need a totally new set
            rowRequestHandler
                    .setReqFirstRow((firstRowInViewPort - (int) (pageLength * cache_rate)));
            int last = firstRowInViewPort + (int) (cache_rate * pageLength)
                    + pageLength - 1;
            if (last >= totalRows) {
                last = totalRows - 1;
            }
            rowRequestHandler.setReqRows(last
                    - rowRequestHandler.getReqFirstRow() + 1);
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
            rowRequestHandler.setReqFirstRow(lastRendered + 1);
            rowRequestHandler.setReqRows((int) ((firstRowInViewPort
                    + pageLength + pageLength * cache_rate) - lastRendered));
            rowRequestHandler.deferRowFetch();
        }
    }

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

        // @Override
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

            VScrollTableRow row = Util.findWidget(elementOver, getRowClass());
            if (row != null) {
                dropDetails.overkey = row.rowKey;
                Element tr = row.getElement();
                Element element = elementOver;
                while (element != null && element.getParentElement() != tr) {
                    element = (Element) element.getParentElement();
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
            return scrollBody.iterator().next().getClass();
        }

        @Override
        public void dragOver(VDragEvent drag) {
            TableDDDetails oldDetails = dropDetails;
            updateDropDetails(drag);
            if (!oldDetails.equals(dropDetails)) {
                deEmphasis();
                final TableDDDetails newDetails = dropDetails;
                VAcceptCallback cb = new VAcceptCallback() {
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
            UIObject.setStyleName(getElement(), CLASSNAME + "-drag", false);
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
            UIObject.setStyleName(getElement(), CLASSNAME + "-drag", true);
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
        public Paintable getPaintable() {
            return VScrollTable.this;
        }

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
    private boolean setRowFocus(VScrollTableRow row) {

        if (selectMode == SELECT_MODE_NONE) {
            return false;
        }

        // Remove previous selection
        if (focusedRow != null && focusedRow != row) {
            focusedRow.removeStyleName(CLASSNAME_SELECTION_FOCUS);
        }

        if (row != null) {

            // Apply focus style to new selection
            row.addStyleName(CLASSNAME_SELECTION_FOCUS);

            /*
             * Trying to set focus on already focused row
             */
            if (row == focusedRow) {
                return false;
            }

            // Set new focused row
            focusedRow = row;

            ensureRowIsVisible(row);

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
        scrollIntoViewVertically(row.getElement());
    }

    /**
     * Scrolls an element into view vertically only. Modified version of
     * Element.scrollIntoView.
     * 
     * @param elem
     *            The element to scroll into view
     */
    private native void scrollIntoViewVertically(Element elem)
    /*-{
        var top = elem.offsetTop;
        var height = elem.offsetHeight;
    
        if (elem.parentNode != elem.offsetParent) {
          top -= elem.parentNode.offsetTop;
        }
    
        var cur = elem.parentNode;
        while (cur && (cur.nodeType == 1)) {
          if (top < cur.scrollTop) {
            cur.scrollTop = top;
          }
          if (top + height > cur.scrollTop + cur.clientHeight) {
            cur.scrollTop = (top + height) - cur.clientHeight;
          }
    
          var offsetTop = cur.offsetTop;
          if (cur.parentNode != cur.offsetParent) {
            offsetTop -= cur.parentNode.offsetTop;
          }
           
          top += offsetTop - cur.scrollTop;
          cur = cur.parentNode;
        }
     }-*/;

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
        if (selectMode == SELECT_MODE_NONE && keycode == getNavigationDownKey()) {
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition() + scrollingVelocity);
            return true;
        } else if (keycode == getNavigationDownKey()) {
            if (selectMode == SELECT_MODE_MULTI && moveFocusDown()) {
                selectFocusedRow(ctrl, shift);

            } else if (selectMode == SELECT_MODE_SINGLE && !shift
                    && moveFocusDown()) {
                selectFocusedRow(ctrl, shift);
            }
            return true;
        }

        // Up navigation
        if (selectMode == SELECT_MODE_NONE && keycode == getNavigationUpKey()) {
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition() - scrollingVelocity);
            return true;
        } else if (keycode == getNavigationUpKey()) {
            if (selectMode == SELECT_MODE_MULTI && moveFocusUp()) {
                selectFocusedRow(ctrl, shift);
            } else if (selectMode == SELECT_MODE_SINGLE && !shift
                    && moveFocusUp()) {
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
            if (selectMode == SELECT_MODE_SINGLE) {
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
                        sendSelectedRows();
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
                            sendSelectedRows();
                        } else {
                            // scroll down by pixels and return, to wait for
                            // new rows, then select the last item in the
                            // viewport
                            selectLastItemInNextRender = true;
                            multiselectPending = shift;
                            scrollByPagelenght(1);
                        }
                    }
                }
            } else {
                /* No selections, go page down by scrolling */
                scrollByPagelenght(1);
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
                        sendSelectedRows();
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
                            sendSelectedRows();
                        } else {
                            // unless waiting for the next rowset already
                            // scroll down by pixels and return, to wait for
                            // new rows, then select the last item in the
                            // viewport
                            selectFirstItemInNextRender = true;
                            multiselectPending = shift;
                            scrollByPagelenght(-1);
                        }
                    }
                }
            } else {
                /* No selections, go page up by scrolling */
                scrollByPagelenght(-1);
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
                        sendSelectedRows();
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
                        sendSelectedRows();
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

    private void scrollByPagelenght(int i) {
        int pixels = i
                * (int) (getFullyVisibleRowCount() * scrollBody.getRowHeight());
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
    public void onBlur(BlurEvent event) {
        hasFocus = false;
        navKeyDown = false;

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
            return !(!hasHorizontalScrollbar() && !hasVerticalScrollbar() && selectMode == SELECT_MODE_NONE);
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
     * @see com.vaadin.terminal.gwt.client.Focusable#focus()
     */
    public void focus() {
        if (isFocusable()) {
            scrollBodyPanel.focus();
        }
    }

    /**
     * Sets the proper tabIndex for scrollBodyPanel (the focusable elemen in the
     * component).
     * 
     * If the component has no explicit tabIndex a zero is given (default
     * tabbing order based on dom hierarchy) or -1 if the component does not
     * need to gain focus. The component needs no focus if it has no scrollabars
     * (not scrollable) and not selectable. Note that in the future shortcut
     * actions may need focus.
     * 
     */
    private void setProperTabIndex() {
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

    public ApplicationConnection getClient() {
        return client;
    }

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

}
