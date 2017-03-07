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
package com.vaadin.tests.util;

import java.util.Iterator;

import org.junit.Assert;

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

    public static void assertIterableEquals(Iterable<?> iterable1,
            Iterable<?> iterable2) {
        Iterator<?> i1 = iterable1.iterator();
        Iterator<?> i2 = iterable2.iterator();

        while (i1.hasNext()) {
            Object o1 = i1.next();
            if (!i2.hasNext()) {
                Assert.fail(
                        "The second iterable contains fewer items than the first. The object "
                                + o1 + " has no match in the second iterable.");
            }
            Object o2 = i2.next();
            Assert.assertEquals(o1, o2);
        }
        if (i2.hasNext()) {
            Assert.fail(
                    "The second iterable contains more items than the first. The object "
                            + i2.next()
                            + " has no match in the first iterable.");
        }
    }

    private TestUtil() {
    }
}
