package com.vaadin.tests.tooltip;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.TooltipTest;
import com.vaadin.tests.util.LoremIpsum;

/**
 * Test to see if long tooltips behave appropriately
 *
 * @author Vaadin Ltd
 */
public class LongTooltipTest extends TooltipTest {

    @Test
    public void tooltipsDontOverflow() throws Exception {
        openTestURL();
        List<TextFieldElement> elements = $(TextFieldElement.class).all();
        checkTooltipNotPresent();
        int i = 0;
        for (TextFieldElement element : elements) {
            checkTooltip(element, "Tooltip " + Integer.toString(i++) + ": "
                    + LoremIpsum.get(1000));
            clearTooltip();
            checkTooltipNotPresent();
        }
    }

}
