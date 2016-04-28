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

import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Focusable;
import com.vaadin.client.UIDL;

public abstract class VOptionGroupBase extends Composite implements Field,
        ClickHandler, ChangeHandler, KeyPressHandler, Focusable, HasEnabled {

    public static final String CLASSNAME_OPTION = "v-select-option";

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public Set<String> selectedKeys;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean multiselect;

    private boolean enabled;

    private boolean readonly;

    /** For internal use only. May be removed or replaced in the future. */
    public int cols = 0;

    /** For internal use only. May be removed or replaced in the future. */
    public int rows = 0;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectionAllowed = true;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean nullSelectionItemAvailable = false;

    /**
     * Widget holding the different options (e.g. ListBox or Panel for radio
     * buttons) (optional, fallbacks to container Panel)
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public Widget optionsContainer;

    /**
     * Panel containing the component.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public final Panel container;

    /** For internal use only. May be removed or replaced in the future. */
    public VTextField newItemField;

    /** For internal use only. May be removed or replaced in the future. */
    public VNativeButton newItemButton;

    public VOptionGroupBase(String classname) {
        container = new FlowPanel();
        initWidget(container);
        optionsContainer = container;
        container.setStyleName(classname);
        immediate = false;
        multiselect = false;
    }

    /*
     * Call this if you wish to specify your own container for the option
     * elements (e.g. SELECT)
     */
    public VOptionGroupBase(Widget w, String classname) {
        this(classname);
        optionsContainer = w;
        container.add(optionsContainer);
    }

    protected boolean isImmediate() {
        return immediate;
    }

    protected boolean isMultiselect() {
        return multiselect;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isReadonly() {
        return readonly;
    }

    protected boolean isNullSelectionAllowed() {
        return nullSelectionAllowed;
    }

    protected boolean isNullSelectionItemAvailable() {
        return nullSelectionItemAvailable;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * @return "cols" specified in uidl, 0 if not specified
     */
    public int getColumns() {
        return cols;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * @return "rows" specified in uidl, 0 if not specified
     */
    public int getRows() {
        return rows;
    }

    public abstract void setTabIndex(int tabIndex);

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == newItemButton
                && !newItemField.getText().equals("")) {
            client.updateVariable(paintableId, "newitem",
                    newItemField.getText(), true);
            newItemField.setText("");
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        if (multiselect) {
            client.updateVariable(paintableId, "selected", getSelectedItems(),
                    immediate);
        } else {
            client.updateVariable(paintableId, "selected", new String[] { ""
                    + getSelectedItem() }, immediate);
        }
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (event.getSource() == newItemField
                && event.getCharCode() == KeyCodes.KEY_ENTER) {
            newItemButton.click();
        }
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

    /** For internal use only. May be removed or replaced in the future. */
    public abstract void buildOptions(UIDL uidl);

    protected abstract String[] getSelectedItems();

    protected abstract void updateEnabledState();

    protected String getSelectedItem() {
        final String[] sel = getSelectedItems();
        if (sel.length > 0) {
            return sel[0];
        } else {
            return null;
        }
    }

}
