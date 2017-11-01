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
package com.vaadin.tests.components.table;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.Align;

/**
 * Test UI for issue #13399 : Left alignment should not be set explicitly
 * instead of relying on default behavior
 *
 * @author Vaadin Ltd
 */
@Theme("tests-table")
public class LeftColumnAlignment extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();

        BeanItemContainer<Bean> container = new BeanItemContainer<>(Bean.class);
        Bean bean = new Bean();
        bean.setName("property");
        container.addBean(bean);
        table.setContainerDataSource(container);

        table.setFooterVisible(true);

        table.setWidth(100, Unit.PIXELS);

        table.setColumnAlignment("name", Align.RIGHT);

        addComponent(table);

        addButton("Align to Left",
                event -> table.setColumnAlignment("name", Align.LEFT));
    }

    @Override
    protected String getTestDescription() {
        return "Left alignment should not be set explicitly instead of relying on default behavior";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13399;
    }

    public static class Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
