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

import com.vaadin.client.renderers.ProgressBarRenderer;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.ProgressBarRendererState;

/**
 * A connector for {@link ProgressBarRenderer}.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.renderers.ProgressBarRenderer.class)
public class ProgressBarRendererConnector
        extends AbstractGridRendererConnector<Double> {

    @Override
    public ProgressBarRenderer getRenderer() {
        return (ProgressBarRenderer) super.getRenderer();
    }

    @Override
    public ProgressBarRendererState getState() {
        return (ProgressBarRendererState) super.getState();
    }
}
