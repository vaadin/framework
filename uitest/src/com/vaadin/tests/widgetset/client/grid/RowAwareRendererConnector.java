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
package com.vaadin.tests.widgetset.client.grid;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(com.vaadin.tests.components.grid.RowAwareRenderer.class)
public class RowAwareRendererConnector extends AbstractRendererConnector<Void> {
    public interface RowAwareRendererRpc extends ServerRpc {
        void clicky(String key);
    }

    public class RowAwareRenderer extends ComplexRenderer<Void> {

        @Override
        public Collection<String> getConsumedEvents() {
            return Arrays.asList(BrowserEvents.CLICK);
        }

        @Override
        public void init(RendererCellReference cell) {
            DivElement div = DivElement.as(DOM.createDiv());
            div.setAttribute("style",
                    "border: 1px solid red; background: pink;");
            div.setInnerText("Click me!");
            cell.getElement().appendChild(div);
        }

        @Override
        public void render(RendererCellReference cell, Void data) {
            // NOOP
        }

        @Override
        public boolean onBrowserEvent(CellReference<?> cell, NativeEvent event) {
            String key = getRowKey((JsonObject) cell.getRow());
            getRpcProxy(RowAwareRendererRpc.class).clicky(key);
            cell.getElement().setInnerText(
                    "row: " + cell.getRowIndex() + ", key: " + key);
            return true;
        }
    }

    @Override
    protected Renderer<Void> createRenderer() {
        // cannot use the default createRenderer as RowAwareRenderer needs a
        // reference to its connector - it has no "real" no-argument constructor
        return new RowAwareRenderer();
    }
}
