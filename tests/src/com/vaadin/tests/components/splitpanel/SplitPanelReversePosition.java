package com.vaadin.tests.components.splitpanel;

import com.vaadin.terminal.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.SplitPanel.SplitterClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanelReversePosition extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setSizeFull();
        hsplit.setImmediate(true);
        hsplit.setSplitPosition(100, Sizeable.UNITS_PIXELS, true);
        hsplit.addListener(new SplitPanel.SplitterClickListener() {
            public void splitterClick(SplitterClickEvent event) {
                System.out.println(hsplit.getSplitPosition());

            }
        });

        TextField field = new TextField("");
        field.setSizeFull();
        hsplit.addComponent(field);

        final VerticalSplitPanel vsplit = new VerticalSplitPanel();
        vsplit.setSizeFull();
        vsplit.setImmediate(true);
        vsplit.setSplitPosition(10, Sizeable.UNITS_PERCENTAGE, true);
        vsplit.addListener(new SplitPanel.SplitterClickListener() {
            public void splitterClick(SplitterClickEvent event) {
                System.out.println(vsplit.getSplitPosition());

            }
        });
        hsplit.addComponent(vsplit);

        addComponent(hsplit);

        field = new TextField("");
        field.setSizeFull();
        vsplit.addComponent(field);

        field = new TextField("");
        field.setSizeFull();
        vsplit.addComponent(field);
    }

    @Override
    protected String getDescription() {
        return "The horizontal split panel should be splitted "
                + "100px from the right and the vertical split panel should "
                + "be splitted 10% from the bottom";

    }

    @Override
    protected Integer getTicketNumber() {
        return 1588;
    }

}
