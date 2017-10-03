package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupInGridLayoutTest extends MultiBrowserTest {

    @Test
    public void slotSizeRemainsUnchangedAfterSelectingItem() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        int before = gridLayout.getSize().getWidth();

        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .first();
        checkBoxGroup.setValue("A");
        int after = gridLayout.getSize().getWidth();

        boolean ok = (before == after);
        // also accept broken layout for unrelated bug #9921 until it is fixed
        if (!ok && BrowserUtil.isChrome(getDesiredCapabilities())) {
            ok = (before == after + 4);
        }

        assertTrue("GridLayout size changed when selecting a value", ok);
    }
}
