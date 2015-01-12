package com.vaadin.tests.server.component.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetTest {

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

        assertEquals(null, tabSheet.getTab(3));
    }

    @Test
    public void selectTab() {
        TabSheet tabSheet = new TabSheet();
        Tab tab1 = tabSheet.addTab(new Label("aaa"));
        Tab tab2 = tabSheet.addTab(new Label("bbb"));
        Tab tab3 = tabSheet.addTab(new Label("ccc"));
        Label componentNotInSheet = new Label("ddd");
        Tab tabNotInSheet = new TabSheet().addTab(new Label("eee"));

        assertEquals(tab1.getComponent(), tabSheet.getSelectedTab());

        // Select tab by component...
        tabSheet.setSelectedTab(tab2.getComponent());
        assertEquals(tab2.getComponent(), tabSheet.getSelectedTab());

        // by tab instance
        tabSheet.setSelectedTab(tab3);
        assertEquals(tab3.getComponent(), tabSheet.getSelectedTab());

        // by index
        tabSheet.setSelectedTab(0);
        assertEquals(tab1.getComponent(), tabSheet.getSelectedTab());

        // Should be no-op...
        tabSheet.setSelectedTab(componentNotInSheet);
        assertEquals(tab1.getComponent(), tabSheet.getSelectedTab());

        // this as well
        tabSheet.setSelectedTab(tabNotInSheet);
        assertEquals(tab1.getComponent(), tabSheet.getSelectedTab());

        // and this
        tabSheet.setSelectedTab(123);
        assertEquals(tab1.getComponent(), tabSheet.getSelectedTab());
    }

    @Test
    public void replaceComponent() {
        TabSheet tabSheet = new TabSheet();
        Label lbl1 = new Label("aaa");
        Label lbl2 = new Label("bbb");
        Label lbl3 = new Label("ccc");
        Label lbl4 = new Label("ddd");

        Tab tab1 = tabSheet.addTab(lbl1);
        tab1.setCaption("tab1");
        tab1.setClosable(true);
        Tab tab2 = tabSheet.addTab(lbl2);
        tab2.setDescription("description");
        tab2.setEnabled(false);

        // Replace component not in tabsheet with one already in tabsheet -
        // should be no-op
        tabSheet.replaceComponent(lbl3, lbl2);
        assertEquals(2, tabSheet.getComponentCount());
        assertSame(tab1, tabSheet.getTab(lbl1));
        assertSame(tab2, tabSheet.getTab(lbl2));
        assertNull(tabSheet.getTab(lbl3));

        // Replace component not in tabsheet with one not in tabsheet either
        // should add lbl4 as last tab
        tabSheet.replaceComponent(lbl3, lbl4);
        assertEquals(3, tabSheet.getComponentCount());
        assertSame(tab1, tabSheet.getTab(lbl1));
        assertSame(tab2, tabSheet.getTab(lbl2));
        assertEquals(2, tabSheet.getTabPosition(tabSheet.getTab(lbl4)));

        // Replace component in tabsheet with another
        // should swap places, tab association should stay the same but tabs
        // should swap metadata
        tabSheet.replaceComponent(lbl1, lbl2);
        assertSame(tab1, tabSheet.getTab(lbl1));
        assertSame(tab2, tabSheet.getTab(lbl2));
        assertEquals(false, tab1.isClosable());
        assertEquals(true, tab2.isClosable());
        assertEquals(false, tab1.isEnabled());
        assertEquals(true, tab2.isEnabled());
        assertEquals("description", tab1.getDescription());
        assertEquals(null, tab2.getDescription());
        assertEquals(3, tabSheet.getComponentCount());
        assertEquals(1, tabSheet.getTabPosition(tabSheet.getTab(lbl1)));
        assertEquals(0, tabSheet.getTabPosition(tabSheet.getTab(lbl2)));

        // Replace component in tabsheet with one not in tabsheet
        // should create a new tab instance for the new component, old tab
        // instance should become unattached
        // tab metadata should be copied from old to new
        tabSheet.replaceComponent(lbl1, lbl3);
        assertEquals(3, tabSheet.getComponentCount());
        assertNull(tabSheet.getTab(lbl1));
        assertNull(tab1.getComponent());
        assertNotNull(tabSheet.getTab(lbl3));
        assertEquals(false, tabSheet.getTab(lbl3).isEnabled());
        assertEquals("description", tab1.getDescription());
        assertEquals(1, tabSheet.getTabPosition(tabSheet.getTab(lbl3)));
    }
}
