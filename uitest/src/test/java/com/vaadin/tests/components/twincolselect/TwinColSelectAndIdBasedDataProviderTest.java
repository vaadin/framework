package com.vaadin.tests.components.twincolselect;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwinColSelectAndIdBasedDataProviderTest extends MultiBrowserTest {

    private TwinColSelectElement twinCS;

    @Before
    public void setUp() {
        openTestURL();
        twinCS = $(TwinColSelectElement.class).first();
    }

    @Test
    public void TestSelection() {
        assertEquals(twinCS.getValues().size(), 1);
        $(ButtonElement.class).first().click();
        assertEquals(twinCS.getValues().size(), 0);
    }
}