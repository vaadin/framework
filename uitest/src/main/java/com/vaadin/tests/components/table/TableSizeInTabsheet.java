package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

@SuppressWarnings("serial")
public class TableSizeInTabsheet extends AbstractReindeerTestUI {

    static final String TABLE = "table";
    static final String TABSHEET = "tabsheet";

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        setContent(layout);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setId(TABSHEET);
        layout.addComponent(tabSheet);
        tabSheet.addTab(new TabComposite(), "Tab");
    }

    public class TabComposite extends CustomComponent {

        public TabComposite() {
            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.setSpacing(false);
            mainLayout.setMargin(false);
            addComponent(mainLayout);
            setCompositionRoot(mainLayout);

            Component table = new Table();
            table.setWidth("100%");
            table.setId(TABLE);
            mainLayout.addComponent(table);
        }
    }

    @Override
    protected String getTestDescription() {
        return "The size calculations fails in IE8 when undefined size table is inside a tabsheet";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12687;
    }

}
