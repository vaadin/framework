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

public class ITablePaging extends Composite implements ITable, Paintable, ClickListener {
	
	private Grid tBody = new Grid();
	private Button nextPage = new Button("&gt;");
	private Button prevPage = new Button("&lt;");
	private Button firstPage = new Button("&lt;&lt;");
	private Button lastPage = new Button("&gt;&gt;");

	
	private int pageLength = 15;
	
	private boolean rowHeaders = false;
	
	private Map columnOrder = new HashMap();
	
	private ApplicationConnection client;
	private String id;
	
	private boolean immediate = false;
	
	private int selectMode = ITable.SELECT_MODE_NONE;
	
	private Vector selectedRowKeys = new Vector();
	
	private int totalRows;

	private HashMap columnWidths = new HashMap();
	
	private HashMap visibleColumns = new HashMap();
	
	private int rows;

	private int firstRow;
	private boolean sortAscending = true;
	private HorizontalPanel pager;
	
	public HashMap rowKeysToTableRows = new HashMap();
	
	public ITablePaging() {

		tBody.setStyleName("itable-tbody");
		
		VerticalPanel panel = new VerticalPanel();
		
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
		if (client.updateComponent(this, uidl, true))
			return;

		this.client = client;
		this.id = uidl.getStringAttribute("id");
		this.immediate = uidl.getBooleanAttribute("immediate");
		this.totalRows = uidl.getIntAttribute("totalrows");
		this.pageLength = uidl.getIntAttribute("pagelength");
		this.firstRow = uidl.getIntAttribute("firstrow");
		this.rows = uidl.getIntAttribute("rows");
		
		if(uidl.hasAttribute("selectmode")) {
			if(uidl.getStringAttribute("selectmode").equals("multi"))
				selectMode = ITable.SELECT_MODE_MULTI;
			else
				selectMode = ITable.SELECT_MODE_SINGLE;
			
			if(uidl.hasAttribute("selected")) {
				Set selectedKeys = uidl.getStringArrayVariableAsSet("selected");
				selectedRowKeys.clear();
				for(Iterator it = selectedKeys.iterator();it.hasNext();)
					selectedRowKeys.add((String) it.next());
			}
		}
		
		if(uidl.hasVariable("sortascending"))
			this.sortAscending = uidl.getBooleanVariable("sortascending");

		if(uidl.hasAttribute("rowheaders"))
			rowHeaders = true;
		
		UIDL rowData = null;
		UIDL visibleColumns = null;
		for(Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL c = (UIDL) it.next();
			if(c.getTag().equals("rows"))
				rowData = c;
			else if(c.getTag().equals("actions"))
				updateActionMap(c);
			else if(c.getTag().equals("visiblecolumns"))
				visibleColumns = c;
		}
		tBody.resize(rows+1, uidl.getIntAttribute("cols") + (rowHeaders ? 1 : 0 ));
		updateHeader(visibleColumns);
		updateBody(rowData);
		
		updatePager();
	}
	
