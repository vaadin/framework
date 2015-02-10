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
package com.vaadin.tests.server.component.grid;

import java.lang.reflect.Field;

import org.easymock.EasyMock;

import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

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
