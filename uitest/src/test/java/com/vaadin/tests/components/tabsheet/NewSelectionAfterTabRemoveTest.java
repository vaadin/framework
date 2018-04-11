package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Automatic test of the default TabSheet selection algorithm when removing a
 * selected tab.
 *
 * @author Vaadin Ltd
 */
public class NewSelectionAfterTabRemoveTest extends MultiBrowserTest {

    @Test
    public void testSelection() throws IOException, InterruptedException {
        openTestURL();

        while (scrollRight()) {
        }

        selectAndClose(tab(19));

        assertTrue("Tab 18 selected", isTabSelected(tab(18)));

        selectAndClose(tab(16));

        assertTrue("Tab 17 selected", isTabSelected(tab(17)));
    }

    /*
     * Select the specified tab and close it.
     */
    private void selectAndClose(TestBenchElement tab)
            throws InterruptedException {
        tab.click(5, 5);

        sleep(10);

        tabClose(tab).click(2, 2);

        sleep(10);
    }

    /*
     * Gets the selected state of the specified tab.
     */
    private boolean isTabSelected(TestBenchElement tab) {
        return tab.getAttribute("class")
                .contains("v-tabsheet-tabitemcell-selected")
                && tab.getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-focus");
    }

    /*
     * Scroll the tabsheet bar to the right.
     */
    private boolean scrollRight() {
        List<WebElement> scrollElements = getDriver()
                .findElements(By.className("v-tabsheet-scrollerNext"));
        if (!scrollElements.isEmpty()) {
            TestBenchElement rightScrollElement = (TestBenchElement) scrollElements
                    .get(0);
            rightScrollElement.click(5, 5);
            return true;
        } else {
            return false;
        }
    }

    /*
     * Provide the tab close button for the specified tab.
     */
    private TestBenchElement tabClose(TestBenchElement tab) {
        return (TestBenchElement) tab
                .findElement(By.className("v-tabsheet-caption-close"));
    }

    /*
     * Provide the tab at specified index.
     */
    private TestBenchElement tab(int index) {
        By by = By.className("v-tabsheet-tabitemcell");

        List<WebElement> tabs = getDriver().findElements(by);

        String expected = "Tab " + index;

        for (WebElement tab : tabs) {
            if (tab.getText().startsWith(expected)) {
                return (TestBenchElement) tab;
            }
        }

        return null;
    }

}
