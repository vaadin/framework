package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SafariRenderingBugWhiteSpace extends TestBase {

    @Override
    protected void setup() {

        final HorizontalSplitPanel split = new HorizontalSplitPanel();

        final Table table = new Table();
        table.addContainerProperty("name", String.class, "");
        table.addContainerProperty("value", String.class, "");

        table.addItem(new Object[] { "test1", "val1" }, "test1");
        table.addItem(new Object[] { "test2", "val2" }, "test2");
        table.addItem(new Object[] { "test3", "val3" }, "test3");
        table.addItem(new Object[] { "test4", "val4" }, "test4");
        table.addItem(new Object[] { "test5", "val5" }, "test5");
        table.addItem(new Object[] { "test6", "val6" }, "test6");
        table.addItem(new Object[] { "test7", "val7" }, "test7");
        table.addItem(new Object[] { "test8", "val8" }, "test8");
        table.addItem(new Object[] { "test9", "val9" }, "test9");
        table.setSelectable(true);
        table.setImmediate(true);
        table.setSizeFull();
        table.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (table.getValue() == null) {
                    split.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
                } else {
                    split.setSplitPosition(20, Sizeable.UNITS_PERCENTAGE);
                }
            }
        });

        split.setFirstComponent(table);
        split.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel editor = new Panel("Editor", pl);
        editor.setSizeFull();
        split.setSecondComponent(editor);
        getLayout().setSizeFull();
        getLayout().addComponent(split);
    }

    @Override
    protected String getDescription() {
        return "White space between header an content should not appear, when selecting and de-selecting first row";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3875;
    }

}
