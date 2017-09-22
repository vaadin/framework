package com.vaadin.tests.components.tabsheet;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TabDescriptionContentModeTest extends SingleBrowserTest {

    @Test
    public void tab_description_content_modes() {
        openTestURL();
        List<WebElement> tabCaptions = findElements(
                By.className("v-captiontext"));

        hoverCaption(tabCaptions.get(0));
        waitUntil(driver -> "First tab description"
                .equals(getDescriptionElement().getText()));

        hoverCaption(tabCaptions.get(1));
        waitUntil(driver -> "Second tab\ndescription"
                .equals(getDescriptionElement().findElement(By.tagName("pre"))
                        .getText()));

        hoverCaption(tabCaptions.get(2));
        waitUntil(
                driver -> "Third tab description".equals(getDescriptionElement()
                        .findElement(By.tagName("b")).getText()));

        hoverCaption(tabCaptions.get(3));
        waitUntil(driver -> "Fourth tab description"
                .equals(getDescriptionElement().findElement(By.tagName("pre"))
                        .getText()));

        $(ButtonElement.class).first().click();
        hoverCaption(tabCaptions.get(3));
        waitUntil(driver -> "Fourth tab description, changed"
                .equals(getDescriptionElement().getText()));
    }

    private void hoverCaption(WebElement captionElement) {
        new Actions(getDriver()).moveToElement(captionElement, 1, 1).perform();
    }

    private WebElement getDescriptionElement() {
        return findElement(By.className("v-tooltip-text"));
    }
}
