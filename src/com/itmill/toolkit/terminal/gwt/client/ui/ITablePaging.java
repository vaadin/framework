package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITablePaging extends Composite implements Paintable, ClickListener {
	
	private Grid tBody = new Grid();
	private Button nextPage = new Button("&gt;");
	private Button prevPage = new Button("&lt;");
	private Button firstPage = new Button("&lt;&lt;");
	private Button lastPage = new Button("&gt;&gt;");

	
	private int pageLength = 15;
	
	private boolean rowHeaders = false;
	
	private Map columnOrder = new HashMap();
	
	private Client client;
	private String id;
	private boolean immediate;
	
	private int totalRows;

	private HashMap columnWidths = new HashMap();
	
	private HashMap visibleColumns = new HashMap();
	
	private int rowHeight = 0;

	private int rows;

	private int firstRow;
	
	public ITablePaging() {

		tBody.setStyleName("itable-tbody");
		
		VerticalPanel panel = new VerticalPanel();
		
		HorizontalPanel pager = new HorizontalPanel();
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

	public void updateFromUIDL(UIDL uidl, Client client) {
		if (client.updateComponent(this, uidl, true))
			return;

		this.client = client;
		this.id = uidl.getStringAttribute("id");
		this.immediate = uidl.getBooleanAttribute("immediate");
		this.totalRows = uidl.getIntAttribute("totalrows");
		this.pageLength = uidl.getIntAttribute("pagelength");
		this.firstRow = uidl.getIntAttribute("firstrow");
		this.rows = uidl.getIntAttribute("rows");

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
				updateVisibleColumns(c);
		}
		tBody.resize(rows+1, visibleColumns.size() + (rowHeaders ? 1 : 0 ));

		updateHeader(columnInfo);
		
		updateBody(rowData);
		
		updatePager();
	}
	
	private void updateVisibleColumns(UIDL c) {
		Iterator it = c.getChildIterator();
		int count = 0;
		visibleColumns.clear();
		while(it.hasNext()) {
			count++;
			UIDL col = (UIDL) it.next();
			visibleColumns.put(col.getStringAttribute("cid"), col.getStringAttribute("caption"));
		}
	}

	private void updateActionMap(UIDL c) {
		// TODO Auto-generated method stub
		
	}

	private void updateHeader(UIDL uidl) {
		if(uidl == null)
			return;
		int colIndex = (rowHeaders ? 1 : 0);

		for(Iterator it = uidl.getChildIterator();it.hasNext();) {
			UIDL col = (UIDL) it.next();
			String cid = col.getStringAttribute("cid");
			tBody.setText(0, colIndex, col.getStringAttribute("caption"));
			DOM.setAttribute(tBody.getCellFormatter().getElement(0, colIndex), "cid", cid);
			colIndex++;
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
	 */
	private void updateBody(UIDL uidl) {
		Iterator it = uidl.getChildIterator();
		
		int curRowIndex = 1;
		while(it.hasNext()){
			UIDL row = (UIDL) it.next();
			int colIndex = 0;
			if(rowHeaders) {
				tBody.setText(curRowIndex, colIndex, row.getStringAttribute("caption"));
				colIndex++;
			}
			Iterator cells = row.getChildIterator();
			while(cells.hasNext()) {
				Object cell = cells.next();
				if (cell instanceof String) {
					tBody.setText(curRowIndex, colIndex, (String) cell);
				} else {
				 	Widget cellContent = client.getWidget((UIDL) cell);
					tBody.setWidget(curRowIndex, colIndex, cellContent);
				}
				colIndex++;
			}
			Element rowElement = tBody.getRowFormatter().getElement(curRowIndex);
			DOM.setIntAttribute(rowElement, "key", uidl.getIntAttribute("key"));
			curRowIndex++;
		}
	}
	
	private void updatePager() {
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
		if(firstRow + pageLength + 1 > totalRows)
			return false;
		return true;
	}

	private boolean isFirstPage() {
		if(firstRow == 0)
			return true;
		return false;
	}

	public void onClick(Widget sender) {
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

}
