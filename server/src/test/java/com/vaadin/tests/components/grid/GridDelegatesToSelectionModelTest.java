package com.vaadin.tests.components.grid;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;

@RunWith(MockitoJUnitRunner.class)
public class GridDelegatesToSelectionModelTest {

    @Mock
    private GridSelectionModel<String> selectionModelMock;

    class CustomGrid extends Grid<String> {
        CustomGrid() {
            super();
            setSelectionModel(selectionModelMock);
        }
    }

    @Test
    public void grid_delegates_selection_methods_to_SelectionModel() {
        CustomGrid grid = new CustomGrid();
        grid.isSelected("");
        grid.getFirstSelectedItem();
        grid.getSelectedItems();
        grid.select("");
        grid.deselect("");
        grid.deselectAll();

        verify(selectionModelMock).isSelected("");
        verify(selectionModelMock).getFirstSelectedItem();
        verify(selectionModelMock).getSelectedItems();
        verify(selectionModelMock).select("");
        verify(selectionModelMock).deselect("");
        verify(selectionModelMock).deselectAll();
    }
}
