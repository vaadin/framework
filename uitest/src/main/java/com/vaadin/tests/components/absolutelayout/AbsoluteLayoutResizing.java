package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalSplitPanel;

public class AbsoluteLayoutResizing extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        AbsoluteLayout al = new AbsoluteLayout();

        TextArea ta = new TextArea();
        ta.setValue("When resizing the layout this text area should also get resized");
        ta.setSizeFull();
        al.addComponent(ta, "left: 10px; right: 10px; top: 10px; bottom: 10px;");

        HorizontalSplitPanel horizPanel = new HorizontalSplitPanel();
        horizPanel.setSizeFull();
        horizPanel.setFirstComponent(al);

        VerticalSplitPanel vertPanel = new VerticalSplitPanel();
        vertPanel.setSizeFull();
        vertPanel.setFirstComponent(horizPanel);

        addComponent(vertPanel);

    }

    @Override
    protected String getDescription() {
        return "Absolute layout should correctly dynamically resize itself";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10427;
    }

}
