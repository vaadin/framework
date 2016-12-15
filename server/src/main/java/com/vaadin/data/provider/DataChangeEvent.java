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
package com.vaadin.data.provider;

import java.util.EventObject;

/**
 * An event fired when the data of a {@code DataProvider} changes.
 *
 *
 * @see DataProviderListener
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class DataChangeEvent extends EventObject {

    /**
     * Creates a new {@code DataChangeEvent} event originating from the given
     * data provider.
     *
     * @param source
     *            the data provider, not null
     */
    public DataChangeEvent(DataProvider<?, ?> source) {
        super(source);
    }

    @Override
    public DataProvider<?, ?> getSource() {
        return (DataProvider<?, ?>) super.getSource();
    }

}
