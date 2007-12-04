/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * TODO make this work (just an early prototype). We may want to have paging
 * style table which will be much lighter than IScrollTable is.
 */
public class ITablePaging extends Composite implements Table, Paintable,
        ClickListener {

    private final Grid tBody = new Grid();
    private final Button nextPage = new Button("&gt;");
    private final Button prevPage = new Button("&lt;");
    private final Button firstPage = new Button("&lt;&lt;");
    private final Button lastPage = new Button("&gt;&gt;");

    private int pageLength = 15;

    private boolean rowHeaders = false;

    private final Map columnOrder = new HashMap();

    private ApplicationConnection client;
    private String id;

    private boolean immediate = false;

    private int selectMode = Table.SELECT_MODE_NONE;

    private final Vector selectedRowKeys = new Vector();

    private int totalRows;

    private final HashMap columnWidths = new HashMap();

    private final HashMap visibleColumns = new HashMap();

    private int rows;

    private int firstRow;
    private boolean sortAscending = true;
    private final HorizontalPanel pager;

    public HashMap rowKeysToTableRows = new HashMap();

    public ITablePaging() {

        tBody.setStyleName("itable-tbody");

        final VerticalPanel panel = new VerticalPanel();

        pager = new HorizontalPanel();
        pager.add(firstPage);
        firstPage.addClickListener(this);
        pager.add(prevPage);
        prevPage.addClickListener(this);
        pager.add(nextPage);
        nextPage.addClickListener(this);
        pager.add(lastPage);
        lastPage.addClickListener(this);

        panel.add(pager);
        panel.add(tBody);

        initWidget(panel);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        this.client = client;
        id = uidl.getStringAttribute("id");
        immediate = uidl.getBooleanAttribute("immediate");
        totalRows = uidl.getIntAttribute("totalrows");
        pageLength = uidl.getIntAttribute("pagelength");
        firstRow = uidl.getIntAttribute("firstrow");
        rows = uidl.getIntAttribute("rows");

        if (uidl.hasAttribute("selectmode")) {
            if (uidl.getStringAttribute("selectmode").equals("multi")) {
                selectMode = Table.SELECT_MODE_MULTI;
            } else {
                selectMode = Table.SELECT_MODE_SINGLE;
            }

            if (uidl.hasAttribute("selected")) {
                final Set selectedKeys = uidl
                        .getStringArrayVariableAsSet("selected");
                selectedRowKeys.clear();
                for (final Iterator it = selectedKeys.iterator(); it.hasNext();) {
                    selectedRowKeys.add(it.next());
                }
            }
        }

        if (uidl.hasVariable("sortascending")) {
            sortAscending = uidl.getBooleanVariable("sortascending");
        }

        if (uidl.hasAttribute("rowheaders")) {
            rowHeaders = true;
        }

        UIDL rowData = null;
        UIDL visibleColumns = null;
        for (final Iterator it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL c = (UIDL) it.next();
            if (c.getTag().equals("rows")) {
                rowData = c;
            } else if (c.getTag().equals("actions")) {
                updateActionMap(c);
            } else if (c.getTag().equals("visiblecolumns")) {
                visibleColumns = c;
            }
        }
        tBody.resize(rows + 1, uidl.getIntAttribute("cols")
                + (rowHeaders ? 1 : 0));
        updateHeader(visibleColumns);
        updateBody(rowData);

        updatePager();
    }

    private void updateHeader(UIDL c) {
        final Iterator it = c.getChildIterator();
        visibleColumns.clear();
        int colIndex = (rowHeaders ? 1 : 0);
        while (it.hasNext()) {
            final UIDL col = (UIDL) it.next();
            final String cid = col.getStringAttribute("cid");
            if (!col.hasAttribute("collapsed")) {
                tBody.setWidget(0, colIndex, new HeaderCell(cid, col
                        .getStringAttribute("caption")));

            }
            colIndex++;
        }
    }

    private void updateActionMap(UIDL c) {
        // TODO Auto-generated method stub

    }

    /**
     * Updates row data from uidl. UpdateFromUIDL delegates updating tBody to
     * this method.
     * 
     * Updates may be to different part of tBody, depending on update type. It
     * can be initial row data, scroll up, scroll down...
     * 
     * @param uidl
     *                which contains row data
     */
    private void updateBody(UIDL uidl) {
        final Iterator it = uidl.getChildIterator();

        int curRowIndex = 1;
        while (it.hasNext()) {
            final UIDL rowUidl = (UIDL) it.next();
            final TableRow row = new TableRow(curRowIndex, String
                    .valueOf(rowUidl.getIntAttribute("key")), rowUidl
                    .hasAttribute("selected"));
            int colIndex = 0;
            if (rowHeaders) {
                tBody.setWidget(curRowIndex, colIndex, new BodyCell(row,
                        rowUidl.getStringAttribute("caption")));
                colIndex++;
            }
            final Iterator cells = rowUidl.getChildIterator();
            while (cells.hasNext()) {
                final Object cell = cells.next();
                if (cell instanceof String) {
                    tBody.setWidget(curRowIndex, colIndex, new BodyCell(row,
                            (String) cell));
                } else {
                    final Widget cellContent = client.getWidget((UIDL) cell);
                    final BodyCell bodyCell = new BodyCell(row);
                    bodyCell.setWidget(cellContent);
                    tBody.setWidget(curRowIndex, colIndex, bodyCell);
                }
                colIndex++;
            }
            curRowIndex++;
        }
    }

    private void updatePager() {
        if (pageLength == 0) {
            pager.setVisible(false);
            return;
        }
        if (isFirstPage()) {
            firstPage.setEnabled(false);
            prevPage.setEnabled(false);
        } else {
            firstPage.setEnabled(true);
            prevPage.setEnabled(true);
        }
        if (hasNextPage()) {
            nextPage.setEnabled(true);
            lastPage.setEnabled(true);
        } else {
            nextPage.setEnabled(false);
            lastPage.setEnabled(false);

        }
    }

    private boolean hasNextPage() {
        if (firstRow + rows + 1 > totalRows) {
            return false;
        }
        return true;
    }

    private boolean isFirstPage() {
        if (firstRow == 0) {
            return true;
        }
        return false;
    }

    public void onClick(Widget sender) {
        if (sender instanceof Button) {
            if (sender == firstPage) {
                client.updateVariable(id, "firstvisible", 0, true);
            } else if (sender == nextPage) {
                client.updateVariable(id, "firstvisible",
                        firstRow + pageLength, true);
            } else if (sender == prevPage) {
                int newFirst = firstRow - pageLength;
                if (newFirst < 0) {
                    newFirst = 0;
                }
                client.updateVariable(id, "firstvisible", newFirst, true);
            } else if (sender == lastPage) {
                client.updateVariable(id, "firstvisible", totalRows
                        - pageLength, true);
            }
        }
        if (sender instanceof HeaderCell) {
            final HeaderCell hCell = (HeaderCell) sender;
            client.updateVariable(id, "sortcolumn", hCell.getCid(), false);
            client.updateVariable(id, "sortascending", (sortAscending ? false
                    : true), true);
        }
    }

    private class HeaderCell extends HTML {

        private String cid;

        public String getCid() {
            return cid;
        }

        public void setCid(String pid) {
            cid = pid;
        }

        HeaderCell(String pid, String caption) {
            super();
            cid = pid;
            addClickListener(ITablePaging.this);
            setText(caption);
            // TODO remove debug color
            DOM.setStyleAttribute(getElement(), "color", "brown");
            DOM.setStyleAttribute(getElement(), "font-weight", "bold");
        }
    }

    /**
     * Abstraction of table cell content. In needs to know on which row it is in
     * case of context click.
     * 
     * @author mattitahvonen
     */
    public class BodyCell extends SimplePanel {
        private final TableRow row;

        public BodyCell(TableRow row) {
            super();
            sinkEvents(Event.BUTTON_LEFT | Event.BUTTON_RIGHT);
            this.row = row;
        }

        public BodyCell(TableRow row2, String textContent) {
            super();
            sinkEvents(Event.BUTTON_LEFT | Event.BUTTON_RIGHT);
            row = row2;
            setWidget(new Label(textContent));
        }

        public void onBrowserEvent(Event event) {
            System.out.println("CEll event: " + event.toString());
            switch (DOM.eventGetType(event)) {
            case Event.BUTTON_RIGHT:
                row.showContextMenu(event);
                Window.alert("context menu un-implemented");
                DOM.eventCancelBubble(event, true);
                break;
            case Event.BUTTON_LEFT:
                if (selectMode > Table.SELECT_MODE_NONE) {
                    row.toggleSelected();
                }
                break;
            default:
                break;
            }
            super.onBrowserEvent(event);
        }
    }

    private class TableRow {

        private final String key;
        private final int rowIndex;
        private boolean selected = false;

        public TableRow(int rowIndex, String rowKey, boolean selected) {
            rowKeysToTableRows.put(rowKey, this);
            this.rowIndex = rowIndex;
            key = rowKey;
            setSelected(selected);
        }

        /**
         * This method is used to set row status. Does not change value on
         * server.
         * 
         * @param selected
         */
        public void setSelected(boolean sel) {
            selected = sel;
            if (selected) {
                selectedRowKeys.add(key);
                DOM.setStyleAttribute(tBody.getRowFormatter().getElement(
                        rowIndex), "background", "yellow");

            } else {
                selectedRowKeys.remove(key);
                DOM.setStyleAttribute(tBody.getRowFormatter().getElement(
                        rowIndex), "background", "transparent");
            }
        }

        public void setContextMenuOptions(HashMap options) {

        }

        /**
         * Toggles rows select state. Also updates state to server according to
         * tables immediate flag.
         * 
         */
        public void toggleSelected() {
            if (selected) {
                setSelected(false);
            } else {
                if (selectMode == Table.SELECT_MODE_SINGLE) {
                    deselectAll();
                }
                setSelected(true);
            }
            client.updateVariable(id, "selected", selectedRowKeys.toArray(),
                    immediate);
        }

        /**
         * Shows context menu for this row.
         * 
         * @param event
         *                Event which triggered context menu. Correct place for
         *                context menu can be determined with it.
         */
        public void showContextMenu(Event event) {
            System.out.println("TODO: Show context menu");
        }
    }

    public void deselectAll() {
        final Object[] keys = selectedRowKeys.toArray();
        for (int i = 0; i < keys.length; i++) {
            final TableRow tableRow = (TableRow) rowKeysToTableRows
                    .get(keys[i]);
            if (tableRow != null) {
                tableRow.setSelected(false);
            }
        }
        // still ensure all selects are removed from
        selectedRowKeys.clear();
    }

    public void add(Widget w) {
        // TODO Auto-generated method stub

    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    public Iterator iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(Widget w) {
        // TODO Auto-generated method stub
        return false;
    }
}
