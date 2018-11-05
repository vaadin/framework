package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FilteringTurkishLocaleTest extends MultiBrowserTest {

    @Test
    public void testEnglishLocale() {
        openTestURL();

        setLocale("en");

        List<String> suggestions = getFilterSuggestions("i");

        assertEquals("Both suggestions should be present", 2,
                suggestions.size());
    }

    @Test
    public void testTurkishLocaleWithDot() {
        openTestURL();

        setLocale("tr");

        List<String> suggestions = getFilterSuggestions("i");

        assertEquals("There should be only one suggestion", 1,
                suggestions.size());
        assertEquals("İ dotted", suggestions.get(0));
    }

    @Test
    public void testTurkishLocaleWithoutDot() {
        openTestURL();

        setLocale("tr");

        List<String> suggestions = getFilterSuggestions("ı");

        assertEquals("There should be only one suggestion", 1,
                suggestions.size());
        assertEquals("I dotless", suggestions.get(0));
    }

    private List<String> getFilterSuggestions(String string) {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.findElement(By.vaadin("#textbox")).sendKeys(string);

        return comboBox.getPopupSuggestions();
    }

    private void setLocale(String locale) {
        NativeSelectElement selector = $(NativeSelectElement.class).first();
        selector.selectByText(locale);
    }

}
