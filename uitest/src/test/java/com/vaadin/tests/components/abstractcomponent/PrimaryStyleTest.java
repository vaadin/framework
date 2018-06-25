package com.vaadin.tests.components.abstractcomponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

import java.util.List;

import com.vaadin.testbench.elements.ButtonElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PrimaryStyleTest extends MultiBrowserTest {

    @Test
    public void testStyleNames() {
        openTestURL();

        // Verify the initial class names for all three components.
        List<WebElement> initialElements = driver
                .findElements(By.className("initial-state"));
        assertThat(initialElements, hasSize(3));

        // Click on a button that updates the styles.
        $(ButtonElement.class).id("update-button").click();

        // Verify that the class names where updated as expected.
        List<WebElement> updatedElements = driver
                .findElements(By.className("updated-correctly"));
        assertThat(updatedElements, hasSize(initialElements.size()));

    }

}
