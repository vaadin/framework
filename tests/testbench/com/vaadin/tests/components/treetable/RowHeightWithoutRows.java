package com.vaadin.tests.components.treetable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TreeTable;

public class RowHeightWithoutRows extends TestBase {

    private TreeTable treeTable = new TreeTable();

    private BeanItemContainer<User> container = new BeanItemContainer<User>(
            User.class);

    @Override
    public void setup() {
        treeTable.setContainerDataSource(container);
        treeTable.setPageLength(0);

        addComponent(treeTable);

        Button refresh = new Button("Add two elements");
        addComponent(refresh);
        refresh.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                addTwoElements();
            }
        });

        Button reset = new Button("Reset");
        addComponent(reset);
        reset.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                container.removeAllItems();
            }
        });

        Button refresh5 = new Button("Add five elements");
        addComponent(refresh5);
        refresh5.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                container.addBean(new User("John", "Doe"));
                container.addBean(new User("Mark", "Twain"));
                container.addBean(new User("M", "T"));
                container.addBean(new User("J", "D"));
                container.addBean(new User("J", "T"));
            }
        });
        addTwoElements();
    }

    private void addTwoElements() {
        container.addBean(new User("John", "Doe"));
        container.addBean(new User("Mark", "Twain"));
    }

    public static class User {
        private String firstName;

        private String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Override
    protected String getDescription() {
        return "Reseting the tree table and then adding five elements should properly update the height of the TreeTable";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9203);
    }
}
