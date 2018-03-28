package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.connectors.grid.MultiSelectionModelConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(com.vaadin.tests.components.grid.GridCustomSelectionModel.MySelectionModel.class)
public class MySelectionModelConnector extends MultiSelectionModelConnector {

    protected class MyMultiSelectionModel extends MultiSelectionModel {
        @Override
        public Renderer<Boolean> getRenderer() {
            return null;
        }
    }

    private ClickSelectHandler<JsonObject> handler;

    @Override
    protected void initSelectionModel() {
        super.initSelectionModel();
        getGrid().setSelectionModel(new MyMultiSelectionModel());
        handler = new ClickSelectHandler<>(getGrid());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        handler.removeHandler();
        handler = null;
    }

}
