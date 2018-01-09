package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxItemIconConnectorResourceTest extends MultiBrowserTest {

    @Test
    public void itemsHaveIcons() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();

        cb.openPopup();

        Iterator<String> it = Arrays.asList("fi.gif", "au.gif", "hu.gif")
                .iterator();

        WebElement suggestionPopup = cb.getSuggestionPopup();
        for (WebElement e : suggestionPopup
                .findElements(By.className("v-icon"))) {
            String imgSrc = e.getAttribute("src");
            imgSrc = imgSrc.substring(imgSrc.lastIndexOf('/') + 1);

            String imageSuffix = it.next();
            assertEquals(imageSuffix, imgSrc);
        }
        assertFalse(it.hasNext());
    }

}
