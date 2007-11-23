package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IListSelect extends IOptionGroupBase {

    public static final String CLASSNAME = "i-select";

    private static final int VISIBLE_COUNT = 10;

    protected ListBox select;

    private int lastSelectedIndex = -1;

    public IListSelect() {
        super(new ListBox(), CLASSNAME);
        select = (ListBox) optionsContainer;
        select.addChangeListener(this);
        select.addClickListener(this);
        select.setStyleName(CLASSNAME + "-select");
        select.setVisibleItemCount(VISIBLE_COUNT);
    }

    protected void buildOptions(UIDL uidl) {
        select.setMultipleSelect(isMultiselect());
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        if (!isMultiselect() && isNullSelectionAllowed()
                && !isNullSelectionItemAvailable()) {
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
        if (getRows() > 0) {
            select.setVisibleItemCount(getRows());
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
        int si = select.getSelectedIndex();
        if (si == -1 && !isNullSelectionAllowed()) {
            select.setSelectedIndex(lastSelectedIndex);
        } else {
            lastSelectedIndex = si;
            if (isMultiselect()) {
                client.updateVariable(id, "selected", getSelectedItems(),
                        isImmediate());
            } else {
                client.updateVariable(id, "selected", new String[] { ""
                        + getSelectedItem() }, isImmediate());
            }
        }
    }

}
