/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VNativeSelect extends VOptionGroupBase implements Field {

    public static final String CLASSNAME = "i-select";

    protected TooltipListBox select;

    public VNativeSelect() {
        super(new TooltipListBox(false), CLASSNAME);
        select = (TooltipListBox) optionsContainer;
        select.setSelect(this);
        select.setVisibleItemCount(1);
        select.addChangeListener(this);
        select.setStyleName(CLASSNAME + "-select");

    }

    @Override
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
        if (BrowserInfo.get().isIE6()) {
            // lazy size change - IE6 uses naive dropdown that does not have a
            // proper size yet
            Util.notifyParentOfSizeChange(this, true);
        }
    }

    @Override
    protected Object[] getSelectedItems() {
        final Vector selectedItemKeys = new Vector();
        for (int i = 0; i < select.getItemCount(); i++) {
            if (select.isItemSelected(i)) {
                selectedItemKeys.add(select.getValue(i));
            }
        }
        return selectedItemKeys.toArray();
    }

    @Override
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

    @Override
    public void setHeight(String height) {
        select.setHeight(height);
        super.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        select.setWidth(width);
        super.setWidth(width);
    }

    @Override
    protected void setTabIndex(int tabIndex) {
        ((TooltipListBox) optionsContainer).setTabIndex(tabIndex);
    }

    public void focus() {
        select.setFocus(true);
    }

}
