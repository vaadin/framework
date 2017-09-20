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
