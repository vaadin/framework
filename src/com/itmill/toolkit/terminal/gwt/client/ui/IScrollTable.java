package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable.IScrollTableBody.IScrollTableRow;

public class IScrollTable extends Composite implements Paintable, ITable, ScrollListener {
	
	/**
	 *  multiple of pagelenght which component will 
	 *  cache when requesting more rows 
	 */
	private static final double CACHE_RATE = 2;
	/** 
	 * fraction of pageLenght which can be scrolled without 
	 * making new request 
	 */
	private static final double CACHE_REACT_RATE = 1;
	
	private int firstRowInViewPort = 0;
	private int pageLength = 15;
	
	private boolean rowHeaders = false;
	
	private String[] columnOrder;
	
	private Client client;
	private String id;
	
	private boolean immediate;

	private int selectMode = ITable.SELECT_MODE_NONE;

	private Vector selectedRowKeys = new Vector();
	
	private boolean initializedAndAttached = false;
	
	private TableHead tHead = new TableHead();

	private ScrollPanel bodyContainer = new ScrollPanel();
	
	private ScrollPanel headerContainer = new ScrollPanel();
	
	private boolean colWidthsInitialized = false;
	private int totalRows;
	private HashMap columnWidths = new HashMap();
	
	private RowRequestHandler rowRequestHandler;
	private IScrollTableBody tBody;
	private int width = -1;
	private int height = -1;
	private int firstvisible = 0;
	private boolean sortAscending;
	private String sortColumn;
	private boolean columnReordering;
	
	public IScrollTable() {
		headerContainer.setStyleName("iscrolltable-header");
		headerContainer.add(tHead);
		DOM.setStyleAttribute(headerContainer.getElement(), "overflow", "hidden");
		
		bodyContainer.addScrollListener(this);
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(headerContainer);
		panel.add(bodyContainer);
		
		rowRequestHandler = new RowRequestHandler();
		
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		if (client.updateComponent(this, uidl, true))
			return;

		this.client = client;
		this.id = uidl.getStringAttribute("id");
		this.immediate = uidl.getBooleanAttribute("immediate");
		this.totalRows = uidl.getIntAttribute("totalrows");
		this.pageLength = uidl.getIntAttribute("pagelength");
		if(pageLength == 0)
			pageLength = totalRows;
		this.firstvisible = uidl.hasVariable("firstvisible") ? uidl.getIntVariable("firstvisible") : 0;
		if(uidl.hasAttribute("rowheaders"))
			rowHeaders = true;
		if(uidl.hasAttribute("width"))
			width = uidl.getIntAttribute("width");
		if(uidl.hasAttribute("height"))
			width = uidl.getIntAttribute("height");
		
		if(uidl.hasVariable("sortascending")) {
			this.sortAscending = uidl.getBooleanVariable("sortascending");
			this.sortColumn = uidl.getStringVariable("sortcolumn");
		}
		
		Set selectedKeys = uidl.getStringArrayVariableAsSet("selected");
		selectedRowKeys.clear();
		for(Iterator it = selectedKeys.iterator();it.hasNext();)
			selectedRowKeys.add((String) it.next());

		
		if(uidl.hasAttribute("selectmode")) {
			if(uidl.getStringAttribute("selectmode").equals("multi"))
				selectMode = ITable.SELECT_MODE_MULTI;
			else
				selectMode = ITable.SELECT_MODE_SINGLE;
		}
		
		if(uidl.hasVariable("columnorder")) {
			this.columnReordering = true;
			this.columnOrder = uidl.getStringArrayVariable("columnorder");
		}
		
		UIDL columnInfo = null;
		UIDL rowData = null;
		for(Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL c = (UIDL) it.next();
			if(c.getTag().equals("cols"))
				columnInfo = c;
			else if(c.getTag().equals("rows"))
				rowData = c;
			else if(c.getTag().equals("actions"))
				updateActionMap(c);
			else if(c.getTag().equals("visiblecolumns"))
				;
		}
		updateHeader(columnInfo);
		
		if(initializedAndAttached) {
			updateBody(rowData, uidl.getIntAttribute("firstrow"),uidl.getIntAttribute("rows"));
		} else {
			getTBody().renderInitialRows(rowData, 
					uidl.getIntAttribute("firstrow"), 
					uidl.getIntAttribute("rows"), 
					totalRows);
			bodyContainer.add(tBody);
			initializedAndAttached = true;
		}
	}
	
