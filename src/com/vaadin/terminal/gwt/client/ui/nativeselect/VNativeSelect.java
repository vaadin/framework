/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.nativeselect;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Field;
import com.vaadin.terminal.gwt.client.ui.listselect.TooltipListBox;
import com.vaadin.terminal.gwt.client.ui.optiongroup.VOptionGroupBase;

public class VNativeSelect extends VOptionGroupBase implements Field {

    public static final String CLASSNAME = "v-select";

    protected TooltipListBox select;

    private boolean firstValueIsTemporaryNullItem = false;

    public VNativeSelect() {
        super(new TooltipListBox(false), CLASSNAME);
        select = (TooltipListBox) optionsContainer;
        select.setSelect(this);
        select.setVisibleItemCount(1);
        select.addChangeHandler(this);
        select.setStyleName(CLASSNAME + "-select");

    }

    @Override
    protected void buildOptions(UIDL uidl) {
        select.setClient(client);
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        firstValueIsTemporaryNullItem = false;

        if (isNullSelectionAllowed() && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            select.addItem("", (String) null);
        }
        boolean selected = false;
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            select.addItem(optionUidl.getStringAttribute("caption"),
                    optionUidl.getStringAttribute("key"));
            if (optionUidl.hasAttribute("selected")) {
                select.setItemSelected(select.getItemCount() - 1, true);
                selected = true;
            }
        }
        if (!selected && !isNullSelectionAllowed()) {
            // null-select not allowed, but value not selected yet; add null and
            // remove when something is selected
            select.insertItem("", (String) null, 0);
            select.setItemSelected(0, true);
            firstValueIsTemporaryNullItem = true;
        }
    }

    @Override
    protected String[] getSelectedItems() {
        final ArrayList<String> selectedItemKeys = new ArrayList<String>();
        for (int i = 0; i < select.getItemCount(); i++) {
            if (select.isItemSelected(i)) {
                selectedItemKeys.add(select.getValue(i));
            }
        }
        return selectedItemKeys.toArray(new String[selectedItemKeys.size()]);
    }

    @Override
    public void onChange(ChangeEvent event) {

        if (select.isMultipleSelect()) {
            client.updateVariable(paintableId, "selected", getSelectedItems(),
                    isImmediate());
        } else {
            client.updateVariable(paintableId, "selected", new String[] { ""
                    + getSelectedItem() }, isImmediate());
        }
        if (firstValueIsTemporaryNullItem) {
            // remove temporary empty item
            select.removeItem(0);
            firstValueIsTemporaryNullItem = false;
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
