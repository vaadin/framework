package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test UI for non-collapsible column distinction in the table.
 *
 * @author Vaadin Ltd
 */
public class CollapsibleTableColumn extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        BeanItemContainer<Bean> container = new BeanItemContainer<>(Bean.class);
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
