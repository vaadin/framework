package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.AbstractListing.AbstractListingExtension;
import com.vaadin.ui.declarative.DesignContext;

import elemental.json.JsonObject;

public class AbstractListingTest {

    private final class TestListing extends AbstractSingleSelect<String>
            implements HasDataProvider<String> {

        /**
         * Used to execute data generation
         *
         * @param initial
         *            {@code true} to mock initial data request; {@code false}
         *            for follow-up request.
         */
        public void runDataGeneration(boolean initial) {
            super.getDataCommunicator().beforeClientResponse(initial);
        }

        @Override
        protected Element writeItem(Element design, String item,
                DesignContext context) {
            return null;
        }

        @Override
        protected void readItems(Element design, DesignContext context) {
        }

        @Override
        public DataProvider<String, ?> getDataProvider() {
            return internalGetDataProvider();
        }

        @Override
        public void setDataProvider(DataProvider<String, ?> dataProvider) {
            internalSetDataProvider(dataProvider);
        }
    }

    private final class CountGenerator
            extends AbstractListingExtension<String> {

        int callCount = 0;

        @Override
        public void generateData(String data, JsonObject jsonObject) {
            ++callCount;
        }

        @Override
        public void destroyData(String data) {
        }

        @Override
        public void refresh(String data) {
            super.refresh(data);
        }
    }

    private static final String[] ITEM_ARRAY = { "Foo", "Bar", "Baz" };

    private TestListing listing;
    private List<String> items;

    @Before
    public void setUp() {
        items = new ArrayList<>(Arrays.asList(ITEM_ARRAY));
        listing = new TestListing();
    }

    @Test
    public void testSetItemsWithCollection() {
        listing.setItems(items);
        List<String> list = new LinkedList<>(items);
        listing.getDataProvider().fetch(new Query())
                .forEach(str -> assertTrue("Unexpected item in data provider",
                        list.remove(str)));
        assertTrue("Not all items from list were in data provider",
                list.isEmpty());
    }

    @Test
    public void testSetItemsWithVarargs() {
        listing.setItems(ITEM_ARRAY);
        listing.getDataProvider().fetch(new Query())
                .forEach(str -> assertTrue("Unexpected item in data provider",
                        items.remove(str)));
        assertTrue("Not all items from list were in data provider",
                items.isEmpty());
    }

    @Test
    public void testSetDataProvider() {
        ListDataProvider<String> dataProvider = DataProvider
                .ofCollection(items);
        listing.setDataProvider(dataProvider);
        assertEquals("setDataProvider did not set data provider", dataProvider,
                listing.getDataProvider());
        listing.setDataProvider(
                DataProvider.fromCallbacks(
                        query -> Stream.of(ITEM_ARRAY).skip(query.getOffset())
                                .limit(query.getLimit()),
                        query -> ITEM_ARRAY.length));
        assertNotEquals("setDataProvider did not replace data provider",
                dataProvider, listing.getDataProvider());
    }

    @Test
    public void testAddDataGeneratorBeforeDataProvider() {
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.setItems("Foo");
        listing.runDataGeneration(true);
        assertEquals("Generator should have been called once", 1,
                generator.callCount);
    }

    @Test
    public void testAddDataGeneratorAfterDataProvider() {
        CountGenerator generator = new CountGenerator();
        listing.setItems("Foo");
        generator.extend(listing);
        listing.runDataGeneration(true);
        assertEquals("Generator should have been called once", 1,
                generator.callCount);
    }

    @Test
    public void testDataNotGeneratedTwice() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.runDataGeneration(true);
        assertEquals("Generator should have been called once", 1,
                generator.callCount);
        listing.runDataGeneration(false);
        assertEquals("Generator should not have been called again", 1,
                generator.callCount);
    }

    @Test
    public void testRemoveDataGenerator() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        generator.remove();
        listing.runDataGeneration(true);
        assertEquals("Generator should not have been called", 0,
                generator.callCount);
    }

    @Test
    public void testDataRefresh() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.runDataGeneration(true);
        assertEquals("Generator should have been called once", 1,
                generator.callCount);
        generator.refresh("Foo");
        listing.runDataGeneration(false);
        assertEquals("Generator should have been called again", 2,
                generator.callCount);
    }
}
