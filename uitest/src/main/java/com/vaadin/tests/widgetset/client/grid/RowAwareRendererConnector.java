package com.vaadin.tests.widgetset.client.grid;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.connectors.grid.AbstractGridRendererConnector;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.client.EmptyEnum;

import elemental.json.JsonObject;

@Connect(com.vaadin.tests.components.grid.RowAwareRenderer.class)
public class RowAwareRendererConnector
        extends AbstractGridRendererConnector<EmptyEnum> {
    public interface RowAwareRendererRpc extends ServerRpc {
        void clicky(String key);
    }

    public class RowAwareRenderer extends ComplexRenderer<EmptyEnum> {

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
        public void render(RendererCellReference cell, EmptyEnum data) {
            // NOOP
        }

        @Override
        public boolean onBrowserEvent(CellReference<?> cell,
                NativeEvent event) {
            String key = getRowKey((JsonObject) cell.getRow());
            getRpcProxy(RowAwareRendererRpc.class).clicky(key);
            cell.getElement().setInnerText(
                    "row: " + cell.getRowIndex() + ", key: " + key);
            return true;
        }
    }

    @Override
    protected Renderer<EmptyEnum> createRenderer() {
        // cannot use the default createRenderer as RowAwareRenderer needs a
        // reference to its connector - it has no "real" no-argument constructor
        return new RowAwareRenderer();
    }
}
