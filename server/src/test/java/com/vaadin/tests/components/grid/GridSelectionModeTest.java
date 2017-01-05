package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridSelectionModeTest {

    private Grid<String> grid;

    @Before
    public void setup() {
        grid = new Grid<>();
        grid.setItems("foo", "bar", "baz");
    }

    @Test
    public void testSelectionModes() {
        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(MultiSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.MULTI).getClass());
        Assert.assertEquals(MultiSelectionModelImpl.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(NoSelectionModel.class,
                grid.setSelectionMode(SelectionMode.NONE).getClass());
        Assert.assertEquals(NoSelectionModel.class,
                grid.getSelectionModel().getClass());

        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.setSelectionMode(SelectionMode.SINGLE).getClass());
        Assert.assertEquals(SingleSelectionModelImpl.class,
                grid.getSelectionModel().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void testNullSelectionMode() {
        grid.setSelectionMode(null);
    }

}
