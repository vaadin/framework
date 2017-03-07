/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
