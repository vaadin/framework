package com.vaadin.tests.layouts.gridlayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that GridLayout handles elements spanning otherwise empty columns
 * correctly (#14335)
 *
 * @since 7.2.5
 * @author markus
 */
public class GridSpanEmptyColumnsTest extends MultiBrowserTest {

    @Test
    public void componentsShouldMoveRight() throws IOException {
        openTestURL();

        LabelElement bigCell = $(LabelElement.class).id("bigCell");
        LabelElement smallCell = $(LabelElement.class).id("smallCell");

        // Width is 1000px. Big cell should take up 2/3, small cell should take
        // up 1/3.
        assertEquals(667, bigCell.getSize().width);
        assertEquals(333, smallCell.getSize().width);

    }

}