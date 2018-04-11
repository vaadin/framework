package com.vaadin.tests.components.table;

import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test if the table sorting indicators update correctly when the table is
 * sorted serverside
 *
 * @author Vaadin Ltd
 */
public class TableSortingIndicator extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table("Test table", buildContainer());
        table.setSizeFull();
        addComponent(table);
        Button sortButton = new Button("Sort", event -> table
                .sort(new Object[] { "val1" }, new boolean[] { false }));
        addComponent(sortButton);
    }

    private Container buildContainer() {
        BeanItemContainer<TestBean> container = new BeanItemContainer<>(
                TestBean.class);
        for (int i = 0; i < 100; ++i) {
            TestBean item = new TestBean();
            item.setVal1(i);
            item.setVal2(randomWord());
            item.setVal3(randomWord());
            container.addBean(item);
        }
        return container;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "The table should have visible sorting indicators.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 8978;
    }

    public class TestBean {
        private Integer val1;
        private String val2;
        private String val3;

        public Integer getVal1() {
            return val1;
        }

        public void setVal1(Integer val1) {
            this.val1 = val1;
        }

        public String getVal2() {
            return val2;
        }

        public void setVal2(String val2) {
            this.val2 = val2;
        }

        public String getVal3() {
            return val3;
        }

        public void setVal3(String val3) {
            this.val3 = val3;
        }
    }

    private String randomWord() {
        Random rng = new Random();
        char[] word = new char[3 + rng.nextInt(10)];
        for (int i = 0; i < word.length; ++i) {
            word[i] = (char) ('a' + rng.nextInt(26));
        }
        return new String(word);
    }
}
