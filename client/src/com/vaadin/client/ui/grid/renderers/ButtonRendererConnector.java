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
package com.vaadin.client.ui.grid.renderers;

import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.ui.grid.renderers.ButtonRenderer.RendererClickEvent;
import com.vaadin.client.ui.grid.renderers.ButtonRenderer.RendererClickHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.RendererClickRpc;

/**
 * A connector for {@link ButtonRenderer}.
 * 
 * @since
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.components.grid.renderers.ButtonRenderer.class)
public class ButtonRendererConnector extends AbstractRendererConnector<String>
        implements RendererClickHandler<JSONObject> {

    HandlerRegistration clickRegistration;

    @Override
    protected void init() {
        clickRegistration = getRenderer().addClickHandler(this);
    }

    @Override
    public void onUnregister() {
        clickRegistration.removeHandler();
    }

    @Override
    public ButtonRenderer<JSONObject> getRenderer() {
        return (ButtonRenderer<JSONObject>) super.getRenderer();
    }

    @Override
    public void onClick(RendererClickEvent<JSONObject> event) {
        getRpcProxy(RendererClickRpc.class).click(
                event.getCell().getRow(),
                event.getCell().getColumn(),
                MouseEventDetailsBuilder.buildMouseEventDetails(event
                        .getNativeEvent()));
    }
}
