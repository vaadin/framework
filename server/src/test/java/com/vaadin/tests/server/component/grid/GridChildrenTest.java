package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.HeaderCell;

public class GridChildrenTest {

    private Grid<Person> grid;

    @Before
    public void createGrid() {
        grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setId("foo");
        grid.addColumn(Person::getLastName).setId("bar");
        grid.addColumn(Person::getEmail).setId("baz");

    }

    @Test
    public void iteratorFindsComponentsInMergedHeader() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        assertEquals(label, i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void removeComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        assertEquals(grid, label.getParent());
        merged.setText("foo");
        assertNull(label.getParent());
    }

    @Test
    public void removeHeaderWithComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        assertEquals(grid, label.getParent());
        grid.removeHeaderRow(0);
        assertNull(label.getParent());
    }

    @Test
    public void removeComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        assertEquals(grid, label.getParent());
        merged.setText("foo");
        assertNull(label.getParent());
    }

    @Test
    public void removeFooterWithComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        assertEquals(grid, label.getParent());
        grid.removeFooterRow(0);
        assertNull(label.getParent());
    }

    @Test
    public void componentsInMergedFooter() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        assertEquals(label, i.next());
        assertFalse(i.hasNext());
        assertEquals(grid, label.getParent());
    }
}
