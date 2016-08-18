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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.UIDL;
import com.vaadin.shared.util.SharedUtil;

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
        select.setMultipleSelect(isMultiselect());

        Set<String> previousKeys = new HashSet<String>();
        for (int i = 0; i < select.getItemCount(); i++) {
            previousKeys.add(select.getValue(i));
        }

        int nextIndex = 0;
        if (!isMultiselect() && isNullSelectionAllowed()
                && !isNullSelectionItemAvailable()) {
            // can't unselect last item in singleselect mode
            updateOrCreateItem("", "null", nextIndex++, previousKeys);
            select.addItem("", (String) null);

            // Null select item can't be selected programmatically, but will
            // remain selected if it was selected by the user. There's no
            // need to deselect when something else is selected since it's only
            // used in single select mode.
        }
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL optionUidl = (UIDL) i.next();
            updateOrCreateItem(optionUidl.getStringAttribute("caption"),
                    optionUidl.getStringAttribute("key"), nextIndex,
                    previousKeys);
            if (optionUidl.hasAttribute("selected")) {
                select.setItemSelected(nextIndex, true);
                lastSelectedIndex = nextIndex;
            } else {
                select.setItemSelected(nextIndex, false);
            }
            nextIndex++;
        }

        // Remove any trailing items not in the UIDL
        while (select.getItemCount() > nextIndex) {
            select.removeItem(nextIndex);
        }

        if (getRows() > 0) {
            select.setVisibleItemCount(getRows());
        }
    }

    private void updateOrCreateItem(String caption, String key, int index,
            Set<String> previousKeys) {
        if (previousKeys.remove(key)) {
            while (select.getItemCount() >= index) {
                String keyAtIndex = select.getValue(index);
                if (SharedUtil.equals(key, keyAtIndex)) {
                    select.setItemText(index, caption);
                    return;
                } else {
                    // Assume the item we're looking at has simply been removed
                    // and that the next item will match our key
                    select.removeItem(index);
                    previousKeys.remove(keyAtIndex);
                }
            }
        }

        // We end up here for new items or if we removed all following items
        // while looking for a match
        select.insertItem(caption, key, index);
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
