package com.vaadin.tests.event.dnd;

import java.util.Optional;

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;
import com.vaadin.ui.themes.ValoTheme;

public class DragAndDropBookExamples {

    public void dragSourceExtensionSamples() {
        Label draggableLabel = new Label("You can grab and drag me");
        DragSourceExtension<Label> dragSource = new DragSourceExtension<>(
                draggableLabel);

        // set the allowed effect
        dragSource.setEffectAllowed(EffectAllowed.MOVE);
        // set the data to transfer
        dragSource.setDataTransferText("hello receiver");
        dragSource.addDragStartListener(
                event -> event.getComponent().addStyleName("dragged"));
        dragSource.addDragEndListener(
                event -> event.getComponent().removeStyleName("dragged"));
    }

    public void dropTargetExtensionSamples() {
        VerticalLayout dropTargetLayout = new VerticalLayout();
        dropTargetLayout.setCaption("Drop things inside me");
        dropTargetLayout.addStyleName(ValoTheme.LAYOUT_CARD);

        // make the layout accept drops
        DropTargetExtension<VerticalLayout> dropTarget = new DropTargetExtension<>(
                dropTargetLayout);

        // set the effect that is allowed, must match what is in the drag source
        dropTarget.setDropEffect(DropEffect.MOVE);

        // catch the drops
        dropTarget.addDropListener(event -> {
            // if the drag source is in the same UI as the target
            Optional<AbstractComponent> dragSource = event
                    .getDragSourceComponent();
            if (dragSource.isPresent() && dragSource.get() instanceof Label) {
                // move the label to the layout
                dropTargetLayout.addComponent(dragSource.get());

                // get possible transfer data
                String message = event.getDataTransferText();
                Notification.show("DropEvent with data transfer: " + message);
            }
        });
    }
}
