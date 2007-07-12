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
	private String paintableId;
	
	private boolean immediate;

	private int selectMode = ITable.SELECT_MODE_NONE;

	private Vector selectedRowKeys = new Vector();
	
	private boolean initializedAndAttached = false;
	
	private TableHead tHead = new TableHead();

	private ScrollPanel bodyContainer = new ScrollPanel();
	
	private boolean colWidthsInitialized = false;
	private int totalRows;
	
	private Set collapsedColumns;
	
	private RowRequestHandler rowRequestHandler;
	private IScrollTableBody tBody;
	private int width = -1;
	private int height = -1;
	private int firstvisible = 0;
	private boolean sortAscending;
	private String sortColumn;
	private boolean columnReordering;
	
	/**
	 * This map contains captions and icon urls for 
	 * actions like:
	 *   * "33_c" -> "Edit"
	 *   * "33_i" -> "http://dom.com/edit.png"
	 */
	private HashMap actionMap = new HashMap();
	private String[] visibleColOrder;
	
	public IScrollTable() {
		// TODO move headerContainer and column selector into TableHead
		
		bodyContainer.addScrollListener(this);
		
		VerticalPanel panel = new VerticalPanel();
		
		
		panel.add(tHead);
		panel.add(bodyContainer);
		
		rowRequestHandler = new RowRequestHandler();
		
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		if (client.updateComponent(this, uidl, true))
			return;

		this.client = client;
		this.paintableId = uidl.getStringAttribute("id");
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
		
		if(uidl.hasVariable("collapsedcolumns")) {
			tHead.setColumnCollapsingAllowed(true);
			this.collapsedColumns = uidl.getStringArrayVariableAsSet("collapsedcolumns");
		} else {
			tHead.setColumnCollapsingAllowed(false);
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
				updateVisibleColumns(c);
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
	
	private void updateVisibleColumns(UIDL c) {
		if(!initializedAndAttached) {
			// add empty cell for col headers
			tHead.addAvailableCell(new HeaderCell(
					"0",
					""));
			Iterator it = c.getChildIterator();
			while(it.hasNext()) {
				UIDL col = (UIDL) it.next();
					tHead.addAvailableCell(
						new HeaderCell(
								col.getStringAttribute("cid"),
								col.getStringAttribute("caption")
							)
						);
			}
		} else {
			// update existing cells
			
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
		Iterator it = c.getChildIterator();
		while(it.hasNext()) {
			UIDL action = (UIDL) it.next();
			String key = action.getStringAttribute("key");
			String caption = action.getStringAttribute("caption");
			actionMap.put(key + "_c", caption);
			if(action.hasAttribute("icon")) {
				// TODO need some uri handling ??
				actionMap.put(key + "_i", action.getStringAttribute("icon"));
			}
		}
		
	}
	
	public String getActionCaption(String actionKey) {
		return (String) actionMap.get(actionKey + "_c");
	}
	
	public String getActionIcon(String actionKey) {
		return (String) actionMap.get(actionKey + "_i");
	}
	

	private void updateHeader(UIDL uidl) {
		if(uidl == null)
			return;

		int colIndex = 0;
		int visibleCols = uidl.getChidlCount();
		if(rowHeaders) {
			tHead.enableColumn("0",colIndex);
			visibleCols++;
			visibleColOrder = new String[visibleCols];
			visibleColOrder[colIndex] = "0";
			colIndex++;
		} else {
			visibleColOrder = new String[visibleCols];
		}
			
		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			visibleColOrder[colIndex] = cid;
			
			tHead.enableColumn(cid, colIndex);
			
			if(col.hasAttribute("sortable")) {
				HeaderCell c = tHead.getHeaderCell(cid);
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
	
	/**
	 * Gives correct column index for given column key ("cid" in UIDL).
	 * 
	 * @param colKey
	 * @return column index of visible columns, -1 if column not visible
	 */
	private int getColIndexByKey(String colKey) {
		// return 0 if asked for rowHeaders
		if("0".equals(colKey))
			return 0;
		for (int i = 0; i < visibleColOrder.length; i++) {
			if(visibleColOrder[i].equals(colKey))
				return i;
		}
		return -1;
	}
	
	private boolean isCollapsedColumn(String colKey) {
		if(collapsedColumns == null)
			return false;
		if(collapsedColumns.contains(colKey))
			return true;
		return false;
	}

	private String getColKeyByIndex(int index) {
		return tHead.getHeaderCell(index).getColKey();
	}

	private void setColWidth(int colIndex, int w) {
		HeaderCell cell = tHead.getHeaderCell(colIndex);
		cell.setWidth(w);
		tBody.setColWidth(colIndex, w);
		String cid = cell.getColKey();;
	}
	
	private int getColWidth(String colKey) {
		return tHead.getHeaderCell(colKey).getWidth();
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
		
		/* Build new columnOrder and update it to server
		 * Note that columnOrder also contains collapsed columns
		 * so we cannot directly build it from cells vector
		 * Loop the old columnOrder and append in order to new array 
		 * unless on moved columnKey. On new index also put the moved key
		 * i == index on columnOrder, j == index on newOrder
		 */
		String oldKeyOnNewIndex = visibleColOrder[newIndex];
		if(rowHeaders)
			newIndex--; // columnOrder don't have rowHeader
		// add back hidden rows, 
		for (int i = 0; i < columnOrder.length; i++) {
			if(columnOrder[i].equals(oldKeyOnNewIndex))
				break; // break loop at target
			if(isCollapsedColumn(columnOrder[i]))
				newIndex++;
		}
		// finally we can build the new columnOrder for server
		String[] newOrder = new String[columnOrder.length];
		for(int i = 0, j = 0; j < newOrder.length; i++) {
			if(j == newIndex) {
				newOrder[j] = columnKey;
				j++;
			}
			if(i == columnOrder.length)
				break;
			if(columnOrder[i].equals(columnKey))
				continue;
			newOrder[j] = columnOrder[i];
			j++;
		}
		columnOrder = newOrder;
		// also update visibleColumnOrder
		int i = rowHeaders ? 1 : 0;
		for (int j = 0; j < newOrder.length; j++) {
			String cid = newOrder[j];
			if(!isCollapsedColumn(cid))
				visibleColOrder[i++] = cid;
		}
		client.updateVariable(paintableId, "columnorder", columnOrder, false);
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
			tHead.setWidth(bodyContainer.getOffsetWidth());
		} else {
			bodyContainer.setWidth(width + "px");
			tHead.setWidth(width);
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
		return 20;
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
		tHead.setHorizontalScrollPosition(scrollLeft);
	
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
			client.updateVariable(this.paintableId, "firstvisible", firstRowInViewPort, false);
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
			client.updateVariable(paintableId, "firstvisible", firstRowInViewPort, false);
			client.updateVariable(paintableId, "reqfirstrow", reqFirstRow, false);
			client.updateVariable(paintableId, "reqrows", reqRows, true);
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
			int first = (int) (firstRowInViewPort - pageLength*CACHE_RATE);
			int reqRows = (int) (2*pageLength*CACHE_RATE + pageLength);
			if(first < 0) {
				reqRows = reqRows + first;
				first = 0;
			}
			this.setReqFirstRow(first);
			this.setReqRows(reqRows);
			run();
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

		private int width;


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
			
			DOM.sinkEvents(td, Event.MOUSEEVENTS);
			
			setElement(td);
		}
		
		public void setWidth(int w) {
			this.width = w;
			DOM.setStyleAttribute(captionContainer, "width", (w - DRAG_WIDGET_WIDTH - 4) + "px");
			setWidth(w + "px");
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
			if(isResizing || DOM.compare(DOM.eventGetTarget(event), colResizeWidget)) {
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
				client.console.log("HeaderCaption: mouse down");
				dragging = true;
				moved = false;
		        colIndex = getColIndexByKey(cid);
				DOM.setCapture(getElement());
				this.headerX = tHead.getAbsoluteLeft();
				client.console.log("HeaderCaption: Caption set to capture mouse events");
				DOM.eventPreventDefault(event);
				break;
			case Event.ONMOUSEUP:
				client.console.log("HeaderCaption: mouseUP");
				dragging = false;
				DOM.releaseCapture(getElement());

				if(!moved) {
					// mouse event was a click to header -> sort column
					if(sortable) {
						if(sortColumn.equals(cid)) {
							// just toggle order
							client.updateVariable(paintableId, "sortascending", !sortAscending, false);
						} else {
							// set table scrolled by this column
							client.updateVariable(paintableId, "sortcolumn", cid, false);
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
				client.console.log("HeaderCaption: Stopped column reordering");
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
					client.console.log("HeaderCaption: Dragging column, optimal index...");
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
					int visibleCellCount = tHead.getVisibleCellCount();
					for(int i = start; i <= visibleCellCount ; i++ ) {
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
					client.console.log(""+closestSlot);
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

		public String getCaption() {
			return DOM.getInnerText(captionContainer);
		}

		public boolean isEnabled() {
			return getParent() != null;
		}


	}
	
	public class TableHead extends Panel implements IActionOwner {
		
		private static final int COLUMN_SELECTOR_WIDTH = 10;
		private static final int COLUMN_SELECTOR_HEIGHT = 10;

		private static final int WRAPPER_WIDTH = 9000;
		
		Vector visibleCells = new Vector();
		
		HashMap availableCells = new HashMap();
		
		Element div = DOM.createDiv();
		Element hTableWrapper = DOM.createDiv();
		Element hTableContainer = DOM.createDiv();
		Element table = DOM.createTable();
		Element headerTableBody = DOM.createTBody();
		Element tr = DOM.createTR();

		private Element columnSelector = DOM.createDiv();

		private int focusedSlot = -1;
		
		private boolean columnCollapsing = false;
		
		public TableHead() {
			DOM.setStyleAttribute(hTableWrapper, "overflow", "hidden");
			DOM.setAttribute(hTableWrapper, "className", "iscrolltable-header");

			// TODO move styles to CSS
			DOM.setStyleAttribute(columnSelector, "width", COLUMN_SELECTOR_WIDTH +"px");
			DOM.setStyleAttribute(columnSelector, "cssFloat", "right");
			DOM.setStyleAttribute(columnSelector, "styleFloat", "right");
			DOM.setStyleAttribute(columnSelector, "height", COLUMN_SELECTOR_HEIGHT + "px");
			DOM.setStyleAttribute(columnSelector, "background", "brown");
			DOM.setStyleAttribute(columnSelector, "display", "none");
			
			DOM.appendChild(table, headerTableBody);
			DOM.appendChild(headerTableBody, tr);
			DOM.appendChild(hTableContainer, table);
			DOM.appendChild(hTableWrapper, hTableContainer);
			DOM.appendChild(div, columnSelector);
			DOM.appendChild(div, hTableWrapper);
			setElement(div);
			
			DOM.sinkEvents(columnSelector, Event.ONCLICK);
		}
		
		public void enableColumn(String cid, int index) {
			HeaderCell c = getHeaderCell(cid);
			if(!c.isEnabled()) {
				setHeaderCell(index, c);
			}
		}

		public int getVisibleCellCount() {
			return visibleCells.size();
		}

		public void addAvailableCell(HeaderCell cell) {
			availableCells.put(cell.getColKey(), cell);
		}

		public void setHorizontalScrollPosition(int scrollLeft) {
			DOM.setIntAttribute(hTableWrapper, "scrollLeft", scrollLeft);
		}
		
		public void setWidth(int width) {
			DOM.setStyleAttribute(hTableWrapper, "width", (width - getScrollBarWidth()) + "px");
			super.setWidth(width + "px");
		}

		public void setColumnCollapsingAllowed(boolean cc) {
			columnCollapsing = cc;
			if(cc) {
				DOM.setStyleAttribute(columnSelector, "display", "block");
			} else {
				DOM.setStyleAttribute(columnSelector, "display", "none");
			}
		}

		public void disableBrowserIntelligence() {
			DOM.setStyleAttribute(hTableContainer, "width", WRAPPER_WIDTH +"px");
		}
		
		public void setHeaderCell(int index, HeaderCell cell) {
			if(index < visibleCells.size()) {
				// insert to right slot
				DOM.insertChild(tr, cell.getElement(), index);
				adopt(cell, null);
				visibleCells.insertElementAt(cell, index);
				
			} else if( index == visibleCells.size()) {
				//simply append
				adopt(cell, tr);
				visibleCells.add(cell);
			} else {
				throw new RuntimeException("Header cells must be appended in order");
			}
		}
		
		public HeaderCell getHeaderCell(int index) {
			if(index < visibleCells.size())
				return (HeaderCell) visibleCells.get(index);
			else 
				return null;
		}
		
		/**
		 * Get's HeaderCell by it's column Key.
		 * 
		 * Note that this returns HeaderCell even if it is currently
		 * collapsed.
		 * 
		 * @param cid Column key of accessed HeaderCell
		 * @return HeaderCell
		 */
		public HeaderCell getHeaderCell(String cid) {
			return (HeaderCell) availableCells.get(cid);
		}
		
		public void moveCell(int oldIndex, int newIndex) {
			HeaderCell hCell = getHeaderCell(oldIndex);
			Element cell = hCell.getElement();

			visibleCells.remove(oldIndex);
			DOM.removeChild(tr, cell);

			DOM.insertChild(tr, cell, newIndex);
			visibleCells.insertElementAt(hCell, newIndex);
		}
		
		public Iterator iterator() {
			return visibleCells.iterator();
		}

		public boolean remove(Widget w) {
			disown(w);
			if(visibleCells.contains(w)) {
				visibleCells.remove(w);
				return true;
			}
			return false;
		}
		
		public void removeCell(String colKey) {
			HeaderCell c = getHeaderCell(colKey);
			remove(c);
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
		
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			if(DOM.compare(DOM.eventGetTarget(event), columnSelector)) {
				int left = DOM.getAbsoluteLeft(columnSelector);
				int top = DOM.getAbsoluteTop(columnSelector) + COLUMN_SELECTOR_WIDTH;
				client.getContextMenu().showAt(this, left, top);
			}
		}

		class VisibleColumnAction extends IAction {
			
			String colKey;
			private boolean collapsed;
			
			public VisibleColumnAction(String colKey) {
				super(IScrollTable.TableHead.this);
				this.colKey = colKey;
				caption = tHead.getHeaderCell(colKey).getCaption();
			}

			public void execute() {
				client.getContextMenu().hide();
				// toggle selected column
				if(collapsedColumns.contains(colKey)) {
					collapsedColumns.remove(colKey);
				} else {
					tHead.removeCell(colKey);
					collapsedColumns.add(colKey);
				}
				
				// update  variable to server
				client.updateVariable(paintableId, "collapsedcolumns", 
						collapsedColumns.toArray(), false);
				// let rowRequestHandler determine proper rows
				rowRequestHandler.refreshContent();
			}

			public void setCollapsed(boolean b) {
				collapsed = b;
			}

			/**
			 * Override default method to distinguish on/off columns
			 */
			public String getHTML() {
				StringBuffer buf = new StringBuffer();
				if(collapsed)
					buf.append("<span class=\"off\">");
				buf.append(super.getHTML());
				if(collapsed)
					buf.append("</span>");
				return buf.toString();
			}
			
			
		}

		public IAction[] getActions() {
			IAction[] actions= new IAction[columnOrder.length];
			
			for (int i = 0; i < columnOrder.length; i++) {
				String cid = columnOrder[i];
				HeaderCell c = getHeaderCell(cid);
				VisibleColumnAction a = new VisibleColumnAction(c.getColKey());
				a.setCaption(c.getCaption());
				if(!c.isEnabled())
					a.setCollapsed(true);
				actions[i] = a;
			}			
			return actions;
		}

		private Iterator getAvailableColumnKeyIterator() {
			return availableCells.keySet().iterator();
		}

		private int getAvailableColumnCount() {
			return availableCells.size();
		}

		public Client getClient() {
			return client;
		}

		public String getPaintableId() {
			return paintableId;
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
				int w = IScrollTable.this.getColWidth(getColKeyByIndex(i));
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

		public class IScrollTableRow extends Panel  implements IActionOwner {
			
			Vector childWidgets = new Vector();
			private boolean selected = false;
			private int rowKey;
			
			private String[] actionKeys = null;
			
			private IScrollTableRow(int rowKey) {
				this.rowKey = rowKey;
				setElement(DOM.createElement("tr"));
				DOM.sinkEvents(getElement(), Event.ONCLICK);
				disableContextMenu(getElement());
				setStyleName("iscrolltable-row");
			}
			
			private native void disableContextMenu(Element el) /*-{
				var row = this;
				el.oncontextmenu = function(e) {
					if(!e)
						e = window.event;
					row.@com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable.IScrollTableBody.IScrollTableRow::showContextMenu(Lcom/google/gwt/user/client/Event;)(e);
					return false;
				};
			}-*/;
			
			public String getKey() {
				return String.valueOf(rowKey);
			}

			public IScrollTableRow(UIDL uidl) {
				this(uidl.getIntAttribute("key"));
				
				// row header
				if(uidl.hasAttribute("caption"))
					addCell(uidl.getStringAttribute("caption"));
				
				if(uidl.hasAttribute("al"))
					actionKeys = uidl.getStringArrayAttribute("al");
				
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
				if(uidl.hasAttribute("selected") && !isSelected())
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
				case Event.ONCLICK:
					client.console.log("Row click");
					if(selectMode > ITable.SELECT_MODE_NONE) {
						toggleSelection();
						client.updateVariable(paintableId, "selected", selectedRowKeys.toArray(), immediate);
					}
					break;

				default:
					break;
				}
				super.onBrowserEvent(event);
			}
			
			public void showContextMenu(Event event) {
				client.console.log("Context menu");
				if(actionKeys != null) {
					int left = DOM.eventGetClientX(event);
					int top = DOM.eventGetClientY(event);
					client.getContextMenu().showAt(this, left, top);
				}
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

			/* (non-Javadoc)
			 * @see com.itmill.toolkit.terminal.gwt.client.ui.IActionOwner#getActions()
			 */
			public IAction[] getActions() {
				if(actionKeys == null)
					return new IAction[] {};
				IAction[] actions = new IAction[actionKeys.length];
				for (int i = 0; i < actions.length; i++) {
					String actionKey = actionKeys[i];
					ITreeAction a = new ITreeAction(this, String.valueOf(rowKey), actionKey);
					a.setCaption(getActionCaption(actionKey));
					actions[i] = a;
				}
				return actions;
			}

			public Client getClient() {
				return client;
			}

			public String getPaintableId() {
				return paintableId;
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
