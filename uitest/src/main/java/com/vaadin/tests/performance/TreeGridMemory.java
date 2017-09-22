package com.vaadin.tests.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.TreeGrid;

public class TreeGridMemory extends AbstractBeansMemoryTest<TreeGrid<Person>> {

    public static final String PATH = "/tree-grid-memory/";

    @WebServlet(urlPatterns = PATH
            + "*", name = "TreeGridServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = TreeGridMemory.class, productionMode = false)
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
    protected TreeGrid<Person> createComponent() {
        TreeGrid<Person> treeGrid = new TreeGrid<>();
        treeGrid.addColumn(Person::getFirstName).setCaption("First Name");
        treeGrid.addColumn(Person::getLastName).setCaption("Last Name");
        treeGrid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getStreetAddress).orElse(null))
                .setCaption("Street");
        treeGrid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getPostalCode).map(Object::toString).orElse(""))
                .setCaption("Zip");
        treeGrid.addColumn(person -> Optional.ofNullable(person.getAddress())
                .map(Address::getCity).orElse(null)).setCaption("City");
        return treeGrid;
    }

    @Override
    protected void setInMemoryContainer(TreeGrid<Person> treeGrid,
            List<Person> data) {
        TreeData<Person> treeData = new TreeData<>();
        treeGrid.setDataProvider(
                new TreeDataProvider<>(treeData));
        List<Person> toExpand = new ArrayList<>();
        if (data.size() != 0 && data.size() % 2 == 0) {
            // treat list as if it were a balanced binary tree
            treeData.addItem(null, data.get(0));
            int n = 0;
            while (2 * n + 2 < data.size()) {
                treeData.addItems(data.get(n),
                        data.subList(2 * n + 1, 2 * n + 3));
                toExpand.add(data.get(n));
                n++;
            }
        } else {
            treeData.addItems(null, data);
        }
        if (initiallyExpanded) {
            treeGrid.expand(toExpand);
        }
    }

    @Override
    protected void setBackendContainer(TreeGrid<Person> component,
            List<Person> data) {
        throw new UnsupportedOperationException();
    }
}
