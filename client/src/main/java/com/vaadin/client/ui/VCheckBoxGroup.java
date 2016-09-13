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

import static com.vaadin.shared.ui.optiongroup.CheckBoxGroupConstants.JSONKEY_ITEM_DISABLED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupConstants;

import elemental.json.JsonObject;

/**
 * The client-side widget for the {@code CheckBoxGroup} component.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class VCheckBoxGroup extends Composite implements Field, ClickHandler,
        com.vaadin.client.Focusable, HasEnabled {

    public static final String CLASSNAME = "v-select-optiongroup";
    public static final String CLASSNAME_OPTION = "v-select-option";

    private final Map<VCheckBox, JsonObject> optionsToItems;

    /**
     * For internal use only. May be removed or replaced in the future.
     */
    public ApplicationConnection client;

    /**
     * Widget holding the different options (e.g. ListBox or Panel for radio
     * buttons) (optional, fallbacks to container Panel)
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public Panel optionsContainer;

    private boolean htmlContentAllowed = false;

    private boolean enabled;
    private boolean readonly;
    private List<BiConsumer<JsonObject, Boolean>> selectionChangeListeners;

    public VCheckBoxGroup() {
        optionsContainer = new FlowPanel();
        initWidget(this.optionsContainer);
        optionsContainer.setStyleName(CLASSNAME);
        optionsToItems = new HashMap<>();
        selectionChangeListeners = new ArrayList<>();
    }

    /*
     * Build all the options
     */
    public void buildOptions(List<JsonObject> items) {
        /*
         * In order to retain focus, we need to update values rather than
         * recreate panel from scratch (#10451). However, the panel will be
         * rebuilt (losing focus) if number of elements or their order is
         * changed.
         */

        Roles.getRadiogroupRole().set(getElement());
        optionsContainer.clear();
        for (JsonObject item : items) {
            String itemHtml = item
                    .getString(CheckBoxGroupConstants.JSONKEY_ITEM_VALUE);
            if (!isHtmlContentAllowed()) {
                itemHtml = WidgetUtil.escapeHTML(itemHtml);
            }
            VCheckBox checkBox = new VCheckBox();

            String iconUrl = item
                    .getString(CheckBoxGroupConstants.JSONKEY_ITEM_ICON);
            if (iconUrl != null && iconUrl.length() != 0) {
                checkBox.icon = client.getIcon(iconUrl);
            }

            checkBox.addStyleName(CLASSNAME_OPTION);
            checkBox.addClickHandler(this);
            checkBox.setHTML(itemHtml);
            checkBox.setValue(item
                    .getBoolean(CheckBoxGroupConstants.JSONKEY_ITEM_SELECTED));
            boolean optionEnabled = !item.getBoolean(JSONKEY_ITEM_DISABLED);
            boolean enabled = optionEnabled && !isReadonly() && isEnabled();
            checkBox.setEnabled(enabled);

            optionsContainer.add(checkBox);
            optionsToItems.put(checkBox, item);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof VCheckBox) {
            VCheckBox source = (VCheckBox) event.getSource();
            if (!source.isEnabled()) {
                // Click events on the text are received even though the
                // checkbox is disabled
                return;
            }

            Boolean selected = source.getValue();

            JsonObject item = optionsToItems.get(source);
            assert item != null;

            new ArrayList<>(selectionChangeListeners)
                    .forEach(listener -> listener.accept(item, selected));
        }
    }

    public void setTabIndex(int tabIndex) {
        for (Widget anOptionsContainer : optionsContainer) {
            FocusWidget widget = (FocusWidget) anOptionsContainer;
            widget.setTabIndex(tabIndex);
        }
    }

    protected void updateEnabledState() {
        boolean optionGroupEnabled = isEnabled() && !isReadonly();
        // sets options enabled according to the widget's enabled,
        // readonly and each options own enabled
        for (Map.Entry<VCheckBox, JsonObject> entry : optionsToItems
                .entrySet()) {
            VCheckBox checkBox = entry.getKey();
            JsonObject value = entry.getValue();
            Boolean isOptionEnabled = !value
                    .getBoolean(CheckBoxGroupConstants.JSONKEY_ITEM_DISABLED);
            checkBox.setEnabled(optionGroupEnabled && isOptionEnabled);
        }
    }

    @Override
    public void focus() {
        Iterator<Widget> iterator = optionsContainer.iterator();
        if (iterator.hasNext()) {
            ((Focusable) iterator.next()).setFocus(true);
        }
    }

    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        if (this.readonly != readonly) {
            this.readonly = readonly;
            updateEnabledState();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            updateEnabledState();
        }
    }

    public Registration addSelectionChangeHandler(
            BiConsumer<JsonObject, Boolean> selectionChanged) {
        selectionChangeListeners.add(selectionChanged);
        return (Registration) () -> selectionChangeListeners
                .remove(selectionChanged);
    }
}
