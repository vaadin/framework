package com.vaadin.tests.widgetset.client.data;

import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.VLabel;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.data.DummyData.DummyComponent;

import elemental.json.JsonObject;

@Connect(DummyComponent.class)
public class DummyComponentConnector extends AbstractListingConnector {

    @Override
    public FlowPanel getWidget() {
        return (FlowPanel) super.getWidget();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);

        dataSource.addDataChangeHandler(range -> {
            assert range.getStart() == 0 && range.getEnd() == dataSource
                    .size() : "Widget only supports full updates.";
            getWidget().clear();
            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                VLabel label = new VLabel();
                getWidget().add(label);
                JsonObject row = dataSource.getRow(i);
                String text = row.getString(DataCommunicatorConstants.DATA);
                if (row.hasKey(DataCommunicatorConstants.SELECTED)
                        && row.getBoolean(DataCommunicatorConstants.SELECTED)) {
                    text = "<b>" + text + "</b>";
                    label.addStyleName("selected");
                }
                label.setHTML(text);
            }
        });
    }
}
