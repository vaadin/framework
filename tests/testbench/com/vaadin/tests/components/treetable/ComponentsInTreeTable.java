package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TreeTable;

public class ComponentsInTreeTable extends TestBase {

    @Override
    protected void setup() {
        TreeTable tt = new TreeTable();
        tt.setWidth("300px");
        addComponent(tt);

        Object id, id2;

        tt.addContainerProperty("foo", Component.class, "");
        tt.addContainerProperty("bar", String.class, "bar");
        tt.addContainerProperty("baz", String.class, "baz");

        id = tt.addItem();
        Layout l = new HorizontalLayout();
        l.addComponent(new Label("bar"));
        l.addComponent(new Label("bar"));
        tt.getContainerProperty(id, "foo").setValue(l);

        id = tt.addItem();
        Label lbl = new Label("<b>foo</b><br/><i>bar</i>");
        lbl.setContentMode(Label.CONTENT_XHTML);
        tt.getContainerProperty(id, "foo").setValue(lbl);

        id2 = tt.addItem();
        tt.setParent(id2, id);
        tt.getContainerProperty(id2, "foo").setValue(new Button("Test"));
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
