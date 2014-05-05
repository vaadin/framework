/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.UIDL;

public class VNativeSelect extends VOptionGroupBase implements Field {

    public static final String CLASSNAME = "v-select";

    protected ListBox select;

    private boolean firstValueIsTemporaryNullItem = false;

    public VNativeSelect() {
        super(new ListBox(false), CLASSNAME);
        select = getOptionsContainer();
        select.setVisibleItemCount(1);
        select.addChangeHandler(this);
        select.setStyleName(CLASSNAME + "-select");

        updateEnabledState();
    }

    protected ListBox getOptionsContainer() {
        return (ListBox) optionsContainer;
    }

    @Override
    public void buildOptions(UIDL uidl) {
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
            /*
             * Workaround to achrome bug that may cause value change event not
             * to fire when selection is done with keyboard.
             * 
             * http://dev.vaadin.com/ticket/10109
             * 
             * Problem is confirmed to exist only on Chrome-Win, but just
             * execute in for all webkits. Probably exists also in other
             * webkits/blinks on windows.
             */
            if (BrowserInfo.get().isWebkit()) {
                select.getElement().blur();
                select.getElement().focus();
            }

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
    public void setTabIndex(int tabIndex) {
        getOptionsContainer().setTabIndex(tabIndex);
    }

    @Override
    protected void updateEnabledState() {
        select.setEnabled(isEnabled() && !isReadonly());
    }

    @Override
    public void focus() {
        select.setFocus(true);
    }
}
