package com.vaadin.tests.performance;

import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableMemory extends AbstractBeansMemoryTest<TreeTable> {

    public static final String PATH = "/tree-table-memory/";

    @WebServlet(urlPatterns = PATH
            + "*", name = "TreeTableServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = TreeTableMemory.class, productionMode = false, widgetset = "com.vaadin.v7.Vaadin7WidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private boolean initiallyExpanded = false;

    @Override
    protected void init(VaadinRequest request) {
        if (request.getParameter("initiallyExpanded") != null) {
            initiallyExpanded = true;
        }
        super.init(request);
    }

    @Override
    protected TreeTable createComponent() {
        TreeTable treeTable = new TreeTable();
        return treeTable;
    }

    @Override
    protected void setInMemoryContainer(TreeTable treeTable,
            List<Person> data) {
        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("firstName", String.class, null);
        container.addContainerProperty("lastName", String.class, null);
        container.addContainerProperty("street", String.class, null);
        container.addContainerProperty("zip", String.class, null);
        container.addContainerProperty("city", String.class, null);
        treeTable.setContainerDataSource(container);

        if (data.size() != 0 && data.size() % 2 == 0) {
            createItem(0, container, data);
            treeTable.setCollapsed(0, false);
            int n = 0;
            while (2 * n + 2 < data.size()) {
                for (int i : new Integer[] { 1, 2 }) {
                    createItem(2 * n + i, container, data);
                    container.setParent(2 * n + i, n);
                    if (initiallyExpanded) {
                        treeTable.setCollapsed(2 * n + i, false);
                    }
                }
                n++;
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                createItem(i, container, data);
            }
        }
    }

    private void createItem(int index, HierarchicalContainer container,
            List<Person> data) {
        Item item = container.addItem(index);
        item.getItemProperty("firstName")
                .setValue(data.get(index).getFirstName());
        item.getItemProperty("lastName")
                .setValue(data.get(index).getLastName());
        item.getItemProperty("street")
                .setValue(Optional.ofNullable(data.get(index).getAddress())
                        .map(Address::getStreetAddress).orElse(null));
        item.getItemProperty("zip")
                .setValue(Optional.ofNullable(data.get(index).getAddress())
                        .map(Address::getPostalCode).map(Object::toString)
                        .orElse(""));
        item.getItemProperty("city")
                .setValue(Optional.ofNullable(data.get(index).getAddress())
                        .map(Address::getCity).orElse(null));
    }

    @Override
    protected void setBackendContainer(TreeTable component, List<Person> data) {
        throw new UnsupportedOperationException();
    }
}
