package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.UIElement;
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

    @Test
    public void testComboBoxTracksScrolledPage() {
        openTestURL("theme=valo");

        ComboBoxElement cb = $(ComboBoxElement.class).last();
        cb.openPopup();
        WebElement popup = cb.getSuggestionPopup();
        Point comboLocation = cb.getLocation();
        Point popupLocation = popup.getLocation();

        // scroll page
        $(UIElement.class).first().scroll(100);

        // make sure animation frame is handled
        sleep(500);

        Point newComboLocation = cb.getLocation();
        Point newPopupLocation = popup.getLocation();
        assertNotEquals("ComboBox didn't move on the page", 0,
                newComboLocation.y - comboLocation.y);
        assertEquals("Popup didn't move with the combo box",
                newComboLocation.y - comboLocation.y,
                newPopupLocation.y - popupLocation.y, 1);
    }

    private void testNoScrollbars(String theme) {
        openTestURL("theme=" + theme);

        for (ComboBoxElement cb : $(ComboBoxElement.class).all()) {
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
