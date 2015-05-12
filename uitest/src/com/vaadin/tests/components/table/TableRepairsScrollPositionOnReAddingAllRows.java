package com.vaadin.tests.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

/**
 * Scroll position should be restored when removing and re-adding all rows in
 * Table.
 * 
 * @author Vaadin Ltd
 */
public class TableRepairsScrollPositionOnReAddingAllRows extends AbstractTestUI {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setup(VaadinRequest request) {
        final BeanItemContainer<TableItem> cont = new BeanItemContainer<TableItem>(
                TableItem.class);
        final List<TableItem> restoringItemList = new ArrayList<TableItem>();

        final Table table = new Table();
        table.setWidth("400px");
        table.setPageLength(-1);
        table.setContainerDataSource(cont);
        table.setSelectable(true);

        Button buttonRestore = new Button("Restore table rows");
        buttonRestore.setId("buttonRestore");
        buttonRestore.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                cont.removeAllItems();
                cont.addAll(restoringItemList);
            }
        });

        Button buttonReAddAllViaAddAll = new Button("Re-add rows all at once");
        buttonReAddAllViaAddAll.setId("buttonReAddAllViaAddAll");
        buttonReAddAllViaAddAll.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                List<TableItem> originalItemIds = new ArrayList<TableItem>(cont
                        .getItemIds());
                cont.removeAllItems();
                cont.addAll(originalItemIds);
            }
        });

        Button buttonReplaceByAnotherCollectionViaAddAll = new Button(
                "Replace by another items (via addAll())");
        buttonReplaceByAnotherCollectionViaAddAll
                .setId("buttonReplaceByAnotherCollectionViaAddAll");
        buttonReplaceByAnotherCollectionViaAddAll
                .addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        cont.removeAllItems();
                        // create new collection (of different items) with other
                        // size
                        List<TableItem> itemList = new ArrayList<TableItem>();
                        for (int i = 0; i < 79; i++) {
                            TableItem ti = new TableItem();
                            ti.setName("AnotherItem1_" + i);
                            itemList.add(ti);
                        }
                        cont.addAll(itemList);
                    }
                });

        Button buttonReplaceByAnotherCollectionViaAdd = new Button(
                "Replace by another items (via add(), add()..)");
        buttonReplaceByAnotherCollectionViaAdd
                .setId("buttonReplaceByAnotherCollectionViaAdd");
        buttonReplaceByAnotherCollectionViaAdd
                .addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        cont.removeAllItems();
                        for (int i = 0; i < 81; i++) {
                            TableItem ti = new TableItem();
                            ti.setName("AnotherItem2_" + i);
                            // add one by one in container
                            cont.addBean(ti);
                        }
                    }
                });

        Button buttonReplaceBySubsetOfSmallerSize = new Button(
                "Replace rows by sub-set of smaller size (size not enought for restoring scroll position)");
        buttonReplaceBySubsetOfSmallerSize
                .setId("buttonReplaceBySubsetOfSmallerSize");
        buttonReplaceBySubsetOfSmallerSize
                .addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        cont.removeAllItems();
                        cont.addAll(restoringItemList.subList(0, 20));
                    }
                });

        Button buttonReplaceByWholeSubsetPlusOneNew = new Button(
                "Replace rows by whole subset plus one new item");
        buttonReplaceByWholeSubsetPlusOneNew
                .setId("buttonReplaceByWholeSubsetPlusOneNew");
        buttonReplaceByWholeSubsetPlusOneNew
                .addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        cont.removeAllItems();

                        List<TableItem> list = new ArrayList<TableItem>(
                                restoringItemList);
                        TableItem ti = new TableItem();
                        ti.setName("AnotherItem3_" + 80);
                        list.add(ti);
                        cont.addAll(list);
                    }
                });

        Button buttonRemoveAllAddOne = new Button(
                "Remove all items and add only one new item");
        buttonRemoveAllAddOne.setId("buttonRemoveAllAddOne");
        buttonRemoveAllAddOne.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                cont.removeAllItems();
                TableItem ti = new TableItem();
                ti.setName("Item_" + 20);
                cont.addBean(ti);
            }
        });

        // This should be the last test as it changes the table datasource
        Button buttonReplaceByNewDatasource = new Button(
                "Remove all items and add new datasource");
        buttonReplaceByNewDatasource.setId("buttonReplaceByNewDatasource");
        buttonReplaceByNewDatasource.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                cont.removeAllItems();
                BeanItemContainer<TableItem> newContainer = new BeanItemContainer<TableItem>(
                        TableItem.class);
                for (int i = 0; i < 50; i++) {
                    TableItem ti = new TableItem();
                    ti.setName("Item_" + i);
                    newContainer.addBean(ti);
                }
                table.setContainerDataSource(newContainer);
            }
        });

        for (int i = 0; i < 80; i++) {
            TableItem ti = new TableItem();
            ti.setName("Item_" + i);
            restoringItemList.add(ti);
            cont.addBean(ti);
        }

        getLayout().addComponent(buttonReAddAllViaAddAll);
        getLayout().addComponent(buttonReplaceByAnotherCollectionViaAddAll);
        getLayout().addComponent(buttonReplaceByAnotherCollectionViaAdd);
        getLayout().addComponent(buttonReplaceBySubsetOfSmallerSize);
        getLayout().addComponent(buttonReplaceByWholeSubsetPlusOneNew);
        getLayout().addComponent(buttonRemoveAllAddOne);
        getLayout().addComponent(buttonReplaceByNewDatasource);
        getLayout().addComponent(buttonRestore);
        getLayout().addComponent(table);
    }

    public class TableItem implements Serializable {
        private static final long serialVersionUID = -745849615488792221L;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 14581;
    }

    @Override
    protected String getTestDescription() {
        return "The scroll position should not be changed if removing and re-adding all rows in Table.";
    }
}
