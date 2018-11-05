package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckCheckBoxGroupWithIdTest extends MultiBrowserTest {
    private CheckBoxGroupElement checkBoxGroup;

    @Before
    public void setUp() {
        openTestURL();
        checkBoxGroup = $(CheckBoxGroupElement.class).first();
    }

    @Test
    public void TestSelection() {
        assertEquals(checkBoxGroup.getValue().size(), 1);
        $(ButtonElement.class).first().click();
        assertEquals(checkBoxGroup.getValue().size(), 0);
    }
}
