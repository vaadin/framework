package com.vaadin.tests.server.component.table;

import junit.framework.TestCase;

import org.apache.commons.lang.SerializationUtils;

import com.vaadin.ui.Table;

public class TableSerializationTest extends TestCase {

    public void testSerialization() {
        Table t = new Table();
        byte[] ser = SerializationUtils.serialize(t);
        Table t2 = (Table) SerializationUtils.deserialize(ser);

    }

    public void testSerializationWithRowHeaders() {
        Table t = new Table();
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_EXPLICIT);
        t.setColumnWidth(null, 100);
        byte[] ser = SerializationUtils.serialize(t);
        Table t2 = (Table) SerializationUtils.deserialize(ser);
    }
}
