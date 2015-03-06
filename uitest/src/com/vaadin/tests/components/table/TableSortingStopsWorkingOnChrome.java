package com.vaadin.tests.components.table;

import java.io.Serializable;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnReorderEvent;
import com.vaadin.ui.Table.ColumnReorderListener;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
@SuppressWarnings("serial")
public class TableSortingStopsWorkingOnChrome extends AbstractTestUI {

    protected static final int ROW_COUNT = 100;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Table table = new Table();
        table.setColumnReorderingAllowed(true);
        table.setSizeFull();

        BeanItemContainer<TestItem> cont = new BeanItemContainer<TestItem>(
                TestItem.class);

        for (int i = 0; i < ROW_COUNT; i++) {
            TestItem ti = new TestItem();
            ti.setValue1("Value1_" + i);
            ti.setValue2("Value2_" + (ROW_COUNT - i));
            ti.setValue3("Value3_" + i);
            ti.setValue4("Value4_" + (ROW_COUNT - i));
            ti.setValue5("Value5_" + i);
            cont.addBean(ti);
        }

        table.setContainerDataSource(cont);
        table.setImmediate(true);
        table.setSelectable(true);
        table.setMultiSelect(false);

        table.setPageLength(10);
        table.setDragMode(Table.TableDragMode.ROW);

        table.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dragAndDropEvent) {

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        });

        table.addColumnReorderListener(new ColumnReorderListener() {

            @Override
            public void columnReorder(ColumnReorderEvent event) {
                System.out.println("columnReorder");
            }
        });

        table.addHeaderClickListener(new HeaderClickListener() {

            @Override
            public void headerClick(HeaderClickEvent event) {
                System.out.println("Header was clicked");
            }
        });

        layout.addComponent(table);

        addComponent(layout);
    }

    public class TestItem implements Serializable {
        private static final long serialVersionUID = -745849615488792221L;

        private String value1;
        private String value2;
        private String value3;
        private String value4;
        private String value5;

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public String getValue3() {
            return value3;
        }

        public void setValue3(String value3) {
            this.value3 = value3;
        }

        public String getValue4() {
            return value4;
        }

        public void setValue4(String value4) {
            this.value4 = value4;
        }

        public String getValue5() {
            return value5;
        }

        public void setValue5(String value5) {
            this.value5 = value5;
        }

    }

    @Override
    protected String getTestDescription() {
        return "After an indeterminate period of time sorting tables via clicking on the column header should not stop working on Chrome";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14796;
    }

}
