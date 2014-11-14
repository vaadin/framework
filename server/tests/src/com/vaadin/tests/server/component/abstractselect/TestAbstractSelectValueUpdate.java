/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.server.component.abstractselect;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;

public class TestAbstractSelectValueUpdate {

    @Test
    public void removeItem_deleteItemFromUnderlyingContainer_selectValueIsUpdated() {
        BeanItemContainer<Object> container = new BeanItemContainer<Object>(
                Object.class);
        Object item1 = new Object();
        Object item2 = new Object();
        container.addBean(item1);
        container.addBean(item2);
        TestSelect select = new TestSelect();
        select.setContainerDataSource(container);

        select.setValue(item1);

        Assert.assertNotNull("Value is null after selection", select.getValue());

        container.removeItem(item1);

        Assert.assertNull("Value is not null after removal", select.getValue());
    }

    @Test
    public void removeItem_multiselectSectionDeleteItemFromUnderlyingContainer_selectValueIsUpdated() {
        BeanItemContainer<Object> container = new BeanItemContainer<Object>(
                Object.class);
        Object item1 = new Object();
        Object item2 = new Object();
        container.addBean(item1);
        container.addBean(item2);
        TestSelect select = new TestSelect();
        select.setMultiSelect(true);
        select.setContainerDataSource(container);

        select.setValue(Collections.singletonList(item1));

        checkSelectedItemsCount(select, 1);

        container.removeItem(item1);

        checkSelectedItemsCount(select, 0);
    }

    private void checkSelectedItemsCount(TestSelect select, int count) {
        Assert.assertNotNull("Selected value is null", select.getValue());
        Assert.assertTrue("Selected value is not a collection",
                select.getValue() instanceof Collection);
        Assert.assertEquals("Wrong number of selected items",
                ((Collection<?>) select.getValue()).size(), count);
    }

    private class TestSelect extends AbstractSelect {

    }
}
