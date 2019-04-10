package com.vaadin.tests.components.treegrid;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridDetailsExpandLastTest extends MultiBrowserTest {

    private TreeGridElement treeGrid;

    private ExpectedCondition<Boolean> expectedConditionDetails(
            final int root) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return getDetails(root) != null;
            }

            @Override
            public String toString() {
                // waiting for...
                return String.format("Root %s details row contents to be found",
                        root);
            }
        };
    }

    private WebElement getDetails(final int root) {
        String text = "details for Root %s";
        try {
            TestBenchElement details = treeGrid.getDetails(root + 2 * root);
            if (details != null) {
                // ensure the details belong to the correct row
                List<WebElement> labels = details
                        .findElements(By.className("v-label"));
                for (WebElement label : labels) {
                    if (String.format(text, root).equals(label.getText())) {
                        return details;
                    }
                }
            }
        } catch (StaleElementReferenceException e) {
            treeGrid = $(TreeGridElement.class).first();
        }
        return null;
    }

    @Test
    public void expandLastRow() {
        openTestURL();
        waitForElementPresent(By.className("v-treegrid"));

        treeGrid = $(TreeGridElement.class).first();
        waitUntil(expectedConditionDetails(1));

        treeGrid.scrollToRow(297);
        waitUntil(expectedConditionDetails(99));

        treeGrid.expandWithClick(297);

        assertEquals("Error notification detected.", 0, treeGrid
                .findElements(By.className("v-Notification-error")).size());

        GridCellElement cell98_1 = treeGrid.getCell(296, 0);
        GridCellElement cell99 = treeGrid.getCell(297, 0);
        WebElement spacer99 = getDetails(99);

        assertThat("Unexpected row location.",
                (double) cell98_1.getLocation().getY()
                        + cell98_1.getSize().getHeight(),
                closeTo(cell99.getLocation().getY(), 2d));
        assertThat("Unexpected spacer location.",
                (double) cell99.getLocation().getY()
                        + cell99.getSize().getHeight(),
                closeTo(spacer99.getLocation().getY(), 2d));
    }
}
