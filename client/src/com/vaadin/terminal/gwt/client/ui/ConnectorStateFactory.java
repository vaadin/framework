/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.SharedState;

public abstract class ConnectorStateFactory extends
        ConnectorClassBasedFactory<SharedState> {
    private static ConnectorStateFactory impl = null;

    /**
     * Creates a SharedState using GWT.create for the given connector, based on
     * its {@link AbstractComponentConnector#getSharedState ()} return type.
     * 
     * @param connector
     * @return
     */
    public static SharedState createState(Class<? extends Connector> connector) {
        return getImpl().create(connector);
    }

    private static ConnectorStateFactory getImpl() {
        if (impl == null) {
            impl = GWT.create(ConnectorStateFactory.class);
        }
        return impl;
    }
}
