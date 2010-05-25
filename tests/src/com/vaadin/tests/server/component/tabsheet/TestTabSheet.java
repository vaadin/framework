package com.vaadin.tests.server.component.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TestTabSheet {

    @Test
    public void addExistingComponent() {
        Component c = new Label("abc");
        TabSheet tabSheet = new TabSheet();
        tabSheet.addComponent(c);
        tabSheet.addComponent(c);

        Iterator<Component> iter = tabSheet.getComponentIterator();

        assertEquals(c, iter.next());
        assertEquals(false, iter.hasNext());
        assertNotNull(tabSheet.getTab(c));
    }
}
