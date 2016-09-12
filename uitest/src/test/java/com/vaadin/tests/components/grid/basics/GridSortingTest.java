package com.vaadin.tests.components.grid.basics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;

public class GridSortingTest extends GridBasicsTest {

    public static final Comparator<DataObject> BIG_RANDOM = Comparator
            .comparing(DataObject::getBigRandom);
    public static final Comparator<DataObject> SMALL_RANDOM = Comparator
            .comparing(DataObject::getSmallRandom);

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Should be browsersSupportingShiftClick but for whatever reason IE11
        // fails to shift click
        return Collections
                .singletonList(Browser.CHROME.getDesiredCapabilities());
    }

    @Test
    public void testSortBySingleColumnByUser() {
        getGridElement().getHeaderCell(0, 6).click();
        int i = 0;
        for (Integer rowNumber : getTestData().sorted(BIG_RANDOM)
                .map(DataObject::getRowNumber).limit(5)
                .collect(Collectors.toList())) {
            Assert.assertEquals(
                    "Grid was not sorted as expected, row number mismatch",
                    rowNumber.toString(),
                    getGridElement().getCell(i++, 3).getText());
        }
    }

    @Test
    public void testSortByMultipleColumnsByUser() {
        getGridElement().getHeaderCell(0, 7).click();
        getGridElement().getHeaderCell(0, 6).click(15, 15, Keys.SHIFT);

        int i = 0;
        for (Integer rowNumber : getTestData()
                .sorted(SMALL_RANDOM.thenComparing(BIG_RANDOM))
                .map(DataObject::getRowNumber).limit(5)
                .collect(Collectors.toList())) {
            Assert.assertEquals(
                    "Grid was not sorted as expected, row number mismatch",
                    rowNumber.toString(),
                    getGridElement().getCell(i++, 3).getText());
        }
    }
}
