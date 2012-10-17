/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client.ui.listselect;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.optiongroup.VOptionGroupBase;

public class VListSelect extends VOptionGroupBase {

    public static final String CLASSNAME = "v-select";

    private static final int VISIBLE_COUNT = 10;

    protected ListBox select;

    private int lastSelectedIndex = -1;

    public VListSelect() {
        super(new ListBox(true), CLASSNAME);
        select = getOptionsContainer();
        select.addChangeHandler(this);
        select.addClickHandler(this);
        select.setVisibleItemCount(VISIBLE_COUNT);
        setStyleName(CLASSNAME);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames();
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        updateStyleNames();
    }

    protected void updateStyleNames() {
        container.setStyleName(getStylePrimaryName());
        select.setStyleName(getStylePrimaryName() + "-select");
    }

    protected ListBox getOptionsContainer() {
        return (ListBox) optionsContainer;
    }

    @Override
    protected void buildOptions(UIDL uidl) {
        select.setMultipleSelect(isMultiselect());
        select.setEnabled(!isDisabled() && !isReadonly());
        select.clear();
        if (!isMultiselect() && isNullSelectionAllowed()
                && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            select.addItem("", (String) null);
        }
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            select.addItem(optionUidl.getStringAttribute("caption"),
                    optionUidl.getStringAttribute("key"));
            if (optionUidl.hasAttribute("selected")) {
                int itemIndex = select.getItemCount() - 1;
                select.setItemSelected(itemIndex, true);
                lastSelectedIndex = itemIndex;
            }
        }
        if (getRows() > 0) {
            select.setVisibleItemCount(getRows());
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
        final int si = select.getSelectedIndex();
        if (si == -1 && !isNullSelectionAllowed()) {
            select.setSelectedIndex(lastSelectedIndex);
        } else {
            lastSelectedIndex = si;
            if (isMultiselect()) {
                client.updateVariable(paintableId, "selected",
                        getSelectedItems(), isImmediate());
            } else {
                client.updateVariable(paintableId, "selected",
                        new String[] { "" + getSelectedItem() }, isImmediate());
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
    protected void setTabIndex(int tabIndex) {
        getOptionsContainer().setTabIndex(tabIndex);
    }

    @Override
    public void focus() {
        select.setFocus(true);
    }
}