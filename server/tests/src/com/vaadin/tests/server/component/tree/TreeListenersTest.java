package com.vaadin.tests.server.component.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;

public class TreeListenersTest extends AbstractListenerMethodsTestBase {
    public void testExpandListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, ExpandEvent.class,
                ExpandListener.class);
    }

    public void testItemClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, ItemClickEvent.class,
                ItemClickListener.class);
    }

    public void testCollapseListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Tree.class, CollapseEvent.class,
                CollapseListener.class);
    }
}
