package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.data.provider.bov.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridNoSelectionModelTest {

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);

    private Grid<Person> grid;
    private GridSelectionModel<Person> model;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setItems(PERSON_A, PERSON_B, PERSON_C);

        model = new NoSelectionModel<>(grid);
        grid.setSelectionModel(model);
    }

    @Test
    public void select() {
        model.select(PERSON_A);

        Assert.assertFalse(model.isSelected(PERSON_A));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());

        model.select(PERSON_B);

        Assert.assertFalse(model.isSelected(PERSON_B));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());
    }

    @Test
    public void changingToSingleSelectionModel() {
        grid.setSelectionModel(new SingleSelectionModelImpl<>(grid));

        grid.getSelectionModel().select(PERSON_B);
        Assert.assertEquals(PERSON_B,
                grid.getSelectionModel().getFirstSelectedItem().get());
    }

    @Test
    public void changingToMultiSelectionModel() {
        grid.setSelectionModel(new MultiSelectionModelImpl<>(grid));

        grid.getSelectionModel().select(PERSON_B);
        Assert.assertEquals(new LinkedHashSet<>(Arrays.asList(PERSON_B)),
                grid.getSelectionModel().getSelectedItems());
    }

}
