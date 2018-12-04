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
