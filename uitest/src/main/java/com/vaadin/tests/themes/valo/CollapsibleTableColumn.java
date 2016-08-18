/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

/**
 * Test UI for non-collapsible column distinction in the table.
 *
 * @author Vaadin Ltd
 */
@Theme("valo")
public class CollapsibleTableColumn extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        BeanItemContainer<Bean> container = new BeanItemContainer<Bean>(
                Bean.class);
        Bean bean = new Bean();
        bean.setName("name");
        bean.setId(1);
        container.addBean(bean);
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsible("name", false);
        addComponent(table);
    }

    @Override
    protected Integer getTicketNumber() {
        return 15489;
    }

    @Override
    protected String getTestDescription() {
        return "Non-collapsible column should be visibly distinct (has an opacity) from "
                + "collapsible column in table column config menu.";
    }

    public static class Bean {

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        private int id;
    }
}
