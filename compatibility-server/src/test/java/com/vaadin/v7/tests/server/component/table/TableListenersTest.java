package com.vaadin.v7.tests.server.component.table;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnReorderEvent;
import com.vaadin.v7.ui.Table.ColumnReorderListener;
import com.vaadin.v7.ui.Table.ColumnResizeEvent;
import com.vaadin.v7.ui.Table.ColumnResizeListener;
import com.vaadin.v7.ui.Table.FooterClickEvent;
import com.vaadin.v7.ui.Table.FooterClickListener;
import com.vaadin.v7.ui.Table.HeaderClickEvent;
import com.vaadin.v7.ui.Table.HeaderClickListener;

public class TableListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testColumnResizeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ColumnResizeEvent.class,
                ColumnResizeListener.class);
    }

    @Test
    public void testItemClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ItemClickEvent.class,
                ItemClickListener.class);
    }

    @Test
    public void testFooterClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, FooterClickEvent.class,
                FooterClickListener.class);
    }

    @Test
    public void testHeaderClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, HeaderClickEvent.class,
                HeaderClickListener.class);
    }

    @Test
    public void testColumnReorderListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ColumnReorderEvent.class,
                ColumnReorderListener.class);
    }
}
