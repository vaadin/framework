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
package com.vaadin.client.connectors.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.ComponentRendererState;
import com.vaadin.ui.renderers.ComponentRenderer;

/**
 * Connector for {@link ComponentRenderer}. The renderer wraps the component
 * into a {@link SimplePanel} to allow handling events correctly.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(ComponentRenderer.class)
public class ComponentRendererConnector
        extends AbstractGridRendererConnector<String> {

    @Override
    protected Renderer<String> createRenderer() {
        return new WidgetRenderer<String, SimplePanel>() {

            @Override
            public SimplePanel createWidget() {
                return GWT.create(SimplePanel.class);
            }

            @Override
            public void render(RendererCellReference cell, String connectorId,
                    SimplePanel widget) {
                ComponentConnector connector = (ComponentConnector) ConnectorMap
                        .get(getConnection()).getConnector(connectorId);
                widget.setWidget(connector.getWidget());
            }
        };
    }

    @Override
    public ComponentRendererState getState() {
        return (ComponentRendererState) super.getState();
    }
}
