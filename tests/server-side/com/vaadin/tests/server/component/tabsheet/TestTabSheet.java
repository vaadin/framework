package com.vaadin.tests.server.component.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

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

    @Test
    public void getComponentFromTab() {
        Component c = new Label("abc");
        TabSheet tabSheet = new TabSheet();
        Tab tab = tabSheet.addTab(c);
        assertEquals(c, tab.getComponent());
    }

    @Test
    public void addTabWithComponentOnly() {
        TabSheet tabSheet = new TabSheet();
        Tab tab1 = tabSheet.addTab(new Label("aaa"));
        Tab tab2 = tabSheet.addTab(new Label("bbb"));
        Tab tab3 = tabSheet.addTab(new Label("ccc"));

        // Check right order of tabs
        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab2));
        assertEquals(2, tabSheet.getTabPosition(tab3));

        // Calling addTab with existing component does not move tab
        tabSheet.addTab(tab1.getComponent());

        // Check right order of tabs
        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab2));
        assertEquals(2, tabSheet.getTabPosition(tab3));
    }

    @Test
    public void addTabWithComponentAndIndex() {
        TabSheet tabSheet = new TabSheet();
        Tab tab1 = tabSheet.addTab(new Label("aaa"));
        Tab tab2 = tabSheet.addTab(new Label("bbb"));
        Tab tab3 = tabSheet.addTab(new Label("ccc"));

        Tab tab4 = tabSheet.addTab(new Label("ddd"), 1);
        Tab tab5 = tabSheet.addTab(new Label("eee"), 3);

        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab4));
        assertEquals(2, tabSheet.getTabPosition(tab2));
        assertEquals(3, tabSheet.getTabPosition(tab5));
        assertEquals(4, tabSheet.getTabPosition(tab3));

        // Calling addTab with existing component does not move tab
        tabSheet.addTab(tab1.getComponent(), 3);

        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab4));
        assertEquals(2, tabSheet.getTabPosition(tab2));
        assertEquals(3, tabSheet.getTabPosition(tab5));
        assertEquals(4, tabSheet.getTabPosition(tab3));
    }

    @Test
    public void addTabWithAllParameters() {
        TabSheet tabSheet = new TabSheet();
        Tab tab1 = tabSheet.addTab(new Label("aaa"));
        Tab tab2 = tabSheet.addTab(new Label("bbb"));
        Tab tab3 = tabSheet.addTab(new Label("ccc"));

        Tab tab4 = tabSheet.addTab(new Label("ddd"), "ddd", null, 1);
        Tab tab5 = tabSheet.addTab(new Label("eee"), "eee", null, 3);

        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab4));
        assertEquals(2, tabSheet.getTabPosition(tab2));
        assertEquals(3, tabSheet.getTabPosition(tab5));
        assertEquals(4, tabSheet.getTabPosition(tab3));

        // Calling addTab with existing component does not move tab
        tabSheet.addTab(tab1.getComponent(), "xxx", null, 3);

        assertEquals(0, tabSheet.getTabPosition(tab1));
        assertEquals(1, tabSheet.getTabPosition(tab4));
        assertEquals(2, tabSheet.getTabPosition(tab2));
        assertEquals(3, tabSheet.getTabPosition(tab5));
        assertEquals(4, tabSheet.getTabPosition(tab3));
    }

    @Test
    public void getTabByPosition() {
        TabSheet tabSheet = new TabSheet();
        Tab tab1 = tabSheet.addTab(new Label("aaa"));
        Tab tab2 = tabSheet.addTab(new Label("bbb"));
        Tab tab3 = tabSheet.addTab(new Label("ccc"));

        assertEquals(tab1, tabSheet.getTab(0));
        assertEquals(tab2, tabSheet.getTab(1));
        assertEquals(tab3, tabSheet.getTab(2));
    }
    
}
