package com.vaadin.tests.components.select;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class EnumSelectTest extends SingleBrowserTest {

    @Test
    public void enumInNativeSelect() {
        openTestURL();
        NativeSelectElement ns = $(NativeSelectElement.class).first();
        List<TestBenchElement> options = ns.getOptions();
        assertEquals("Some value", options.get(1).getText());
        assertEquals("Some other value", options.get(2).getText());
    }

    @Test
    public void enumInComboBox() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        List<String> options = cb.getPopupSuggestions();
        assertEquals("Some value", options.get(1));
        assertEquals("Some other value", options.get(2));
    }

    @Test
    public void enumInComboBoxFiltering() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.findElement(By.vaadin("#textbox")).sendKeys(" other ");
        List<String> options = cb.getPopupSuggestions();
        assertEquals("Only one item should match filter", 1, options.size());
        assertEquals("Invalid option matched filter", "Some other value",
                options.get(0));
    }
}
