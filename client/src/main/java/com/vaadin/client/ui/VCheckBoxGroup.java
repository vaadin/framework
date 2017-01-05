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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widgets.FocusableFlowPanelComposite;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ListingJsonConstants;

import elemental.json.JsonObject;

/**
 * The client-side widget for the {@code CheckBoxGroup} component.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class VCheckBoxGroup extends FocusableFlowPanelComposite
        implements Field, ClickHandler, HasEnabled {

    public static final String CLASSNAME = "v-select-optiongroup";
    public static final String CLASSNAME_OPTION = "v-select-option";

    private final Map<VCheckBox, JsonObject> optionsToItems;

    /**
     * For internal use only. May be removed or replaced in the future.
     */
    public ApplicationConnection client;

    private boolean htmlContentAllowed = false;

    private boolean enabled;
    private boolean readonly;
    private List<BiConsumer<JsonObject, Boolean>> selectionChangeListeners;

    public VCheckBoxGroup() {
        getWidget().setStyleName(CLASSNAME);
        optionsToItems = new HashMap<>();
        selectionChangeListeners = new ArrayList<>();
    }

    /*
     * Build all the options
     */
    public void buildOptions(List<JsonObject> items) {
        Roles.getGroupRole().set(getElement());
        int i = 0;
        int widgetsToRemove = getWidget().getWidgetCount() - items.size();
        if (widgetsToRemove < 0) {
            widgetsToRemove = 0;
        }
        List<Widget> remove = new ArrayList<>(widgetsToRemove);
        for (Widget widget : getWidget()) {
            if (i < items.size()) {
                updateItem((VCheckBox) widget, items.get(i), false);
                i++;
            } else {
                remove.add(widget);
            }
        }
        remove.stream().forEach(this::remove);
        while (i < items.size()) {
            updateItem(new VCheckBox(), items.get(i), true);
            i++;
        }
    }

    private void remove(Widget widget) {
        getWidget().remove(widget);
        optionsToItems.remove(widget);
    }

    private void updateItem(VCheckBox widget, JsonObject item,
            boolean requireInitialization) {
        String itemHtml = item
                .getString(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        if (!isHtmlContentAllowed()) {
            itemHtml = WidgetUtil.escapeHTML(itemHtml);
        }

        String iconUrl = item.getString(ListingJsonConstants.JSONKEY_ITEM_ICON);
        if (iconUrl != null && iconUrl.length() != 0) {
            Icon icon = client.getIcon(iconUrl);
            itemHtml = icon.getElement().getString() + itemHtml;
        }

        widget.setHTML(itemHtml);
        widget.setValue(
                item.getBoolean(ListingJsonConstants.JSONKEY_ITEM_SELECTED));
        setOptionEnabled(widget, item);

        if (requireInitialization) {
            widget.addStyleName(CLASSNAME_OPTION);
            widget.addClickHandler(this);
            getWidget().add(widget);
        }
        optionsToItems.put(widget, item);
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
            if (BrowserInfo.get().isWebkit() || BrowserInfo.get().isIE11()) {
                // Webkit does not focus non-text input elements on click
                // (#11854)
                source.setFocus(true);
            }
            Boolean selected = source.getValue();

            JsonObject item = optionsToItems.get(source);
            assert item != null;

            new ArrayList<>(selectionChangeListeners)
                    .forEach(listener -> listener.accept(item, selected));
        }
    }

    public void setTabIndex(int tabIndex) {
        for (Widget anOptionsContainer : getWidget()) {
            FocusWidget widget = (FocusWidget) anOptionsContainer;
            widget.setTabIndex(tabIndex);
        }
    }

    /**
     * Updates the checkbox's enabled state according to the widget's enabled,
     * read only and the item's enabled.
     *
     * @param checkBox
     *            the checkbox to update
     * @param item
     *            the item for the checkbox
     */
    protected void setOptionEnabled(VCheckBox checkBox, JsonObject item) {
        boolean optionEnabled = !item
                .getBoolean(ListingJsonConstants.JSONKEY_ITEM_DISABLED);
        boolean enabled = optionEnabled && !isReadonly() && isEnabled();
        checkBox.setEnabled(enabled);
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
            optionsToItems.forEach(this::setOptionEnabled);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            optionsToItems.forEach(this::setOptionEnabled);
        }
    }

    public Registration addSelectionChangeHandler(
            BiConsumer<JsonObject, Boolean> selectionChanged) {
        selectionChangeListeners.add(selectionChanged);
        return (Registration) () -> selectionChangeListeners
                .remove(selectionChanged);
    }

}
