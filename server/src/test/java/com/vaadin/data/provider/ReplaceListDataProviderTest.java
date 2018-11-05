package com.vaadin.data.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test class that verifies that ReplaceListDataProvider functions the way it's
 * meant to.
 *
 */
public class ReplaceListDataProviderTest {

    private static final StrBean TEST_OBJECT = new StrBean("Foo", 10, -1);
    private ReplaceListDataProvider dataProvider = new ReplaceListDataProvider(
            new ArrayList<>(Arrays.asList(TEST_OBJECT)));

    @Test
    public void testGetIdOfItem() {
        Object id = dataProvider.fetch(new Query<>()).findFirst()
                .map(dataProvider::getId).get();
        assertEquals("DataProvider not using correct identifier getter",
                TEST_OBJECT.getId(), id);
    }

    @Test
    public void testGetIdOfReplacementItem() {
        assertFalse("Test object was stale before making any changes.",
                dataProvider.isStale(TEST_OBJECT));

        dataProvider.refreshItem(new StrBean("Replacement TestObject", 10, -2));

        StrBean fromDataProvider = dataProvider.fetch(new Query<>()).findFirst()
                .get();
        Object id = dataProvider.getId(fromDataProvider);

        assertNotEquals("DataProvider did not return the replacement",
                TEST_OBJECT, fromDataProvider);

        assertEquals("DataProvider not using correct identifier getter",
                TEST_OBJECT.getId(), id);

        assertTrue("Old test object should be stale",
                dataProvider.isStale(TEST_OBJECT));
    }
}
