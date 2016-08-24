package com.vaadin.tests.components.grid.basics;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.DesiredCapabilities;

public class GridSortingTest extends GridBasicsTest {

    public static final Comparator<DataObject> BIG_RANDOM = Comparator
            .comparing(DataObject::getBigRandom);
    public static final Comparator<DataObject> SMALL_RANDOM = Comparator
            .comparing(DataObject::getSmallRandom);

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingShiftClick();
    }

    @Test
    public void testSortBySingleColumnByUser() {
        getGridElement().getHeaderCell(0, 3).click();
        int i = 0;
        for (Integer rowNumber : getTestData().sorted(BIG_RANDOM)
                .map(DataObject::getRowNumber).limit(5)
                .collect(Collectors.toList())) {
            Assert.assertEquals(
                    "Grid was not sorted as expected, row number mismatch",
                    rowNumber.toString(), getGridElement().getCell(i++, 0).getText());
        }
    }

    @Test
    public void testSortByMultipleColumnsByUser() {
        getGridElement().getHeaderCell(0, 4).click();
        getGridElement().getHeaderCell(0, 3).click(15, 15, Keys.SHIFT);

        int i = 0;
        for (Integer rowNumber : getTestData()
                .sorted(SMALL_RANDOM.thenComparing(BIG_RANDOM))
                .map(DataObject::getRowNumber).limit(5)
                .collect(Collectors.toList())) {
            Assert.assertEquals(
                    "Grid was not sorted as expected, row number mismatch",
                    rowNumber.toString(), getGridElement().getCell(i++, 0).getText());
        }
    }
}
