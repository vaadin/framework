package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class ComponentsInTreeTable extends TestBase {

    @Override
    protected void setup() {
        TreeTable tt = new TreeTable();
        tt.setWidth("300px");
        tt.setHeight("300px");
        addComponent(tt);

        tt.addContainerProperty("component", Component.class, "");
        tt.addContainerProperty("type", String.class, "bar");

        Layout l = new HorizontalLayout();
        l.addComponent(new Label("bar"));
        l.addComponent(new Label("bar"));
        tt.addItem(new Object[] { l, "HorizontalLayout" }, 1);

        l = new VerticalLayout();
        l.addComponent(new Label("baz"));
        l.addComponent(new Label("baz"));
        tt.addItem(new Object[] { l, "VerticalLayout" }, 2);

        Label lbl = new Label("<b>foo</b><br/><i>bar</i>");
        lbl.setContentMode(Label.CONTENT_XHTML);
        tt.addItem(new Object[] { lbl, "Label" }, 3);

        tt.addItem(new Object[] { new Button("Test"), "Button" }, 4);
        tt.setParent(4, 3);
    }

    @Override
    protected String getDescription() {
        return "Components in TreeTable cells should be rendered inline with the expand/collapse arrow";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7387;
    }
}