	private void updateHeader(UIDL c) {
		Iterator it = c.getChildIterator();
		visibleColumns.clear();
		int colIndex = (rowHeaders ? 1 : 0);
		while(it.hasNext()) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			if(!col.hasAttribute("collapsed")) {
				tBody.setWidget(0, colIndex, 
						new HeaderCell(cid, 
								col.getStringAttribute("caption")));

			}
			colIndex++;
		}
	}

	private void updateActionMap(UIDL c) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Updates row data from uidl. UpdateFromUIDL delegates updating 
	 * tBody to this method.
	 * 
	 * Updates may be to different part of tBody, depending on update type.
	 * It can be initial row data, scroll up, scroll down...
	 * 
	 * @param uidl which contains row data
	 */
	private void updateBody(UIDL uidl) {
		Iterator it = uidl.getChildIterator();
		
		int curRowIndex = 1;
		while(it.hasNext()){
			UIDL rowUidl = (UIDL) it.next();
			TableRow row = new TableRow(
					curRowIndex, 
					String.valueOf(rowUidl.getIntAttribute("key")),
					rowUidl.hasAttribute("selected"));
			int colIndex = 0;
			if(rowHeaders) {
				tBody.setWidget(curRowIndex, colIndex, 
						new BodyCell(row, rowUidl.getStringAttribute("caption")));
				colIndex++;
			}
			Iterator cells = rowUidl.getChildIterator();
			while(cells.hasNext()) {
				Object cell = cells.next();
				if (cell instanceof String) {
					tBody.setWidget(curRowIndex, colIndex, 
							new BodyCell(row, (String) cell));
				} else {
				 	Widget cellContent = client.getWidget((UIDL) cell);
				 	BodyCell bodyCell = new BodyCell(row);
				 	bodyCell.setWidget(cellContent);
					tBody.setWidget(curRowIndex, colIndex, bodyCell);
				}
				colIndex++;
			}
			curRowIndex++;
		}
	}
	
	private void updatePager() {
		if(pageLength == 0) {
			pager.setVisible(false);
			return;
		}
		if(isFirstPage()) {
			firstPage.setEnabled(false);
			prevPage.setEnabled(false);
		} else {
			firstPage.setEnabled(true);
			prevPage.setEnabled(true);
		}
		if(hasNextPage()) {
			nextPage.setEnabled(true);
			lastPage.setEnabled(true);
		} else {
			nextPage.setEnabled(false);
			lastPage.setEnabled(false);

		}
	}

	private boolean hasNextPage() {
		if(firstRow + rows + 1 > totalRows)
			return false;
		return true;
	}

	private boolean isFirstPage() {
		if(firstRow == 0)
			return true;
		return false;
	}

	public void onClick(Widget sender) {
		if (sender instanceof Button) {
			if(sender == firstPage)
				client.updateVariable(this.id, "firstvisible", 0, true);
			else if(sender == nextPage)
				client.updateVariable(this.id, "firstvisible", firstRow + pageLength, true);
			else if(sender == prevPage) {
				int newFirst = firstRow - pageLength;
				if(newFirst < 0)
					newFirst = 0;
				client.updateVariable(this.id, "firstvisible", newFirst, true);
			} else if (sender == lastPage) {
				client.updateVariable(this.id, "firstvisible", totalRows - pageLength, true);
			}
		}
		if (sender instanceof HeaderCell) {
			HeaderCell hCell = (HeaderCell) sender;
			client.updateVariable(this.id, "sortcolumn", hCell.getCid(), false);
			client.updateVariable(this.id, "sortascending", ( sortAscending ? false : true ), true);
		}
	}

	private class HeaderCell extends HTML {
		
		private String cid;

		public String getCid() {
			return cid;
		}

		public void setCid(String pid) {
			this.cid = pid;
		}

		HeaderCell(String pid, String caption) {
			super();
			this.cid = pid;
			addClickListener(ITablePaging.this);
			setText(caption);
			// TODO remove debug color
			DOM.setStyleAttribute(getElement(), "color", "brown");
			DOM.setStyleAttribute(getElement(), "font-weight", "bold");
		}
	}
	
	/**
	 * Abstraction of table cell content. In needs to know on which row it
	 * is in case of context click.
	 * 
	 * @author mattitahvonen
	 */
	public class BodyCell extends SimplePanel {
		private TableRow row;
		
		public BodyCell(TableRow row) {
			super();
			this.sinkEvents(Event.BUTTON_LEFT | Event.BUTTON_RIGHT);
			this.row = row;
		}
		
		public BodyCell(TableRow row2, String textContent) {
			super();
			this.sinkEvents(Event.BUTTON_LEFT | Event.BUTTON_RIGHT);
			this.row = row2;
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
				if(ITablePaging.this.selectMode > ITable.SELECT_MODE_NONE)
					row.toggleSelected();
				break;
			default:
				break;
			}
			super.onBrowserEvent(event);
		}
	}
	
	private class TableRow {
		
		private String key;
		private int rowIndex;
		private boolean selected = false;

		public TableRow(int rowIndex, String rowKey, boolean selected) {
			ITablePaging.this.rowKeysToTableRows.put(rowKey, this);
			this.rowIndex = rowIndex;
			this.key = rowKey;
			setSelected(selected);
		}

		/**
		 * This method is used to set row status. Does not change value on server.
		 * @param selected
		 */
		public void setSelected(boolean sel) {
			this.selected = sel;
			if(selected) {
				selectedRowKeys.add(key);
				DOM.setStyleAttribute(
						ITablePaging.this.tBody.getRowFormatter().getElement(rowIndex),
						"background", "yellow");
				
			} else {
				selectedRowKeys.remove(key);
				DOM.setStyleAttribute(
						ITablePaging.this.tBody.getRowFormatter().getElement(rowIndex),
						"background", "transparent");
			}
		}

		
		public void setContextMenuOptions(HashMap options) {
			
		}
		
		/**
		 * Toggles rows select state. Also updates state to server according to tables immediate flag.
		 *
		 */
		public void toggleSelected() {
			if(selected) {
				setSelected(false);
			} else {
				if(ITablePaging.this.selectMode == ITable.SELECT_MODE_SINGLE) {
					ITablePaging.this.deselectAll();
				}
				setSelected(true);
			}
			client.updateVariable(id, "selected", selectedRowKeys.toArray(), immediate);
		}
		
		/**
		 * Shows context menu for this row.
		 * 
		 * @param event Event which triggered context menu. Correct place for context menu can be determined with it.
		 */
		public void showContextMenu(Event event) {
			System.out.println("TODO: Show context menu");
		}
	}

	public void deselectAll() {
		Object[] keys = selectedRowKeys.toArray();
		for (int i = 0; i < keys.length; i++) {
			TableRow tableRow = (TableRow) rowKeysToTableRows.get(keys[i]);
			if(tableRow != null)
				tableRow.setSelected(false);
		}
		// still ensure all selects are removed from 
		selectedRowKeys.clear();
	}
}
