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
import com.vaadin.client.UIDL;

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

        updateEnabledState();
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
    public void buildOptions(UIDL uidl) {
        int scrollTop = select.getElement().getScrollTop();
        int rowCount = getRows();
        select.setMultipleSelect(isMultiselect());
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
        // FIXME: temporary hack for preserving the scroll state when the
        // contents haven't been changed obviously. This should be dealt with in
        // the rewrite.
        if (rowCount == getRows()) {
            select.getElement().setScrollTop(scrollTop);
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