	private IScrollTableBody getTBody() {
		if(tBody == null || totalRows != tBody.getTotalRows()) {
			if(tBody != null)
				tBody.removeFromParent();
			tBody = new IScrollTableBody();
		}
		return tBody;
	}

	private void updateActionMap(UIDL c) {
		// TODO Auto-generated method stub
		
	}

	private void updateHeader(UIDL uidl) {
		if(uidl == null)
			return;
		int columnCount = uidl.getChidlCount();
		int colIndex = 0;
		if(rowHeaders) {
			columnCount++;
			HeaderCell c = (HeaderCell) tHead.getHeaderCell(0);
			if(c == null) {
				tHead.setHeaderCell(0, new HeaderCell("0", ""));
			}
			colIndex++;
		}
			
		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			HeaderCell c = (HeaderCell) tHead.getHeaderCell(colIndex);
			if(c != null && c.getColKey().equals(cid)) {
				c.setText(col.getStringAttribute("caption"));
			} else {
				c = new HeaderCell(cid, col.getStringAttribute("caption"));
				tHead.setHeaderCell(colIndex, c);
			}
			if(col.hasAttribute("sortable")) {
				c.setSortable(true);
				if(cid.equals(sortColumn))
					c.setSorted(true);
				else
					c.setSorted(false);
			}
			colIndex++;
		}
	}
	
	/**
	 * @param uidl which contains row data
	 * @param firstRow first row in data set
	 * @param reqRows amount of rows in data set
	 */
	private void updateBody(UIDL uidl, int firstRow, int reqRows) {
 		if(uidl == null || reqRows < 1)
			return;
 		
 		tBody.renderRows(uidl, firstRow, reqRows);
 		
 		int optimalFirstRow = (int) (firstRowInViewPort - pageLength*CACHE_RATE);
 		while(tBody.getFirstRendered() < optimalFirstRow) {
// 			client.console.log("removing row from start");
 			tBody.unlinkRow(true);
 		}
 		int optimalLastRow = (int) (firstRowInViewPort + pageLength + pageLength*CACHE_RATE);
 		while(tBody.getLastRendered() > optimalLastRow) {
// 			client.console.log("removing row from the end");
 			tBody.unlinkRow(false);
 		}
		
	}
	
	
	private int getColIndexByKey(String colKey) {
		// return 0 if asked for rowHeaders
		if("0".equals(colKey))
			return 0;
		int index = -1;
		for (int i = 0; i < columnOrder.length; i++) {
			if(columnOrder[i].equals(colKey)) {
				index = i;
				break;
			}
		}
		if(rowHeaders)
			index++;
		return index;
	}
	
	private String getColKeyByIndex(int index) {
		return tHead.getHeaderCell(index).getColKey();
	}

	private void setColWidth(int colIndex, int w) {
		HeaderCell cell = tHead.getHeaderCell(colIndex);
		cell.setWidth(w);
		tBody.setColWidth(colIndex, w);
		String cid = cell.getColKey();;
		columnWidths.put(cid,new Integer(w));
	}
	
	private int getColWidth(String colKey) {
		return ( (Integer) this.columnWidths.get(colKey)).intValue();
	}
	
	private IScrollTableRow getRenderedRowByKey(String key) {
		Iterator it = tBody.iterator();
		IScrollTableRow r = null;
		while(it.hasNext()) {
			r = (IScrollTableRow) it.next();
			if(r.getKey().equals(key))
				return r;
		}
		return null;
	}
	
	private int getRenderedRowCount() {
		return tBody.getLastRendered()-tBody.getFirstRendered();
	}
	
	private void reOrderColumn(String columnKey, int newIndex) {
		
		int oldIndex = getColIndexByKey(columnKey);
		
		// Change header order
		tHead.moveCell(oldIndex, newIndex);

		// Change body order
		tBody.moveCol(oldIndex, newIndex);
		
		// build new columnOrder and update it to server
		
		String[] newOrder = new String[columnOrder.length];
		
		Iterator hCells = tHead.iterator();

		if(rowHeaders)
			hCells.next();
		int index = 0;
		while(hCells.hasNext()) {
			newOrder[index++] = ((HeaderCell) hCells.next()).getColKey();
		}
		columnOrder = newOrder;
		client.updateVariable(id, "columnorder", newOrder, false);
		
	}

	/**
	 * This method has logick which rows needs to be requested from
	 * server when user scrolls
	 *
	 */
	public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
		if(!initializedAndAttached)
			return;
		
		rowRequestHandler.cancel();
		
		// fix headers horizontal scrolling
		headerContainer.setHorizontalScrollPosition(scrollLeft);

		firstRowInViewPort = (int) Math.ceil( scrollTop / (double) tBody.getRowHeight() );
		client.console.log("At scrolltop: " + scrollTop + " At row " + firstRowInViewPort);
		
		int postLimit = (int) (firstRowInViewPort + pageLength + pageLength*CACHE_REACT_RATE);
		if(postLimit > totalRows -1 )
			postLimit = totalRows - 1;
		int preLimit = (int) (firstRowInViewPort - pageLength*CACHE_REACT_RATE);
		if(preLimit < 0)
			preLimit = 0;
		int lastRendered = tBody.getLastRendered();
		int firstRendered = tBody.getFirstRendered();
		if( postLimit <= lastRendered && preLimit >= firstRendered ) {
			client.updateVariable(this.id, "firstvisible", firstRowInViewPort, false);
			return; // scrolled withing "non-react area"
		}
		
		if(firstRowInViewPort - pageLength*CACHE_RATE > lastRendered ||
				firstRowInViewPort + pageLength + pageLength*CACHE_RATE < firstRendered ) {
			// need a totally new set
			client.console.log("Table: need a totally new set");
			rowRequestHandler.setReqFirstRow((int) (firstRowInViewPort - pageLength*CACHE_RATE));
			rowRequestHandler.setReqRows((int) (2*CACHE_RATE*pageLength + pageLength));
			rowRequestHandler.deferRowFetch();
			return;
		}
		if(preLimit < firstRendered ) {
			// need some rows to the beginning of the rendered area
			client.console.log("Table: need some rows to the beginning of the rendered area");
			rowRequestHandler.setReqFirstRow((int) (firstRowInViewPort - pageLength*CACHE_RATE));
			rowRequestHandler.setReqRows(firstRendered - rowRequestHandler.getReqFirstRow());
			rowRequestHandler.deferRowFetch();

			return;
		}
		if(postLimit > lastRendered) {
			// need some rows to the end of the rendered area
			client.console.log("need some rows to the end of the rendered area");
			rowRequestHandler.setReqFirstRow(lastRendered + 1);
			rowRequestHandler.setReqRows((int) ((firstRowInViewPort + pageLength + pageLength*CACHE_RATE) - lastRendered));
			rowRequestHandler.deferRowFetch();
		}
		
	}
	
	
	
	protected void onAttach() {
		
		super.onAttach();
		int bodyWidth = tBody.getOffsetWidth();
		
		// sync column widths
		initColumnWidths();

		if(height  < 0) {
			bodyContainer.setHeight((tBody.getRowHeight()*pageLength) + "px");
		} else {
			bodyContainer.setHeight(height + "px");
		}

		if(width  < 0) {
			bodyWidth = tBody.getOffsetWidth();
			bodyContainer.setWidth((tBody.getOffsetWidth() + getScrollBarWidth() ) + "px");
			headerContainer.setWidth((tBody.getOffsetWidth()) + "px");
		} else {
			bodyContainer.setWidth(width + "px");
			headerContainer.setWidth(width + "px");
		}
		
		tHead.disableBrowserIntelligence();
		
		if(firstvisible > 0)
			bodyContainer.setScrollPosition(firstvisible*tBody.getRowHeight());
		
		DeferredCommand.add(new Command() {
			public void execute() {
				if(totalRows - 1 > tBody.getLastRendered()) {
					// fetch cache rows
					rowRequestHandler.setReqFirstRow(tBody.getLastRendered()+1);
					rowRequestHandler.setReqRows((int) (pageLength*CACHE_RATE));
					rowRequestHandler.deferRowFetch();
				}
			}
		});

		
	}

	/**
	 * Run when receices its initial content. Syncs headers and bodys
	 * "natural widths and saves the values.
	*/
	private void initColumnWidths() {
		Iterator headCells = tHead.iterator();
		int i = 0;
		while(headCells.hasNext()) {
			Element hCell = ((HeaderCell) headCells.next()).getElement();
			int hw = DOM.getIntAttribute(hCell, "offsetWidth");
			int cw = tBody.getColWidth(i);
			int w = (hw > cw ? hw : cw) + IScrollTableBody.CELL_EXTRA_WIDTH;
			setColWidth(i , w);
			i++;
		}
	}

	private int getScrollBarWidth() {
		// TODO Auto-generated method stub
		return 30;
	}

	private class RowRequestHandler extends Timer {
		
		private int reqFirstRow = 0;
		private int reqRows = 0;
		
		public void deferRowFetch() {
			if(reqRows > 0 && reqFirstRow < totalRows)
				schedule(250);
		}

		public void setReqFirstRow(int reqFirstRow) {
			if(reqFirstRow < 0)
				reqFirstRow = 0;
			else if(reqFirstRow >= totalRows)
				reqFirstRow = totalRows - 1;
			this.reqFirstRow = reqFirstRow;
		}

		public void setReqRows(int reqRows) {
			this.reqRows = reqRows;
		}

		public void run() {
			client.console.log("Getting " + reqRows + " rows from " + reqFirstRow);
			client.updateVariable(id, "firstvisible", firstRowInViewPort, false);
			client.updateVariable(id, "reqfirstrow", reqFirstRow, false);
			client.updateVariable(id, "reqrows", reqRows, true);
		}

		public int getReqFirstRow() {
			return reqFirstRow;
		}

		public int getReqRows() {
			return reqRows;
		}
		
	}
	
	public class HeaderCell extends Widget {
		
		private static final int DRAG_WIDGET_WIDTH = 2;
		
		private static final int MINIMUM_COL_WIDTH = 20;

		Element td = DOM.createTD();

		Element captionContainer = DOM.createDiv();
		
		Element colResizeWidget = DOM.createDiv();
		
		Element floatingCopyOfHeaderCell;
		
		private boolean sortable = false;
		private String cid;
		private boolean dragging;
		
		private int dragStartX;
		private int colIndex;
		private int originalWidth;

		private boolean isResizing;

		private int headerX;

		private boolean moved;

		private int closestSlot;


		private HeaderCell(){};
		
		public void setSortable(boolean b) {
			if(b == sortable)
				return;
			sortable = b;
		}
		
		public HeaderCell(String colId, String headerText) {
			this.cid = colId;

			DOM.setStyleAttribute(colResizeWidget,"display", "block");
			DOM.setStyleAttribute(colResizeWidget, "width",  DRAG_WIDGET_WIDTH +"px");
			DOM.setStyleAttribute(colResizeWidget,"height", "20px");
			DOM.setStyleAttribute(colResizeWidget,"cssFloat", "right");
			DOM.setStyleAttribute(colResizeWidget, "styleFloat", "right");
			DOM.setStyleAttribute(colResizeWidget,"background", "brown");
			DOM.setStyleAttribute(colResizeWidget,"cursor", "e-resize");
			DOM.sinkEvents(colResizeWidget,Event.MOUSEEVENTS);
			
			setText(headerText);
			
			DOM.appendChild(td, colResizeWidget);

			
			DOM.setStyleAttribute(captionContainer, "cssFloat", "right");
			DOM.setStyleAttribute(captionContainer, "styleFloat", "right");
			DOM.setStyleAttribute(captionContainer, "overflow", "hidden");
			DOM.setStyleAttribute(captionContainer, "white-space", "nowrap");
			DOM.setStyleAttribute(captionContainer, "display", "inline");
			DOM.sinkEvents(captionContainer, Event.MOUSEEVENTS);

			DOM.appendChild(td, captionContainer);
			
			setElement(td);
		}
		
		public void setWidth(int w) {
			DOM.setStyleAttribute(captionContainer, "width", (w - DRAG_WIDGET_WIDTH - 4) + "px");
			setWidth(w + "px");
		}
		
		public void setText(String headerText) {
			DOM.setInnerHTML(captionContainer, headerText);
		}
		public String getColKey() {
			return cid;
		}
		
		private void setSorted(boolean sorted) {
			if(sorted) {
				if(sortAscending)
					this.setStyleName("headerCellAsc");
				else
					this.setStyleName("headerCellDesc");
			} else {
				this.setStyleName("headerCell");
			}
		}

		/**
		 * Handle column reordering.
		 */
		public void onBrowserEvent(Event event) {
			
			Element target = DOM.eventGetTarget(event);
			
			if(isResizing || DOM.compare(target, colResizeWidget)) {
				onResizeEvent(event);
			} else {
				handleCaptionEvent(event);
			}
			

			super.onBrowserEvent(event);
		}

		
		private void createFloatingCopy() {
			floatingCopyOfHeaderCell = DOM.createDiv();
			DOM.setInnerHTML(floatingCopyOfHeaderCell, DOM.getInnerHTML(td));
			floatingCopyOfHeaderCell = DOM.getChild(floatingCopyOfHeaderCell, 1);
			// TODO isolate non-standard css attribute (filter)
			// TODO move styles to css file
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "position", "absolute");
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "background", "#000000");
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "color", "#ffffff");
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "opacity", "0.5");
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "filter", "alpha(opacity=100)");
			updateFloatingCopysPosition(DOM.getAbsoluteLeft(td), DOM.getAbsoluteTop(td));
			DOM.appendChild(IScrollTable.this.getElement(), floatingCopyOfHeaderCell);
		}
		
		private void updateFloatingCopysPosition(int x, int y) {
			x -= DOM.getIntAttribute(floatingCopyOfHeaderCell, "offsetWidth")/2;
			DOM.setStyleAttribute(floatingCopyOfHeaderCell, "left", x + "px");
			if(y > 0)
				DOM.setStyleAttribute(floatingCopyOfHeaderCell, "top", (y + 7) + "px");
		}
		
		private void hideFloatingCopy() {
			DOM.removeChild(IScrollTable.this.getElement(), floatingCopyOfHeaderCell);
			floatingCopyOfHeaderCell = null;
		}
		
		private void handleCaptionEvent(Event event) {
			switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
				dragging = true;
				moved = false;
		        colIndex = getColIndexByKey(cid);
				DOM.setCapture(getElement());
				
				this.headerX = tHead.getAbsoluteLeft();
				
				DOM.eventPreventDefault(event);
				break;
			case Event.ONMOUSEUP:
				dragging = false;
				DOM.releaseCapture(getElement());

				if(!moved) {
					// mouse event was a click to header -> sort column
					if(sortable) {
						if(sortColumn.equals(cid)) {
							// just toggle order
							client.updateVariable(id, "sortascending", !sortAscending, false);
						} else {
							// set table scrolled by this column
							client.updateVariable(id, "sortcolumn", cid, false);
						}
						// get also cache columns at the same request
						bodyContainer.setScrollPosition(0);
						firstvisible = 0;
						rowRequestHandler.setReqFirstRow(0);
						rowRequestHandler.setReqRows((int) (2*pageLength*CACHE_RATE + pageLength));
						rowRequestHandler.deferRowFetch();
					}
					break;
				}
				System.out.println("Stopped column reordering");
				hideFloatingCopy();
				tHead.removeSlotFocus();
				if(closestSlot != colIndex &&  closestSlot != (colIndex + 1) ) {
					if(closestSlot > colIndex)
						reOrderColumn(cid, closestSlot - 1);
					else
						reOrderColumn(cid, closestSlot);
				}
				break;
			case Event.ONMOUSEMOVE:
				if (dragging) {
					System.out.print("Dragging column, optimal index...");
					if(!moved) {
						createFloatingCopy();
						moved = true;
					}
					int x = DOM.eventGetClientX(event);
					int slotX = headerX;
					closestSlot = colIndex;
					int closestDistance = -1;
					int start = 0;
					if(rowHeaders) {
						start++;
					}
					for(int i = start; i <= columnWidths.size() ; i++ ) {
						if(i > 0) {
							String colKey = getColKeyByIndex(i-1);
							slotX += getColWidth(colKey);
						}
						int dist = Math.abs(x - slotX);
						if(closestDistance == -1 || dist < closestDistance) {
							closestDistance = dist;
							closestSlot = i;
						}
					}
					tHead.focusSlot(closestSlot);
					
					updateFloatingCopysPosition(x, -1);
					System.out.println(closestSlot);
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
			        originalWidth = IScrollTable.this.tBody.getColWidth(colIndex);
			        DOM.eventPreventDefault(event);
		      		break;
		      	case Event.ONMOUSEUP:
		      		isResizing = false;
				    DOM.releaseCapture(getElement());
		      		break;
		      	case Event.ONMOUSEMOVE:
				    if (isResizing) {
				        int deltaX = DOM.eventGetClientX(event) - dragStartX ;
				        if(deltaX == 0)
				        	return;
				        
				        int newWidth = originalWidth + deltaX;
				        if(newWidth < MINIMUM_COL_WIDTH)
				        	newWidth = MINIMUM_COL_WIDTH;
				        setColWidth(colIndex, newWidth);
				      }
		      		break;
		      	default:
		      		break;
		    }
		}


	}
	
	public class TableHead extends Panel {

		private static final int WRAPPER_WIDTH = 9000;
		
		Vector cells = new Vector();
		
		Element div = DOM.createDiv();
		Element table = DOM.createTable();
		Element tBody = DOM.createTBody();
		Element tr = DOM.createTR();

		private int focusedSlot = -1;
		
		public TableHead() {
			DOM.appendChild(table, tBody);
			DOM.appendChild(tBody, tr);
			DOM.appendChild(div, table);
			setElement(div);
		}
		
		public void disableBrowserIntelligence() {
			DOM.setStyleAttribute(div, "width", WRAPPER_WIDTH +"px");
		}
		
		public void setHeaderCell(int index, HeaderCell cell) {
			if(index < cells.size()) {
				// replace
				// TODO remove old correctly
				// insert to right slot
			} else if( index == cells.size()) {
				//append
				adopt(cell, tr);
				cells.add(cell);
			} else {
				throw new RuntimeException("Header cells must be appended in order");
			}
		}
		
		public HeaderCell getHeaderCell(int index) {
			if(index < cells.size())
				return (HeaderCell) cells.get(index);
			else 
				return null;
		}
		
		public void moveCell(int oldIndex, int newIndex) {
			HeaderCell hCell = getHeaderCell(oldIndex);
			Element cell = hCell.getElement();

			cells.remove(oldIndex);
			DOM.removeChild(tr, cell);

			DOM.insertChild(tr, cell, newIndex);
			cells.insertElementAt(hCell, newIndex);
		}
		
		public Iterator iterator() {
			return cells.iterator();
		}

		public boolean remove(Widget w) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private void focusSlot(int index) {
			removeSlotFocus();
			if(index > 0)
				DOM.setStyleAttribute(DOM.getChild(tr, index - 1), "borderRight", "2px solid black");
			else
				DOM.setStyleAttribute(DOM.getChild(tr, index), "borderLeft", "2px solid black");
			focusedSlot = index;
		}

		private void removeSlotFocus() {
			if(focusedSlot < 0)
				return;
			if(focusedSlot == 0)
				DOM.setStyleAttribute(DOM.getChild(tr, focusedSlot), "borderLeft", "none");
			else if( focusedSlot > 0)
				DOM.setStyleAttribute(DOM.getChild(tr, focusedSlot - 1), "borderRight", "none");
			focusedSlot = -1;
		}

		
	}
	
	/**
	 * This Panel can only contain IScrollTAbleRow type of 
	 * widgets. This "simulates" very large table, keeping 
	 * spacers which take room of unrendered rows.
	 * 
	 * @author mattitahvonen
	 *
	 */
	public class IScrollTableBody extends Panel {

		public static final int CELL_EXTRA_WIDTH = 20;

		public static final int DEFAULT_ROW_HEIGHT = 25;
		
		private int rowHeight = -1;
		
		private List renderedRows = new Vector();
		
		private boolean initDone = false;
		
		private int totalRows;

		Element preSpacer = DOM.createDiv();
		Element postSpacer = DOM.createDiv();
		
		Element container = DOM.createDiv();
		
		Element tBody  = DOM.createTBody();
		Element table = DOM.createTable();

		private int firstRendered;

		private int lastRendered;

		IScrollTableBody() {
			
			constructDOM();
			
			setElement(container);
			
		}
		
		private void constructDOM() {
			DOM.setAttribute(table, "className", "iscrolltable-table");
			DOM.setAttribute(preSpacer, "className", "iscrolltable-rowspacer");
			DOM.setAttribute(postSpacer, "className", "iscrolltable-rowspacer");

			DOM.appendChild(table, tBody);
			DOM.appendChild(container, preSpacer);
			DOM.appendChild(container, table);
			DOM.appendChild(container, postSpacer);
			
		}
		
		
		public void renderInitialRows(UIDL rowData, int firstIndex, int rows, int totalRows) {
			this.totalRows = totalRows;
			this.firstRendered = firstIndex;
			this.lastRendered = firstIndex + rows - 1 ;
			Iterator it = rowData.getChildIterator();
			while(it.hasNext()) {
				IScrollTableRow row = new IScrollTableRow((UIDL) it.next());
				addRow(row);
			}
			if(isAttached())
				fixSpacers();
		}
		
		public void renderRows(UIDL rowData, int firstIndex, int rows) {
			Iterator it = rowData.getChildIterator();
			if(firstIndex == lastRendered + 1) {
				while(it.hasNext()) {
					IScrollTableRow row = createRow((UIDL) it.next());
					addRow(row);
					lastRendered++;
				}
				fixSpacers();
			} else if(firstIndex + rows == firstRendered) {
				IScrollTableRow[] rowArray = new IScrollTableRow[rows];
				int i = rows;
				while(it.hasNext()) {
					i--;
					rowArray[i] = createRow((UIDL) it.next());
				}
				for(i = 0 ; i < rows; i++) {
					addRowBeforeFirstRendered(rowArray[i]);
					firstRendered--;
				}
//			} else if (firstIndex > lastRendered || firstIndex + rows < firstRendered) {
			} else if (true) {
				// complitely new set of rows
				// create one row before truncating row
				IScrollTableRow row = createRow((UIDL) it.next());
				while(lastRendered + 1 > firstRendered)
					unlinkRow(false);
				firstRendered = firstIndex;
				lastRendered = firstIndex - 1 ;
				fixSpacers();
				addRow(row);
				lastRendered++;
				while(it.hasNext()) {
					addRow(createRow((UIDL) it.next()));
					lastRendered++;
				}
				fixSpacers();
			} else {
				// sorted or column reordering changed
				client.console.log("Bad update" + firstIndex + "/"+ rows);
			}
		}
		
		/**
		 * This mehtod is used to instantiate new rows for this table.
		 * It automatically sets correct widths to rows cells and assigns 
		 * correct client reference for child widgets.
		 * 
		 * This method can be called only after table has been initialized
		 * 
		 * @param uidl
		 * @param client2
		 */
		private IScrollTableRow createRow(UIDL uidl) {
			IScrollTableRow row = new IScrollTableRow(uidl);
			int cells = DOM.getChildCount(row.getElement());
			for(int i = 0; i < cells; i++) {
				Element cell = DOM.getChild(row.getElement(), i);
				int w = getColWidth(i);
				DOM.setStyleAttribute(cell, "width", w + "px");
				DOM.setStyleAttribute(DOM.getFirstChild(cell), "width", w + "px");
			}
			return row;
		}

		private void addRowBeforeFirstRendered(IScrollTableRow row) {
			DOM.insertChild(tBody, row.getElement(), 0);
			adopt(row, null);
			renderedRows.add(0, row);
		}
		
		private void addRow(IScrollTableRow row) {
			DOM.appendChild(tBody, row.getElement());
			adopt(row, null);
			renderedRows.add(row);
		}
		
		public Iterator iterator() {
			return renderedRows.iterator();
		}
		
		public void unlinkRow(boolean fromBeginning) {
			if(lastRendered - firstRendered < 0)
				return;
			int index;
			if(fromBeginning) {
				index = 0;
				firstRendered++;
			} else {
				index = renderedRows.size() - 1;
				lastRendered--;
			}
			IScrollTableRow toBeRemoved = (IScrollTableRow) renderedRows.get(index);
			this.disown(toBeRemoved);
			renderedRows.remove(index);
			fixSpacers();
		}

		public boolean remove(Widget w) {
			throw new UnsupportedOperationException();
		}
		
		protected void onAttach() {
			super.onAttach();
			fixSpacers();
			// fix container blocks height to avoid "bouncing" when scrolling
			DOM.setStyleAttribute(container, "height", totalRows*getRowHeight() + "px");
		}
		
		private void fixSpacers() {
			DOM.setStyleAttribute(preSpacer, "height", getRowHeight()*firstRendered + "px");
			DOM.setStyleAttribute(postSpacer, "height", getRowHeight()*(totalRows - 1  - lastRendered) + "px");
		}

		public int getTotalRows() {
			return totalRows;
		}
		
		public int getRowHeight() {
			if(initDone)
				return rowHeight;
			else {
				if(DOM.getChildCount(tBody) > 0) {
					rowHeight = DOM.getIntAttribute(tBody, "offsetHeight")/DOM.getChildCount(tBody);
				} else {
					return DEFAULT_ROW_HEIGHT;
				}
				initDone = true;
				return rowHeight;
			}
		}

		public int getColWidth(int i) {
			Element e = DOM.getChild(DOM.getChild(tBody, 0), i);
			return DOM.getIntAttribute(e, "offsetWidth");
		}

		public void setColWidth(int colIndex, int w) {
			int rows = DOM.getChildCount(tBody);
			for(int i = 0; i < rows; i++) {
				Element cell = DOM.getChild(DOM.getChild(tBody, i), colIndex);
				DOM.setStyleAttribute(cell, "width", w + "px");
				DOM.setStyleAttribute(DOM.getFirstChild(cell), "width", w + "px");
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
			Iterator rows = iterator();
			while(rows.hasNext()) {
				IScrollTableRow row = (IScrollTableRow) rows.next();
				
				Element td = DOM.getChild(row.getElement(), oldIndex);
				DOM.removeChild(row.getElement(), td);

				DOM.insertChild(row.getElement(), td, newIndex);
				
			}

		}

		public class IScrollTableRow extends Panel {
			
			Vector childWidgets = new Vector();
			private boolean selected = false;
			private int rowKey;
			
			private IScrollTableRow(int rowKey) {
				this.rowKey = rowKey;
				this.selected = selected;
				setElement(DOM.createElement("tr"));
				DOM.sinkEvents(getElement(), Event.BUTTON_RIGHT | Event.ONCLICK);
				setStyleName("iscrolltable-row");
			}
			
			public String getKey() {
				return String.valueOf(rowKey);
			}

			public IScrollTableRow(UIDL uidl) {
				this(uidl.getIntAttribute("key"));
				if(uidl.hasAttribute("caption"))
					addCell(uidl.getStringAttribute("caption"));
				Iterator cells = uidl.getChildIterator();
				while(cells.hasNext()) {
					Object cell = cells.next();
					if (cell instanceof String) {
						addCell(cell.toString());
					} else {
					 	Widget cellContent = client.getWidget((UIDL) cell);
					 	(( Paintable) cellContent).updateFromUIDL((UIDL) cell, client);
					}
				}
				if(uidl.hasAttribute("selected"))
					toggleSelection();
			}
			
			public void addCell(String text) {
				// String only content is optimized by not using Label widget
				Element td = DOM.createTD();
				Element container = DOM.createDiv();
				DOM.setAttribute(container, "className", "iscrolltable-cellContent");
				DOM.setInnerHTML(container, text);
				DOM.appendChild(td, container);
				DOM.appendChild(getElement(), td);
			}
			
			public void addCell(Widget w) {
				Element td = DOM.createTD();
				Element container = DOM.createDiv();
				DOM.setAttribute(container, "className", "iscrolltable-cellContent");
				DOM.appendChild(td, container);
				DOM.appendChild(getElement(), td);
				adopt(w, container);
				childWidgets.add(w);
			}

			public Iterator iterator() {
				return childWidgets.iterator();
			}

			public boolean remove(Widget w) {
				// TODO Auto-generated method stub
				return false;
			}

			public void onBrowserEvent(Event event) {
				switch (DOM.eventGetType(event)) {
				case Event.BUTTON_RIGHT:
					// TODO
					System.out.println("Context menu");
					break;
					
				case Event.ONCLICK:
					System.out.println("Row click");
					if(selectMode > ITable.SELECT_MODE_NONE) {
						toggleSelection();
						client.updateVariable(id, "selected", selectedRowKeys.toArray(), immediate);
					}
					break;

				default:
					break;
				}
				super.onBrowserEvent(event);
			}

			public boolean isSelected() {
				return selected;
			}

			private void toggleSelection() {
				selected = !selected;
				if(selected) {
					if(selectMode == ITable.SELECT_MODE_SINGLE)
						IScrollTable.this.deselectAll();
					selectedRowKeys.add(String.valueOf(rowKey));
					
					setStyleName("iscrolltable-selRow");
				} else {
					selectedRowKeys.remove(String.valueOf(rowKey));
					setStyleName("iscrolltable-row");
				}
			}
			
		}

	}

	public void deselectAll() {
		Object[] keys = selectedRowKeys.toArray();
		for (int i = 0; i < keys.length; i++) {
			IScrollTableRow row = getRenderedRowByKey((String) keys[i]);
			if(row != null && row.isSelected())
				row.toggleSelection();
		}
		// still ensure all selects are removed from (not necessary rendered)
		selectedRowKeys.clear();
		
	}
}
