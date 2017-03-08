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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
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
        Assert.assertEquals("DataProvider not using correct identifier getter",
                TEST_OBJECT.getId(), id);
    }

    @Test
    public void testGetIdOfReplacementItem() {
        Assert.assertFalse("Test object was stale before making any changes.",
                dataProvider.isStale(TEST_OBJECT));

        dataProvider.refreshItem(new StrBean("Replacement TestObject", 10, -2));

        StrBean fromDataProvider = dataProvider.fetch(new Query<>()).findFirst()
                .get();
        Object id = dataProvider.getId(fromDataProvider);

        Assert.assertNotEquals("DataProvider did not return the replacement",
                TEST_OBJECT, fromDataProvider);

        Assert.assertEquals("DataProvider not using correct identifier getter",
                TEST_OBJECT.getId(), id);

        Assert.assertTrue("Old test object should be stale",
                dataProvider.isStale(TEST_OBJECT));
    }

}
