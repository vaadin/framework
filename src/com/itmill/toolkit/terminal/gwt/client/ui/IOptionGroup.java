package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IOptionGroup extends IOptionGroupBase {
	
	private static final String CLASSNAME = "i-select-optiongroup";
	
	private Panel panel;
	
	private Map optionsToKeys;
	
	public IOptionGroup() {
		super(CLASSNAME);
		panel = (Panel) optionsContainer;
		optionsToKeys = new HashMap();
	}
	
	/*
	 * Return true if no elements were changed, false otherwise.
	 */
	protected void buildOptions(UIDL uidl) {
		panel.clear();
		for(Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL opUidl = (UIDL) it.next();
			CheckBox op;
			if(multiselect) {
				op = new ICheckBox();
				op.setText(opUidl.getStringAttribute("caption"));
			} else {
				op = new RadioButton(id, opUidl.getStringAttribute("caption"));
			}
			op.setStyleName(CLASSNAME_OPTION);
			op.setChecked(opUidl.getBooleanAttribute("selected"));
			op.setEnabled(!opUidl.getBooleanAttribute("disabled") && !readonly && !disabled);
			op.addClickListener(this);
			optionsToKeys.put(op, opUidl.getStringAttribute("key"));
			panel.add(op);
		}
	}

	protected Object[] getSelectedItems() {
		return selectedKeys.toArray();
	}
	
	public void onClick(Widget sender) {
		super.onClick(sender);
		if(sender instanceof CheckBox) {
			boolean selected = ((CheckBox) sender).isChecked();
			String key = (String) optionsToKeys.get(sender);
			if(!multiselect) selectedKeys.clear();
			if(selected) selectedKeys.add(key);
			else selectedKeys.remove(key);
			client.updateVariable(id, "selected", getSelectedItems(), immediate);
		}
	}

}
