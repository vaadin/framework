	package com.itmill.toolkit.terminal.gwt.client.ui.scrolltable;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

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

	protected static int DEFAULT_ROW_HEIGHT = 25;
	
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

	private Client client;

	
	IScrollTableBody(Client client) {
		this.client = client;
		
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
			IScrollTableRow row = new IScrollTableRow((UIDL) it.next(), client);
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
		} else if (firstIndex > lastRendered || firstIndex + rows < firstRendered) {
			// complitely new set of rows
			// create one row before truncating row
			IScrollTableRow row = createRow((UIDL) it.next());
			while(lastRendered + 1 > firstRendered)
				unlinkRow(false);

			addRow(row);
			firstRendered = firstIndex;
			this.lastRendered = firstIndex + rows - 1 ;
			while(it.hasNext())
				addRow(createRow((UIDL) it.next()));
			fixSpacers();
		} else {
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
		IScrollTableRow row = new IScrollTableRow(uidl, client);
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

}
