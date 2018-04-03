package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextArea;

/**
 * Test UI for text area: drag image should contain text-area text.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class DragAndDropTextArea extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout dndLayout = new VerticalLayout();

        TextArea area = new TextArea();
        area.setValue("text");

        dndLayout.addComponent(area);
        DragAndDropWrapper wrapper = new DragAndDropWrapper(dndLayout);
        wrapper.setDragStartMode(DragStartMode.WRAPPER);
        addComponent(wrapper);
    }

    @Override
    protected String getTestDescription() {
        return "Drag image for textarea should contain text-area text";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13557;
    }

}
