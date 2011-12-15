package com.vaadin.tests.util;

import junit.framework.Assert;

public class TestUtil {
    public static void assertArrays(Object[] actualObjects,
            Object[] expectedObjects) {
        Assert.assertEquals(
                "Actual contains a different number of values than was expected",
                expectedObjects.length, actualObjects.length);

        for (int i = 0; i < actualObjects.length; i++) {
            Object actual = actualObjects[i];
            Object expected = expectedObjects[i];

            Assert.assertEquals("Item[" + i + "] does not match", expected,
                    actual);
        }

    }

}
