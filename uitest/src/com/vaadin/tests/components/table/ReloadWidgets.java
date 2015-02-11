package com.vaadin.tests.components.table;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ReloadWidgets extends AbstractTestUI {

    int pressed = 0;

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table(null, new BeanItemContainer<Bean>(
                Bean.class));
        table.setId("table");
        table.setSizeFull();

        table.setColumnHeader("col1", "Text");
        table.setColumnHeader("col2", "Button");

        fillTable(table);

        Button button = new Button("Refresh");
        button.setId("refresh");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.removeAllItems();
                fillTable(table);
            }
        });
        getLayout().addComponent(button);
        getLayout().addComponent(table);
        getLayout().setExpandRatio(table, 1f);
    }

    @Override
    protected String getTestDescription() {
        return "Table should always populate button widgets to column 2";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16611;
    }

    private void fillTable(Table table) {
        int i = 0;
        int size = pressed % 2 == 0 ? 500 : 499;
        pressed++;
        for (int step = 0; step < i + size; step++) {
            String caption = Integer.toString(step);
            Button button = new Button(caption);
            button.setId(caption);
            Bean itemId = new Bean(caption, button);
            table.addItem(itemId);
        }
    }

    public class Bean {
        private String col1;
        private Button col2;

        public Bean(String col1, Button col2) {
            this.col1 = col1;
            this.col2 = col2;
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public Button getCol2() {
            return col2;
        }

        public void setCol2(Button col2) {
            this.col2 = col2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Bean bean = (Bean) o;

            if (!col1.equals(bean.col1)) {
                return false;
            }
            if (!col2.equals(bean.col2)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = col1.hashCode();
            result = 31 * result + col2.hashCode();
            return result;
        }
    }
}
