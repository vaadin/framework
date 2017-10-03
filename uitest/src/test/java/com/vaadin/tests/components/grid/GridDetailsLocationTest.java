package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDetailsLocationTest extends MultiBrowserTest {

    private static final int detailsDefaultHeight = 51;
    private static final int detailsDefinedHeight = 33;

    private static class Param {
        private final int rowIndex;
        private final boolean useGenerator;
        private final boolean scrollFirstToBottom;

        public Param(int rowIndex, boolean useGenerator,
                boolean scrollFirstToBottom) {
            this.rowIndex = rowIndex;
            this.useGenerator = useGenerator;
            this.scrollFirstToBottom = scrollFirstToBottom;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public boolean useGenerator() {
            return useGenerator;
        }

        public boolean scrollFirstToBottom() {
            return scrollFirstToBottom;
        }

        @Override
        public String toString() {
            return "Param [rowIndex=" + getRowIndex() + ", useGenerator="
                    + useGenerator() + ", scrollFirstToBottom="
                    + scrollFirstToBottom() + "]";
        }

    }

    public static Collection<Param> parameters() {
        List<Param> data = new ArrayList<>();

        int[] params = { 0, 500, 999 };

        for (int rowIndex : params) {

            data.add(new Param(rowIndex, true, false));
            data.add(new Param(rowIndex, true, true));
        }

        return data;
    }

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void toggleAndScroll() throws Throwable {
        for (Param param : parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());

                // the tested method
                toggleAndScroll(param.getRowIndex());

                verifyLocation(param);
            } catch (Throwable t) {
                throw new Throwable("" + param, t);
            }
        }
    }

    @Test
    public void scrollAndToggle() throws Throwable {
        for (Param param : parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());

                // the tested method
                scrollAndToggle(param.getRowIndex());

                verifyLocation(param);

            } catch (Throwable t) {
                throw new Throwable("" + param, t);
            }
        }
    }

    @Test
    public void testDetailsHeightWithGenerator() {
        openTestURL();
        useGenerator(true);
        toggleAndScroll(5);

        verifyDetailsRowHeight(5, detailsDefinedHeight, 0);
        verifyDetailsDecoratorLocation(5, 0, 0);

        toggleAndScroll(0);

        verifyDetailsRowHeight(0, detailsDefinedHeight, 0);
        // decorator elements are in DOM in the order they have been added
        verifyDetailsDecoratorLocation(0, 0, 1);

        verifyDetailsRowHeight(5, detailsDefinedHeight, 1);
        verifyDetailsDecoratorLocation(5, 1, 0);
    }

    private void verifyDetailsRowHeight(int rowIndex, int expectedHeight,
            int visibleIndexOfSpacer) {
        waitForDetailsVisible();
        WebElement details = getDetailsElement(visibleIndexOfSpacer);
        assertEquals("Wrong details row height", expectedHeight,
                details.getSize().getHeight());
    }

    private void verifyDetailsDecoratorLocation(int row,
            int visibleIndexOfSpacer, int visibleIndexOfDeco) {
        WebElement detailsElement = getDetailsElement(visibleIndexOfSpacer);
        WebElement detailsDecoElement = getDetailsDecoElement(
                visibleIndexOfDeco);
        GridElement.GridRowElement rowElement = getGrid().getRow(row);

        assertEquals("Details deco top position does not match row top pos",
                rowElement.getLocation().getY(),
                detailsDecoElement.getLocation().getY());
        assertEquals(
                "Details deco bottom position does not match details bottom pos",
                detailsElement.getLocation().getY()
                        + detailsElement.getSize().getHeight(),
                detailsDecoElement.getLocation().getY()
                        + detailsDecoElement.getSize().getHeight());
    }

    private void verifyLocation(Param param) {
        assertFalse("Notification was present",
                isElementPresent(By.className("v-Notification")));

        TestBenchElement headerRow = getGrid().getHeaderRow(0);
        final int topBoundary = headerRow.getLocation().getX()
                + headerRow.getSize().height;
        final int bottomBoundary = getGrid().getLocation().getX()
                + getGrid().getSize().getHeight()
                - getHorizontalScrollbar().getSize().height;

        GridElement.GridRowElement row = getGrid().getRow(param.getRowIndex());
        final int rowTop = row.getLocation().getX();

        waitForDetailsVisible();
        WebElement details = getDetailsElement();
        final int detailsBottom = details.getLocation().getX()
                + details.getSize().getHeight();

        assertGreaterOrEqual("Row top should be inside grid, gridTop:"
                + topBoundary + " rowTop" + rowTop, topBoundary, rowTop);
        assertLessThanOrEqual(
                "Decorator bottom should be inside grid, gridBottom:"
                        + bottomBoundary + " decoratorBotton:" + detailsBottom,
                detailsBottom, bottomBoundary);

        verifyDetailsRowHeight(param.getRowIndex(),
                param.useGenerator() ? detailsDefinedHeight
                        : detailsDefaultHeight,
                0);
        verifyDetailsDecoratorLocation(param.getRowIndex(), 0, 0);

        assertFalse("Notification was present",
                isElementPresent(By.className("v-Notification")));
    }

    private final By locator = By.className("v-grid-spacer");

    private WebElement getDetailsElement() {
        return getDetailsElement(0);
    }

    private WebElement getDetailsElement(int index) {
        return findElements(locator).get(index);
    }

    private WebElement getDetailsDecoElement(int index) {
        return findElements(By.className("v-grid-spacer-deco")).get(index);
    }

    private void waitForDetailsVisible() {
        waitUntil(new ExpectedCondition<WebElement>() {

            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement detailsElement = getDetailsElement();
                    return detailsElement.isDisplayed()
                            && detailsElement.getSize().getHeight() > 3
                                    ? detailsElement
                                    : null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "visibility of element located by " + locator;
            }

        }, 5);
        waitForElementVisible(By.className("v-grid-spacer"));
    }

    private void scrollToBottom(boolean scrollFirstToBottom) {
        if (scrollFirstToBottom) {
            executeScript("arguments[0].scrollTop = 9999999",
                    getVerticalScrollbar());
        }
    }

    private void useGenerator(boolean use) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        boolean isChecked = isCheckedValo(checkBox);
        if (use != isChecked) {
            clickValo(checkBox);
        }
    }

    @SuppressWarnings("boxing")
    private boolean isCheckedValo(CheckBoxElement checkBoxElement) {
        WebElement checkbox = checkBoxElement.findElement(By.tagName("input"));
        Object value = executeScript("return arguments[0].checked;", checkbox);
        return (Boolean) value;
    }

    private void clickValo(CheckBoxElement checkBoxElement) {
        checkBoxElement.click(5, 5);
    }

    private void scrollAndToggle(int row) {
        setRow(row);
        getScrollAndToggle().click();
    }

    private void toggleAndScroll(int row) {
        setRow(row);
        getToggleAndScroll().click();
    }

    private ButtonElement getScrollAndToggle() {
        return $(ButtonElement.class).caption("Scroll and toggle").first();
    }

    private ButtonElement getToggleAndScroll() {
        return $(ButtonElement.class).caption("Toggle and scroll").first();
    }

    private void setRow(int row) {
        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys(String.valueOf(row),
                Keys.ENTER, Keys.TAB);
    }

    private GridElement getGrid() {
        return $(GridElement.class).first();
    }

    private WebElement getVerticalScrollbar() {
        WebElement scrollBar = getGrid()
                .findElement(By.className("v-grid-scroller-vertical"));
        return scrollBar;
    }

    private WebElement getHorizontalScrollbar() {
        WebElement scrollBar = getGrid()
                .findElement(By.className("v-grid-scroller-horizontal"));
        return scrollBar;
    }
}
