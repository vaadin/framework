package com.vaadin.tests.components.grid;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;

public class GridDelegatesToSelectionModelTest {

    private GridSelectionModel<String> selectionModelMock;

    private CustomGrid grid;

    private class CustomGrid extends Grid<String> {
        CustomGrid() {
            super();
            setSelectionModel(selectionModelMock);
        }
    }

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        selectionModelMock = Mockito.mock(GridSelectionModel.class);
        grid = new CustomGrid();
    }

    @Test
    public void grid_getSelectedItems_delegated_to_SelectionModel() {
        grid.getSelectedItems();
        verify(selectionModelMock).getSelectedItems();
    }

    @Test
    public void grid_select_delegated_to_SelectionModel() {
        grid.select("");
        verify(selectionModelMock).select("");
    }

    @Test
    public void grid_deselect_delegated_to_SelectionModel() {
        grid.deselect("");
        verify(selectionModelMock).deselect("");
    }

    @Test
    public void grid_deselectAll_delegated_to_SelectionModel() {
        grid.deselectAll();
        verify(selectionModelMock).deselectAll();
    }
}
