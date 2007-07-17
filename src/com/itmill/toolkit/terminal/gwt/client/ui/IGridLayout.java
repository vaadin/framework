package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IGridLayout extends FlexTable implements Paintable {

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		clear();
		if (uidl.hasAttribute("caption"))
			setTitle(uidl.getStringAttribute("caption"));
		int row = 0, column = 0;
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL r = (UIDL) i.next();
			if ("gr".equals(r.getTag())) {
				row++;
				column = 0;
				for (Iterator j = r.getChildIterator(); j.hasNext();) {
					UIDL c = (UIDL) j.next();
					if ("gc".equals(c.getTag())) {
						column++;
						if (c.hasAttribute("w")) {
							int w = c.getIntAttribute("w");
							((FlexCellFormatter) getCellFormatter())
									.setColSpan(row, column, w);
						}
						UIDL u = c.getChildUIDL(0);
						if (u != null) {
							Widget child = client.getWidget(u);
							setWidget(row, column, child);
							((Paintable) child).updateFromUIDL(u, client);
						}
					}
				}
			}
		}
	}

}
