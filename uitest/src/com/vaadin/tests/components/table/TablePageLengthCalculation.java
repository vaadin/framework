package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TablePageLengthCalculation extends TestBase {

    @Override
    public void setup() {

        Window window = new Window();
        window.setCaption("usermanagement");
        window.center();
        window.setWidth(40, Window.UNITS_PERCENTAGE);
        window.setHeight(40, Window.UNITS_PERCENTAGE);
        window.setModal(true);
        getMainWindow().addWindow(window);

        TabSheet tab = new TabSheet();
        tab.setSizeFull();

        tab.addTab(new TableView(), "users", null);
        tab.addTab(new TableView(), "groups", null);

        window.setContent(tab);
    }

    public class TableView extends Table {
        private static final long serialVersionUID = 1L;

        public TableView() {
            setSizeFull();

            addContainerProperty("name", String.class, "name");
            addContainerProperty("right", Boolean.class, "right");
        }
    }

    @Override
    protected String getDescription() {
        return "Resize the window and change the selected tab. In Opera 10.50 the updated pagelength will be calculated as a float and not an integer, causing an \"Internal Error\"";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4374;
    }
}
