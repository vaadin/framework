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
import com.vaadin.ui.components.grid.GridDropEvent;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.components.grid.SourceDataProviderUpdater;

public class GridRowDraggerOneGridTest {

    public class TestGridRowDragger extends GridRowDragger<String> {

        public TestGridRowDragger(Grid<String> grid) {
            super(grid);
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
    private TestGridRowDragger dragger;
    private List<String> draggedItems;

    @Before
    public void setupListCase() {
        source = new Grid<>();
        source.addColumn(s -> s).setId("1");
        dragger = new TestGridRowDragger(source);
    }

    private void drop(String dropIndex, DropLocation dropLocation,
            String... items) {
        draggedItems = new ArrayList<>(Arrays.asList(items));
        dragger.handleDrop(new GridDropEvent<>(source, null, null, null,
                dropIndex, dropLocation, null));
    }

    private void verifyDataProvider(String... items) {
        Collection<String> list = ((ListDataProvider<String>) source
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

        verifyDataProvider("1", "2", "0");

        drop("0", DropLocation.BELOW, "1");

        verifyDataProvider("2", "0", "1");

        drop("1", DropLocation.ABOVE, "2");

        verifyDataProvider("0", "2", "1");
    }

    @Test
    public void listDataProvider_dropAboveFirst() {
        source.setItems("0", "1");

        drop("0", DropLocation.ABOVE, "1");
        verifyDataProvider("1", "0");
    }

    @Test
    public void listDataProvider_customCalculator() {
        source.setItems("0", "1");

        AtomicInteger trigger = new AtomicInteger();
        dragger.setDropIndexCalculator(event -> {
            trigger.incrementAndGet();
            return 0;
        });

        drop("1", DropLocation.BELOW, "0");

        Assert.assertEquals("Custom calculator should be invoked", 1,
                trigger.get());
        verifyDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculatorReturnsMax_droppedToEnd() {
        source.setItems("0", "1", "2");

        dragger.setDropIndexCalculator(event -> {
            return Integer.MAX_VALUE;
        });

        drop("1", DropLocation.ABOVE, "0");

        verifyDataProvider("1", "2", "0");
    }

    @Test
    public void listDataProvider_calledOnlyOnce() {

        final int[] times = new int[1];

        source.setItems("0", "1", "2");

        source.getDataProvider().addDataProviderListener(ev -> times[0]++);

        dragger.setDropIndexCalculator(event -> {
            return Integer.MAX_VALUE;
        });

        drop("1", DropLocation.ABOVE, "0");

        verifyDataProvider("1", "2", "0");

        Assert.assertArrayEquals("DataProvider should be invoked only once", new int[] { 1 }, times);
    }

    @Test
    public void noopSourceUpdater() {
        source.setItems("0", "1", "2");

        dragger.setSourceDataProviderUpdater(SourceDataProviderUpdater.NOOP);

        drop("2", DropLocation.ABOVE, "0", "1");

        verifyDataProvider("0", "1", "0", "1", "2");

    }

    @Test
    public void alwaysDropToEndCalculator() {
        source.setItems("0", "1", "2");

        dragger.setDropIndexCalculator(DropIndexCalculator.alwaysDropToEnd());

        drop("1", DropLocation.ABOVE, "0");

        verifyDataProvider("1", "2", "0");
    }

    @Test
    public void dropTwoFromEnd_beginning() {
        source.setItems("0", "1", "2", "3");

        drop("0", DropLocation.ABOVE, "2", "3");

        verifyDataProvider("2", "3", "0", "1");
    }

    @Test
    public void dropTwoFromEnd_middle() {
        source.setItems("0", "1", "2", "3");

        drop("1", DropLocation.ABOVE, "2", "3");

        verifyDataProvider("0", "2", "3", "1");
    }

    @Test
    public void dropTwoFromEnd_aboveOneThatIsDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3");

        drop("2", DropLocation.ABOVE, "2", "3");

        verifyDataProvider("0", "1", "2", "3");
    }

    @Test
    public void dragAndAboveFirst_thatIsAlsoDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3");

        drop("2", DropLocation.ABOVE, "2", "3");

        verifyDataProvider("0", "1", "2", "3");
    }

    @Test
    public void dropFromBeginning_afterOneDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3", "4");

        drop("3", DropLocation.BELOW, "0", "1", "3");

        verifyDataProvider("2", "0", "1", "3", "4");
    }

    @Test
    public void dropMixedSet_onOneOfTheDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3", "4");

        drop("2", DropLocation.BELOW, "0", "2", "4");

        verifyDataProvider("1", "0", "2", "4", "3");
    }

    @Test
    public void dropOnSortedGrid_byDefault_dropsToTheEnd() {
        Assert.assertFalse(
                "Default drops on sorted grid rows should not be allowed",
                dragger.getGridDropTarget().isDropAllowedOnRowsWhenSorted());

        source.setItems("0", "1", "2", "3", "4");

        drop("3", DropLocation.BELOW, "1");

        verifyDataProvider("0", "2", "3", "1", "4");

        source.sort("1");

        drop(null, DropLocation.EMPTY, "0");

        verifyDataProvider("2", "3", "1", "4", "0");
    }

}
