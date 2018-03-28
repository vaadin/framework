package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

/**
 * Test UI for DnD image element size
 *
 * @author Vaadin Ltd
 */
public class DragAndDropRelativeWidth extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout layout = new CssLayout();
        layout.setWidth(300, Unit.PIXELS);

        Label label = new Label("drag source");
        label.addStyleName("drag-source");
        label.setWidth(100, Unit.PERCENTAGE);
        DragAndDropWrapper wrapper = new DragAndDropWrapper(label);
        wrapper.setWidth(100, Unit.PERCENTAGE);
        wrapper.setDragStartMode(DragStartMode.COMPONENT);

        layout.addComponent(wrapper);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Set explicit size for drag image element using calclulated size from the source";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14617;
    }

}
