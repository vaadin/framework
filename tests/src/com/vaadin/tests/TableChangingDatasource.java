/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class TableChangingDatasource extends CustomComponent implements
        ClickListener {
    Table t;
    Table[] ta = new Table[4];
    private int mode = 0;

    public TableChangingDatasource() {
        final OrderedLayout main = new OrderedLayout();

        main.addComponent(new Label(
                "Table should look sane after data source changes"));

        t = new Table();

        t.setWidth(500);
        t.setHeight(300);

        ta[0] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 0);
        ta[1] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 7);
        ta[2] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 5);
        ta[3] = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(3, 1);

        main.addComponent(t);
        main.addComponent(new Button("switch DS", this));

        setCompositionRoot(main);

    }

    public void buttonClick(ClickEvent event) {
        int i = mode % 4;
        t.setContainerDataSource(ta[i].getContainerDataSource());
        mode++;
    }
}
