package com.vaadin.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.data.BackEndDataSource;
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.server.data.Query;
import com.vaadin.ui.AbstractListing.AbstractListingExtension;

import elemental.json.JsonObject;

public class AbstractListingTest {

    private final class TestListing extends
            AbstractSingleSelect<String> {

        protected TestListing() {
            setSelectionModel(new SimpleSingleSelection());
        }

        /**
         * Used to execute data generation
         */
        public void runDataGeneration() {
            super.getDataCommunicator().beforeClientResponse(true);
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

    private static final String[] ITEM_ARRAY = new String[] { "Foo", "Bar",
            "Baz" };

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
        listing.getDataSource().apply(new Query()).forEach(
                str -> Assert.assertTrue("Unexpected item in data source",
                        items.remove(str)));
        Assert.assertTrue("Not all items from list were in data source",
                items.isEmpty());
    }

    @Test
    public void testSetItemsWithVarargs() {
        listing.setItems(ITEM_ARRAY);
        listing.getDataSource().apply(new Query()).forEach(
                str -> Assert.assertTrue("Unexpected item in data source",
                        items.remove(str)));
        Assert.assertTrue("Not all items from list were in data source",
                items.isEmpty());
    }

    @Test
    public void testSetDataSource() {
        ListDataSource<String> dataSource = DataSource.create(items);
        listing.setDataSource(dataSource);
        Assert.assertEquals("setDataSource did not set data source", dataSource,
                listing.getDataSource());
        listing.setDataSource(new BackEndDataSource<>(q -> Stream.of(ITEM_ARRAY)
                .skip(q.getOffset()).limit(q.getLimit()),
                q -> ITEM_ARRAY.length));
        Assert.assertNotEquals("setDataSource did not replace data source",
                dataSource, listing.getDataSource());
    }

    @Test
    public void testAddDataGeneratorBeforeDataSource() {
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.setItems("Foo");
        listing.runDataGeneration();
        Assert.assertEquals("Generator should have been called once", 1,
                generator.callCount);
    }

    @Test
    public void testAddDataGeneratorAfterDataSource() {
        CountGenerator generator = new CountGenerator();
        listing.setItems("Foo");
        generator.extend(listing);
        listing.runDataGeneration();
        Assert.assertEquals("Generator should have been called once", 1,
                generator.callCount);
    }

    @Test
    public void testDataNotGeneratedTwice() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.runDataGeneration();
        Assert.assertEquals("Generator should have been called once", 1,
                generator.callCount);
        listing.runDataGeneration();
        Assert.assertEquals("Generator should not have been called again", 1,
                generator.callCount);
    }

    @Test
    public void testRemoveDataGenerator() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        generator.remove();
        listing.runDataGeneration();
        Assert.assertEquals("Generator should not have been called", 0,
                generator.callCount);
    }

    @Test
    public void testDataRefresh() {
        listing.setItems("Foo");
        CountGenerator generator = new CountGenerator();
        generator.extend(listing);
        listing.runDataGeneration();
        Assert.assertEquals("Generator should have been called once", 1,
                generator.callCount);
        generator.refresh("Foo");
        listing.runDataGeneration();
        Assert.assertEquals("Generator should have been called again", 2,
                generator.callCount);
    }
}
