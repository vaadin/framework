package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxSuggestionPageLengthTest extends MultiBrowserTest {

    @Test
    public void testSuggestionsPageLength0() {
        openTestURL();

        WebElement textboxPageLength0 = $(ComboBoxElement.class).first()
                .findElement(By.tagName("input"));
        textboxPageLength0.sendKeys("c");
        assertSuggestions("abc", "cde");
    }

    @Test
    public void testSuggestionsPageLength2() {
        openTestURL();

        WebElement textboxPageLength2 = $(ComboBoxElement.class).get(1)
                .findElement(By.tagName("input"));
        textboxPageLength2.sendKeys("e");
        assertSuggestions("cde", "efg");
    }

    private void assertSuggestions(String... expected) {
        assertEquals(Arrays.asList(expected), getSuggestionsOnScreen());
    }

    private List<String> getSuggestionsOnScreen() {
        List<WebElement> suggestionElements = getDriver()
                .findElements(By.cssSelector(
                        ".v-filterselect-suggestpopup .gwt-MenuItem span"));

        List<String> suggestions = new ArrayList<>();
        for (WebElement suggestion : suggestionElements) {
            suggestions.add(suggestion.getText());
        }
        return suggestions;
    }

}
