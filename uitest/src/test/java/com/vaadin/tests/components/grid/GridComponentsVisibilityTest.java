package com.vaadin.tests.components.grid;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.IntStream;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridComponentsVisibilityTest extends MultiBrowserTest {

    @Test
    public void changingVisibilityOfComponentInFirstRowShouldNotThrowClientSideExceptions() {
        testHideComponent(grid -> 0);
    }

    @Test
    public void changingVisibilityOfComponentShouldNotThrowClientSideExceptions() {
        testHideComponent(grid -> ThreadLocalRandom.current().nextInt(1, (int) grid.getRowCount() - 1));
    }

    @Test
    public void changingVisibilityOfComponentInLastRowShouldNotThrowClientSideExceptions() {
        testHideComponent(grid -> (int) grid.getRowCount() - 1);
    }

    private void testHideComponent(Function<GridElement, Integer> rowUnderTestSupplier) {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();
        int rowUnderTest = rowUnderTestSupplier.apply(grid);
        assertTrue("Text field should be visible", grid.getCell(rowUnderTest, 1)
            .isElementPresent(TextFieldElement.class));
        assertOtherConnectorsArePresent(grid, rowUnderTest);

        clearDebugMessages();
        clickVisibilityToggleButton(grid, rowUnderTest);
        assertNotClientSideErrors();
        assertOnlyTextFieldOnTestedRowIsNotPresent(grid, rowUnderTest);

        clearDebugMessages();
        clickVisibilityToggleButton(grid, rowUnderTest);
        assertNotClientSideErrors();
        assertAllTextFieldsArePresent(grid, rowUnderTest);

        clearDebugMessages();
        clickVisibilityToggleButton(grid, rowUnderTest);
        assertNotClientSideErrors();
        assertOnlyTextFieldOnTestedRowIsNotPresent(grid, rowUnderTest);

        clickVisibilityToggleButton(grid, rowUnderTest);
    }

    private void assertOnlyTextFieldOnTestedRowIsNotPresent(GridElement grid, int rowUnderTest) {
        assertFalse("Text field should not be visible", grid.getCell(rowUnderTest, 1)
            .isElementPresent(TextFieldElement.class));
        assertOtherConnectorsArePresent(grid, rowUnderTest);
    }

    private void assertAllTextFieldsArePresent(GridElement grid, int rowUnderTest) {
        assertTrue("Text field should be visible", grid.getCell(rowUnderTest, 1)
            .isElementPresent(TextFieldElement.class));
        assertOtherConnectorsArePresent(grid, rowUnderTest);
    }

    private void assertNotClientSideErrors() {
        assertNoErrorNotifications();
        // Should not log "Widget is still attached to the DOM ..." error message
        assertNoDebugMessage(Level.SEVERE);
    }

    private void clickVisibilityToggleButton(GridElement grid, int rowUnderTest) {
        grid.getRow(rowUnderTest).getCell(2).findElement(By.className("v-button")).click();
    }

    private void assertOtherConnectorsArePresent(GridElement grid, int rowUnderTest) {
        IntStream.range(1, (int) grid.getRowCount())
            .filter(row -> row != rowUnderTest)
            .forEach(row ->
                assertTrue("Text field should be visible", grid.getCell(row, 1)
                    .isElementPresent(TextFieldElement.class))
            );
    }
}
