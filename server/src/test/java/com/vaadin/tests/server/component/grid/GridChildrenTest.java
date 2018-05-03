package com.vaadin.tests.server.component.grid;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Label;

public class GridChildrenTest {

    private Grid grid;

    @Before
    public void createGrid() {
        grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("bar");
        grid.addColumn("baz");

    }

    @Test
    public void iteratorFindsComponentsInMergedHeader() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void removeComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeHeaderWithComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeHeaderRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeHeaderFooterComponentWhenColumnIsRemoved() {
        HeaderRow h1 = grid.prependHeaderRow();
        FooterRow f1 = grid.addFooterRowAt(0);
        Button headerButton = new Button("This is a button, mkay?");
        Button footerButton = new Button("This is a button, mkay?");
        h1.getCell("foo").setComponent(headerButton);
        f1.getCell("foo").setComponent(footerButton);

        Assert.assertEquals(grid, headerButton.getParent());
        Assert.assertEquals(grid, footerButton.getParent());
        grid.removeColumn("foo");
        Assert.assertNull(headerButton.getParent());
        Assert.assertNull(footerButton.getParent());
    }

    @Test
    public void joinedHeaderComponentDetachedWhenLastColumnIsRemoved() {
        HeaderRow h1 = grid.prependHeaderRow();
        FooterRow f1 = grid.addFooterRowAt(0);
        Button headerButton = new Button("This is a button, mkay?");
        Button footerButton = new Button("This is a button, mkay?");

        HeaderCell mergedHeader = h1.join("foo", "bar", "baz");
        FooterCell mergedFooter = f1.join("foo", "bar", "baz");

        mergedHeader.setComponent(headerButton);
        mergedFooter.setComponent(footerButton);

        Assert.assertEquals(grid, headerButton.getParent());
        Assert.assertEquals(grid, footerButton.getParent());
        grid.removeColumn("foo");
        Assert.assertEquals(grid, headerButton.getParent());
        Assert.assertEquals(grid, footerButton.getParent());
        Assert.assertEquals(headerButton, mergedHeader.getComponent());
        Assert.assertEquals(footerButton, mergedFooter.getComponent());

        grid.removeColumn("bar");
        // Component is not moved from merged cell to the last remaining cell
        Assert.assertNull(headerButton.getParent());
        Assert.assertNull(footerButton.getParent());
    }

    @Test
    public void joinedHeaderComponentDetachedWhenLastColumnIsRemovedReverseOrder() {
        HeaderRow h1 = grid.prependHeaderRow();
        FooterRow f1 = grid.addFooterRowAt(0);
        Button headerButton = new Button("This is a button, mkay?");
        Button footerButton = new Button("This is a button, mkay?");

        HeaderCell mergedHeader = h1.join("foo", "bar", "baz");
        FooterCell mergedFooter = f1.join("foo", "bar", "baz");

        mergedHeader.setComponent(headerButton);
        mergedFooter.setComponent(footerButton);

        Assert.assertEquals(grid, headerButton.getParent());
        Assert.assertEquals(grid, footerButton.getParent());
        grid.removeColumn("baz");
        Assert.assertEquals(grid, headerButton.getParent());
        Assert.assertEquals(grid, footerButton.getParent());
        Assert.assertEquals(headerButton, mergedHeader.getComponent());
        Assert.assertEquals(footerButton, mergedFooter.getComponent());

        grid.removeColumn("bar");
        // Component is not moved from merged cell to the last remaining cell
        Assert.assertNull(headerButton.getParent());
        Assert.assertNull(footerButton.getParent());
    }

    @Test
    public void removeHeaderComponentWhenColumnIsRemovedFromDataSource() {
        Indexed i = new IndexedContainer();
        i.addContainerProperty("c1", String.class, "does not matter 1");
        i.addContainerProperty("c2", String.class, "does not matter 2");
        Grid grid = new Grid();
        grid.setContainerDataSource(i);

        HeaderRow h1 = grid.prependHeaderRow();
        Button button = new Button("This is a button, mkay?");
        h1.getCell("c1").setComponent(button);

        Assert.assertEquals(grid, button.getParent());
        i.removeContainerProperty("c1");
        Assert.assertNull(button.getParent());
    }

    @Test
    public void removeComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeFooterWithComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeFooterRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void componentsInMergedFooter() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
        Assert.assertEquals(grid, label.getParent());
    }
}
