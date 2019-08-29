package com.vaadin.tests.components.treegrid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
public class TreeGridBigDetailsManager extends AbstractTestUI {

    private TreeGrid<String> treeGrid;
    private TreeDataProvider<String> treeDataProvider;
    private List<String> items = new ArrayList<String>();

    private void initializeDataProvider() {
        TreeData<String> data = new TreeData<>();
        for (int i = 0; i < 100; i++) {
            String root = "Root " + i;
            items.add(root);
            data.addItem(null, root);
            for (int j = 0; j < 10; j++) {
                String branch = "Branch " + i + "/" + j;
                items.add(branch);
                data.addItem(root, branch);
                for (int k = 0; k < 3; k++) {
                    String leaf = "Leaf " + i + "/" + j + "/" + k;
                    items.add(leaf);
                    data.addItem(branch, leaf);
                }
            }
        }
        treeDataProvider = new TreeDataProvider<>(data);
    }

    @Override
    protected void setup(VaadinRequest request) {
        initializeDataProvider();
        treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(treeDataProvider);
        treeGrid.setSizeFull();
        treeGrid.addColumn(String::toString).setCaption("String")
                .setId("string");
        treeGrid.addColumn((i) -> items.indexOf(i)).setCaption("Index");
        treeGrid.setHierarchyColumn("string");
        treeGrid.setDetailsGenerator(
                row -> new Label("details for " + row.toString()));
        treeGrid.addItemClickListener(event -> {
            treeGrid.setDetailsVisible(event.getItem(),
                    !treeGrid.isDetailsVisible(event.getItem()));
        });

        Button showDetails = new Button("Show all details", event -> {
            for (String id : items) {
                treeGrid.setDetailsVisible(id, true);
            }
        });
        showDetails.setId("showDetails");
        Button hideDetails = new Button("Hide all details", event -> {
            for (String id : items) {
                treeGrid.setDetailsVisible(id, false);
            }
        });
        hideDetails.setId("hideDetails");
        Button expandAll = new Button("Expand all", event -> {
            treeGrid.expand(items);
        });
        expandAll.setId("expandAll");
        Button collapseAll = new Button("Collapse all", event -> {
            treeGrid.collapse(items);
        });
        collapseAll.setId("collapseAll");
        @SuppressWarnings("deprecation")
        Button scrollTo55 = new Button("Scroll to 55",
                event -> treeGrid.scrollTo(55));
        scrollTo55.setId("scrollTo55");
        scrollTo55.setVisible(false);
        Button scrollTo3055 = new Button("Scroll to 3055",
                event -> treeGrid.scrollTo(3055));
        scrollTo3055.setId("scrollTo3055");
        scrollTo3055.setVisible(false);
        Button scrollToEnd = new Button("Scroll to end",
                event -> treeGrid.scrollToEnd());
        scrollToEnd.setId("scrollToEnd");
        scrollToEnd.setVisible(false);
        Button scrollToStart = new Button("Scroll to start",
                event -> treeGrid.scrollToStart());
        scrollToStart.setId("scrollToStart");
        scrollToStart.setVisible(false);

        Button toggle15 = new Button("Toggle 15",
                event -> treeGrid.setDetailsVisible(items.get(15),
                        !treeGrid.isDetailsVisible(items.get(15))));
        toggle15.setId("toggle15");
        toggle15.setVisible(false);

        Button toggle3000 = new Button("Toggle 3000",
                event -> treeGrid.setDetailsVisible(items.get(3000),
                        !treeGrid.isDetailsVisible(items.get(3000))));
        toggle3000.setId("toggle3000");
        toggle3000.setVisible(false);

        Button addGrid = new Button("Add grid", event -> {
            addComponent(treeGrid);
            getLayout().setExpandRatio(treeGrid, 2);
            scrollTo55.setVisible(true);
            scrollTo3055.setVisible(true);
            scrollToEnd.setVisible(true);
            scrollToStart.setVisible(true);
            toggle15.setVisible(true);
            toggle3000.setVisible(true);
        });
        addGrid.setId("addGrid");

        addComponents(
                new HorizontalLayout(showDetails, hideDetails, expandAll,
                        collapseAll),
                new HorizontalLayout(scrollTo55, scrollTo3055, scrollToEnd,
                        scrollToStart),
                new HorizontalLayout(addGrid, toggle15, toggle3000));

        getLayout().getParent().setHeight("100%");
        getLayout().setHeight("100%");
        treeGrid.setHeight("100%");
        setHeight("100%");
    }

    @Override
    protected String getTestDescription() {
        return "Expanding and collapsing with and without open details rows shouldn't cause exceptions. "
                + "Details row should be reopened upon expanding if it was open before collapsing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11288;
    }

}
