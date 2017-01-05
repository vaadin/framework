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
