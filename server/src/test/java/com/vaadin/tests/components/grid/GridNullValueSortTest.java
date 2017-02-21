package com.vaadin.tests.components.grid;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridNullValueSortTest {

    private Grid<TestClass> grid;
    private Column<TestClass, String> stringColumn;
    private Column<TestClass, Object> nonComparableColumn;

    @Test
    public void sortWithNullValues() {
        this.grid.sort(this.stringColumn);
        this.grid.sort(this.nonComparableColumn);
        this.grid.getDataCommunicator().beforeClientResponse(true);
    }

    @Before
    public void setup() {
        VaadinSession.setCurrent(null);
        this.grid = new Grid<TestClass>();
        this.stringColumn = this.grid.addColumn(bean -> bean.stringField);
        this.nonComparableColumn = this.grid.addColumn(bean -> bean.nonComparableField);

        this.grid.setItems(new TestClass(null, new Object()), new TestClass("something", null));
        new MockUI().setContent(grid);
    }

    private static class TestClass {

        private final String stringField;
        private final Object nonComparableField;

        TestClass(final String stringField, final Object nonComparableField) {
            this.stringField = stringField;
            this.nonComparableField = nonComparableField;
        }
    }
}
