package com.vaadin.tests.components.nativeselect;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static junit.framework.TestCase.assertEquals;

public class NativeSelectSetNullTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testCaptionSelected() {
        getButtonOnId("setNull");
        assertEquals(NativeSelectSetNull.EMPTY_SELECTION_TEXT,
                getSelect().getValue());
    }

    @Test
    public void changeSelectedValue() {
        getButtonOnId("changeSelect").click();
        assertEquals(3, Integer.valueOf(getSelect().getValue()).intValue());
    }

    @Test
    public void clearSelection() {
        getButtonOnId("clear").click();
        assertEquals(NativeSelectSetNull.EMPTY_SELECTION_TEXT,
                getSelect().getValue());
    }

    @Test
    public void valuePreservedAfterAllowEmptySelectionChanged() {
        getSelect().setValue("2");
        getButtonOnId("disable").click();
        assertEquals(2, Integer.valueOf(getSelect().getValue()).intValue());

        getButtonOnId("disable").click();
        getButtonOnId("setNull").click();
        assertEquals(NativeSelectSetNull.EMPTY_SELECTION_TEXT,
                getSelect().getValue());

    }

    protected NativeSelectElement getSelect() {
        return $(NativeSelectElement.class).first();
    }

    protected WebElement getButtonOnId(String id) {
        return findElement(By.id(id));
    }
}
