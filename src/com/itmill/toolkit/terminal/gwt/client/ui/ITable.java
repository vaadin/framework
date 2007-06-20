package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITable extends Composite implements Paintable, ScrollListener {
	
	/**
	 *  multiple of pagelenght which component will 
	 *  cache when requesting more rows 
	 */
	private static final double CACHE_RATE = 3;
	/** 
	 * fraction of pageLenght which can be scrolled without 
	 * making new request 
	 */
	private static final double CACHE_REACT_RATE = 1;
	
	private int firstRendered = -1;
	private int lastRendered = -1;
	private int firstRowInViewPort = 0;
	private int pageLength = 15;
	
	private boolean rowHeaders = false;
	
	private Map columnOrder = new HashMap();
	
	private Client client;
	private String id;
	private boolean immediate;
	
	private FlexTable tHead = new FlexTable();
	private FlexTable tBody = new FlexTable();
	
	private ScrollPanel bodyContainer = new ScrollPanel();
	private VerticalPanel bodyContent = new VerticalPanel();
	
	private ScrollPanel headerContainer = new ScrollPanel();
	
	private HTML preSpacer = new HTML();
	private HTML postSpacer = new HTML();
	
	private boolean colWidthsInitialized = false;
	private int totalRows;
	private HashMap columnWidths = new HashMap();
	
	private int rowHeight = 0;
	private RowRequestHandler rowRequestHandler;
	
	public ITable() {
		headerContainer.add(tHead);
		DOM.setStyleAttribute(headerContainer.getElement(), "overflow", "hidden");
		
		tBody.setStyleName("itable-tbody");
		
		bodyContent.add(preSpacer);
		bodyContent.add(tBody);
		bodyContent.add(postSpacer);
		//TODO remove debug color
		DOM.setStyleAttribute(postSpacer.getElement(), "background", "gray");
		bodyContainer.add(bodyContent);
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
		if(uidl.hasAttribute("rowheaders"))
			rowHeaders = true;
		
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
		
		updateBody(rowData, uidl.getIntAttribute("firstrow"),uidl.getIntAttribute("rows"));
		
		if(!colWidthsInitialized) {
			DeferredCommand.add(new Command() {
				public void execute() {
					initSize();
					updateSpacers();
					bodyContainer.setScrollPosition(getRowHeight()*(firstRowInViewPort -1));
					colWidthsInitialized = true;
					if(totalRows - 1 > lastRendered) {
						// fetch cache rows
						rowRequestHandler.setReqFirstRow(lastRendered+1);
						rowRequestHandler.setReqRows((int) (pageLength*CACHE_RATE));
						rowRequestHandler.deferRowFetch();
					}
				}
			});
		}
	}
	
	private void updateActionMap(UIDL c) {
		// TODO Auto-generated method stub
		
	}

	private void updateHeader(UIDL uidl) {
		if(uidl == null)
			return;
		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			int colIndex = getColIndexByKey(cid);
			if(colIndex > -1)
				setHeaderText(colIndex, col.getStringAttribute("caption"));
			DOM.setAttribute(tHead.getFlexCellFormatter().getElement(0, colIndex), "cid", cid);
		}
	}
	
	/**
	 * Updates row data from uidl. UpdateFromUIDL delegates updating 
	 * tBody to this method.
	 * 
	 * Updates may be to different part of tBody, depending on update type.
	 * It can be initial row data, scroll up, scroll down...
	 * 
	 * @param uidl which contains row data
	 * @param firstRow first row in data set
	 * @param reqRows amount of rows in data set
	 */
	private void updateBody(UIDL uidl, int firstRow, int reqRows) {
 		if(uidl == null || reqRows < 1)
			return;
		
		Iterator it = uidl.getChildIterator();
		
		if(firstRendered == -1 || firstRow == lastRendered + 1) {
			//initial data to body or appending rows to table
			while(it.hasNext()) {
				appendRow( (UIDL) it.next() );
				if(colWidthsInitialized)
					updateSpacers();
			}
//			lastRendered = firstRow + reqRows - 1;
			if(firstRendered == -1) {
				firstRendered = firstRow;
			}
		} else if(firstRendered == firstRow + reqRows) {
			// add received rows before old ones
			int rowsAdded = 0;
			while(it.hasNext()){
				tBody.insertRow(rowsAdded);
				updateSpacers();
				updateRow( (UIDL) it.next(), rowsAdded);
			}
			firstRendered = firstRow;
		} else {
			// complitely new set received, truncate body and recurse
			tBody.clear();
			firstRendered = -1;
			lastRendered = -1;
			updateBody(uidl, firstRow, reqRows);
		}
		trimBody();
	}
	
	/**
	 * Returns calculated height of row.
	 * @return height in pixels
	 */
	private int getRowHeight() {
		if(rowHeight == 0)
			rowHeight = tBody.getOffsetHeight()/getRenderedRowCount();
		return rowHeight;
	}

	/**
	 * This method removes rows from body which are "out of
	 * cache area" to keep amount of rendered rows sane.
	 */
	private void trimBody() {
		int toBeRemovedFromTheBeginning = (int) (firstRowInViewPort - CACHE_RATE*pageLength) - firstRendered;
		int toBeRemovedFromTheEnd = lastRendered - (int) (firstRowInViewPort + CACHE_RATE*pageLength + pageLength);
		if(toBeRemovedFromTheBeginning > 0) {
			// remove extra rows from the beginning of the table
			while(toBeRemovedFromTheBeginning > 0) {
				tBody.removeRow(0);
				firstRendered++;
				toBeRemovedFromTheBeginning--;
				updateSpacers();
			}
		}
		if(toBeRemovedFromTheEnd > 0) {
			// remove extra rows from the end of the table
			while(toBeRemovedFromTheEnd > 0) {
				tBody.removeRow(tBody.getRowCount() - 1);
				toBeRemovedFromTheEnd--;
				lastRendered--;
				updateSpacers();
			}
		}
//		bodyContainer.setScrollPosition(getRowHeight()*firstRowInViewPort);
	}
	
	private void appendRow(UIDL uidl) {
		lastRendered++;
		updateRow(uidl, lastRendered);
	}

	private void updateRow(UIDL uidl, int rowIndex) {
		int colIndex = 0;
		if(rowHeaders) {
			setCellContent(rowIndex, colIndex, uidl.getStringAttribute("caption"));
			colIndex++;
		}
		
		for(Iterator it = uidl.getChildIterator(); it.hasNext();) {
			Object cell = it.next();
			if (cell instanceof String) {
				setCellContent(rowIndex, colIndex, (String) cell);
			} else {
				setCellContent(rowIndex, colIndex, (UIDL) cell);
			}
			colIndex++;
		}
		Element row = tBody.getRowFormatter().getElement(rowIndex);
		DOM.setIntAttribute(row, "key", uidl.getIntAttribute("key"));
	}
	
	private int getColIndexByKey(String colKey) {
		return Integer.parseInt(colKey) - 1 + (rowHeaders ? 1 : 0);
	}
	
	private String getColKeyByIndex(int index) {
		return DOM.getAttribute(tHead.getCellFormatter().getElement(0, index), "cid");
	}

	public void setHeaderText(int colIndex, String text) {
		tHead.setText(0, colIndex, text);
	}
	
	public void setCellContent(int rowId, int colId, UIDL cell) {
		if(cell == null)
			return;
	 	Widget cellContent = client.getWidget(cell);
		tBody.setWidget(rowId, colId, cellContent);
		((Paintable)cell).updateFromUIDL(cell, client);
		tBody.getCellFormatter().setWordWrap(rowId, colId, false);
	}
	
	public void setCellContent(int rowId, int colId, String text) {
		HTML cellContent = new HTML();
		cellContent.setText(text);
		tBody.setWidget(rowId, colId, cellContent);
	}
	
	/**
	 * Run when receices its initial content. Syncs headers and bodys
	 * "natural widths and saves the values.
	 */
	private void initSize() {
		int cols = tHead.getCellCount(0);
		FlexCellFormatter hf = tHead.getFlexCellFormatter();
		FlexCellFormatter bf = tBody.getFlexCellFormatter();
		for (int i = 0; i < cols; i++) {
			Element hCell = hf.getElement(0, i);
			Element bCell = bf.getElement(1, i);
			int hw = DOM.getIntAttribute(hCell, "offsetWidth");
			int cw = DOM.getIntAttribute(bCell, "offsetWidth");
			setColWidth(i , hw > cw ? hw : cw);
		}
		
		bodyContainer.setHeight(tBody.getOffsetHeight() + "px");
		bodyContainer.setWidth((tBody.getOffsetWidth() + 20) + "px");
		
	}

	private void setColWidth(int colIndex, int w) {
		String cid = getColKeyByIndex(colIndex);
		tHead.getCellFormatter().setWidth(0, colIndex, w + "px");
		tBody.getCellFormatter().setWidth(0, colIndex, w + "px");
		columnWidths.put(cid,new Integer(w));
	}
	
	private int getColWidth(String colKey) {
		return ( (Integer) this.columnWidths.get(colKey)).intValue();
	}
	
	private void updateSpacers() {
		int preSpacerHeight = (firstRendered)*getRowHeight();
		int postSpacerHeight = (totalRows - 1 - lastRendered)*getRowHeight();
		preSpacer.setHeight(preSpacerHeight+"px");
		postSpacer.setHeight(postSpacerHeight + "px");
	}
	
	private int getRenderedRowCount() {
		return lastRendered-firstRendered;
	}

	/**
	 * This method has logick which rows needs to be requested from
	 * server when user scrolls
	 *
	 */
	public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
		rowRequestHandler.cancel();
		
		firstRowInViewPort = (int) Math.ceil( scrollTop / rowHeight );
		client.console.log("At scrolltop: " + scrollTop + " At row " + firstRowInViewPort);
		
		int postLimit = (int) (firstRowInViewPort + pageLength + pageLength*CACHE_REACT_RATE);
		if(postLimit > totalRows)
			postLimit = totalRows;
		int preLimit = (int) (firstRowInViewPort - pageLength*CACHE_REACT_RATE);
		if(preLimit < 0)
			preLimit = 0;
		if(
				(postLimit <= lastRendered && preLimit >= firstRendered )
				) {
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
}
