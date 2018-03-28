package com.vaadin.data.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.junit.Test;

import com.vaadin.shared.Registration;

/**
 * @author Vaadin Ltd
 *
 */
public class AbstractDataProviderTest {

    private static class TestDataProvider
            extends AbstractDataProvider<Object, Object> {

        @Override
        public Stream<Object> fetch(Query<Object, Object> t) {
            return null;
        }

        @Override
        public int size(Query<Object, Object> t) {
            return 0;
        }

        @Override
        public boolean isInMemory() {
            return false;
        }
    }

    @Test
    public void refreshAll_notifyListeners() {
        TestDataProvider dataProvider = new TestDataProvider();
        AtomicReference<DataChangeEvent<Object>> event = new AtomicReference<>();
        dataProvider.addDataProviderListener(ev -> {
            assertNull(event.get());
            event.set(ev);
        });
        dataProvider.refreshAll();
        assertNotNull(event.get());
        assertEquals(dataProvider, event.get().getSource());
    }

    @Test
    public void removeListener_listenerIsNotNotified() {
        TestDataProvider dataProvider = new TestDataProvider();
        AtomicReference<DataChangeEvent<Object>> event = new AtomicReference<>();
        Registration registration = dataProvider
                .addDataProviderListener(ev -> event.set(ev));
        registration.remove();
        dataProvider.refreshAll();
        assertNull(event.get());
    }
}
