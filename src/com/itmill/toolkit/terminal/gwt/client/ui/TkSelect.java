package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkSelect extends Composite implements Paintable, ChangeListener {
	
	Label caption = new Label();
	ListBox select = new ListBox();
	private Client client;
	private String id;
	private boolean immediate;
	
	
	public TkSelect() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(select);
		select.addChangeListener(this);
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		this.id = uidl.getStringAttribute("id");
		this.immediate = uidl.getBooleanAttribute("immediate");
		
		if (uidl.hasAttribute("caption")) caption.setText(uidl.getStringAttribute("caption")); 
		
		if(uidl.hasAttribute("selectmode"))
			select.setMultipleSelect(true);
		else
			select.setMultipleSelect(false);

		UIDL options = uidl.getChildUIDL(0);
		
		select.clear();
		for (Iterator i = options.getChildIterator(); i.hasNext();) {
			UIDL optionUidl = (UIDL)i.next();
			select.addItem(optionUidl.getStringAttribute("caption"), optionUidl.getStringAttribute("key"));
			if(optionUidl.hasAttribute("selected"))
				select.setItemSelected(select.getItemCount()-1, true);
		}
	}

	public void onChange(Widget sender) {
		if(select.isMultipleSelect()) {
			Vector selectedItemKeys = new Vector();
			for(int i = 0; i < select.getItemCount();i++) {
				if(select.isItemSelected(i))
					selectedItemKeys.add(select.getValue(i));
			}
			Object[] values = selectedItemKeys.toArray();
			client.updateVariable(id, "selected", values, immediate);
		} else {
			client.updateVariable(id, "selected", new String[] { "" + select.getValue(select.getSelectedIndex())}, immediate);
		}
	}
}
