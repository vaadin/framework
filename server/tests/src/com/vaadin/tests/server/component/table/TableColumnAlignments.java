package com.vaadin.tests.server.component.table;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;

public class TableColumnAlignments {

    @Test
    public void defaultColumnAlignments() {
        for (int properties = 0; properties < 10; properties++) {
            Table t = TableGenerator.createTableWithDefaultContainer(
                    properties, 10);
            Object[] expected = new Object[properties];
            for (int i = 0; i < properties; i++) {
                expected[i] = Align.LEFT;
            }
            org.junit.Assert.assertArrayEquals("getColumnAlignments", expected,
                    t.getColumnAlignments());
        }
    }

    @Test
    public void explicitColumnAlignments() {
        int properties = 5;
        Table t = TableGenerator
                .createTableWithDefaultContainer(properties, 10);
        Align[] explicitAlignments = new Align[] { Align.CENTER, Align.LEFT,
                Align.RIGHT, Align.RIGHT, Align.LEFT };

        t.setColumnAlignments(explicitAlignments);

        assertArrayEquals("Explicit visible columns, 5 properties",
                explicitAlignments, t.getColumnAlignments());
    }

    @Test
    public void invalidColumnAlignmentStrings() {
        Table t = TableGenerator.createTableWithDefaultContainer(3, 7);
        Align[] defaultAlignments = new Align[] { Align.LEFT, Align.LEFT,
                Align.LEFT };
        try {
            t.setColumnAlignments(new Align[] { Align.RIGHT, Align.RIGHT });
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }

        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

    }

    @Test
    public void columnAlignmentForPropertyNotInContainer() {
        Table t = TableGenerator.createTableWithDefaultContainer(3, 7);
        Align[] defaultAlignments = new Align[] { Align.LEFT, Align.LEFT,
                Align.LEFT };
        try {
            t.setColumnAlignment("Property 1200", Align.LEFT);
            // FIXME: Uncomment as there should be an exception (#6475)
            // junit.framework.Assert
            // .fail("No exception thrown for property not in container");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }

        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

        // FIXME: Uncomment as null should be returned (#6474)
        // junit.framework.Assert.assertEquals(
        // "Column alignment for property not in container returned",
        // null, t.getColumnAlignment("Property 1200"));

    }

    @Test
    public void invalidColumnAlignmentsLength() {
        Table t = TableGenerator.createTableWithDefaultContainer(7, 7);
        Align[] defaultAlignments = new Align[] { Align.LEFT, Align.LEFT,
                Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT };

        try {
            t.setColumnAlignments(new Align[] { Align.LEFT });
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

        try {
            t.setColumnAlignments(new Align[] {});
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

        try {
            t.setColumnAlignments(new Align[] { Align.LEFT, Align.LEFT,
                    Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT,
                    Align.LEFT });
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

    }

    @Test
    public void explicitColumnAlignmentOneByOne() {
        int properties = 5;
        Table t = TableGenerator
                .createTableWithDefaultContainer(properties, 10);
        Align[] explicitAlignments = new Align[] { Align.CENTER, Align.LEFT,
                Align.RIGHT, Align.RIGHT, Align.LEFT };

        Align[] currentAlignments = new Align[] { Align.LEFT, Align.LEFT,
                Align.LEFT, Align.LEFT, Align.LEFT };

        for (int i = 0; i < properties; i++) {
            t.setColumnAlignment("Property " + i, explicitAlignments[i]);
            currentAlignments[i] = explicitAlignments[i];

            assertArrayEquals("Explicit visible columns, " + i
                    + " alignments set", currentAlignments,
                    t.getColumnAlignments());
        }

    }
}
