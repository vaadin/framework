package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ISelect extends IOptionGroupBase {

    public static final String CLASSNAME = "i-select";

    protected ListBox select;

    public ISelect() {
        super(new ListBox(false), CLASSNAME);
        select = (ListBox) optionsContainer;
        select.setVisibleItemCount(1);
        select.addChangeListener(this);
        select.setStyleName(CLASSNAME + "-select");

    }

    protected void buildOptions(UIDL uidl) {
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        if (isNullSelectionAllowed() && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            select.addItem("", null);
        }
        for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
            UIDL optionUidl = (UIDL) i.next();
            select.addItem(optionUidl.getStringAttribute("caption"), optionUidl
                    .getStringAttribute("key"));
            if (optionUidl.hasAttribute("selected")) {
                select.setItemSelected(select.getItemCount() - 1, true);
            }
        }
    }

    protected Object[] getSelectedItems() {
        Vector selectedItemKeys = new Vector();
        for (int i = 0; i < select.getItemCount(); i++) {
            if (select.isItemSelected(i)) {
                selectedItemKeys.add(select.getValue(i));
            }
        }
        return selectedItemKeys.toArray();
    }

    public void onChange(Widget sender) {
        if (select.isMultipleSelect()) {
            client.updateVariable(id, "selected", getSelectedItems(),
                    isImmediate());
        } else {
            client.updateVariable(id, "selected", new String[] { ""
                    + getSelectedItem() }, isImmediate());
        }
    }
}
