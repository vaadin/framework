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

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.Registration;

/**
 * @author Vaadin Ltd
 *
 */
public class AbstractDataSourceTest {

    private static class TestDataSource extends AbstractDataSource<Object> {
        @Override
        public Stream<Object> apply(Query t) {
            return null;
        }

        @Override
        public int size(Query t) {
            return 0;
        }

        @Override
        public boolean isInMemory() {
            return false;
        }
    }

    @Test
    public void refreshAll_notifyListeners() {
        AbstractDataSource<Object> dataSource = new TestDataSource();
        AtomicReference<DataChangeEvent> event = new AtomicReference<>();
        dataSource.addDataSourceListener(ev -> {
            Assert.assertNull(event.get());
            event.set(ev);
        });
        dataSource.refreshAll();
        Assert.assertNotNull(event.get());
        Assert.assertEquals(dataSource, event.get().getSource());
    }

    @Test
    public void removeListener_listenerIsNotNotified() {
        AbstractDataSource<Object> dataSource = new TestDataSource();
        AtomicReference<DataChangeEvent> event = new AtomicReference<>();
        Registration registration = dataSource
                .addDataSourceListener(ev -> event.set(ev));
        registration.remove();
        dataSource.refreshAll();
        Assert.assertNull(event.get());
    }
}
