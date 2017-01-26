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

import java.util.Collections;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;

import elemental.json.JsonObject;

/**
 * @author Vaadin Ltd
 *
 */
public class DataCommunicatorTest {

    private static final Object TEST_OBJECT = new Object();

    private static class TestUI extends UI {

        private final VaadinSession session;

        TestUI(VaadinSession session) {
            this.session = session;
        }

        @Override
        protected void init(VaadinRequest request) {
        }

        @Override
        public VaadinSession getSession() {
            return session;
        }

        @Override
        public Future<Void> access(Runnable runnable) {
            runnable.run();
            return null;
        }
    }

    private static class TestDataProvider extends ListDataProvider<Object>
            implements Registration {

        private Registration registration;

        public TestDataProvider() {
            super(Collections.singleton(TEST_OBJECT));
        }

        @Override
        public Registration addDataProviderListener(
                DataProviderListener<Object> listener) {
            registration = super.addDataProviderListener(listener);
            return this;
        }

        @Override
        public void remove() {
            registration.remove();
            registration = null;
        }

        public boolean isListenerAdded() {
            return registration != null;
        }

    }

    private static class TestDataCommunicator extends DataCommunicator<Object> {
        protected void extend(UI ui) {
            super.extend(ui);
        }
    }

    private static class TestDataGenerator implements DataGenerator<Object> {
        Object refreshed = null;
        Object generated = null;

        @Override
        public void generateData(Object item, JsonObject jsonObject) {
            generated = item;
        }

        @Override
        public void refreshData(Object item) {
            refreshed = item;
        }
    }

    private final MockVaadinSession session = new MockVaadinSession(
            Mockito.mock(VaadinService.class));

    @Test
    public void attach_dataProviderListenerIsNotAddedBeforeAttachAndAddedAfter() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();

        TestDataProvider dataProvider = new TestDataProvider();
        communicator.setDataProvider(dataProvider, null);

        Assert.assertFalse(dataProvider.isListenerAdded());

        communicator.extend(ui);

        Assert.assertTrue(dataProvider.isListenerAdded());
    }

    @Test
    public void detach_dataProviderListenerIsRemovedAfterDetach() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();

        TestDataProvider dataProvider = new TestDataProvider();
        communicator.setDataProvider(dataProvider, null);

        communicator.extend(ui);

        Assert.assertTrue(dataProvider.isListenerAdded());

        communicator.detach();

        Assert.assertFalse(dataProvider.isListenerAdded());
    }

    @Test
    public void refresh_dataProviderListenerCallsRefreshInDataGeneartors() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();
        communicator.extend(ui);

        TestDataProvider dataProvider = new TestDataProvider();
        communicator.setDataProvider(dataProvider, null);

        TestDataGenerator generator = new TestDataGenerator();
        communicator.addDataGenerator(generator);

        // Generate initial data.
        communicator.beforeClientResponse(true);
        Assert.assertEquals("DataGenerator generate was not called",
                TEST_OBJECT, generator.generated);
        generator.generated = null;

        // Make sure data does not get re-generated
        communicator.beforeClientResponse(false);
        Assert.assertEquals("DataGenerator generate was called again", null,
                generator.generated);

        // Refresh a data object to trigger an update.
        dataProvider.refreshItem(TEST_OBJECT);

        Assert.assertEquals("DataGenerator refresh was not called", TEST_OBJECT,
                generator.refreshed);

        // Test refreshed data generation
        communicator.beforeClientResponse(false);
        Assert.assertEquals("DataGenerator generate was not called",
                TEST_OBJECT, generator.generated);
    }

}
