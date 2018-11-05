package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.TextArea;

public class DragAndDropWrapperInPanel extends TestBase {

    @Override
    protected void setup() {

        addComponent(new Button("Click to resize", event -> {
            for (int i = 1; i < getLayout().getComponentCount(); ++i) {
                Component c = getLayout().getComponent(i);
                c.setWidth("400px");
                c.setHeight("200px");
            }
        }));

        Component content;

        content = new Button("Undefined-sized Button");
        content.setSizeUndefined();
        addDnDPanel(content);

        content = new Label("Full-sized Label");
        content.setSizeFull();
        addDnDPanel(content);

        content = new TextArea(null, "200x100px TextArea");
        content.setWidth("200px");
        content.setHeight("100px");
        addDnDPanel(content);
    }

    @Override
    protected String getDescription() {
        return "A full-sized DragAndDropWrapper causes scrollbars inside Panel";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6880;
    }

    private void addDnDPanel(Component content) {
        Panel panel = new Panel();
        panel.setSizeUndefined();
        panel.setWidth("300px");
        panel.setHeight("150px");
        DragAndDropWrapper dndWrapper = new DragAndDropWrapper(content);
        dndWrapper.setSizeFull();
        panel.setContent(dndWrapper);
        addComponent(panel);
    }
}
