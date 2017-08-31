package com.vaadin.tests.components.checkboxgroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.GridLayoutElement;
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

        Assert.assertEquals("GridLayout size changed when selecting a value",
                before, after);
    }
}
