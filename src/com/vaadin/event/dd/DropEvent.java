package com.vaadin.event.dd;

import com.vaadin.event.Transferable;

public class DropEvent extends DragAndDropEvent {

    public DropEvent(Transferable tr, TargetDetails details) {
        super(tr, details);
    }
}
