package com.vaadin.tests.components.draganddropwrapper;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.declarative.DesignContext;

public class DragAndDropWrapperDeclarativeTest
        extends DeclarativeTestBase<DragAndDropWrapper> {

    @Test
    public void testDefaultDnDWrapper() {
        Button okButton = new Button("OK");
        String input = "<vaadin-drag-and-drop-wrapper>"
                + new DesignContext().createElement(okButton)
                + "</vaadin-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }

    @Test
    public void testNoDragImage() {
        Button okButton = new Button("OK");
        String input = "<vaadin-drag-and-drop-wrapper drag-start-mode='wrapper'>"
                + new DesignContext().createElement(okButton)
                + "</vaadin-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        wrapper.setDragStartMode(DragStartMode.WRAPPER);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }

    @Test
    public void testWithDragImage() {
        Button dragImage = new Button("Cancel");
        Button okButton = new Button("OK");
        String input = "<vaadin-drag-and-drop-wrapper drag-start-mode='component_other'>"
                + new DesignContext().createElement(okButton)
                + new DesignContext().createElement(dragImage)
                        .attr(":drag-image", true)
                + "</vaadin-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        wrapper.setDragStartMode(DragStartMode.COMPONENT_OTHER);
        wrapper.setDragImageComponent(dragImage);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }
}
