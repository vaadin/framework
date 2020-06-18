package com.vaadin.tests.dnd;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

public class CriterionSerialization extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button();
        button.setCaption("drag me");
        button.setId("drag");
        DragSourceExtension<Button> dragSource = new DragSourceExtension<>(
                button);
        dragSource.setPayload("test", "value");
        dragSource.addDragStartListener(e -> {
            log("drag started");
        });
        dragSource.addDragEndListener(e -> {
            log("drag ended");
        });

        Button dropArea1 = new Button();
        dropArea1.setCaption("drop here works");
        dropArea1.setId("dropWorks");
        DropTargetExtension<Button> dropTarget = new DropTargetExtension<>(
                dropArea1);
        dropTarget.addDropListener(e -> {
            log("dropArea1 drop listener invoked (expected to happen)");
        });
        dropTarget.setDropCriterion("test", "value");

        Button dropArea2 = new Button();
        dropArea2.setCaption("drop here fails");
        dropArea2.setId("dropFails");
        DropTargetExtension<Button> dropTarget2 = new DropTargetExtension<>(
                dropArea2);
        dropTarget2.addDropListener(e -> {
            log("dropArea2 drop listener invoked (should not happen)");
        });
        dropTarget2.setDropCriterion("test", "value2");

        getLayout().addComponents(button, dropArea1, dropArea2);
    }

    @Override
    protected String getTestDescription() {
        return "Dropping the draggable button on the button without matching "
                + "Criterion should not trigger drop listener.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11909;
    }
}
