package com.itmill.toolkit.terminal.gwt.client.ui.scrolltable;

import java.util.HashMap;
import java.util.Iterator;
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
import com.itmill.toolkit.terminal.gwt.client.ui.ITable;

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
			tBody = new IScrollTableBody(client);
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
	
}
