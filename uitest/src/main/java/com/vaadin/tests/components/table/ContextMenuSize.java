package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test UI for table context menu position and size.
 *
 * @author Vaadin Ltd
 */
public class ContextMenuSize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.setPageLength(1);
        table.addActionHandler(new Handler() {

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("action1"),
                        new Action("action2"), new Action("action3"),
                        new Action("action4"), new Action("action5"),
                        new Action("action6"), new Action("action7"),
                        new Action("action8"), new Action("action9"),
                        new Action("action10"), new Action("action11"),
                        new Action("action12"), new Action("action13"),
                        new Action("action14") };
            }
        });
        BeanItemContainer<Bean> container = new BeanItemContainer<>(Bean.class);
        container.addBean(new Bean());
        table.setContainerDataSource(container);
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "If context menu original position doesn't allow to show it then "
                + "its bottom should be aligned with the window bottom and height "
                + "should be reset after repositioning.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14863;
    }

    public static class Bean {

        public String getName() {
            return "name";
        }

        public void setName() {
        }
    }
}
