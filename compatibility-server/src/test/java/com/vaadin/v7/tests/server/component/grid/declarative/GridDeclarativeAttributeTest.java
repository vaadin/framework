package com.vaadin.v7.tests.server.component.grid.declarative;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.MultiSelectionModel;
import com.vaadin.v7.ui.Grid.NoSelectionModel;
import com.vaadin.v7.ui.Grid.SingleSelectionModel;

/**
 * Tests declarative support for Grid properties.
 *
 * @since
 * @author Vaadin Ltd
 */
public class GridDeclarativeAttributeTest extends DeclarativeTestBase<Grid> {

    @Test
    public void testBasicAttributes() {

        String design = "<vaadin7-grid editable rows=20 frozen-columns=-1 "
                + "editor-save-caption='Tallenna' editor-cancel-caption='Peruuta' column-reordering-allowed>";

        Grid grid = new Grid();
        grid.setEditorEnabled(true);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(20);
        grid.setFrozenColumnCount(-1);
        grid.setEditorSaveCaption("Tallenna");
        grid.setEditorCancelCaption("Peruuta");
        grid.setColumnReorderingAllowed(true);

        testRead(design, grid);
        testWrite(design, grid);
    }

    @Test
    public void testFrozenColumnsAttributes() {
        String design = "<vaadin7-grid frozen-columns='2'><table>" //
                + "<colgroup><col><col><col></colgroup></table></vaadin7-grid>";

        Grid grid = new Grid();
        grid.addColumn("property-0", String.class);
        grid.addColumn("property-1", String.class);
        grid.addColumn("property-2", String.class);
        grid.setFrozenColumnCount(2);

        testRead(design, grid);
    }

    @Test
    public void testSelectionMode() {
        String design = "<vaadin7-grid selection-mode='none'>";
        assertSame(NoSelectionModel.class,
                read(design).getSelectionModel().getClass());

        design = "<vaadin7-grid selection-mode='single'>";
        assertSame(SingleSelectionModel.class,
                read(design).getSelectionModel().getClass());

        design = "<vaadin7-grid selection-mode='multi'>";
        assertSame(MultiSelectionModel.class,
                read(design).getSelectionModel().getClass());
    }
}
