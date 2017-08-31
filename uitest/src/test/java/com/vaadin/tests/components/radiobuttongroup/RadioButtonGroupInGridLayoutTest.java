package com.vaadin.tests.components.radiobuttongroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RadioButtonGroupInGridLayoutTest extends MultiBrowserTest {

    @Test
    public void slotSizeRemainsUnchangedAfterSelectingItem() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        int before = gridLayout.getSize().getWidth();

        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();
        radioButtonGroup.setValue("A");
        int after = gridLayout.getSize().getWidth();

        Assert.assertEquals("GridLayout size changed when selecting a value",
                before, after);
    }
}
