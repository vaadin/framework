package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * Test for #8291 and #7666: NegativeArraySizeException when Table scrolled to
 * the end and its size reduced.
 */
public class Ticket8291 extends UI {

    @Override
    public void init(VaadinRequest request) {
        setContent(new TestView());
    }

    private static class DecimateFilter implements Filter {
        @Override
        public boolean passesFilter(Object itemId, Item item)
                throws UnsupportedOperationException {
            return ((((TestObject) itemId).property3 % 10) == 0);
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return true;
        }
    }

    private static class TestView extends HorizontalLayout {

        private Filter filter = null;

        private boolean reduceData;

        private TestView() {
            final Table table = new Table();
            List<TestObject> data = createData(1000);
            final BeanItemContainer<TestObject> container = new BeanItemContainer<TestObject>(
                    TestObject.class, data) {

                @Override
                public int size() {
                    if (reduceData) {
                        return 100;
                    } else {
                        return super.size();
                    }
                }
            };
            table.setContainerDataSource(container);
            addComponent(table);
            Button button = new Button("Click");
            button.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    reduceData = !reduceData;
                    table.refreshRowCache();
                }
            });
            addComponent(button);
            Button button2 = new Button("Filter");
            button2.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    if (filter != null) {
                        container.removeAllContainerFilters();
                        filter = null;
                    } else {
                        filter = new DecimateFilter();
                        container.addContainerFilter(filter);
                    }
                    table.refreshRowCache();
                }
            });
            addComponent(button2);
        }
    }

    private static List<TestObject> createData(int count) {
        ArrayList<TestObject> data = new ArrayList<TestObject>(count);
        for (int i = 0; i < count; i++) {
            data.add(new TestObject("string-" + i, new Date(), i));
        }
        return data;
    }

    public static class TestObject {

        private String property1;
        private Date property2;
        private Integer property3;

        public TestObject(String property1, Date property2, Integer property3) {
            this.property1 = property1;
            this.property2 = property2;
            this.property3 = property3;
        }

        public String getProperty1() {
            return property1;
        }

        public Date getProperty2() {
            return property2;
        }

        public Integer getProperty3() {
            return property3;
        }

    }

}
