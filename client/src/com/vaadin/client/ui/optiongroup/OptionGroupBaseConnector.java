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

package com.vaadin.client.ui.optiongroup;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VNativeButton;
import com.vaadin.client.ui.VOptionGroupBase;
import com.vaadin.client.ui.VTextField;

public abstract class OptionGroupBaseConnector extends AbstractFieldConnector
        implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().selectedKeys = uidl.getStringArrayVariableAsSet("selected");

        getWidget().setReadonly(isReadOnly());
        getWidget().multiselect = "multi".equals(uidl
                .getStringAttribute("selectmode"));
        getWidget().immediate = getState().immediate;
        getWidget().nullSelectionAllowed = uidl
                .getBooleanAttribute("nullselect");
        getWidget().nullSelectionItemAvailable = uidl
                .getBooleanAttribute("nullselectitem");

        if (uidl.hasAttribute("cols")) {
            getWidget().cols = uidl.getIntAttribute("cols");
        }
        if (uidl.hasAttribute("rows")) {
            getWidget().rows = uidl.getIntAttribute("rows");
        }

        final UIDL ops = uidl.getChildUIDL(0);

        if (getWidget().getColumns() > 0) {
            getWidget().container.setWidth(getWidget().getColumns() + "em");
            if (getWidget().container != getWidget().optionsContainer) {
                getWidget().optionsContainer.setWidth("100%");
            }
        }

        getWidget().buildOptions(ops);

        if (uidl.getBooleanAttribute("allownewitem")) {
            if (getWidget().newItemField == null) {
                getWidget().newItemButton = new VNativeButton();
                getWidget().newItemButton.setText("+");
                getWidget().newItemButton.addClickHandler(getWidget());
                getWidget().newItemButton
                        .addStyleName(StyleConstants.UI_WIDGET);
                getWidget().newItemField = new VTextField();
                getWidget().newItemField.client = getConnection();
                getWidget().newItemField.paintableId = getConnectorId();
                getWidget().newItemField.addKeyPressHandler(getWidget());
                getWidget().newItemField.addStyleName(StyleConstants.UI_WIDGET);

            }
            getWidget().newItemField.setEnabled(getWidget().isEnabled()
                    && !getWidget().isReadonly());
            getWidget().newItemButton.setEnabled(getWidget().isEnabled()
                    && !getWidget().isReadonly());

            if (getWidget().newItemField == null
                    || getWidget().newItemField.getParent() != getWidget().container) {
                getWidget().container.add(getWidget().newItemField);
                getWidget().container.add(getWidget().newItemButton);
                final int w = getWidget().container.getOffsetWidth()
                        - getWidget().newItemButton.getOffsetWidth();
                getWidget().newItemField.setWidth(Math.max(w, 0) + "px");
            }
        } else if (getWidget().newItemField != null) {
            getWidget().container.remove(getWidget().newItemField);
            getWidget().container.remove(getWidget().newItemButton);
        }

        getWidget().setTabIndex(getState().tabIndex);

    }

    @Override
    public VOptionGroupBase getWidget() {
        return (VOptionGroupBase) super.getWidget();
    }
}
