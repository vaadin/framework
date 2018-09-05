package com.vaadin.tests.dnd;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.criteria.Payload;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DragAndDropPayload extends AbstractTestUIWithLog {

    private final Label stringLabel = new Label(
            "Drag source with string payload");
    private final Label integerLabel = new Label(
            "Drag source with integer payload");
    private final Label doubleLabel = new Label(
            "Drag source with double payload");

    private final Label targetLabel = new Label("Drop target");

    @Override
    protected void setup(VaadinRequest request) {
        DragSourceExtension<Label> stringDragSource = new DragSourceExtension<>(
                stringLabel);
        stringDragSource.setPayload("payload_key", "string_value");

        DragSourceExtension<Label> integerDragSource = new DragSourceExtension<>(
                integerLabel);
        integerDragSource.setPayload("payload_key", 42);

        DragSourceExtension<Label> doubleDragSource = new DragSourceExtension<>(
                doubleLabel);
        doubleDragSource.setPayload("payload_key", 3.14);

        DropTargetExtension<Label> dropTarget = new DropTargetExtension<>(
                targetLabel);
        dropTarget.addDropListener(event -> event.getDataTransferData()
                .entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(Payload.ITEM_PREFIX))
                .forEach(entry -> log
                        .log(entry.getKey() + " -> " + entry.getValue())));

        VerticalLayout dragSources = new VerticalLayout(stringLabel,
                integerLabel, doubleLabel);
        VerticalLayout dropTargets = new VerticalLayout(targetLabel);
        getLayout()
                .addComponent(new HorizontalLayout(dragSources, dropTargets));
    }
}
