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
package com.vaadin.client.ui.formlayout;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.VFormLayout;
import com.vaadin.client.ui.VFormLayout.Caption;
import com.vaadin.client.ui.VFormLayout.ErrorFlag;
import com.vaadin.client.ui.VFormLayout.VFormLayoutTable;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutState;
import com.vaadin.ui.FormLayout;

@Connect(FormLayout.class)
public class FormLayoutConnector extends AbstractLayoutConnector {

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        VFormLayoutTable formLayoutTable = getWidget().table;

        formLayoutTable.setMargins(new MarginInfo(getState().marginsBitmask));
        formLayoutTable.setSpacing(getState().spacing);

    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        VFormLayout formLayout = getWidget();
        VFormLayoutTable formLayoutTable = getWidget().table;

        int childId = 0;

        formLayoutTable.setRowCount(getChildComponents().size());

        for (ComponentConnector child : getChildComponents()) {
            Widget childWidget = child.getWidget();

            Caption caption = formLayoutTable.getCaption(childWidget);
            if (caption == null) {
                caption = formLayout.new Caption(child);
                caption.addClickHandler(formLayoutTable);
            }

            ErrorFlag error = formLayoutTable.getError(childWidget);
            if (error == null) {
                error = formLayout.new ErrorFlag(child);
            }

            formLayoutTable.setChild(childId, childWidget, caption, error);
            childId++;
        }

        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() == this) {
                continue;
            }

            formLayoutTable.cleanReferences(oldChild.getWidget());
        }

    }

    @Override
    public void updateCaption(ComponentConnector component) {
        getWidget().table.updateCaption(component.getWidget(),
                component.getState(), component.isEnabled());
        boolean hideErrors = false;

        // FIXME This incorrectly depends on AbstractFieldConnector
        if (component instanceof AbstractFieldConnector) {
            hideErrors = ((AbstractFieldConnector) component).getState().hideErrors;
        }

        getWidget().table.updateError(component.getWidget(),
                component.getState().errorMessage, hideErrors);
    }

    @Override
    public VFormLayout getWidget() {
        return (VFormLayout) super.getWidget();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        TooltipInfo info = null;

        if (element != getWidget().getElement()) {
            Object node = Util.findWidget(element, VFormLayout.Caption.class);

            if (node != null) {
                VFormLayout.Caption caption = (VFormLayout.Caption) node;
                info = caption.getOwner().getTooltipInfo(element);
            } else {

                node = Util.findWidget(element, VFormLayout.ErrorFlag.class);

                if (node != null) {
                    VFormLayout.ErrorFlag flag = (VFormLayout.ErrorFlag) node;
                    info = flag.getOwner().getTooltipInfo(element);
                }
            }
        }

        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tooltips are fetched from child connectors -> there's no quick way of
         * checking whether there might a tooltip hiding somewhere
         */
        return true;
    }

}
