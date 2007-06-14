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
	
	private int firstRendered = 0;
	private int lastRendered = 0;
	private int firstRowInViewPort = 1;
	private int pageLength = 15;
	
	private int rowHeaders = 0;
	
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
	
	private int rowHeight = 22;
	private RowRequestHandler rowRequestHandler;
	
	public ITable() {
		headerContainer.add(tHead);
		DOM.setStyleAttribute(headerContainer.getElement(), "overflow", "hidden");
		
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
		this.client = client;
		this.id = uidl.getStringAttribute("id");
		this.immediate = uidl.getBooleanAttribute("immediate");
		this.totalRows = uidl.getIntAttribute("totalrows");
		
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
		
		updateBody(rowData);
		
		if(!colWidthsInitialized) {
			DeferredCommand.add(new Command() {
				public void execute() {
					initSize();
					updateSpacers();
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
	
	private void updateBody(UIDL uidl) {
		if(uidl == null)
			return;
		
		Iterator it = uidl.getChildIterator();
		UIDL row = (UIDL) it.next();
		if(firstRendered == 0)
			firstRendered = row.getIntAttribute("key");
		if(row.getIntAttribute("key") == lastRendered + 1) {
			while(it.hasNext())
				appendRow( (UIDL) it.next() );
		}
	}
	
	private void appendRow(UIDL uidl) {
		lastRendered++;
		updateRow(uidl, lastRendered);
	}

	private void updateRow(UIDL uidl, int rowIndex) {
		int colIndex = 0;
		for(Iterator it = uidl.getChildIterator(); it.hasNext();) {
			Object cell = it.next();
			if (cell instanceof String) {
				setCellContent(rowIndex, colIndex, (String) cell);
			} else {
				setCellContent(rowIndex, colIndex, (UIDL) cell);
			}
			colIndex++;
		}
		
	}
	
	
	private int getRowIndex(int rowKey) {
		return rowKey - firstRendered;
	}
	
	private int getColIndexByKey(String colKey) {
		return Integer.parseInt(colKey) - 1;
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
	}
	
	public void setCellContent(int rowId, int colId, String text) {
		tBody.setText(rowId, colId, text);
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
		bodyContainer.setWidth(tBody.getOffsetWidth() + "px");
		
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
		rowHeight = tBody.getOffsetHeight()/getRenderedRowCount();
		int preSpacerHeight = (firstRendered - 1)*rowHeight;
		int postSpacerHeight = (totalRows - lastRendered)*rowHeight;
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
		
		firstRowInViewPort = scrollTop / rowHeight;
		client.console.log("At scrolltop: " + scrollTop + " At row " + firstRowInViewPort);
		
		int postLimit = (int) (firstRowInViewPort + pageLength + pageLength*CACHE_REACT_RATE);
		int preLimit = (int) (firstRowInViewPort - pageLength*CACHE_REACT_RATE);
		if(
				postLimit > lastRendered &&
				( preLimit < 1 || preLimit < firstRendered )
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
			rowRequestHandler.setReqRows((int) (2*CACHE_RATE*pageLength + pageLength));
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
		
		private int reqFirstRow;
		private int reqRows;
		
		public void deferRowFetch() {
			schedule(250);
		}

		public void setReqFirstRow(int reqFirstRow) {
			if(reqFirstRow < 1)
				reqFirstRow = 1;
			this.reqFirstRow = reqFirstRow;
		}

		public void setReqRows(int reqRows) {
			this.reqRows = reqRows;
		}

		public void run() {
//			client.updateVariable(id, "firstvisible", firstRowInViewPort, false);
			client.updateVariable(id, "reqfirstrow", reqFirstRow, false);
			client.updateVariable(id, "reqrows", reqRows, true);
		}
		
	}
}
