package com.vaadin.tests.components.tabsheet;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ScrollbarsInNestedTabsheets extends TestBase {

    @Override
    public void setup() {
        setTheme("chameleon");
        final Label l = new Label("Select Sub Tab 2");
        final TabSheet t = new TabSheet();
        final TabSheet t2 = getTabSheet();
        t.addTab(t2, "Main Tab");
        addComponent(l);
        addComponent(t);
    }

    private TabSheet getTabSheet() {
        final TabSheet t = new TabSheet();
        t.addTab(getDummyLayout1(), "Sub Tab 1");
        t.addTab(getDummyLayout2(), "Sub Tab 2");

        return t;
    }

    private Layout getDummyLayout1() {
        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new DateField("Date"));

        return l;
    }

    private Layout getDummyLayout2() {
        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new DateField("Date"));
        l.addComponent(new LegacyTextField("TextField"));

        return l;
    }

    @Override
    protected String getDescription() {
        return "Nested tabsheets show unwanted scrollbars with Chameleon theme when the inner tabsheet is resized";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8625;
    }

}
