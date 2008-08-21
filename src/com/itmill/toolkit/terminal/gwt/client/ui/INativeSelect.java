/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class INativeSelect extends IOptionGroupBase implements Field {

    public static final String CLASSNAME = "i-select";

    protected TooltipListBox select;

    public INativeSelect() {
        super(new TooltipListBox(false), CLASSNAME);
        select = (TooltipListBox) optionsContainer;
        select.setSelect(this);
        select.setVisibleItemCount(1);
        select.addChangeListener(this);
        select.setStyleName(CLASSNAME + "-select");

    }

    protected void buildOptions(UIDL uidl) {
        select.setClient(client);
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        if (isNullSelectionAllowed() && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            select.addItem("", null);
        }
        boolean selected = false;
        for (final Iterator i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            select.addItem(optionUidl.getStringAttribute("caption"), optionUidl
                    .getStringAttribute("key"));
            if (optionUidl.hasAttribute("selected")) {
                select.setItemSelected(select.getItemCount() - 1, true);
                selected = true;
            }
        }
        if (!selected && !isNullSelectionAllowed()) {
            // null-select not allowed, but value not selected yet; add null and
            // remove when something is selected
            select.insertItem("", null, 0);
            select.setItemSelected(0, true);
        }

    }

    protected Object[] getSelectedItems() {
        final Vector selectedItemKeys = new Vector();
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
        if (!isNullSelectionAllowed() && "null".equals(select.getValue(0))) {
            // remove temporary empty item
            select.removeItem(0);
        }
    }

    public void setHeight(String height) {
        select.setHeight(height);
        super.setHeight(height);
    }

    public void setWidth(String width) {
        select.setWidth(width);
        super.setWidth(width);
    }

}
