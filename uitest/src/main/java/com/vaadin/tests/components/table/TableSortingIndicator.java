/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.table;

import java.util.Random;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

/**
 * Test if the table sorting indicators update correctly when the table is
 * sorted serverside
 * 
 * @author Vaadin Ltd
 */
public class TableSortingIndicator extends AbstractTestUI {

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
        Button sortButton = new Button("Sort", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.sort(new Object[] { "val1" }, new boolean[] { false });
            }
        });
        addComponent(sortButton);
    }

    private Container buildContainer() {
        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
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
