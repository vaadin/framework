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
package com.vaadin.event;

import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;

/**
 * Parent class for {@link Transferable} implementations that have a Vaadin
 * container as a data source. The transfer is associated with an item
 * (identified by its Id) and optionally also a property identifier (e.g. a
 * table column identifier when transferring a single table cell).
 * 
 * The component must implement the interface
 * {@link com.vaadin.data.Container.Viewer}.
 * 
 * In most cases, receivers of data transfers should depend on this class
 * instead of its concrete subclasses.
 * 
 * @since 6.3
 */
public abstract class DataBoundTransferable extends TransferableImpl {

    public DataBoundTransferable(Component sourceComponent,
            Map<String, Object> rawVariables) {
        super(sourceComponent, rawVariables);
    }

    /**
     * Returns the identifier of the item being transferred.
     * 
     * @return item identifier
     */
    public abstract Object getItemId();

    /**
     * Returns the optional property identifier that the transfer concerns.
     * 
     * This can be e.g. the table column from which a drag operation originated.
     * 
     * @return property identifier
     */
    public abstract Object getPropertyId();

    /**
     * Returns the container data source from which the transfer occurs.
     * 
     * {@link com.vaadin.data.Container.Viewer#getContainerDataSource()} is used
     * to obtain the underlying container of the source component.
     * 
     * @return Container
     */
    public Container getSourceContainer() {
        Component sourceComponent = getSourceComponent();
        if (sourceComponent instanceof Container.Viewer) {
            return ((Container.Viewer) sourceComponent)
                    .getContainerDataSource();
        } else {
            // this should not happen
            return null;
        }
    }
}
