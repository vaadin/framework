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
package com.vaadin.client.ui.customcomponent;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.VCustomComponent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.ui.CustomComponent;

@Connect(value = CustomComponent.class, loadStyle = LoadStyle.EAGER)
public class CustomComponentConnector extends AbstractHasComponentsConnector {

    @Override
    public VCustomComponent getWidget() {
        return (VCustomComponent) super.getWidget();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP, custom component dont render composition roots caption
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        VCustomComponent customComponent = getWidget();
        if (getChildComponents().size() == 1) {
            ComponentConnector newChild = getChildComponents().get(0);
            customComponent.setWidget(newChild.getWidget());
        } else {
            customComponent.setWidget(null);
        }

    }
}
