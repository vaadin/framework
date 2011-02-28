package com.vaadin.tests.server.component.table;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import com.vaadin.ui.Table;

public class TableColumnAlignments {

    @Test
    public void defaultColumnAlignments() {
        for (int properties = 0; properties < 10; properties++) {
            Table t = TableGenerator.createTableWithDefaultContainer(
                    properties, 10);
            Object[] expected = new Object[properties];
            for (int i = 0; i < properties; i++) {
                expected[i] = Table.ALIGN_LEFT;
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
        String[] explicitAlignments = new String[] { Table.ALIGN_CENTER,
                Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,
                Table.ALIGN_LEFT };

        t.setColumnAlignments(explicitAlignments);

        assertArrayEquals("Explicit visible columns, 5 properties",
                explicitAlignments, t.getColumnAlignments());
    }

    @Test
    public void invalidColumnAlignmentStrings() {
        Table t = TableGenerator.createTableWithDefaultContainer(3, 7);
        String[] defaultAlignments = new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT };
        try {
            t.setColumnAlignments(new String[] { "a", "b", "c" });
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }

        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

    }

    @Test
    public void invalidColumnAlignmentString() {
        Table t = TableGenerator.createTableWithDefaultContainer(3, 7);
        String[] defaultAlignments = new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT };
        try {
            t.setColumnAlignment("Property 1", "a");
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
        String[] defaultAlignments = new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT };
        try {
            t.setColumnAlignment("Property 1200", Table.ALIGN_LEFT);
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
        String[] defaultAlignments = new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT };

        try {
            t.setColumnAlignments(new String[] { Table.ALIGN_LEFT });
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

        try {
            t.setColumnAlignments(new String[] {});
            junit.framework.Assert
                    .fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        assertArrayEquals("Invalid change affected alignments",
                defaultAlignments, t.getColumnAlignments());

        try {
            t.setColumnAlignments(new String[] { Table.ALIGN_LEFT,
                    Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
                    Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
                    Table.ALIGN_LEFT });
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
        String[] explicitAlignments = new String[] { Table.ALIGN_CENTER,
                Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,
                Table.ALIGN_LEFT };

        String[] currentAlignments = new String[] { Table.ALIGN_LEFT,
                Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT,
                Table.ALIGN_LEFT };

        for (int i = 0; i < properties; i++) {
            t.setColumnAlignment("Property " + i, explicitAlignments[i]);
            currentAlignments[i] = explicitAlignments[i];

            assertArrayEquals("Explicit visible columns, " + i
                    + " alignments set", currentAlignments,
                    t.getColumnAlignments());
        }

    }
}
