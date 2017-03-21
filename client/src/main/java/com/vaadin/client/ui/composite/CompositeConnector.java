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
package com.vaadin.client.ui.composite;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.ui.Composite;

@Connect(value = Composite.class, loadStyle = LoadStyle.EAGER)
public class CompositeConnector extends AbstractHasComponentsConnector {

    @Override
    protected Widget createWidget() {
        throw new UnsupportedOperationException(
                "Composite has no widget of its own");
    }

    private boolean hasChildConnector() {
        return !getChildComponents().isEmpty();
    }

    private ComponentConnector getChildConnector() {
        assert hasChildConnector();
        return getChildComponents().get(0);
    }

    @Override
    public Widget getWidget() {
        if (!hasChildConnector()) {
            // This happens in doInit for instance when setConnectorId is called
            return new Label("This widget should not end up anywhere ever");
        } else {
            return getChildConnector().getWidget();
        }
    }

    @Override
    public HasComponentsConnector getParent() {
        return (HasComponentsConnector) super.getParent();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        getParent().updateCaption(this);
    }

    @Override
    public AbstractComponentState getState() {
        if (!hasChildConnector()) {
            return new AbstractComponentState();
        } else {
            return getChildConnector().getState();
        }
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
    }
}
