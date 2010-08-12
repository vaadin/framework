/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
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
        VHasDropHandler, KeyPressHandler, KeyDownHandler, FocusHandler,
        BlurHandler, Focusable {

    public static final String CLASSNAME = "v-table";
    public static final String CLASSNAME_SELECTION_FOCUS = CLASSNAME + "-focus";

    public static final String ITEM_CLICK_EVENT_ID = "itemClick";
    public static final String HEADER_CLICK_EVENT_ID = "handleHeaderClick";
    public static final String FOOTER_CLICK_EVENT_ID = "handleFooterClick";
    public static final String COLUMN_RESIZE_EVENT_ID = "columnResize";

    private static final double CACHE_RATE_DEFAULT = 2;

    /**
     * The default multi select mode where simple left clicks only selects one
     * item, CTRL+left click selects multiple items and SHIFT-left click selects
     * a range of items.
     */
    private static final int MULTISELECT_MODE_DEFAULT = 0;

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

    private Timer scrollingVelocityTimer = null;;

    /**
     * Represents a select range of rows
     */
    private class SelectionRange {
        /**
         * The starting key of the range
         */
        private int startRowKey;

        /**
         * The ending key of the range
         */
        private int endRowKey;

        /**
         * Constuctor.
         * 
         * @param startRowKey
         *            The range start. Must be less than endRowKey
         * @param endRowKey
         *            The range end. Must be bigger than startRowKey
         */
        public SelectionRange(int startRowKey, int endRowKey) {
            this.startRowKey = startRowKey;
            this.endRowKey = endRowKey;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return startRowKey + "-" + endRowKey;
        }

        public boolean inRange(int key) {
            return key >= startRowKey && key <= endRowKey;
        }

        public int getStartKey() {
            return startRowKey;
        }

        public int getEndKey() {
            return endRowKey;
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

    private final FocusableScrollPanel scrollBodyPanel = new FocusableScrollPanel();

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

    private int multiselectmode;

    public VScrollTable() {
        scrollBodyPanel.setStyleName(CLASSNAME + "-body-wrapper");

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            scrollBodyPanel.addKeyPressHandler(this);
        } else {
            scrollBodyPanel.addKeyDownHandler(this);
        }

        scrollBodyPanel.addFocusHandler(this);
        scrollBodyPanel.addBlurHandler(this);

        scrollBodyPanel.addScrollHandler(this);
        scrollBodyPanel.setStyleName(CLASSNAME + "-body");

        setStyleName(CLASSNAME);

        add(tHead);
        add(scrollBodyPanel);
        add(tFoot);

        rowRequestHandler = new RowRequestHandler();

        /*
         * We need to use the sinkEvents method to catch the keyUp events so we
         * can cache a single shift. KeyUpHandler cannot do this.
         */
        sinkEvents(Event.ONKEYUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user
     * .client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONKEYUP) {
            if (event.getKeyCode() == KeyCodes.KEY_SHIFT) {
                sendSelectedRows();
                selectionRangeStart = null;
            } else if ((event.getKeyCode() == getNavigationUpKey()
                    || event.getKeyCode() == getNavigationDownKey()
                    || event.getKeyCode() == getNavigationPageUpKey() || event
                    .getKeyCode() == getNavigationPageDownKey())
                    && !event.getShiftKey()) {
                sendSelectedRows();

                if (scrollingVelocityTimer != null) {
                    scrollingVelocityTimer.cancel();
                    scrollingVelocityTimer = null;
                    scrollingVelocity = 10;
                }
            }
        }
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
        if (selectMode > VScrollTable.SELECT_MODE_NONE) {
            if (focusedRow == null && scrollBody.iterator().hasNext()) {
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
        if (selectMode > VScrollTable.SELECT_MODE_NONE) {
            if (focusedRow == null && scrollBody.iterator().hasNext()) {
                return setRowFocus((VScrollTableRow) scrollBody.iterator()
                        .next());
            } else {
                VScrollTableRow prev = getPreviousRow(focusedRow, offset);
                if (prev != null) {
                    return setRowFocus(prev);
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
            if (selectMode > SELECT_MODE_NONE && !ctrlSelect && !shiftSelect) {
                deselectAll();
                focusedRow.toggleSelection();
                selectionRangeStart = focusedRow;
            }

            // Ctrl+arrows moves selection head
            else if (selectMode > SELECT_MODE_NONE && ctrlSelect
                    && !shiftSelect) {
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

        // Note: changing the immediateness of this
        // might
        // require changes to "clickEvent" immediateness
        // also.
        if (multiselectmode == MULTISELECT_MODE_DEFAULT) {
            // Convert ranges to a set of strings
            Set<String> ranges = new HashSet<String>();
            for (SelectionRange range : selectedRowRanges) {
                ranges.add(range.toString());
            }

            // Send the selected row ranges
            client.updateVariable(paintableId, "selectedRanges", ranges
                    .toArray(new String[selectedRowRanges.size()]), false);
        }

        // Send the selected rows
        client.updateVariable(paintableId, "selected", selectedRowKeys
                .toArray(new String[selectedRowKeys.size()]), immediate);

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
        return 32;
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
    @SuppressWarnings("unchecked")
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

        multiselectmode = uidl.hasAttribute("multiselectmode") ? uidl
                .getIntAttribute("multiselectmode") : MULTISELECT_MODE_DEFAULT;

        setCacheRate(uidl.hasAttribute("cr") ? uidl.getDoubleAttribute("cr")
                : CACHE_RATE_DEFAULT);

        recalcWidths = uidl.hasAttribute("recalcWidths");
        if (recalcWidths) {
            tHead.clear();
            tFoot.clear();
        }

        if (uidl.hasAttribute("pagelength")) {
            pageLength = uidl.getIntAttribute("pagelength");
        } else {
            // pagelenght is "0" meaning scrolling is turned off
            pageLength = totalRows;
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

        if (uidl.hasVariable("sortascending")) {
            sortAscending = uidl.getBooleanVariable("sortascending");
            sortColumn = uidl.getStringVariable("sortcolumn");
        }

        if (uidl.hasVariable("selected")) {
            final Set<String> selectedKeys = uidl
                    .getStringArrayVariableAsSet("selected");
            if (scrollBody != null) {
                Iterator<Widget> iterator = scrollBody.iterator();
                while (iterator.hasNext()) {
                    VScrollTableRow row = (VScrollTableRow) iterator.next();
                    boolean selected = selectedKeys.contains(row.getKey());
                    if (selected != row.isSelected()) {
                        row.toggleSelection();
                    }
                }
            }
        }

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
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
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
            updateBody(rowData, uidl.getIntAttribute("firstrow"), uidl
                    .getIntAttribute("rows"));
            if (headerChangedDuringUpdate) {
                lazyAdjustColumnWidths.schedule(1);
            } else {
                // webkits may still bug with their disturbing scrollbar bug,
                // See #3457
                // run overflow fix for scrollable area
                DeferredCommand.addCommand(new Command() {
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

            scrollBody.renderInitialRows(rowData, uidl
                    .getIntAttribute("firstrow"), uidl.getIntAttribute("rows"));
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
        selectionChanged = false;

        // This is called when the Home button has been pressed and the pages
        // changes
        if (selectFirstItemInNextRender) {
            selectFirstRenderedRow(false);
            selectFirstItemInNextRender = false;
        }

        if (focusFirstItemInNextRender) {
            selectFirstRenderedRow(true);
            focusFirstItemInNextRender = false;
        }

        // This is called when the End button has been pressed and the pages
        // changes
        if (selectLastItemInNextRender) {
            selectLastRenderedRow(false);
            selectLastItemInNextRender = false;
        }

        if (focusLastItemInNextRender) {
            selectLastRenderedRow(true);
            focusLastItemInNextRender = false;
        }

        if (focusedRow != null) {
            if (!focusedRow.isAttached()) {
                // focused row has orphaned, can't focus
                focusedRow = null;
                if (SELECT_MODE_SINGLE == selectMode
                        && selectedRowKeys.size() > 0) {
                    // try to focusa row currently selected and in viewport
                    String selectedRowKey = selectedRowKeys.iterator().next();
                    if (selectedRowKey != null) {
                        setRowFocus(getRenderedRowByKey(selectedRowKey));
                    }
                }
                // TODO what should happen in multiselect mode?
            } else {
                setRowFocus(getRenderedRowByKey(focusedRow.getKey()));
            }
        }

        if (!isFocusable()) {
            scrollBodyPanel.getElement().setTabIndex(-1);
        } else {
            scrollBodyPanel.getElement().setTabIndex(0);
        }

        rendering = false;
        headerChangedDuringUpdate = false;

        // Ensure that the focus has not scrolled outside the viewport
        if (focusedRow != null)
            ensureRowIsVisible(focusedRow);
    }

    protected VScrollTableBody createScrollBody() {
        return new VScrollTableBody();
    }

    /**
     * Selects the last rendered row in the table
     * 
     * @param focusOnly
     *            Should the focus only be moved to the last row
     */
    private void selectLastRenderedRow(boolean focusOnly) {
        VScrollTableRow row = null;
        Iterator<Widget> it = scrollBody.iterator();
        while (it.hasNext()) {
            row = (VScrollTableRow) it.next();
        }
        if (row != null) {
            setRowFocus(row);
            if (!focusOnly) {
                deselectAll();
                selectFocusedRow(false, false);
                sendSelectedRows();
            }
        }

    }

    /**
     * Selects the first rendered row
     * 
     * @param focusOnly
     *            Should the focus only be moved to the first row
     */
    private void selectFirstRenderedRow(boolean focusOnly) {
        setRowFocus((VScrollTableRow) scrollBody.iterator().next());
        if (!focusOnly) {
            deselectAll();
            selectFocusedRow(false, false);
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
            tHead.enableColumn("0", colIndex);
            visibleCols++;
            visibleColOrder = new String[visibleCols];
            visibleColOrder[colIndex] = "0";
            colIndex++;
        } else {
            visibleColOrder = new String[visibleCols];
            tHead.removeCell("0");
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
            tFoot.enableColumn("0", colIndex);
            colIndex++;
        } else {
            tFoot.removeCell("0");
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
        if ("0".equals(colKey)) {
            return 0;
        }
        for (int i = 0; i < visibleColOrder.length; i++) {
            if (visibleColOrder[i].equals(colKey)) {
                return i;
            }
        }
        return -1;
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
        // Set header column width
        final HeaderCell hcell = tHead.getHeaderCell(colIndex);
        hcell.setWidth(w, isDefinedWidth);

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
            needsReLayout = true;

            if (expandRatioDivider > 0) {
                // visible columns have some active expand ratios, excess
                // space is divided according to them
                headCells = tHead.iterator();
                i = 0;
                while (headCells.hasNext()) {
                    HeaderCell hCell = (HeaderCell) headCells.next();
                    if (hCell.getExpandRatio() > 0) {
                        int w = widths[i];
                        final int newSpace = (int) (extraSpace * (hCell
                                .getExpandRatio() / expandRatioDivider));
                        w += newSpace;
                        widths[i] = w;
                    }
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
                        final int newSpace = extraSpace * w / totalWidthR;
                        w += newSpace;
                        widths[i] = w;
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
            DeferredCommand.addCommand(new Command() {
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
        DeferredCommand.addCommand(new Command() {
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
                if ((firstRowInViewPort + pageLength > scrollBody
                        .getLastRendered())
                        || (firstRowInViewPort < scrollBody.getFirstRendered())) {
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
            if (client.hasActiveRequest()) {
                // if client connection is busy, don't bother loading it more
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

            }
        }

        public int getReqFirstRow() {
            return reqFirstRow;
        }

        public int getReqRows() {
            return reqRows;
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

        public void setSortable(boolean b) {
            sortable = b;
        }

        public void setNaturalMinimumColumnWidth(int w) {
            naturalWidth = w;
        }

        public HeaderCell(String colId, String headerText) {
            cid = colId;

            DOM.setElementProperty(colResizeWidget, "className", CLASSNAME
                    + "-resizer");
            DOM.sinkEvents(colResizeWidget, Event.MOUSEEVENTS);

            setText(headerText);

            DOM.appendChild(td, colResizeWidget);

            DOM.setElementProperty(captionContainer, "className", CLASSNAME
                    + "-caption-container");

            // ensure no clipping initially (problem on column additions)
            DOM.setStyleAttribute(captionContainer, "overflow", "visible");

            DOM.sinkEvents(captionContainer, Event.MOUSEEVENTS);

            DOM.appendChild(td, captionContainer);

            DOM.sinkEvents(td, Event.MOUSEEVENTS);

            setElement(td);
        }

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

                captionContainer.getStyle().setPropertyPx("width", w);

                /*
                 * if we already have tBody, set the header width properly, if
                 * not defer it. IE will fail with complex float in table header
                 * unless TD width is not explicitly set.
                 */
                if (scrollBody != null) {
                    int tdWidth = width + scrollBody.getCellExtraWidth();
                    setWidth(tdWidth + "px");
                } else {
                    DeferredCommand.addCommand(new Command() {
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
                    if (dragging && DOM.eventGetType(event) == Event.ONMOUSEUP) {
                        // Handle releasing column header on spacer #5318
                        handleCaptionEvent(event);
                    } else {
                        onResizeEvent(event);
                    }
                } else {
                    handleCaptionEvent(event);
                    if (DOM.eventGetType(event) == Event.ONMOUSEUP) {
                        scrollBodyPanel.setFocus(true);
                    }
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
            updateFloatingCopysPosition(DOM.getAbsoluteLeft(td), DOM
                    .getAbsoluteTop(td));
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
                client.updateVariable(paintableId, "headerClickEvent", details
                        .toString(), false);
                client.updateVariable(paintableId, "headerClickCID", cid, true);
            }
        }

        protected void handleCaptionEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                if (columnReordering) {
                    dragging = true;
                    moved = false;
                    colIndex = getColIndexByKey(cid);
                    DOM.setCapture(getElement());
                    headerX = tHead.getAbsoluteLeft();
                    DOM.eventPreventDefault(event); // prevent selecting text
                }
                break;
            case Event.ONMOUSEUP:
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
                }

                if (!moved) {
                    // mouse event was a click to header -> sort column
                    if (sortable) {
                        if (sortColumn.equals(cid)) {
                            // just toggle order
                            client.updateVariable(paintableId, "sortascending",
                                    !sortAscending, false);
                        } else {
                            // set table scrolled by this column
                            client.updateVariable(paintableId, "sortcolumn",
                                    cid, false);
                        }
                        // get also cache columns at the same request
                        scrollBodyPanel.setScrollPosition(0);
                        firstvisible = 0;
                        rowRequestHandler.setReqFirstRow(0);
                        rowRequestHandler.setReqRows((int) (2 * pageLength
                                * cache_rate + pageLength));
                        rowRequestHandler.deferRowFetch();
                    }
                    fireHeaderClickedEvent(event);
                    break;
                }
                break;
            case Event.ONMOUSEMOVE:
                if (dragging) {
                    if (!moved) {
                        createFloatingCopy();
                        moved = true;
                    }
                    final int x = DOM.eventGetClientX(event)
                            + DOM.getElementPropertyInt(tHead.hTableWrapper,
                                    "scrollLeft");
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

                    updateFloatingCopysPosition(DOM.eventGetClientX(event), -1);
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
                // readjust undefined width columns
                lazyAdjustColumnWidths.cancel();
                lazyAdjustColumnWidths.schedule(1);
                fireColumnResizeEvent(cid, originalWidth, getColWidth(cid));
                break;
            case Event.ONMOUSEMOVE:
                if (isResizing) {
                    final int deltaX = DOM.eventGetClientX(event) - dragStartX;
                    if (deltaX == 0) {
                        return;
                    }

                    int newWidth = originalWidth + deltaX;
                    if (newWidth < scrollBody.getCellExtraWidth()) {
                        newWidth = scrollBody.getCellExtraWidth();
                    }
                    setColWidth(colIndex, newWidth, true);
                }
                break;
            default:
                break;
            }
        }

        public String getCaption() {
            return DOM.getInnerText(captionContainer);
        }

        public boolean isEnabled() {
            return getParent() != null;
        }

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

                    final int hw = ((Element) getElement().getLastChild())
                            .getOffsetWidth()
                            + scrollBody.getCellExtraWidth();
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

    }

    /**
     * HeaderCell that is header cell for row headers.
     * 
     * Reordering disabled and clicking on it resets sorting.
     */
    public class RowHeadersHeaderCell extends HeaderCell {

        RowHeadersHeaderCell() {
            super("0", "");
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

            availableCells.put("0", new RowHeadersHeaderCell());
        }

        @Override
        public void clear() {
            for (String cid : availableCells.keySet()) {
                removeCell(cid);
            }
            availableCells.clear();
            availableCells.put("0", new RowHeadersHeaderCell());
        }

        public void updateCellsFromUIDL(UIDL uidl) {
            Iterator<?> it = uidl.getChildIterator();
            HashSet<String> updated = new HashSet<String>();
            updated.add("0");
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
                hTableWrapper.getStyle().setProperty("position", "relative");
                hTableWrapper.getStyle().setPropertyPx("left", -scrollLeft);
            } else {
                hTableWrapper.setScrollLeft(scrollLeft);
            }
        }

        public void setColumnCollapsingAllowed(boolean cc) {
            if (cc) {
                DOM.setStyleAttribute(columnSelector, "display", "block");
            } else {
                DOM.setStyleAttribute(columnSelector, "display", "none");
            }
        }

        public void disableBrowserIntelligence() {
            DOM.setStyleAttribute(hTableContainer, "width", WRAPPER_WIDTH
                    + "px");
        }

        public void enableBrowserIntelligence() {
            DOM.setStyleAttribute(hTableContainer, "width", "");
        }

        public void setHeaderCell(int index, HeaderCell cell) {
            if (cell.isEnabled()) {
                // we're moving the cell
                DOM.removeChild(tr, cell.getElement());
                orphan(cell);
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
            if (index < visibleCells.size()) {
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
                DOM.setElementProperty(DOM.getFirstChild(DOM.getChild(tr,
                        index - 1)), "className", CLASSNAME + "-resizer "
                        + CLASSNAME + "-focus-slot-right");
            } else {
                DOM.setElementProperty(DOM.getFirstChild(DOM
                        .getChild(tr, index)), "className", CLASSNAME
                        + "-resizer " + CLASSNAME + "-focus-slot-left");
            }
            focusedSlot = index;
        }

        private void removeSlotFocus() {
            if (focusedSlot < 0) {
                return;
            }
            if (focusedSlot == 0) {
                DOM.setElementProperty(DOM.getFirstChild(DOM.getChild(tr,
                        focusedSlot)), "className", CLASSNAME + "-resizer");
            } else if (focusedSlot > 0) {
                DOM.setElementProperty(DOM.getFirstChild(DOM.getChild(tr,
                        focusedSlot - 1)), "className", CLASSNAME + "-resizer");
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

            public VisibleColumnAction(String colKey) {
                super(VScrollTable.TableHead.this);
                this.colKey = colKey;
                caption = tHead.getHeaderCell(colKey).getCaption();
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
                final VisibleColumnAction a = new VisibleColumnAction(c
                        .getColKey());
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

    }

    /**
     * A cell in the footer
     */
    public class FooterCell extends Widget {
        private Element td = DOM.createTD();
        private Element captionContainer = DOM.createDiv();
        private char align = ALIGN_LEFT;
        private int width = -1;
        private float expandRatio = 0;
        private String cid;
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
                    DeferredCommand.addCommand(new Command() {
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
                client.updateVariable(paintableId, "footerClickEvent", details
                        .toString(), false);
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
                            .getOffsetWidth()
                            + scrollBody.getCellExtraWidth();
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
            super("0", "");
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

            availableCells.put("0", new RowHeadersFooterCell());
        }

        @Override
        public void clear() {
            for (String cid : availableCells.keySet()) {
                removeCell(cid);
            }
            availableCells.clear();
            availableCells.put("0", new RowHeadersFooterCell());
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
            updated.add("0");
            while (columnIterator.hasNext()) {
                final UIDL col = (UIDL) columnIterator.next();
                final String cid = col.getStringAttribute("cid");
                updated.add(cid);

                String caption = col.getStringAttribute("fcaption");
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
            VScrollTableRow first = null;
            if (renderedRows.size() > 0) {
                first = (VScrollTableRow) renderedRows.get(0);
            }
            if (first != null && first.getStyleName().indexOf("-odd") == -1) {
                row.addStyleName(CLASSNAME + "-row-odd");
            } else {
                row.addStyleName(CLASSNAME + "-row");
            }
            if (row.isSelected()) {
                row.addStyleName("v-selected");
            }
            tBodyElement.insertBefore(row.getElement(), tBodyElement
                    .getFirstChild());
            adopt(row);
            renderedRows.add(0, row);
        }

        private void addRow(VScrollTableRow row) {
            VScrollTableRow last = null;
            if (renderedRows.size() > 0) {
                last = (VScrollTableRow) renderedRows
                        .get(renderedRows.size() - 1);
            }
            if (last != null && last.getStyleName().indexOf("-odd") == -1) {
                row.addStyleName(CLASSNAME + "-row-odd");
            } else {
                row.addStyleName(CLASSNAME + "-row");
            }
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
                    next.addCell(null, "", ALIGN_LEFT, "", true);
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

            private static final int DRAGMODE_MULTIROW = 2;
            protected ArrayList<Widget> childWidgets = new ArrayList<Widget>();
            private boolean selected = false;
            protected final int rowKey;
            private List<UIDL> pendingComponentPaints;

            private String[] actionKeys = null;
            private final TableRowElement rowElement;
            private boolean mDown;

            private VScrollTableRow(int rowKey) {
                this.rowKey = rowKey;
                rowElement = Document.get().createTRElement();
                setElement(rowElement);
                DOM.sinkEvents(getElement(), Event.MOUSEEVENTS
                        | Event.ONDBLCLICK | Event.ONCONTEXTMENU
                        | Event.ONKEYDOWN);
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
                    addCell(uidl, buildCaptionHtmlSnippet(uidl), aligns[col++],
                            "", true);
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

                    if (cell instanceof String) {
                        addCell(uidl, cell.toString(), aligns[col++], style,
                                false);
                    } else {
                        final Paintable cellContent = client
                                .getPaintable((UIDL) cell);

                        addCell(uidl, (Widget) cellContent, aligns[col++],
                                style);
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
                addCell(null, "_", 'b', "", true);
            }

            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML) {
                // String only content is optimized by not using Label widget
                final Element td = DOM.createTD();
                final Element container = DOM.createDiv();
                String className = CLASSNAME + "-cell-content";
                if (style != null && !style.equals("")) {
                    className += " " + CLASSNAME + "-cell-content-" + style;
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

            public void addCell(UIDL rowUidl, Widget w, char align, String style) {
                final Element td = DOM.createTD();
                final Element container = DOM.createDiv();
                String className = CLASSNAME + "-cell-content";
                if (style != null && !style.equals("")) {
                    className += " " + CLASSNAME + "-cell-content-" + style;
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
                    DOM.removeChild(DOM.getParent(w.getElement()), w
                            .getElement());
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
                    // Note: the 'immediate' logic would need to be more
                    // involved (see #2104), but iscrolltable always sends
                    // select event, even though nullselectionallowed wont let
                    // the change trough. Will need to be updated if that is
                    // changed.
                    client
                            .updateVariable(
                                    paintableId,
                                    "clickEvent",
                                    details.toString(),
                                    !(event.getButton() == Event.BUTTON_LEFT
                                            && !doubleClick
                                            && selectMode > Table.SELECT_MODE_NONE && immediate));
                }
            }

            /**
             * Add this to the element mouse down event by using
             * element.setPropertyJSO
             * ("onselectstart",applyDisableTextSelectionIEHack()); Remove it
             * then again when the mouse is depressed in the mouse up event.
             * 
             * @return Returns the JSO preventing text selection
             */
            private native JavaScriptObject applyDisableTextSelectionIEHack()
            /*-{
                    return function(){ return false; };
            }-*/;

            /*
             * React on click that occur on content cells only
             */
            @Override
            public void onBrowserEvent(Event event) {
                if (enabled) {
                    int type = event.getTypeInt();
                    Element targetTdOrTr = getEventTargetTdOrTr(event);
                    if (type == Event.ONCONTEXTMENU) {
                        showContextMenu(event);
                        event.stopPropagation();
                        return;
                    }

                    if (targetTdOrTr != null) {
                        switch (type) {
                        case Event.ONDBLCLICK:
                            handleClickEvent(event, targetTdOrTr);
                            break;
                        case Event.ONMOUSEUP:
                            mDown = false;
                            handleClickEvent(event, targetTdOrTr);
                            scrollBodyPanel.setFocus(true);
                            if (event.getButton() == Event.BUTTON_LEFT
                                    && selectMode > Table.SELECT_MODE_NONE) {

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
                                    toggleSelection();
                                    setRowFocus(this);

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
                                    } else if (selectMode == SELECT_MODE_SINGLE
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
                            break;
                        case Event.ONMOUSEDOWN:
                            if (dragmode != 0
                                    && event.getButton() == NativeEvent.BUTTON_LEFT) {
                                mDown = true;
                                VTransferable transferable = new VTransferable();
                                transferable.setDragSource(VScrollTable.this);
                                transferable.setData("itemId", "" + rowKey);
                                NodeList<TableCellElement> cells = rowElement
                                        .getCells();
                                for (int i = 0; i < cells.getLength(); i++) {
                                    if (cells.getItem(i).isOrHasChild(
                                            targetTdOrTr)) {
                                        HeaderCell headerCell = tHead
                                                .getHeaderCell(i);
                                        transferable.setData("propertyId",
                                                headerCell.cid);
                                        break;
                                    }
                                }

                                VDragEvent ev = VDragAndDropManager.get()
                                        .startDrag(transferable, event, true);
                                if (dragmode == DRAGMODE_MULTIROW
                                        && selectMode == SELECT_MODE_MULTI
                                        && selectedRowKeys
                                                .contains("" + rowKey)) {
                                    ev.createDragImage(
                                            (Element) scrollBody.tBodyElement
                                                    .cast(), true);
                                    Element dragImage = ev.getDragImage();
                                    int i = 0;
                                    for (Iterator<Widget> iterator = scrollBody
                                            .iterator(); iterator.hasNext();) {
                                        VScrollTableRow next = (VScrollTableRow) iterator
                                                .next();
                                        Element child = (Element) dragImage
                                                .getChild(i++);
                                        if (!selectedRowKeys.contains(""
                                                + next.rowKey)) {
                                            child.getStyle().setVisibility(
                                                    Visibility.HIDDEN);
                                        }
                                    }
                                } else {
                                    ev.createDragImage(getElement(), true);
                                }
                                event.preventDefault();
                                event.stopPropagation();
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
                                                    applyDisableTextSelectionIEHack());
                                }

                                event.stopPropagation();
                            }

                            if (!isFocusable()) {
                                scrollBodyPanel.getElement().setTabIndex(-1);
                            } else {
                                scrollBodyPanel.getElement().setTabIndex(0);
                            }

                            break;
                        case Event.ONMOUSEOUT:
                            mDown = false;
                            break;
                        default:
                            break;
                        }
                    }
                }
                super.onBrowserEvent(event);
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
                Element targetTdOrTr = null;

                final Element eventTarget = DOM.eventGetTarget(event);
                final Element eventTargetParent = DOM.getParent(eventTarget);
                final Element eventTargetGrandParent = DOM
                        .getParent(eventTargetParent);

                final Element thisTrElement = getElement();

                if (eventTarget == thisTrElement) {
                    // This was a click on the TR element
                    targetTdOrTr = eventTarget;
                    // rowTarget = true;
                } else if (thisTrElement == eventTargetParent) {
                    // Target parent is the TR, so the actual target is the TD
                    targetTdOrTr = eventTarget;
                } else if (thisTrElement == eventTargetGrandParent) {
                    // Target grand parent is the TR, so the parent is the TD
                    targetTdOrTr = eventTargetParent;
                } else {
                    /*
                     * This is a workaround to make Labels, read only TextFields
                     * and Embedded in a Table clickable (see #2688). It is
                     * really not a fix as it does not work with a custom read
                     * only components (not extending VLabel/VEmbedded).
                     */
                    Widget widget = Util.findWidget(eventTarget, null);
                    if (widget != this) {
                        while (widget != null && widget.getParent() != this) {
                            widget = widget.getParent();
                        }
                        if (widget != null) {
                            // widget is now the closest widget to this row
                            if (widget instanceof VLabel
                                    || widget instanceof VEmbedded
                                    || (widget instanceof VTextField && ((VTextField) widget)
                                            .isReadOnly())) {
                                Element tdElement = eventTargetParent;
                                while (DOM.getParent(tdElement) != thisTrElement) {
                                    tdElement = DOM.getParent(tdElement);
                                }
                                targetTdOrTr = tdElement;
                            }
                        }
                    }
                }

                return targetTdOrTr;
            }

            public void showContextMenu(Event event) {
                if (enabled && actionKeys != null) {
                    int left = event.getClientX();
                    int top = event.getClientY();
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
                removeKeyFromSelectedRange(rowKey);
            }

            /**
             * Is called when a user clicks an item when holding SHIFT key down.
             * This will select a new range from the last cell clicked
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
                int startKey;
                if (selectionRangeStart != null) {
                    startKey = Integer.valueOf(selectionRangeStart.getKey());
                } else {
                    startKey = Integer.valueOf(focusedRow.getKey());
                }
                int endKey = rowKey;
                if (endKey < startKey) {
                    // Swap keys if in the wrong order
                    startKey ^= endKey;
                    endKey ^= startKey;
                    startKey ^= endKey;
                }

                // Deselect previous items if so desired
                if (deselectPrevious) {
                    deselectAll();
                }

                // Select the range (not including this row)
                VScrollTableRow startRow = getRenderedRowByKey(String
                        .valueOf(startKey));
                VScrollTableRow endRow = getRenderedRowByKey(String
                        .valueOf(endKey));

                // If start row is null then we have a multipage selection from
                // above
                if (startRow == null) {
                    startRow = (VScrollTableRow) scrollBody.iterator().next();
                    setRowFocus(endRow);
                }

                if (endRow == null) {
                    setRowFocus(startRow);
                }

                Iterator<Widget> rows = scrollBody.iterator();
                boolean startSelection = false;
                while (rows.hasNext()) {
                    VScrollTableRow row = (VScrollTableRow) rows.next();
                    if (row == startRow || startSelection) {
                        startSelection = true;
                        if (!row.isSelected()) {
                            row.toggleSelection();
                        }
                        selectedRowKeys.add(row.getKey());
                    }

                    if (row == endRow && row != null) {
                        startSelection = false;
                    }
                }

                // Add range
                if (startRow != endRow) {
                    selectedRowRanges.add(new SelectionRange(startKey, endKey));
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
                    final TreeAction a = new TreeAction(this, String
                            .valueOf(rowKey), actionKey);
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
    }

    /**
     * Deselects all items
     */
    public void deselectAll() {
        final Object[] keys = selectedRowKeys.toArray();
        for (int i = 0; i < keys.length; i++) {
            final VScrollTableRow row = getRenderedRowByKey((String) keys[i]);
            if (row != null && row.isSelected()) {
                row.toggleSelection();
                removeKeyFromSelectedRange(Integer.parseInt(row.getKey()));
            }
        }
        // still ensure all selects are removed from (not necessary rendered)
        selectedRowKeys.clear();
        selectedRowRanges.clear();
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

        int rowHeight = (int) scrollBody.getRowHeight();
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

            if (!rendering) {
                // readjust undefined width columns
                lazyAdjustColumnWidths.cancel();
                lazyAdjustColumnWidths.schedule(LAZY_COLUMN_ADJUST_TIMEOUT);
            }

        } else {
            super.setWidth("");
        }

        if (!isFocusable()) {
            scrollBodyPanel.getElement().setTabIndex(-1);
        } else {
            scrollBodyPanel.getElement().setTabIndex(0);
        }
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

            int totalUndefinedNaturaWidths = usedMinimumWidth
                    - totalExplicitColumnsWidths;

            // we have some space that can be divided optimally
            HeaderCell hCell;
            colIndex = 0;
            headCells = tHead.iterator();
            while (headCells.hasNext()) {
                hCell = (HeaderCell) headCells.next();
                if (!hCell.isDefinedWidth()) {
                    int w = hCell.getNaturalColumnWidth(colIndex);
                    int newSpace;
                    if (expandRatioDivider > 0) {
                        // divide excess space by expand ratios
                        newSpace = (int) (w + extraSpace
                                * hCell.getExpandRatio() / expandRatioDivider);
                    } else {
                        if (totalUndefinedNaturaWidths != 0) {
                            // divide relatively to natural column widths
                            newSpace = w + extraSpace * w
                                    / totalUndefinedNaturaWidths;
                        } else {
                            newSpace = w;
                        }
                    }
                    setColWidth(colIndex, newSpace, false);
                }
                colIndex++;
            }
            if ((height == null || "".equals(height))
                    && totalRows == pageLength) {
                // fix body height (may vary if lazy loading is offhorizontal
                // scrollbar appears/disappears)
                int bodyHeight = scrollBody.getRequiredHeight();
                boolean needsSpaceForHorizontalSrollbar = (availW < usedMinimumWidth);
                if (needsSpaceForHorizontalSrollbar) {
                    bodyHeight += Util.getNativeScrollbarSize();
                }
                int heightBefore = getOffsetHeight();
                scrollBodyPanel.setHeight(bodyHeight + "px");
                if (heightBefore != getOffsetHeight()) {
                    Util.notifyParentOfSizeChange(VScrollTable.this, false);
                }
            }
            scrollBody.reLayoutComponents();
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    Util.runWebkitOverflowAutoFix(scrollBodyPanel.getElement());
                }
            });
        }
    };

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

        if (!isFocusable()) {
            scrollBodyPanel.getElement().setTabIndex(-1);
        } else {
            scrollBodyPanel.getElement().setTabIndex(0);
        }
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
                    DeferredCommand.addCommand(new Command() {
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
        String s = uidl.getStringAttribute("caption");
        if (uidl.hasAttribute("icon")) {
            s = "<img src=\""
                    + client
                            .translateVaadinUri(uidl.getStringAttribute("icon"))
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
            DeferredCommand.addCommand(new Command() {
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
                dropDetails.dropLocation = DDUtil.getVerticalDropLocation(row
                        .getElement(), drag.getCurrentGwtEvent().getClientY(),
                        0.2);
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
                    if (row != null) {
                        String stylename = ROWSTYLEBASE
                                + lastEmphasized.dropLocation.toString()
                                        .toLowerCase();
                        VScrollTableRow.setStyleName(row.getElement(),
                                stylename, false);
                    }
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
                    if (row != null) {
                        String stylename = ROWSTYLEBASE
                                + details.dropLocation.toString().toLowerCase();
                        VScrollTableRow.setStyleName(row.getElement(),
                                stylename, true);
                    }
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

            // Trying to set focus on already focused row
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
        if (keycode == KeyCodes.KEY_TAB) {
            // Do not handle tab key
            return false;
        }

        // Down navigation
        if (selectMode == SELECT_MODE_NONE && keycode == getNavigationDownKey()) {
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition()
                    + scrollingVelocity);
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
                    .getScrollPosition()
                    - scrollingVelocity);
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
                    .getHorizontalScrollPosition()
                    - scrollingVelocity);
            return true;

        } else if (keycode == getNavigationRightKey()) {
            // Right navigation
            scrollBodyPanel.setHorizontalScrollPosition(scrollBodyPanel
                    .getHorizontalScrollPosition()
                    + scrollingVelocity);
            return true;
        }

        // Select navigation
        if (selectMode > SELECT_MODE_NONE
                && keycode == getNavigationSelectKey()) {
            if (selectMode == SELECT_MODE_SINGLE) {
                boolean wasSelected = focusedRow.isSelected();
                deselectAll();
                if (!wasSelected || !nullSelectionAllowed) {
                    focusedRow.toggleSelection();
                }
            } else {
                focusedRow.toggleSelection();
            }

            sendSelectedRows();
            return true;
        }

        // Page Down navigation
        if (keycode == getNavigationPageDownKey()) {
            int rowHeight = (int) scrollBody.getRowHeight();
            int offset = pageLength * rowHeight - rowHeight;
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition()
                    + offset);
            if (selectMode > SELECT_MODE_NONE) {
                if (!moveFocusDown(pageLength - 2)) {
                    final int lastRendered = scrollBody.getLastRendered();
                    if (lastRendered == totalRows - 1) {
                        selectLastRenderedRow(false);
                    } else {
                        selectLastItemInNextRender = true;
                    }
                } else {
                    selectFocusedRow(false, false);
                    sendSelectedRows();
                }
            }
            return true;
        }

        // Page Up navigation
        if (keycode == getNavigationPageUpKey()) {
            int rowHeight = (int) scrollBody.getRowHeight();
            int offset = pageLength * rowHeight - rowHeight;
            scrollBodyPanel.setScrollPosition(scrollBodyPanel
                    .getScrollPosition()
                    - offset);
            if (selectMode > SELECT_MODE_NONE) {
                if (!moveFocusUp(pageLength - 2)) {
                    final int firstRendered = scrollBody.getFirstRendered();
                    if (firstRendered == 0) {
                        selectFirstRenderedRow(false);
                    } else {
                        selectFirstItemInNextRender = true;
                    }
                } else {
                    selectFocusedRow(false, false);
                    sendSelectedRows();
                }
            }
            return true;
        }

        // Goto start navigation
        if (keycode == getNavigationStartKey()) {
            if (selectMode > SELECT_MODE_NONE) {
                final int firstRendered = scrollBody.getFirstRendered();
                boolean focusOnly = ctrl;
                if (firstRendered == 0) {
                    selectFirstRenderedRow(focusOnly);
                } else if (focusOnly) {
                    focusFirstItemInNextRender = true;
                } else {
                    selectFirstItemInNextRender = true;
                }
            }
            scrollBodyPanel.setScrollPosition(0);
            return true;
        }

        // Goto end navigation
        if (keycode == getNavigationEndKey()) {
            if (selectMode > SELECT_MODE_NONE) {
                final int lastRendered = scrollBody.getLastRendered();
                boolean focusOnly = ctrl;
                if (lastRendered == totalRows - 1) {
                    selectLastRenderedRow(focusOnly);
                } else if (focusOnly) {
                    focusLastItemInNextRender = true;
                } else {
                    selectLastItemInNextRender = true;
                }
            }
            scrollBodyPanel.setScrollPosition(scrollBody.getOffsetHeight());
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google
     * .gwt.event.dom.client.KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event) {
        if (hasFocus) {
            if (handleNavigation(event.getNativeEvent().getKeyCode(), event
                    .isControlKeyDown()
                    || event.isMetaKeyDown(), event.isShiftKeyDown())) {
                event.preventDefault();
            }

            // Start the velocityTimer
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        if (hasFocus) {
            if (handleNavigation(event.getNativeEvent().getKeyCode(), event
                    .isControlKeyDown()
                    || event.isMetaKeyDown(), event.isShiftKeyDown())) {
                event.preventDefault();
            }

            // Start the velocityTimer
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
            scrollBodyPanel.addStyleName("focused");
            hasFocus = true;

            // Focus a row if no row is in focus
            if (focusedRow == null) {
                setRowFocus((VScrollTableRow) scrollBody.iterator().next());
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
        scrollBodyPanel.removeStyleName("focused");
        hasFocus = false;

        // Unfocus any row
        setRowFocus(null);
    }

    /**
     * Removes a key from a range if the key is found in a selected range
     * 
     * @param key
     *            The key to remove
     */
    private void removeKeyFromSelectedRange(int key) {
        for (SelectionRange range : selectedRowRanges) {
            if (range.inRange(key)) {
                int start = range.getStartKey();
                int end = range.getEndKey();

                if (start < key && end > key) {
                    selectedRowRanges.add(new SelectionRange(start, key - 1));
                    selectedRowRanges.add(new SelectionRange(key + 1, end));
                } else if (start == key && start < end) {
                    selectedRowRanges.add(new SelectionRange(start + 1, end));
                } else if (end == key && start < end) {
                    selectedRowRanges.add(new SelectionRange(start, end - 1));
                }

                selectedRowRanges.remove(range);

                break;
            }
        }
    }

    /**
     * Can the Table be focused?
     * 
     * @return True if the table can be focused, else false
     */
    public boolean isFocusable() {
        if (scrollBody != null) {
            boolean hasVerticalScrollbars = scrollBody.getOffsetHeight() > scrollBodyPanel
                    .getOffsetHeight();
            boolean hasHorizontalScrollbars = scrollBody.getOffsetWidth() > scrollBodyPanel
                    .getOffsetWidth();
            return !(!hasHorizontalScrollbars && !hasVerticalScrollbars && selectMode == SELECT_MODE_NONE);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.Focusable#focus()
     */
    public void focus() {
        scrollBodyPanel.focus();
    }
}
