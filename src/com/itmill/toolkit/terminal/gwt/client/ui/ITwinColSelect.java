package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITwinColSelect extends IOptionGroupBase {
	
	private static final String CLASSNAME = "i-select-twincol";
	
	private static final int VISIBLE_COUNT = 10;
	
	private ListBox options;
	
	private ListBox selections;
	
	private IButton add;
	
	private IButton remove;
	
	public ITwinColSelect() {
		super(CLASSNAME);
		options = new ListBox();
		selections = new ListBox();
		options.setVisibleItemCount(VISIBLE_COUNT);
		selections.setVisibleItemCount(VISIBLE_COUNT);
		options.setStyleName(CLASSNAME+"-options");
		selections.setStyleName(CLASSNAME+"-selections");
		Panel buttons = new FlowPanel();
		buttons.setStyleName(CLASSNAME+"-buttons");
		add = new IButton();
		remove = new IButton();
		add.setText(">>");
		remove.setText("<<");
		add.addClickListener(this);
		remove.addClickListener(this);
		Panel p = ((Panel)optionsContainer);
		p.add(options);
		buttons.add(add);
		HTML br = new HTML("&nbsp;");
		br.setStyleName(CLASSNAME+"-deco");
		buttons.add(br);
		buttons.add(remove);
		p.add(buttons);
		p.add(selections);
	}

	protected void buildOptions(UIDL uidl) {
		boolean enabled = !disabled && !readonly;
		options.setMultipleSelect(multiselect);
		selections.setMultipleSelect(multiselect);
		options.setEnabled(enabled);
		selections.setEnabled(enabled);
		add.setEnabled(enabled);
		remove.setEnabled(enabled);
		options.clear();
		selections.clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL optionUidl = (UIDL)i.next();
			if(optionUidl.hasAttribute("selected")) {
				selections.addItem(optionUidl.getStringAttribute("caption"), optionUidl.getStringAttribute("key"));
			} else
				options.addItem(optionUidl.getStringAttribute("caption"), optionUidl.getStringAttribute("key"));
		}
	}

	protected Object[] getSelectedItems() {
		Vector selectedItemKeys = new Vector();
		for(int i = 0; i < selections.getItemCount(); i++) {
			selectedItemKeys.add(selections.getValue(i));
		}
		return selectedItemKeys.toArray();
	}
	
	private String[] getItemsToAdd() {
		String[] selectedIndexes = new String[options.getItemCount()];
		for(int i = 0; i < options.getItemCount(); i++) {
			if(options.isItemSelected(i))
				selectedIndexes[i] = options.getValue(i);
		}
		return selectedIndexes;
	}
	
	private String[] getItemsToRemove() {
		String[] selectedIndexes = new String[selections.getItemCount()];
		for(int i = 0; i < selections.getItemCount(); i++) {
			if(selections.isItemSelected(i))
				selectedIndexes[i] = selections.getValue(i);
		}
		return selectedIndexes;
	}
	
	public void onClick(Widget sender) {
		super.onClick(sender);
		if(sender == add) {
			String[] sel = getItemsToAdd();
			for(int i=0; i < sel.length; i++) {
				if(sel[i]!=null) selectedKeys.add(sel[i]);
			}
			client.updateVariable(id, "selected", selectedKeys.toArray(), true); // Immediate
		} else if(sender == remove) {
			String[] sel = getItemsToRemove();
			for(int i=0; i < sel.length; i++) {
				if(sel[i]!=null) selectedKeys.remove(sel[i]);
			}
			client.updateVariable(id, "selected", selectedKeys.toArray(), true); // Immediate
		}
	}

}
