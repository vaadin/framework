package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

public class RadioButtonGroupInGridLayoutTest extends MultiBrowserTest {

    @Test
    public void slotSizeRemainsUnchangedAfterSelectingItem() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        String before = gridLayout.getCssValue("width");

        RadioButtonGroupElement radioButtonGroup = $(RadioButtonGroupElement.class).first();
        radioButtonGroup.setValue("A");
        String after = gridLayout.getCssValue("width");

        Assert.assertEquals(before, after);
    }
}
