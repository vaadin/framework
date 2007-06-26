package com.itmill.toolkit.terminal.gwt.client.ui.scrolltable;

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
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.ITable;

public class IScrollTable extends Composite implements Paintable, ITable, ScrollListener {
	
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
	
	private int firstRowInViewPort = 0;
	private int pageLength = 15;
	
	private boolean rowHeaders = false;
	
	private Map columnOrder = new HashMap();
	
	private Client client;
	private String id;
	private boolean immediate;
	
	private boolean initializedAndAttached = false;
	
	private FlexTable tHead = new FlexTable();

	private ScrollPanel bodyContainer = new ScrollPanel();
	
	private ScrollPanel headerContainer = new ScrollPanel();
	
	private boolean colWidthsInitialized = false;
	private int totalRows;
	private HashMap columnWidths = new HashMap();
	
	private RowRequestHandler rowRequestHandler;
	private IScrollTableBody tBody;
	private int width = -1;
	private int height = -1;
	private int firstvisible;
	
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
		this.firstvisible = uidl.getIntVariable("firstvisible");
		if(uidl.hasAttribute("rowheaders"))
			rowHeaders = true;
		if(uidl.hasAttribute("width"))
			width = uidl.getIntAttribute("width");
		if(uidl.hasAttribute("height"))
			width = uidl.getIntAttribute("height");
		
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
		return DOM.getAttribute(tHead.getCellFormatter().getElement(0, index), "cid");
	}

	public void setHeaderText(int colIndex, String text) {
		tHead.setText(0, colIndex, text);
	}
	
	private void setColWidth(int colIndex, int w) {
		String cid = getColKeyByIndex(colIndex);
		tHead.getCellFormatter().setWidth(0, colIndex, w + "px");
		tBody.setColWidth(colIndex, w);
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
		
		firstRowInViewPort = (int) Math.ceil( scrollTop / tBody.getRowHeight() );
		client.console.log("At scrolltop: " + scrollTop + " At row " + firstRowInViewPort);
		
		int postLimit = (int) (firstRowInViewPort + pageLength + pageLength*CACHE_REACT_RATE);
		if(postLimit > totalRows)
			postLimit = totalRows;
		int preLimit = (int) (firstRowInViewPort - pageLength*CACHE_REACT_RATE);
		if(preLimit < 0)
			preLimit = 0;
		int lastRendered = tBody.getLastRendered();
		int firstRendered = tBody.getFirstRendered();
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
		} else {
			bodyContainer.setWidth(width + "px");
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
		FlexCellFormatter hf = tHead.getFlexCellFormatter();
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
}
