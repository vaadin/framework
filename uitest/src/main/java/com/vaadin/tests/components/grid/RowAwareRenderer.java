package com.vaadin.tests.components.grid;

import com.vaadin.tests.components.grid.CustomRendererUI.Data;
import com.vaadin.tests.widgetset.client.EmptyEnum;
import com.vaadin.tests.widgetset.client.grid.RowAwareRendererConnector.RowAwareRendererRpc;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.AbstractRenderer;

public class RowAwareRenderer extends AbstractRenderer<Data, EmptyEnum> {
    public RowAwareRenderer(final Label debugLabel) {
        super(EmptyEnum.class, "");
        registerRpc(new RowAwareRendererRpc() {
            @Override
            public void clicky(String key) {
                Data data = getParentGrid().getDataCommunicator().getKeyMapper()
                        .get(key);
                debugLabel.setValue("key: " + key + ", itemId: " + data);
            }
        });
    }
}
