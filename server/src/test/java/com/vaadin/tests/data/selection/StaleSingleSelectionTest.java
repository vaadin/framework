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
package com.vaadin.tests.data.selection;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.data.provider.StrBean;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;

public class StaleSingleSelectionTest
        extends AbstractStaleSelectionTest<AbstractSingleSelect<StrBean>> {

    @Test
    public void testGridSingleSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);

        select.setValue(toReplace);

        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(),
                -1);
        dataProvider.refreshItem(replacement);

        assertIsStale(toReplace);
        Assert.assertFalse("Selection should not contain stale values",
                dataProvider.isStale(select.getValue()));

        Assert.assertEquals("Selected item id did not match original.",
                toReplace.getId(), dataProvider.getId(select.getValue()));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> getParams() {
        return Stream
                .of(new NativeSelect<>(), new ComboBox<>(),
                        new RadioButtonGroup<>())
                .map(c -> new Object[] { c.getClass().getSimpleName(), c })
                .collect(Collectors.toList());
    }

}
