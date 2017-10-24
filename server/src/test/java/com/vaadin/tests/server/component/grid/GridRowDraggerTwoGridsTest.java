package com.vaadin.tests.server.component.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.DropIndexCalculator;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.components.grid.GridDropEvent;
import com.vaadin.ui.components.grid.SourceDataProviderUpdater;

public class GridRowDraggerTwoGridsTest {

    public class TestGridRowDragger extends GridRowDragger<String> {

        public TestGridRowDragger(Grid<String> source, Grid<String> target) {
            super(source, target);
        }

        @Override
        public void handleDrop(GridDropEvent<String> event) {
            super.handleDrop(event);
        }

        @Override
        public List<String> getDraggedItems() {
            return draggedItems;
        }
    }

    private Grid<String> source;
    private Grid<String> target;
    private TestGridRowDragger dragger;
    private List<String> draggedItems;

    @Before
    public void setupListCase() {
        source = new Grid<>();
        target = new Grid<>();
        target.addColumn(s -> s).setId("1");
        dragger = new TestGridRowDragger(source, target);

        target.setItems(); // setup to use list data provider
    }

    private void drop(String dropIndex, DropLocation dropLocation,
            String... items) {
        draggedItems = new ArrayList<>(Arrays.asList(items));
        dragger.handleDrop(new GridDropEvent<>(target, null, null, null,
                dropIndex, dropLocation, null));
    }

    private void verifySourceDataProvider(String... items) {
        Collection<String> list = ((ListDataProvider<String>) source
                .getDataProvider()).getItems();
        Assert.assertArrayEquals("Invalid items in source data provider", items,
                list.toArray());
    }

    private void verifyTargetDataProvider(String... items) {
        Collection<String> list = ((ListDataProvider<String>) target
                .getDataProvider()).getItems();
        Assert.assertArrayEquals("Invalid items in target data provider", items,
                list.toArray());
    }

    private static void setCustomDataProvider(Grid<String> grid) {
        grid.setDataProvider((so, i, l) -> null, null);
    }

    private static void setCustomDataProvider(Grid<String> grid,
            String... items) {
        grid.setDataProvider((so, i, l) -> Stream.of(items), null);
    }

    @Test
    public void listDataProviders_basicOperation() {
        source.setItems("0", "1", "2");

        drop(null, null, "0");

        verifySourceDataProvider("1", "2");
        verifyTargetDataProvider("0");

        drop("0", DropLocation.BELOW, "1");

        verifySourceDataProvider("2");
        verifyTargetDataProvider("0", "1");

        drop("1", DropLocation.ABOVE, "2");

        verifySourceDataProvider();
        verifyTargetDataProvider("0", "2", "1");
    }

    @Test
    public void listDataProvider_dropAboveFirst() {
        source.setItems("0");
        target.setItems("1");

        drop("1", DropLocation.ABOVE, "0");
        verifySourceDataProvider();
        verifyTargetDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculator() {
        source.setItems("0");
        target.setItems("1");

        AtomicInteger trigger = new AtomicInteger();
        dragger.setDropIndexCalculator(event -> {
            trigger.incrementAndGet();
            return 0;
        });

        drop("1", DropLocation.BELOW, "0");

        Assert.assertEquals("Custom calculator should be invoked", 1,
                trigger.get());
        verifySourceDataProvider();
        verifyTargetDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculatorReturnsMax_droppedToEnd() {
        source.setItems("0");
        target.setItems("1", "2");

        dragger.setDropIndexCalculator(event -> {
            return Integer.MAX_VALUE;
        });

        drop("1", DropLocation.ABOVE, "0");

        verifySourceDataProvider();
        verifyTargetDataProvider("1", "2", "0");
    }

    @Test
    public void customSourceDataProvider_isInvoked() {
        setCustomDataProvider(source, "0", "1");
        target.setItems("2");

        AtomicInteger updaterTrigger = new AtomicInteger();
        List<String> droppedItems = new ArrayList<>();
        dragger.setSourceDataProviderUpdater((event, dp, items) -> {
            updaterTrigger.incrementAndGet();
            droppedItems.addAll(items);
        });

        drop("2", DropLocation.BELOW, "0", "1");

        Assert.assertEquals("source updater not triggered", 1,
                updaterTrigger.get());
        Assert.assertArrayEquals(droppedItems.toArray(),
                new Object[] { "0", "1" });
        verifyTargetDataProvider("2", "0", "1");
    }

    @Test
    public void noopSourceUpdater() {
        source.setItems("0", "1");
        target.setItems("2");

        dragger.setSourceDataProviderUpdater(SourceDataProviderUpdater.NOOP);

        drop("2", DropLocation.ABOVE, "0", "1");

        verifySourceDataProvider("0", "1");
        verifyTargetDataProvider("0", "1", "2");
    }

    @Test
    public void alwaysDropToEndCalculator() {
        source.setItems("0");
        target.setItems("1", "2");

        dragger.setDropIndexCalculator(DropIndexCalculator.ALWAYS_DROP_TO_END);

        drop("1", DropLocation.ABOVE, "0");

        verifySourceDataProvider();
        verifyTargetDataProvider("1", "2", "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customSourceDataProvider_noCustomSourceUpdater_unsupportedOperationExceptionThrown() {
        setCustomDataProvider(source);

        drop(null, DropLocation.BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_noCustomCalculatorAndNoCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        setCustomDataProvider(target);

        drop(null, DropLocation.BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_customCalculatorAndNoCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        setCustomDataProvider(target);
        dragger.setDropIndexCalculator(event -> 0);

        drop(null, DropLocation.BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_noCustomCalculatorAndCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        source.setItems("0");

        setCustomDataProvider(target);
        dragger.setTargetDataProviderUpdater((event, dp, index, items) -> {
        });

        drop(null, DropLocation.BELOW, "0");
    }

    @Test
    public void customTargetDataProvider_customCalculatorAndCustomTargetUpdater_triggeredWithMaxIndex() {
        source.setItems("0");
        setCustomDataProvider(target, "1", "2", "3");

        AtomicInteger updaterTrigger = new AtomicInteger(-1);
        dragger.setTargetDataProviderUpdater(
                (event, dp, index, items) -> updaterTrigger.set(index));

        AtomicInteger calculatorTrigger = new AtomicInteger();
        dragger.setDropIndexCalculator(event -> {
            calculatorTrigger.incrementAndGet();
            return 2;
        });

        drop("1", DropLocation.ABOVE, "2");

        Assert.assertEquals("custom calculator not triggered", 1,
                calculatorTrigger.get());
        // getting value from custom calculator
        Assert.assertEquals("given drop index to target updater is wrong", 2,
                updaterTrigger.get());
    }

    @Test
    public void dropOnSortedGrid_byDefault_dropsToTheEnd() {
        Assert.assertFalse(
                "Default drops on sorted grid rows should not be allowed",
                dragger.getGridDropTarget().isDropAllowedOnSortedGridRows());

        source.setItems("0", "1", "2");
        target.setItems("4", "5");

        target.sort("1");

        drop(null, DropLocation.EMPTY, "0");

        verifySourceDataProvider("1", "2");
        verifyTargetDataProvider("4", "5", "0");
    }
}
