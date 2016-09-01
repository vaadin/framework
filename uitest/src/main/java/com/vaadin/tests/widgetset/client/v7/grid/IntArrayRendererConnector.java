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
package com.vaadin.tests.widgetset.client.v7.grid;

import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.connectors.AbstractGridRendererConnector;
import com.vaadin.v7.client.renderers.Renderer;
import com.vaadin.v7.client.widget.grid.RendererCellReference;

@Connect(com.vaadin.v7.tests.components.grid.IntArrayRenderer.class)
public class IntArrayRendererConnector
        extends AbstractGridRendererConnector<int[]> {

    public static class IntArrayRenderer implements Renderer<int[]> {
        private static final String JOINER = " :: ";

        @Override
        public void render(RendererCellReference cell, int[] data) {
            String text = "";
            for (int i : data) {
                text += i + JOINER;
            }
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - JOINER.length());
            }
            cell.getElement().setInnerText(text);
        }
    }

    @Override
    public IntArrayRenderer getRenderer() {
        return (IntArrayRenderer) super.getRenderer();
    }
}
