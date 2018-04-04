package com.vaadin.tests.components.table;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

/**
 * Test UI for empty table: empty table (without any data) throws client side
 * exception if it's a target for DnD.
 *
 * @author Vaadin Ltd
 */
public class DndEmptyTable extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label source = new Label("label");
        DragAndDropWrapper wrapper = new DragAndDropWrapper(source);
        wrapper.setDragStartMode(DragStartMode.WRAPPER);
        addComponent(wrapper);

        Table target = new Table();
        target.setWidth(100, Unit.PERCENTAGE);
        addComponent(target);
        target.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Drag and drop into empty table should not throws client side exception.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13655;
    }

}
