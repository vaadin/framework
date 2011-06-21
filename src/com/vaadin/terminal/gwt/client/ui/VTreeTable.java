/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.terminal.gwt.client.ui.VTreeTable.VTreeTableScrollBody.VTreeTableRow;

public class VTreeTable extends VScrollTable {

    public static final String ATTRIBUTE_HIERARCHY_COLUMN_INDEX = "hci";
    private boolean collapseRequest;
    private boolean selectionPending;
    private int colIndexOfHierarchy;
    private String collapsedRowKey;
    private VTreeTableScrollBody scrollBody;

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        FocusableScrollPanel widget = null;
        int scrollPosition = 0;
        if (collapseRequest) {
            widget = (FocusableScrollPanel) getWidget(1);
            scrollPosition = widget.getScrollPosition();
        }
        colIndexOfHierarchy = uidl
                .hasAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) ? uidl
                .getIntAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) : 0;
        super.updateFromUIDL(uidl, client);
        if (collapseRequest) {
            if (collapsedRowKey != null && scrollBody != null) {
                VScrollTableRow row = getRenderedRowByKey(collapsedRowKey);
                if (row != null) {
                    setRowFocus(row);
                    focus();
                }
            }

            int scrollPosition2 = widget.getScrollPosition();
            if (scrollPosition != scrollPosition2) {
                VConsole.log("TT scrollpos from " + scrollPosition + " to "
                        + scrollPosition2);
                widget.setScrollPosition(scrollPosition);
            }
            collapseRequest = false;
        }
        if (uidl.hasAttribute("focusedRow")) {
            // TODO figure out if the row needs to focused at all

            // scrolled to parent by the server, focusedRow is probably the sam
            // as the first row in view port
        }
    }

    @Override
    protected VScrollTableBody createScrollBody() {
        scrollBody = new VTreeTableScrollBody();
        return scrollBody;
    }

    class VTreeTableScrollBody extends VScrollTable.VScrollTableBody {
        private int identWidth = -1;

        VTreeTableScrollBody() {
            super();
        }

        @Override
        protected VScrollTableRow createRow(UIDL uidl, char[] aligns2) {
            return new VTreeTableRow(uidl, aligns2);
        }

        class VTreeTableRow extends
                VScrollTable.VScrollTableBody.VScrollTableRow {

            private boolean isTreeCellAdded = false;
            private SpanElement treeSpacer;
            private boolean open;
            private int depth;
            private boolean canHaveChildren;
            private Widget widgetInHierarchyColumn;

            public VTreeTableRow(UIDL uidl, char[] aligns2) {
                super(uidl, aligns2);
            }

            @Override
            public void addCell(UIDL rowUidl, String text, char align,
                    String style, boolean textIsHTML, boolean isSorted) {
                super.addCell(rowUidl, text, align, style, textIsHTML, isSorted);

                addTreeSpacer(rowUidl);
            }

            private boolean addTreeSpacer(UIDL rowUidl) {
                if (cellShowsTreeHierarchy(getElement().getChildCount() - 1)) {
                    Element container = (Element) getElement().getLastChild()
                            .getFirstChild();

                    if (rowUidl.hasAttribute("icon")) {
                        // icons are in first content cell in TreeTable
                        ImageElement icon = Document.get().createImageElement();
                        icon.setClassName("v-icon");
                        icon.setAlt("icon");
                        icon.setSrc(client.translateVaadinUri(rowUidl
                                .getStringAttribute("icon")));
                        container.insertFirst(icon);
                    }

                    String classname = "v-treetable-treespacer";
                    if (rowUidl.getBooleanAttribute("ca")) {
                        canHaveChildren = true;
                        open = rowUidl.getBooleanAttribute("open");
                        classname += open ? " v-treetable-node-open"
                                : " v-treetable-node-closed";
                    }

                    treeSpacer = Document.get().createSpanElement();

                    treeSpacer.setClassName(classname);
                    container.insertFirst(treeSpacer);
                    depth = rowUidl.hasAttribute("depth") ? rowUidl
                            .getIntAttribute("depth") : 0;
                    setIdent();
                    isTreeCellAdded = true;
                    return true;
                }
                return false;
            }

            private boolean cellShowsTreeHierarchy(int curColIndex) {
                if (isTreeCellAdded) {
                    return false;
                }
                return curColIndex == colIndexOfHierarchy
                        + (showRowHeaders ? 1 : 0);
            }

            @Override
            public void onBrowserEvent(Event event) {
                if (event.getEventTarget().cast() == treeSpacer
                        && treeSpacer.getClassName().contains("node")) {
                    if (event.getTypeInt() == Event.ONMOUSEUP) {
                        sendToggleCollapsedUpdate(getKey());
                    }
                    return;
                }
                super.onBrowserEvent(event);
            }

            @Override
            public void addCell(UIDL rowUidl, Widget w, char align,
                    String style, boolean isSorted) {
                super.addCell(rowUidl, w, align, style, isSorted);
                if (addTreeSpacer(rowUidl)) {
                    widgetInHierarchyColumn = w;
                }

            }

            private void setIdent() {
                if (getIdentWidth() > 0 && depth != 0) {
                    treeSpacer.getStyle().setWidth(
                            (depth + 1) * getIdentWidth(), Unit.PX);
                }
            }

            @Override
            protected void onAttach() {
                super.onAttach();
                if (getIdentWidth() < 0) {
                    detectIdent(this);
                }
            }

            @Override
            public RenderSpace getAllocatedSpace(Widget child) {
                if (widgetInHierarchyColumn == child) {
                    final int hierarchyAndIconWidth = getHierarchyAndIconWidth();
                    final RenderSpace allocatedSpace = super
                            .getAllocatedSpace(child);
                    return new RenderSpace() {
                        @Override
                        public int getWidth() {
                            return allocatedSpace.getWidth()
                                    - hierarchyAndIconWidth;
                        }

                        @Override
                        public int getHeight() {
                            return allocatedSpace.getHeight();
                        }

                    };
                }
                return super.getAllocatedSpace(child);
            }

            private int getHierarchyAndIconWidth() {
                int consumedSpace = treeSpacer.getOffsetWidth();
                if (treeSpacer.getParentElement().getChildCount() > 2) {
                    // icon next to tree spacer
                    consumedSpace += ((com.google.gwt.dom.client.Element) treeSpacer
                            .getNextSibling()).getOffsetWidth();
                }
                return consumedSpace;
            }

        }

        private int getIdentWidth() {
            return identWidth;
        }

        private void detectIdent(VTreeTableRow vTreeTableRow) {
            identWidth = vTreeTableRow.treeSpacer.getOffsetWidth();
            if (identWidth == 0) {
                identWidth = -1;
                return;
            }
            Iterator<Widget> iterator = iterator();
            while (iterator.hasNext()) {
                VTreeTableRow next = (VTreeTableRow) iterator.next();
                next.setIdent();
            }
        }
    }

    /**
     * Icons rendered into first actual column in TreeTable, not to row header
     * cell
     */
    @Override
    protected String buildCaptionHtmlSnippet(UIDL uidl) {
        if (uidl.getTag().equals("column")) {
            return super.buildCaptionHtmlSnippet(uidl);
        } else {
            String s = uidl.getStringAttribute("caption");
            return s;
        }
    }

    @Override
    protected boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        VTreeTableRow focusedRow = (VTreeTableRow) getFocusedRow();
        if (focusedRow != null) {
            if (focusedRow.canHaveChildren
                    && ((keycode == KeyCodes.KEY_RIGHT && !focusedRow.open) || (keycode == KeyCodes.KEY_LEFT && focusedRow.open))) {
                if (!ctrl) {
                    client.updateVariable(paintableId, "selectCollapsed", true,
                            false);
                }
                sendToggleCollapsedUpdate(focusedRow.getKey());
                return true;
            } else if (keycode == KeyCodes.KEY_RIGHT && focusedRow.open) {
                // already expanded, move selection down if next is on a deeper
                // level (is-a-child)
                VTreeTableScrollBody body = (VTreeTableScrollBody) focusedRow
                        .getParent();
                Iterator<Widget> iterator = body.iterator();
                VTreeTableRow next = null;
                while (iterator.hasNext()) {
                    next = (VTreeTableRow) iterator.next();
                    if (next == focusedRow) {
                        next = (VTreeTableRow) iterator.next();
                        break;
                    }
                }
                if (next != null) {
                    if (next.depth > focusedRow.depth) {
                        selectionPending = true;
                        return super.handleNavigation(getNavigationDownKey(),
                                ctrl, shift);
                    }
                } else {
                    // Note, a minor change here for a bit false behavior if
                    // cache rows is disabled + last visible row + no childs for
                    // the node
                    selectionPending = true;
                    return super.handleNavigation(getNavigationDownKey(), ctrl,
                            shift);
                }
            } else if (keycode == KeyCodes.KEY_LEFT) {
                // already collapsed move selection up to parent node
                // do on the server side as the parent is not necessary
                // rendered on the client, could check if parent is visible if
                // a performance issue arises

                client.updateVariable(paintableId, "focusParent",
                        focusedRow.getKey(), true);
                return true;
            }
        }
        return super.handleNavigation(keycode, ctrl, shift);
    }

    private void sendToggleCollapsedUpdate(String rowKey) {
        collapsedRowKey = rowKey;
        collapseRequest = true;
        client.updateVariable(paintableId, "toggleCollapsed", rowKey, true);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONKEYUP && selectionPending) {
            sendSelectedRows();
        }
    }

    @Override
    protected void sendSelectedRows() {
        super.sendSelectedRows();
        selectionPending = false;
    }

    @Override
    protected void reOrderColumn(String columnKey, int newIndex) {
        super.reOrderColumn(columnKey, newIndex);
        // current impl not intelligent enough to survive without visiting the
        // server to redraw content
        client.sendPendingVariableChanges();
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style + " v-treetable");
    }

    @Override
    protected void updateTotalRows(UIDL uidl) {
        // Make sure that initializedAndAttached & al are not reset when the
        // totalrows are updated on expand/collapse requests.
        int newTotalRows = uidl.getIntAttribute("totalrows");
        if (collapseRequest) {
            setTotalRows(newTotalRows);
        } else {
            super.setTotalRows(newTotalRows);
        }
    }
}
