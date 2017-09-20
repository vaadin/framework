/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.Focusable;
import com.vaadin.client.connectors.AbstractMultiSelectConnector.MultiSelectWidget;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.listselect.ListSelectState;

import elemental.json.JsonObject;

/**
 * A simple list select for selecting multiple items.
 *
 * @author Vaadin Ltd
 */
public class VListSelect extends Composite
        implements Field, Focusable, HasEnabled, MultiSelectWidget {

    private List<BiConsumer<Set<String>, Set<String>>> selectionChangeListeners = new ArrayList<>();

    /** Container for select. Kept for DOM backwards compatibility. */
    protected final FlowPanel container;
    /** The select component. */
    protected final ListBox select;

    private boolean enabled;
    private boolean readOnly;
    private FastStringSet selectedItemKeys = FastStringSet.create();

    /**
     * Constructs a simple ListSelect widget in multiselect mode.
     */
    public VListSelect() {
        container = new FlowPanel();

        initWidget(container);

        select = new ListBox();
        select.setMultipleSelect(true);

        // Add event handlers
        select.addClickHandler(
                clickEvent -> selectionEvent(clickEvent.getSource()));
        select.addChangeHandler(
                changeEvent -> selectionEvent(changeEvent.getSource()));

        container.add(select);

        updateEnabledState();
        setStylePrimaryName(ListSelectState.PRIMARY_STYLENAME);
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        select.setStyleName(style + "-select");
    }

    /**
     * Sets the number of visible items for the list select.
     *
     * @param rows
     *            the number of items to show
     * @see ListBox#setVisibleItemCount(int)
     */
    public void setRows(int rows) {
        if (select.getVisibleItemCount() != rows) {
            select.setVisibleItemCount(rows);
        }
    }

    /**
     * Returns the number of visible items for the list select.
     *
     * @return the number of items to show
     * @see ListBox#setVisibleItemCount(int)
     */
    public int getRows() {
        return select.getVisibleItemCount();
    }

    @Override
    public Registration addSelectionChangeListener(
            BiConsumer<Set<String>, Set<String>> listener) {
        Objects.nonNull(listener);
        selectionChangeListeners.add(listener);
        return (Registration) () -> selectionChangeListeners.remove(listener);
    }

    @Override
    public void setItems(List<JsonObject> items) {
        selectedItemKeys = FastStringSet.create();
        for (int i = 0; i < items.size(); i++) {
            final JsonObject item = items.get(i);
            // reuse existing option if possible
            final String key = MultiSelectWidget.getKey(item);
            if (i < select.getItemCount()) {
                select.setItemText(i, MultiSelectWidget.getCaption(item));
                select.setValue(i, key);
            } else {
                select.addItem(MultiSelectWidget.getCaption(item), key);
            }
            final boolean selected = MultiSelectWidget.isSelected(item);
            select.setItemSelected(i, selected);
            if (selected) {
                selectedItemKeys.add(key);
            }
        }

        // remove extra
        for (int i = select.getItemCount() - 1; i >= items.size(); i--) {
            select.removeItem(i);
        }
    }

    /**
     * Gets the currently selected item values.
     *
     * @return the currently selected item keys
     */
    protected FastStringSet getSelectedItems() {
        final FastStringSet selectedItemKeys = FastStringSet.create();
        for (int i = 0; i < select.getItemCount(); i++) {
            if (select.isItemSelected(i)) {
                selectedItemKeys.add(select.getValue(i));
            }
        }
        return selectedItemKeys;
    }

    private void selectionEvent(Object source) {
        if (source == select) {
            // selection can change by adding and at the same time removing
            // previous keys, or by just adding (e.g. when modifier keys are
            // pressed)
            final Set<String> newSelectedItemKeys = new HashSet<>();
            final Set<String> removedItemKeys = new HashSet<>();
            for (int i = 0; i < select.getItemCount(); i++) {
                String key = select.getValue(i);
                boolean selected = select.isItemSelected(i);
                boolean wasSelected = selectedItemKeys.contains(key);
                if (selected && !wasSelected) {
                    newSelectedItemKeys.add(key);
                    selectedItemKeys.add(key);
                } else if (!selected && wasSelected) {
                    removedItemKeys.add(key);
                    selectedItemKeys.remove(key);
                }
            }
            selectionChangeListeners.forEach(
                    l -> l.accept(newSelectedItemKeys, removedItemKeys));
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

    /**
     * Sets the tab index.
     *
     * @param tabIndex
     *            the tab index to set
     */
    public void setTabIndex(int tabIndex) {
        select.setTabIndex(tabIndex);
    }

    /**
     * Gets the tab index.
     *
     * @return the tab index
     */
    public int getTabIndex() {
        return select.getTabIndex();
    }

    /**
     * Sets this select as read only, meaning selection cannot be changed.
     *
     * @param readOnly
     *            {@code true} for read only, {@code false} for not read only
     */
    public void setReadOnly(boolean readOnly) {
        if (this.readOnly != readOnly) {
            this.readOnly = readOnly;
            updateEnabledState();
        }
    }

    /**
     * Returns {@code true} if this select is in read only mode, {@code false}
     * if not.
     *
     * @return {@code true} for read only, {@code false} for not read only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            updateEnabledState();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void updateEnabledState() {
        select.setEnabled(isEnabled() && !isReadOnly());
    }

    @Override
    public void focus() {
        select.setFocus(true);
    }
}
