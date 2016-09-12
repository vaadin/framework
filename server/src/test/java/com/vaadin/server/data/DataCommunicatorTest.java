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

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;

/**
 * @author Vaadin Ltd
 *
 */
public class DataCommunicatorTest {

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
    }

    private static class TestDataSource extends ListDataSource<Object>
            implements Registration {

        private Registration registration;

        public TestDataSource() {
            super(Collections.singleton(new Object()));
        }

        @Override
        public Registration addDataSourceListener(DataSourceListener listener) {
            registration = super.addDataSourceListener(listener);
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

    private MockVaadinSession session = new MockVaadinSession(
            Mockito.mock(VaadinService.class));

    @Test
    public void attach_dataSourceListenerIsNotAddedBeforeAttachAndAddedAfter() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();

        TestDataSource dataSource = new TestDataSource();
        communicator.setDataSource(dataSource);

        Assert.assertFalse(dataSource.isListenerAdded());

        communicator.extend(ui);

        Assert.assertTrue(dataSource.isListenerAdded());
    }

    @Test
    public void detach_dataSourceListenerIsRemovedAfterDetach() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();

        TestDataSource dataSource = new TestDataSource();
        communicator.setDataSource(dataSource);

        communicator.extend(ui);

        Assert.assertTrue(dataSource.isListenerAdded());

        communicator.detach();

        Assert.assertFalse(dataSource.isListenerAdded());
    }

}
