package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TestBenchElementRightClickTest extends MultiBrowserTest {
    TestBenchElement cell;
    LabelElement label;

    @Before
    public void init() {
        openTestURL();
        cell = $(TableElement.class).id("id1").getCell(1, 1);
        label = $(LabelElement.class).id("label1");
    }

    @Test
    public void testTableRightClick() {
        cell.contextClick();
        String actual = label.getText();
        String expected = "RightClick";
        Assert.assertEquals("TestBenchElement right click fails", expected,
                actual);

    }

    @Test
    public void testTableDoubleClick() {
        cell.doubleClick();
        String actual = label.getText();
        String expected = "DoubleClick";
        Assert.assertEquals("TestBenchElement double click fails", expected,
                actual);
    }
}
