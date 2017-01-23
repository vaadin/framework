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
                .forEach(i -> Assert.assertFalse(
                        "Selection should not contain stale values",
                        dataProvider.isStale(i)));

        Assert.assertTrue("Selection did not contain an item with matching Id.",
                select.getSelectedItems().stream().map(dataProvider::getId)
                        .filter(i -> dataProvider.getId(toReplace).equals(i))
                        .findFirst().isPresent());
        Assert.assertTrue("Stale element is not considered selected.",
                select.isSelected(toReplace));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> getParams() {
        return Stream
                .of(new ListSelect<>(), new TwinColSelect<>(),
                        new CheckBoxGroup<>())
                .map(c -> new Object[] { c.getClass().getSimpleName(), c })
                .collect(Collectors.toList());
    }
}
