/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Vaadin Ltd
 */

@SuppressWarnings("serial")
public class SetPageFirstItemLoadsNeededRowsOnly extends AbstractTestUI {

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
        table.setVisibleColumns(new Object[] { "i" });
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
