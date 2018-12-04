package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.grid.GridConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.components.grid.GridExtensionCommunication.GridClickExtension;

import elemental.json.JsonObject;

@Connect(GridClickExtension.class)
public class GridClickExtensionConnector extends AbstractExtensionConnector {

    public interface GridClickServerRpc extends ServerRpc {

        public void click(String row, String column, MouseEventDetails click);
    }

    @Override
    protected void extend(ServerConnector target) {
        Grid<JsonObject> grid = getParent().getWidget();
        grid.addBodyClickHandler(this::handleClick);
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    private void handleClick(GridClickEvent event) {
        CellReference<?> cellRef = event.getTargetCell();

        // Gather needed information.
        JsonObject row = (JsonObject) cellRef.getRow();
        String rowKey = row.getString(DataCommunicatorConstants.KEY);
        String columnId = getParent().getColumnId(cellRef.getColumn());
        MouseEventDetails clickDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent());

        getRpcProxy(GridClickServerRpc.class).click(rowKey, columnId,
                clickDetails);
    }
}
