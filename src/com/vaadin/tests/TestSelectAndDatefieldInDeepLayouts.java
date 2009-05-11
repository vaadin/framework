/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.util.Collection;
import java.util.Vector;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;

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
        final OrderedLayout root = (OrderedLayout) getOrderedLayout();
        setCompositionRoot(root);

        root.addComponent(getSelect());
        root.addComponent(getDateField());
        root.addComponent(getSelect());
        root.addComponent(getDateField());

        final Panel p1 = getPanel();
        root.addComponent(p1);

        p1.addComponent(getSelect());
        p1.addComponent(getDateField());
        p1.addComponent(getSelect());
        p1.addComponent(getDateField());

        final OrderedLayout l1 = (OrderedLayout) getOrderedLayout();
        p1.addComponent(l1);

        l1.addComponent(getSelect());
        l1.addComponent(getDateField());
        l1.addComponent(getSelect());
        l1.addComponent(getDateField());

        final Panel p2 = getPanel();
        l1.addComponent(p2);

        p2.addComponent(getSelect());
        p2.addComponent(getDateField());
        p2.addComponent(getSelect());
        p2.addComponent(getDateField());

    }

    AbstractLayout getOrderedLayout() {
        final OrderedLayout l = new OrderedLayout();
        l.setCaption(getCaption("orderedlayout"));
        return l;
    }

    Panel getPanel() {
        final Panel panel = new Panel();
        panel.setCaption(getCaption("panel"));
        return panel;
    }

    Component getSelect() {
        return new Select(getCaption("select"), getSelectOptions());
    }

    Component getDateField() {
        return new DateField(getCaption("datefield"));
    }

    private Collection getSelectOptions() {
        final Collection opts = new Vector(3);
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
