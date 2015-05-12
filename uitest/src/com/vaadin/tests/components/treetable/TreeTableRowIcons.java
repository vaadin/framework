package com.vaadin.tests.components.treetable;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;

public class TreeTableRowIcons extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        addComponent(layout);

        layout.addComponent(createTreeTableAndPopulate(new ThemeResource(
                "../runo/icons/16/ok.png")));
        layout.addComponent(createTreeTableAndPopulate(FontAwesome.ANDROID));
    }

    private TreeTable createTreeTableAndPopulate(Resource icon) {
        TreeTable tt = new TreeTable();
        tt.addContainerProperty("Foo", String.class, "");
        tt.setColumnWidth("Foo", 100);
        tt.addContainerProperty("Bar", String.class, "");
        tt.setColumnWidth("Bar", 100);
        tt.setIcon(icon);
        tt.setHeight(400, PIXELS);

        Object item1 = tt.addItem(new Object[] { "Foo", "Bar" }, null);
        Object item2 = tt.addItem(new Object[] { "Foo2", "Bar2" }, null);
        tt.setItemIcon(item1, icon);
        tt.setItemIcon(item2, icon);

        tt.setParent(item2, item1);

        tt.setCollapsed(item1, false);

        return tt;
    }

    @Override
    protected String getTestDescription() {
        return "TreeTable should support font icons for items";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14077;
    }

}