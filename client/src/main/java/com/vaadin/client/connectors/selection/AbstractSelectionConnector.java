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
package com.vaadin.client.connectors.selection;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.data.selection.SelectionModel;

/**
 * The client-side connector for selection extensions.
 * 
 * @author Vaadin Ltd.
 * 
 * @since
 */
public abstract class AbstractSelectionConnector extends
        AbstractExtensionConnector {

    private SelectionModel<String> model = null;

    @Override
    protected void extend(ServerConnector target) {
        if (!(target instanceof AbstractListingConnector)) {
            throw new IllegalArgumentException(
                    "Cannot extend a connector that is not an "
                            + AbstractListingConnector.class.getSimpleName());
        }
        model = createSelectionModel();
        ((AbstractListingConnector) target).setSelectionModel(model);
    }

    /**
     * Creates a selection model object to be used by the Connector.
     * 
     * @return created selection model
     */
    protected abstract SelectionModel<String> createSelectionModel();

    @Override
    public AbstractListingConnector getParent() {
        return (AbstractListingConnector) super.getParent();
    }

    /**
     * Returns the client-side selection model associated with this connector.
     * 
     * @return the selection model in use
     */
    protected SelectionModel<String> getSelectionModel() {
        return model;
    }
}
