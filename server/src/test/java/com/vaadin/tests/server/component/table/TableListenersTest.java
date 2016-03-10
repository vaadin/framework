package com.vaadin.tests.server.component.table;

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
    public void testColumnResizeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ColumnResizeEvent.class,
                ColumnResizeListener.class);
    }

    public void testItemClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ItemClickEvent.class,
                ItemClickListener.class);
    }

    public void testFooterClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, FooterClickEvent.class,
                FooterClickListener.class);
    }

    public void testHeaderClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, HeaderClickEvent.class,
                HeaderClickListener.class);
    }

    public void testColumnReorderListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ColumnReorderEvent.class,
                ColumnReorderListener.class);
    }
}
