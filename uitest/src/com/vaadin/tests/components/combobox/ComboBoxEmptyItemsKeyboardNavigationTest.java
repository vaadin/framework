package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxEmptyItemsKeyboardNavigationTest extends MultiBrowserTest {

    @Test
    public void navigatingUpOnAnEmptyMenuDoesntThrowErrors() {
        setDebug(true);
        openTestURL();

        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.sendKeys("a", Keys.ARROW_UP);

        List<WebElement> errors = findElements(By.className("SEVERE"));

        assertThat(errors, empty());
    }
}