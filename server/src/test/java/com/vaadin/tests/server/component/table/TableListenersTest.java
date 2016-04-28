package com.vaadin.tests.server.component.table;

import org.junit.Test;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnReorderListener;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.Table.ColumnResizeListener;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;

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
