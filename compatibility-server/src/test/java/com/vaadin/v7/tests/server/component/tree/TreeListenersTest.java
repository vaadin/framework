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
package com.vaadin.v7.tests.server.component.tree;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.CollapseEvent;
import com.vaadin.v7.ui.Tree.CollapseListener;
import com.vaadin.v7.ui.Tree.ExpandEvent;
import com.vaadin.v7.ui.Tree.ExpandListener;

public class TreeListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testExpandListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, ExpandEvent.class,
                ExpandListener.class);
    }

    @Test
    public void testItemClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, ItemClickEvent.class,
                ItemClickListener.class);
    }

    @Test
    public void testCollapseListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, CollapseEvent.class,
                CollapseListener.class);
    }
}
