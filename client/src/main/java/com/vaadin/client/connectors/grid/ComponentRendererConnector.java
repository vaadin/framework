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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.ComponentRendererState;
import com.vaadin.ui.renderers.ComponentRenderer;
import elemental.json.JsonObject;

/**
 * Connector for {@link ComponentRenderer}. The renderer wraps the component
 * into a {@link SimplePanel} to allow handling events correctly.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(ComponentRenderer.class)
public class ComponentRendererConnector
        extends ClickableRendererConnector<String> {

    @Override
    public com.vaadin.client.connectors.grid.ComponentRenderer getRenderer() {
        return (com.vaadin.client.connectors.grid.ComponentRenderer) super.getRenderer();
    }

    @Override
    protected HandlerRegistration addClickHandler(ClickableRenderer.RendererClickHandler<JsonObject> handler) {
        return getRenderer().addClickHandler(handler);
    }

    @Override
    protected ClickableRenderer<String, SimplePanel> createRenderer() {
        return new com.vaadin.client.connectors.grid.ComponentRenderer() {
            @Override
            public ApplicationConnection getConnectorConnection() {
                return getConnection();
            }
        };
    }

    @Override
    public ComponentRendererState getState() {
        return (ComponentRendererState) super.getState();
    }
}
