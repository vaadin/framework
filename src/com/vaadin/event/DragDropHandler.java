package com.vaadin.event;

public interface DragDropHandler extends DropHandler {

    public void handleDragRequest(DragRequest event, Transferable transferable,
            DragDropDetails dragDropDetails);

}
