package com.vaadin.v7.tests.server.component.treetable;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.ui.declarative.DesignException;
import com.vaadin.v7.tests.server.component.table.TableDeclarativeTest;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TreeTable;

/**
 * Test declarative support for {@link TreeTable}.
 *
 * @since
 * @author Vaadin Ltd
 */
public class TreeTableDeclarativeTest extends TableDeclarativeTest {

    @Test
    public void testAttributes() {
        String design = "<vaadin7-tree-table animations-enabled>";
        TreeTable table = getTable();
        table.setAnimationsEnabled(true);

        testRead(design, table);
        testWrite(design, table);
    }

    @Test
    public void testHierarchy() {
        String design = "<vaadin7-tree-table>" //
                + "<table>" //
                + "<colgroup><col property-id=''></colgroup>" //
                + "<tbody>" //
                + "  <tr item-id='1'><td></tr>" //
                + "    <tr depth=1 item-id='1.1'><td></tr>" //
                + "    <tr depth=1 item-id='1.2'><td></tr>" //
                + "      <tr depth=2 item-id='1.2.1'><td></tr>" //
                + "        <tr depth=3 item-id='1.2.1.1'><td></tr>" //
                + "      <tr depth=2 item-id='1.2.2'><td></tr>" //
                + "  <tr item-id='2'><td></tr>" //
                + "    <tr depth=1 item-id='2.1'><td></tr>" //
                + "</tbody>" //
                + "</table>" //
                + "</vaadin7-tree-table>";

        TreeTable table = getTable();
        table.addContainerProperty("", String.class, "");

        table.addItem("1");
        table.addItem("1.1");
        table.setParent("1.1", "1");
        table.addItem("1.2");
        table.setParent("1.2", "1");
        table.addItem("1.2.1");
        table.setParent("1.2.1", "1.2");
        table.addItem("1.2.1.1");
        table.setParent("1.2.1.1", "1.2.1");
        table.addItem("1.2.2");
        table.setParent("1.2.2", "1.2");
        table.addItem("2");
        table.addItem("2.1");
        table.setParent("2.1", "2");

        testRead(design, table);
        testWrite(design, table, true);
    }

    @Test
    public void testCollapsed() {
        String design = "<vaadin7-tree-table>" //
                + "  <table>" //
                + "    <colgroup><col property-id=''></colgroup>" //
                + "    <tbody>" //
                + "      <tr item-id='1' collapsed=false><td></tr>" //
                + "        <tr depth=1 item-id='1.1'><td></tr>" //
                + "          <tr depth=2 item-id='1.1.1'><td></tr>" //
                + "    </tbody>" //
                + "  </table>" //
                + "</vaadin7-tree-table>";

        TreeTable table = getTable();
        table.addContainerProperty("", String.class, "");

        table.addItem("1");
        table.setCollapsed("1", false);
        table.addItem("1.1");
        table.setParent("1.1", "1");
        table.addItem("1.1.1");
        table.setParent("1.1.1", "1.1");

        testRead(design, table);
        testWrite(design, table, true);
    }

    @Test
    public void testMalformedHierarchy() {
        assertMalformed("<tr depth=-4><td>");
        assertMalformed("<tr depth=1><td>");
        assertMalformed("<tr><td><tr depth=3><td>");
    }

    protected void assertMalformed(String hierarchy) {
        String design = "<vaadin7-tree-table>" //
                + "  <table>" //
                + "    <colgroup><col property-id=''></colgroup>" //
                + "    <tbody>" + hierarchy + "</tbody>" //
                + "  </table>" //
                + "</vaadin7-tree-table>";

        try {
            read(design);
            fail("Malformed hierarchy should fail: " + hierarchy);
        } catch (DesignException expected) {
        }
    }

    @Override
    protected void compareBody(Table read, Table expected) {
        super.compareBody(read, expected);

        for (Object itemId : read.getItemIds()) {
            assertEquals("parent of item " + itemId,
                    ((TreeTable) expected).getParent(itemId),
                    ((TreeTable) read).getParent(itemId));
            assertEquals("collapsed status of item " + itemId,
                    ((TreeTable) expected).isCollapsed(itemId),
                    ((TreeTable) read).isCollapsed(itemId));
        }
    }

    @Override
    protected TreeTable getTable() {
        return new TreeTable();
    }

    @Override
    protected String getTag() {
        return "vaadin7-tree-table";
    }
}
