package com.vaadin.tests.server.component.abstractlisting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Listing;
import com.vaadin.server.data.BackEndDataSource;
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.server.data.Query;
import com.vaadin.ui.AbstractListing;

public class AbstractListingTest {

    private static final String[] ITEM_ARRAY = new String[] { "Foo", "Bar",
            "Baz" };

    private Listing<String> listing;
    private List<String> items;

    @Before
    public void setUp() {
        items = new ArrayList<>(Arrays.asList(ITEM_ARRAY));
        listing = new AbstractListing<String>() {
        };
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
}
