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
package com.vaadin.client.connectors;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.metadata.TypeDataStore;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.client.renderers.ImageRenderer;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * A connector for {@link ImageRenderer}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.renderers.ImageRenderer.class)
public class ImageRendererConnector extends ClickableRendererConnector<String> {

    @Override
    public ImageRenderer getRenderer() {
        return (ImageRenderer) super.getRenderer();
    }

    @Override
    public String decode(JsonValue value) {
        URLReference reference = (URLReference) JsonDecoder.decodeValue(
                TypeDataStore.getType(URLReference.class), value, null,
                getConnection());

        return reference != null ? reference.getURL() : null;
    }

    @Override
    protected HandlerRegistration addClickHandler(
            RendererClickHandler<JsonObject> handler) {
        return getRenderer().addClickHandler(handler);
    }
}
