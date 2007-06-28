package com.itmill.toolkit.terminal.gwt.client.ui.scrolltable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
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
	
	private Grid tHead = new Grid(1,1);

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
		if(rowHeaders)
			columnCount++;
		tHead.resizeColumns(columnCount);
			
		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			int colIndex = getColIndexByKey(cid);
			if(colIndex > -1) {
				HeaderCell c = (HeaderCell) tHead.getWidget(0, colIndex);
				if(c != null && c.getColKey().equals(cid)) {
					c.setText(col.getStringAttribute("caption"));
				} else {
					c = new HeaderCell(cid, col.getStringAttribute("caption"));
					tHead.setWidget(0, colIndex, c );
				}
				if(col.hasAttribute("sortable")) {
					c.setSortable(true);
					if(cid.equals(sortColumn))
						c.setSorted(true);
					else
						c.setSorted(false);
				}
			}
		}
		if(rowHeaders) {
			HeaderCell c = (HeaderCell) tHead.getWidget(0, 0);
			if(c == null) {
				tHead.setWidget(0, 0, new HeaderCell("0", ""));
			}
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
		return Integer.parseInt(colKey) - 1 + (rowHeaders ? 1 : 0);
	}
	
	private String getColKeyByIndex(int index) {
		return ((HeaderCell) tHead.getWidget(0, index)).getColKey();
	}

	private void setColWidth(int colIndex, int w) {
		HeaderCell cell = (HeaderCell) tHead.getWidget(0, colIndex);
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
		
		// sync column widths
		initColumnWidths();

		if(height  < 0) {
			bodyContainer.setHeight((tBody.getRowHeight()*pageLength) + "px");
		} else {
			bodyContainer.setHeight(height + "px");
		}

		if(width  < 0) {
			bodyContainer.setWidth((tBody.getOffsetWidth() + getScrollBarWidth() ) + "px");
			headerContainer.setWidth((tBody.getOffsetWidth()) + "px");
		} else {
			bodyContainer.setWidth(width + "px");
			headerContainer.setWidth(width + "px");
		}
		
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
		int cols = tHead.getCellCount(0);
		CellFormatter hf = tHead.getCellFormatter();
			for (int i = 0; i < cols; i++) {
				Element hCell = hf.getElement(0, i);
				int hw = DOM.getIntAttribute(hCell, "offsetWidth");
				int cw = tBody.getColWidth(i);
				int w = (hw > cw ? hw : cw) + IScrollTableBody.CELL_EXTRA_WIDTH;
				setColWidth(i , w);
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
	
	public class HeaderCell extends Widget implements ClickListener {
		
		private static final int DRAG_WIDGET_WIDTH = 2;
		
		private static final int MINIMUM_COL_WIDTH = 20;

		// TODO create a simple single row table widget that has these widgets as cells
		// and change next line to  ...DOM.createTD();
		// Grid is way too overkill for this and it removes on extra div from document structure
		Element td = DOM.createDiv();

		Element captionContainer = DOM.createDiv();
		
		Element dragWidget = DOM.createDiv();
		
		private boolean sortable = false;
		private String cid;
		private boolean dragging;
		
		private int dragStartX;
		private int colIndex;
		private int originalWidth;

		private boolean isResizing;

		private int headerX;

		private boolean moved;


		private HeaderCell(){};
		
		public void setSortable(boolean b) {
			if(b == sortable)
				return;
			sortable = b;
		}
		
		public HeaderCell(String colId, String headerText) {
			this.cid = colId;

			DOM.setStyleAttribute(dragWidget,"display", "block");
			DOM.setStyleAttribute(dragWidget, "width",  DRAG_WIDGET_WIDTH +"px");
			DOM.setStyleAttribute(dragWidget,"height", "20px");
			DOM.setStyleAttribute(dragWidget,"cssFloat", "right");
			DOM.setStyleAttribute(dragWidget, "styleFloat", "right");
			DOM.setStyleAttribute(dragWidget,"background", "brown");
			DOM.setStyleAttribute(dragWidget,"cursor", "e-resize");
			DOM.sinkEvents(dragWidget,Event.MOUSEEVENTS);
			
			setText(headerText);
			
			DOM.appendChild(td, dragWidget);

			
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
		
		/**
		 * Listens clicks to headers text. This changes sorting.
		 */
		public void onClick(Widget sender) {
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
			
			if(isResizing || DOM.compare(target, dragWidget)) {
				onResizeEvent(event);
			} else {
				handleCaptionEvent(event);
			}
			

			super.onBrowserEvent(event);
		}

		private void handleCaptionEvent(Event event) {
			switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
				dragging = true;
				moved = false;
				DOM.setCapture(getElement());
				System.out.println("Started column reordering");
				this.headerX = tHead.getAbsoluteLeft();
				
				DOM.eventPreventDefault(event);
				break;
			case Event.ONMOUSEUP:
				dragging = false;
				System.out.println("Stopped column reordering");
				DOM.releaseCapture(getElement());
				break;
			case Event.ONMOUSEMOVE:
				if (dragging) {
					moved = true;
					System.out.print("Dragging column, optimal index...");
					int x = DOM.eventGetClientX(event);
					int slotX = headerX;
					int closestIndex = 0;
					int closestDistance = -1;
					int start = 0;
					if(rowHeaders) {
						start++;
						slotX += getColWidth(getColKeyByIndex(0));
					}
					for(int i = start ; i < columnWidths.size(); i++ ) {
						String colKey = getColKeyByIndex(i);
						int dist = Math.abs(x - slotX);
						if(closestDistance == -1 || dist < closestDistance) {
							closestDistance = dist;
							closestIndex = i;
						}
						slotX += getColWidth(colKey);
					}
					focusSlot(closestIndex);
					System.out.println(closestIndex);

				}
				break;
			default:
				break;
			}
		}
		
		private void focusSlot(int slot) {
			// TODO consider some workaround for this
			removeSlotFocus();
			DOM.setStyleAttribute(tHead.getCellFormatter().getElement(0, slot), "borderLeft", "2px solid black");
		}

		private void removeSlotFocus() {
			for(int i = 0; i < tHead.getColumnCount();i++)
				DOM.setStyleAttribute(tHead.getCellFormatter().getElement(0, i), "borderLeft", "none");
		}

		private int getClosestColumnIndex(int x) {
			return 1;
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
	
}
