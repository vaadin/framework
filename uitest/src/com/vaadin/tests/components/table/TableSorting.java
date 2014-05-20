package com.vaadin.tests.components.table;

import java.io.Serializable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class TableSorting extends TestBase {

    @Override
    public void setup() {
        final Label showID = new Label("");
        final Table testTable = new Table();

        BeanItemContainer<TestItem> cont = new BeanItemContainer<TestItem>(
                TestItem.class);

        for (int i = 0; i < 20; i++) {
            TestItem ti = new TestItem();
            ti.setTestName("Name_" + i);
            cont.addBean(ti);
        }
        testTable.setContainerDataSource(cont);
        testTable.setImmediate(true);
        testTable.setSelectable(true);
        testTable.setMultiSelect(false);
        testTable.setVisibleColumns(new Object[] { "testName" });

        // Handle selection change.
        testTable.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                System.out.println("ValueChanged: "
                        + testTable.getValue().toString());
                showID.setCaption("ID: " + testTable.getValue().toString());
            }
        });
        addComponent(testTable);
        addComponent(showID);
    }

    public class TestItem implements Serializable {
        private static final long serialVersionUID = -745849615488792221L;
        private String testName;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }
    }

    @Override
    protected String getDescription() {
        return "Click the header to sort the table, then click on \"Name_10\". This should show ID: com.vaadin.tests.components.table.TableSorting$TestItem@<hex id> below the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4537;
    }
}
