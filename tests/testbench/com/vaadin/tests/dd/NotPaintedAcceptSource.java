package com.vaadin.tests.dd;

import com.vaadin.data.Item;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Table.TableTransferable;

public class NotPaintedAcceptSource extends TestBase {

    @Override
    protected void setup() {
        final Table source1 = createTable("Source 1");
        final Table source2 = createTable("Source 2");
        final Table target = createTable("Target");

        source1.setDragMode(TableDragMode.ROW);
        source2.setDragMode(TableDragMode.ROW);

        target.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new SourceIs(source1, source2);
            }

            @Override
            public void drop(DragAndDropEvent event) {
                TableTransferable transferable = (TableTransferable) event
                        .getTransferable();
                Item item = transferable.getSourceComponent().getItem(
                        transferable.getItemId());
                Object value = item.getItemProperty("value").getValue();
                AbstractSelectTargetDetails targetDetails = (AbstractSelectTargetDetails) event
                        .getTargetDetails();
                Object targetItemId = targetDetails.getItemIdOver();
                Object addItemAfter = target.addItemAfter(targetItemId);
                target.getItem(addItemAfter).getItemProperty("value")
                        .setValue(value);
                transferable.getSourceComponent().removeItem(
                        transferable.getItemId());
            }
        });

        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(source1);
        horizontalLayout.addComponent(target);

        addComponent(horizontalLayout);

        addComponent(new Button("Swap sources", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (source1.getParent() != null) {
                    horizontalLayout.replaceComponent(source1, source2);
                } else {
                    horizontalLayout.replaceComponent(source2, source1);
                }
                target.markAsDirty();
            }
        }));

    }

    private Table createTable(String caption) {
        Table table = new Table(caption);
        table.addContainerProperty("value", String.class, "");
        for (int i = 0; i < 10; i++) {
            table.addItem(new Object[] { caption + " value " + i },
                    Integer.valueOf(i));
        }
        table.setWidth("300px");
        return table;
    }

    @Override
    protected String getDescription() {
        return "Including a component in an accept criterion when the actual component is not included in a layout and thus not painted should still allow painting the component properly when it is added to a layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8730);
    }

}
