package com.itmill.toolkit.tests.components.tabsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.AbstractComponentContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class RemoveTabs extends TestBase {

    protected TabSheet tabsheet;

    protected Component[] tab = new Component[5];

    private Button closeCurrent;
    private Button closeFirst;
    private Button closeLast;
    private Button reorderTabs;

    @Override
    protected String getDescription() {
        return "Tests the removal of individual tabs from a Tabsheet. No matter what is done in this test the tab caption \"Tab X\" should always match the content \"Tab X\". Use \"remove first\" and \"remove active\" buttons to remove the first or the active tab. The \"reorder\" button reverses the order by adding and removing all components.";
    }

    @Override
    protected void setup() {
        tabsheet = new TabSheet();
        for (int i = 1; i <= tab.length; i++) {
            tab[i - 1] = new Label("This is the contents of tab " + i);
            tab[i - 1].setCaption("Tab " + i);

            tabsheet.addComponent(tab[i - 1]);
        }

        getLayout().addComponent(tabsheet);

        closeCurrent = new Button("Close current tab");
        closeCurrent.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                closeCurrentTab();

            }
        });

        closeFirst = new Button("close first tab");
        closeFirst.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                closeFirstTab();

            }
        });

        closeLast = new Button("close last tab");
        closeLast.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                closeLastTab();

            }
        });

        reorderTabs = new Button("reorder");
        reorderTabs.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                reorder();

            }
        });

        getLayout().addComponent(closeFirst);
        getLayout().addComponent(closeLast);
        getLayout().addComponent(closeCurrent);
        getLayout().addComponent(reorderTabs);

    }

    private void closeCurrentTab() {
        Component c = tabsheet.getSelectedTab();
        if (c != null) {
            tabsheet.removeComponent(c);
        }
    }

    private void closeFirstTab() {
        tabsheet.removeComponent((Component) tabsheet.getComponentIterator()
                .next());
    }

    @SuppressWarnings("unchecked")
    private void closeLastTab() {
        Iterator i = tabsheet.getComponentIterator();
        Component last = null;
        while (i.hasNext()) {
            last = (Component) i.next();

        }
        tabsheet.removeComponent(last);
    }

    @SuppressWarnings("unchecked")
    private void reorder() {
        AbstractComponentContainer container = tabsheet;

        if (container != null) {
            List<Component> c = new ArrayList<Component>();
            Iterator<Component> i = container.getComponentIterator();
            while (i.hasNext()) {
                Component comp = i.next();
                c.add(comp);
            }
            container.removeAllComponents();

            for (int j = c.size() - 1; j >= 0; j--) {
                container.addComponent(c.get(j));
            }

        }
    }
}
