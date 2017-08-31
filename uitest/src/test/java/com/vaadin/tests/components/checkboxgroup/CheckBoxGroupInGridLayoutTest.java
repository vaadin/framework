package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

public class CheckBoxGroupInGridLayoutTest extends MultiBrowserTest {

    @Test
    public void slotSizeRemainsUnchangedAfterSelectingItem() {
        openTestURL();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        String before = gridLayout.getCssValue("width");

        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class).first();
        checkBoxGroup.setValue("A");
        String after = gridLayout.getCssValue("width");

        Assert.assertEquals(before, after);
    }
}
