package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboboxPopupScrollingTest extends MultiBrowserTest {

    @Test
    public void testNoScrollbarsValo() {
        testNoScrollbars("valo");
    }

    @Test
    public void testNoScrollbarsChameleon() {
        testNoScrollbars("chameleon");
    }

    @Test
    public void testNoScrollbarsRuno() {
        testNoScrollbars("runo");
    }

    @Test
    public void testNoScrollbarsReindeer() {
        testNoScrollbars("reindeer");
    }

    private void testNoScrollbars(String theme) {
        openTestURL("theme=" + theme);

        for (CustomComboBoxElement cb : $(CustomComboBoxElement.class).all()) {
            String caption = cb.getCaption();
            cb.openPopup();
            WebElement popup = cb.getSuggestionPopup();
            WebElement scrollable = popup
                    .findElement(By.className("v-filterselect-suggestmenu"));
            assertNoHorizontalScrollbar(scrollable, caption);
            assertNoVerticalScrollbar(scrollable, caption);
        }
    }

}
