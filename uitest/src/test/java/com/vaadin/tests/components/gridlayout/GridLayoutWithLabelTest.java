package com.vaadin.tests.components.gridlayout;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutWithLabelTest extends MultiBrowserTest {

    @Test
    public void selectingOptionShouldNotCauseLabelToChangeSize() {
        openTestURL();
        AbstractElement gridLayout = $(GridLayoutElement.class).first();
        CheckBoxElement cb = $(CheckBoxElement.class).first();

        int before = gridLayout.getSize().getWidth();
        cb.click(); // Turn on
        cb.click(); // Turn off
        int after = gridLayout.getSize().getWidth();

        Assert.assertEquals(
                "layout width should not have changed after checkbox was toggled",
                before, after);
    }
}
