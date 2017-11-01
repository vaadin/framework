package com.vaadin.tests.elements.checkboxgroup;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupSetSelectionTest extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private CheckBoxGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(CheckBoxGroupElement.class).first();

    }

    @Test
    public void testSetSelection() {
        group.setValue(NEW_VALUE);
        assertEquals(Collections.singletonList(NEW_VALUE), group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        assertEquals(Collections.singletonList(NEW_VALUE), group.getValue());
    }

    @Test
    public void testSelectAll() {
        List<String> value = Arrays.asList("item1", "item2", "item3");
        group.setValue(value);
        assertEquals(value, group.getValue());
    }

    @Test
    public void testSelectNone() {
        testSelectByText();

        group.setValue();
        assertEquals(Collections.emptyList(), group.getValue());
    }

    @Test
    public void testDeselect() {
        testSelectAll();
        testSetSelection();
    }

}
