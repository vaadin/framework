/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

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
