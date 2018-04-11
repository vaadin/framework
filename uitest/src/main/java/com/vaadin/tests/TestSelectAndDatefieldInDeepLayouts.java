package com.vaadin.tests;

import java.util.Collection;
import java.util.Vector;

import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Select;

/**
 * This test has a somewhat deep layout within one page. At the bottom, Select
 * and Datefield render their popups incorrectly. Popus tend to be "left behind"
 * from the actual components. When the page is even bigger or longer, the
 * popups are eventually rendered outside the visual parts of the page.
 *
 * @author Ville Ingman
 *
 */
public class TestSelectAndDatefieldInDeepLayouts extends CustomComponent {

    public TestSelectAndDatefieldInDeepLayouts() {
        final AbstractOrderedLayout root = getOrderedLayout();
        setCompositionRoot(root);

        root.addComponent(getSelect());
        root.addComponent(getDateField());
        root.addComponent(getSelect());
        root.addComponent(getDateField());

        final VerticalLayout p1Layout = createPanelLayout();
        final Panel p1 = getPanel(p1Layout);
        p1.setContent(p1Layout);
        root.addComponent(p1);

        p1Layout.addComponent(getSelect());
        p1Layout.addComponent(getDateField());
        p1Layout.addComponent(getSelect());
        p1Layout.addComponent(getDateField());

        final AbstractOrderedLayout l1 = getOrderedLayout();
        p1Layout.addComponent(l1);

        l1.addComponent(getSelect());
        l1.addComponent(getDateField());
        l1.addComponent(getSelect());
        l1.addComponent(getDateField());

        final VerticalLayout p2Layout = createPanelLayout();
        final Panel p2 = getPanel(p2Layout);
        l1.addComponent(p2);

        p2Layout.addComponent(getSelect());
        p2Layout.addComponent(getDateField());
        p2Layout.addComponent(getSelect());
        p2Layout.addComponent(getDateField());
    }

    VerticalLayout getOrderedLayout() {
        final VerticalLayout l = new VerticalLayout();
        l.setCaption(getCaption("orderedlayout"));
        return l;
    }

    private VerticalLayout createPanelLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        return layout;
    }

    Panel getPanel(ComponentContainer content) {
        final Panel panel = new Panel(content);
        panel.setCaption(getCaption("panel"));
        return panel;
    }

    Component getSelect() {
        return new Select(getCaption("select"), getSelectOptions());
    }

    Component getDateField() {
        return new TestDateField(getCaption("datefield"));
    }

    private Collection<String> getSelectOptions() {
        final Collection<String> opts = new Vector<>(3);
        opts.add(getCaption("opt 1"));
        opts.add(getCaption("opt 2"));
        opts.add(getCaption("opt 3"));
        return opts;
    }

    private String getCaption(String string) {
        return string + (Math.random() * 99999.9);
        // This is Java 5 code:
        // return string + " " + UUID.randomUUID().toString().substring(0, 5);
    }

}
