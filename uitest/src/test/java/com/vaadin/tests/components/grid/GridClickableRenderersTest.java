package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridClickableRenderersTest extends MultiBrowserTest {

    @Test
    public void clickableRenderersPresent() {
        openTestURL();
        assertTrue(isElementPresent(By.className("v-nativebutton")));
        assertTrue(isElementPresent(By.className("gwt-Image")));
    }

    @Test
    public void buttonRendererReturnsCorrectItem() {
        openTestURL();
        List<WebElement> findElements = findElements(
                By.className("v-nativebutton"));
        WebElement firstRowTextButton = findElements.get(0);
        WebElement firstRowHtmlButton = findElements.get(1);
        assertEquals("button 1 text", firstRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        assertEquals("button 1 html", firstRowHtmlButton.getText());

        WebElement secondRowTextButton = findElements.get(3);
        WebElement secondRowHtmlButton = findElements.get(4);
        assertEquals("button 2 text", secondRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        assertEquals("button 2 html", secondRowHtmlButton.getText());

        LabelElement label = $(LabelElement.class).get(1);

        firstRowTextButton.click();
        assertEquals("first row clicked", label.getText());

        secondRowTextButton.click();
        assertEquals("second row clicked", label.getText());
    }

    @Test
    public void checkBoxRendererClick() {
        openTestURL();
        WebElement firstRowButton = findElements(By.className("v-nativebutton"))
                .get(2);
        WebElement secondRowButton = findElements(
                By.className("v-nativebutton")).get(5);
        LabelElement label = $(LabelElement.class).get(2);

        firstRowButton.click();
        assertEquals("first row false", label.getText());

        secondRowButton.click();
        assertEquals("second row true", label.getText());
    }
}
