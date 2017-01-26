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

import com.vaadin.client.renderers.ObjectRenderer;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.ObjectRendererState;

/**
 * Connector for {@link ObjectRenderer}.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.renderers.ObjectRenderer.class)
public class ObjectRendererConnector
        extends AbstractGridRendererConnector<String> {

    @Override
    public ObjectRenderer getRenderer() {
        return (ObjectRenderer) super.getRenderer();
    }

    @Override
    public ObjectRendererState getState() {
        return (ObjectRendererState) super.getState();
    }
}
