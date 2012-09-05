package com.vaadin.tests.dd;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class DDTest4 extends TestBase {

    java.util.Random r = new java.util.Random(1);

    HorizontalLayout hl = new HorizontalLayout();
    Table table = new Table("Drag and drop sortable table");

    @Override
    protected void setup() {
        UI w = getLayout().getUI();

        TestUtils
                .injectCSS(
                        w,
                        ".v-table-row-drag-middle .v-table-cell-content {"
                                + "        background-color: inherit ; border-bottom: 1px solid cyan;"
                                + "}"
                                + ".v-table-row-drag-middle .v-table-cell-wrapper {"
                                + "        margin-bottom: -1px;" + "}" + ""

                );

        // hl.addComponent(tree1);
        hl.addComponent(table);
        // hl.addComponent(tree2);
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.setExpandRatio(table, 1);
        table.setWidth("100%");
        table.setPageLength(10);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        table.setSelectable(true);
        table.setMultiSelect(true);
        populateTable();
        addComponent(hl);

        /*
         * Make table rows draggable
         */
        table.setDragMode(Table.TableDragMode.ROW);

        table.setDropHandler(new DropHandler() {
            // accept only drags from this table
            AcceptCriterion crit = new SourceIs(table);

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return crit;
            }

            @Override
            public void drop(DragAndDropEvent dropEvent) {
                AbstractSelectTargetDetails dropTargetData = (AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails();
                DataBoundTransferable transferable = (DataBoundTransferable) dropEvent
                        .getTransferable();
                Object itemIdOver = dropTargetData.getItemIdOver();
                Object itemId = transferable.getItemId();
                if (itemId == null || itemIdOver == null
                        || itemId.equals(itemIdOver)) {
                    return; // no move happened
                }

                // IndexedContainer goodies... (hint: don't use it in real apps)
                IndexedContainer containerDataSource = (IndexedContainer) table
                        .getContainerDataSource();
                int newIndex = containerDataSource.indexOfId(itemIdOver) - 1;
                if (dropTargetData.getDropLocation() != VerticalDropLocation.TOP) {
                    newIndex++;
                }
                if (newIndex < 0) {
                    newIndex = 0;
                }
                Object idAfter = containerDataSource.getIdByIndex(newIndex);
                Collection<?> selections = (Collection<?>) table.getValue();
                if (selections != null && selections.contains(itemId)) {
                    // dragged a selected item, if multiple rows selected, drag
                    // them too (functionality similar to apple mail)
                    for (Object object : selections) {
                        moveAfter(containerDataSource, object, idAfter);
                    }

                } else {
                    // move just the dragged row, not considering selection at
                    // all
                    moveAfter(containerDataSource, itemId, idAfter);
                }

            }

            private void moveAfter(IndexedContainer containerDataSource,
                    Object itemId, Object idAfter) {
                try {
                    IndexedContainer clone = null;
                    clone = (IndexedContainer) containerDataSource.clone();
                    containerDataSource.removeItem(itemId);
                    Item newItem = containerDataSource.addItemAfter(idAfter,
                            itemId);
                    Item item = clone.getItem(itemId);
                    for (Object propId : item.getItemPropertyIds()) {
                        newItem.getItemProperty(propId).setValue(
                                item.getItemProperty(propId).getValue());
                    }

                    // TODO Auto-generated method stub
                } catch (CloneNotSupportedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

    }

    private void populateTable() {
        table.addContainerProperty("Name", String.class, "");
        table.addContainerProperty("Weight", Integer.class, 0);

        PersonContainer testData = PersonContainer.createWithTestData();

        for (int i = 0; i < 10; i++) {
            Item addItem = table.addItem("Item" + i);
            Person p = testData.getIdByIndex(i);
            addItem.getItemProperty("Name").setValue(
                    p.getFirstName() + " " + p.getLastName());
            addItem.getItemProperty("Weight").setValue(50 + r.nextInt(60));
        }

    }

    @Override
    protected String getDescription() {
        return "dd";
    }

    @Override
    protected Integer getTicketNumber() {
        return 119;
    }

}
