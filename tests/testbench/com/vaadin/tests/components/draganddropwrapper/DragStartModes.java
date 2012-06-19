package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

public class DragStartModes extends TestBase {

    @Override
    protected void setup() {

        TestUtils.injectCSS(getMainWindow(),
                ".v-ddwrapper { background: #ACF; }");

        addComponent(makeWrapper(DragStartMode.NONE));
        addComponent(makeWrapper(DragStartMode.COMPONENT));
        addComponent(makeWrapper(DragStartMode.WRAPPER));
        addComponent(makeWrapper(DragStartMode.HTML5));

        addComponent(new Label("Drop here"));
    }

    private Component makeWrapper(DragStartMode mode) {
        Label label = new Label("Drag start mode: " + mode);
        label.setDebugId("label" + mode);
        DragAndDropWrapper wrapper = new DragAndDropWrapper(label);
        wrapper.setHTML5DataFlavor("Text", "HTML5!");
        wrapper.setDragStartMode(mode);
        wrapper.setWidth("200px");
        return wrapper;
    }

    @Override
    protected String getDescription() {
        return "Different drag start modes should show correct drag images";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8949;
    }

}
