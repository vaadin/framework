package com.vaadin.tests.components.twincolselect;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwinColSelectAndIdBasedDataProviderTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void TestSelection() {
        assertEquals(getTwinColElement().getValues().size(), 1);
        $(ButtonElement.class).first().click();
        assertEquals(getTwinColElement().getValues().size(), 0);
    }

    private TwinColSelectElement getTwinColElement() {
        return $(TwinColSelectElement.class).first();
    }
}
