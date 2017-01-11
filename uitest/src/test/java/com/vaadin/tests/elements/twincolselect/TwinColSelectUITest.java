package com.vaadin.tests.elements.twincolselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TwinColSelectUITest extends MultiBrowserTest {
    TwinColSelectElement multiSelect;
    LabelElement multiCounterLbl;

    @Before
    public void init() {
        openTestURL();
        multiSelect = $(TwinColSelectElement.class).first();
        multiCounterLbl = $(LabelElement.class).id("multiCounterLbl");
    }

    @Test
    public void testSelectDeselectByText() {
        multiSelect.selectByText("item2");
        Assert.assertEquals("1: [item1, item2]", multiCounterLbl.getText());
        multiSelect.selectByText("item3");
        Assert.assertEquals("2: [item1, item2, item3]",
                multiCounterLbl.getText());
        multiSelect.deselectByText("item2");
        Assert.assertEquals("3: [item1, item3]", multiCounterLbl.getText());
    }

    @Test
    public void testDeselectSelectByText() {
        multiSelect.deselectByText("item1");
        Assert.assertEquals("1: []", multiCounterLbl.getText());
        multiSelect.selectByText("item1");
        Assert.assertEquals("2: [item1]", multiCounterLbl.getText());
    }

    @Test
    public void testGetAvailableOptions() {
        assertAvailableOptions("item2", "item3");
        multiSelect.selectByText("item2");
        assertAvailableOptions("item3");
        multiSelect.deselectByText("item1");
        assertAvailableOptions("item1", "item3");
    }

    private void assertAvailableOptions(String... items) {
        List<String> optionTexts = multiSelect.getAvailableOptions();
        Assert.assertArrayEquals(items, optionTexts.toArray());
    }
}
