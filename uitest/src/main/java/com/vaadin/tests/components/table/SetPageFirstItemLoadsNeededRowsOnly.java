package com.vaadin.tests.components.table;

import java.io.Serializable;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.ui.Table;


@SuppressWarnings("serial")
public class SetPageFirstItemLoadsNeededRowsOnly
        extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        addComponent(layout);

        final Label label = new Label("");
        addComponent(label);

        BeanContainer<String, Bean> beans = new BeanContainer<String, Bean>(
                Bean.class) {
            @Override
            public List<String> getItemIds(int startIndex, int numberOfIds) {
                label.setValue("rows requested: " + numberOfIds);
                return super.getItemIds(startIndex, numberOfIds);
            }
        };

        beans.setBeanIdProperty("i");
        for (int i = 0; i < 2000; i++) {
            beans.addBean(new Bean(i));
        }

        final Table table = new Table("Beans", beans);
        table.setVisibleColumns("i");
        layout.addComponent(table);

        table.setCurrentPageFirstItemIndex(table.size() - 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Only cached rows and rows in viewport should be rendered after "
                + "calling table.setCurrentPageFirstItemIndex(n) - as opposed to all rows "
                + "between the previous position and new position";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14135;
    }

    public class Bean implements Serializable {

        private Integer i;

        public Bean(Integer i) {
            this.i = i;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }
    }
}
