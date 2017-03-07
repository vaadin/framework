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
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TwinColSelect;

public class StaleMultiSelectionTest
        extends AbstractStaleSelectionTest<AbstractMultiSelect<StrBean>> {

    @Test
    public void testSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);

        select.select(toReplace);

        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(),
                -1);
        dataProvider.refreshItem(replacement);

        assertIsStale(toReplace);
        select.getSelectedItems()
                .forEach(item -> Assert.assertFalse(
                        "Selection should not contain stale values",
                        dataProvider.isStale(item)));

        Object oldId = dataProvider.getId(toReplace);
        Assert.assertTrue("Selection did not contain an item with matching Id.",
                select.getSelectedItems().stream().map(dataProvider::getId)
                        .anyMatch(oldId::equals));
        Assert.assertTrue("Stale element is not considered selected.",
                select.isSelected(toReplace));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> getParams() {
        return Stream
                .of(new ListSelect<>(), new TwinColSelect<>(),
                        new CheckBoxGroup<>())
                .map(component -> new Object[] {
                        component.getClass().getSimpleName(), component })
                .collect(Collectors.toList());
    }
}
