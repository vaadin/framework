package com.vaadin.data.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Future;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.provider.DataCommunicator.ActiveDataHandler;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * @author Vaadin Ltd
 *
 */
public class DataCommunicatorTest {

    private static final Object TEST_OBJECT = new Object();
    private static final Object TEST_OBJECT_TWO = new Object();

    public static class TestUI extends UI {

        private final VaadinSession session;

        public TestUI(VaadinSession session) {
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
            super(new ArrayList());
            addItem(TEST_OBJECT);
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

        public void setItem(Object item) {
            clear();
            addItem(item);
        }

        public void clear() {
            getItems().clear();
        }

        public void addItem(Object item) {
            getItems().add(item);
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

        assertFalse(dataProvider.isListenerAdded());

        communicator.extend(ui);

        assertTrue(dataProvider.isListenerAdded());
    }

    @Test
    public void detach_dataProviderListenerIsRemovedAfterDetach() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataCommunicator communicator = new TestDataCommunicator();

        TestDataProvider dataProvider = new TestDataProvider();
        communicator.setDataProvider(dataProvider, null);

        communicator.extend(ui);

        assertTrue(dataProvider.isListenerAdded());

        communicator.detach();

        assertFalse(dataProvider.isListenerAdded());
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
        assertEquals("DataGenerator generate was not called", TEST_OBJECT,
                generator.generated);
        generator.generated = null;

        // Make sure data does not get re-generated
        communicator.beforeClientResponse(false);
        assertEquals("DataGenerator generate was called again", null,
                generator.generated);

        // Refresh a data object to trigger an update.
        dataProvider.refreshItem(TEST_OBJECT);

        assertEquals("DataGenerator refresh was not called", TEST_OBJECT,
                generator.refreshed);

        // Test refreshed data generation
        communicator.beforeClientResponse(false);
        assertEquals("DataGenerator generate was not called", TEST_OBJECT,
                generator.generated);
    }

    @Test
    public void refreshDataProviderRemovesOldObjectsFromActiveDataHandler() {
        session.lock();

        UI ui = new TestUI(session);

        TestDataProvider dataProvider = new TestDataProvider();

        TestDataCommunicator communicator = new TestDataCommunicator();
        communicator.setDataProvider(dataProvider, null);

        communicator.extend(ui);
        communicator.beforeClientResponse(true);

        DataKeyMapper keyMapper = communicator.getKeyMapper();
        assertTrue("Object not mapped by key mapper",
                keyMapper.has(TEST_OBJECT));

        ActiveDataHandler handler = communicator.getActiveDataHandler();
        assertTrue("Object not amongst active data",
                handler.getActiveData().containsKey(TEST_OBJECT));

        dataProvider.setItem(TEST_OBJECT_TWO);
        dataProvider.refreshAll();

        communicator.beforeClientResponse(false);

        // assert that test object is marked as removed
        assertTrue("Object not marked as dropped",
                handler.getDroppedData().containsKey(TEST_OBJECT));

        communicator
                .setPushRows(Range.between(0, communicator.getMinPushSize()));
        communicator.beforeClientResponse(false);

        assertFalse("Object still mapped by key mapper",
                keyMapper.has(TEST_OBJECT));
        assertTrue("Object not mapped by key mapper",
                keyMapper.has(TEST_OBJECT_TWO));

        assertFalse("Object still amongst active data",
                handler.getActiveData().containsKey(TEST_OBJECT));
        assertTrue("Object not amongst active data",
                handler.getActiveData().containsKey(TEST_OBJECT_TWO));
    }

    @Test
    public void testDestroyData() {
        session.lock();
        UI ui = new TestUI(session);
        TestDataCommunicator communicator = new TestDataCommunicator();
        TestDataProvider dataProvider = new TestDataProvider();
        communicator.setDataProvider(dataProvider, null);
        communicator.extend(ui);
        // Put a test object into a cache
        communicator.pushData(1, Collections.singletonList(TEST_OBJECT));
        // Put the test object into an update queue
        communicator.refresh(TEST_OBJECT);
        // Drop the test object from the cache
        String key = communicator.getKeyMapper().key(TEST_OBJECT);
        JsonArray keys = Json.createArray();
        keys.set(0, key);
        communicator.onDropRows(keys);
        // Replace everything
        communicator.setDataProvider(
                new ListDataProvider<>(Collections.singleton(new Object())));
        // The communicator does not have to throw exceptions during
        // request finalization
        communicator.beforeClientResponse(false);
        assertFalse("Stalled object in KeyMapper",
                communicator.getKeyMapper().has(TEST_OBJECT));
    }

    @Test
    public void testFilteringLock() {
        session.lock();
        UI ui = new TestUI(session);
        TestDataCommunicator communicator = new TestDataCommunicator();
        communicator.extend(ui);

        ListDataProvider<Object> dataProvider = DataProvider.ofItems("one",
                "two", "three");
        SerializableConsumer<SerializablePredicate<Object>> filterSlot = communicator
                .setDataProvider(dataProvider, null);
        communicator.beforeClientResponse(true);

        // Mock empty request
        filterSlot.accept(t -> String.valueOf(t).contains("a"));
        communicator.beforeClientResponse(false);

        // Assume client clears up the filter
        filterSlot.accept(t -> String.valueOf(t).contains(""));
        communicator.beforeClientResponse(false);

        // And in the next request sets a non-matching filter
        // and has the data request for previous change
        communicator.onRequestRows(0, 3, 0, 0);
        filterSlot.accept(t -> String.valueOf(t).contains("a"));
        communicator.beforeClientResponse(false);

        // Mark communicator clean
        ui.getConnectorTracker().markClean(communicator);

        assertTrue("Communicator should be marked for hard reset",
                communicator.reset);
        assertFalse("DataCommunicator should not be marked as dirty",
                ui.getConnectorTracker().isDirty(communicator));

        // Set a filter that gets results again.
        filterSlot.accept(t -> String.valueOf(t).contains(""));

        assertTrue("DataCommunicator should be marked as dirty",
                ui.getConnectorTracker().isDirty(communicator));
    }


    @Test(expected = IllegalStateException.class)
    public void requestTooMuchRowsFail() {
        TestDataCommunicator communicator = new TestDataCommunicator();
        communicator.onRequestRows(0, communicator.getMaximumAllowedRows() + 10,
                0, 0);
    }
}
