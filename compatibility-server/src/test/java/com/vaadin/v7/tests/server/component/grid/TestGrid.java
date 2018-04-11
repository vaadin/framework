package com.vaadin.v7.tests.server.component.grid;

import java.lang.reflect.Field;

import org.easymock.EasyMock;

import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.server.communication.data.RpcDataProviderExtension;
import com.vaadin.v7.ui.Grid;

/**
 * A Grid attached to a mock UI with a mock ConnectorTracker.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TestGrid extends Grid {

    public TestGrid() {
        super();
        init();
    }

    public TestGrid(IndexedContainer c) {
        super(c);
        init();
    }

    public RpcDataProviderExtension getDataProvider() throws Exception {
        Field dseField = Grid.class.getDeclaredField("datasourceExtension");
        dseField.setAccessible(true);
        return (RpcDataProviderExtension) dseField.get(this);
    }

    private void init() {
        UI mockUI = EasyMock.createNiceMock(UI.class);
        ConnectorTracker mockCT = EasyMock
                .createNiceMock(ConnectorTracker.class);
        EasyMock.expect(mockUI.getConnectorTracker()).andReturn(mockCT)
                .anyTimes();
        EasyMock.replay(mockUI, mockCT);

        setParent(mockUI);
    }
}
