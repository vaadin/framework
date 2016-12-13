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
package com.vaadin.server.data;

import com.vaadin.event.SimpleEventRouter;
import com.vaadin.shared.Registration;

/**
 * Abstract data provider implementation which takes care of refreshing data
 * from the underlying data provider.
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public abstract class AbstractDataProvider<T, F> implements DataProvider<T, F> {

    private SimpleEventRouter<DataChangeEvent> eventRouter = new SimpleEventRouter<>();

    @Override
    public Registration addDataProviderListener(DataProviderListener listener) {
        return eventRouter.addListener(listener);
    }

    @Override
    public void refreshAll() {
        fireEvent(new DataChangeEvent(this));
    }

    /**
     * Sends the event to all listeners.
     *
     * @param event
     *            the event to be sent to all listeners.
     */
    protected void fireEvent(DataChangeEvent event) {
        eventRouter.fireEvent(event);
    }
}
