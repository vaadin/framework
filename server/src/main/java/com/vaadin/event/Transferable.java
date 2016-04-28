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
package com.vaadin.event;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.ui.Component;

/**
 * Transferable wraps the data that is to be imported into another component.
 * Currently Transferable is only used for drag and drop.
 * 
 * @since 6.3
 */
public interface Transferable extends Serializable {

    /**
     * Returns the data from Transferable by its data flavor (aka data type).
     * Data types can be any string keys, but MIME types like "text/plain" are
     * commonly used.
     * <p>
     * Note, implementations of {@link Transferable} often provide a better
     * typed API for accessing data.
     * 
     * @param dataFlavor
     *            the data flavor to be returned from Transferable
     * @return the data stored in the Transferable or null if Transferable
     *         contains no data for given data flavour
     */
    public Object getData(String dataFlavor);

    /**
     * Stores data of given data flavor to Transferable. Possibly existing value
     * of the same data flavor will be replaced.
     * 
     * @param dataFlavor
     *            the data flavor
     * @param value
     *            the new value of the data flavor
     */
    public void setData(String dataFlavor, Object value);

    /**
     * @return a collection of data flavors ( data types ) available in this
     *         Transferable
     */
    public Collection<String> getDataFlavors();

    /**
     * @return the component that created the Transferable or null if the source
     *         component is unknown
     */
    public Component getSourceComponent();

}
