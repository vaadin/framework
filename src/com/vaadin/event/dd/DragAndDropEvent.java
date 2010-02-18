package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.event.Transferable;

public class DragAndDropEvent implements Serializable {
    private static final long serialVersionUID = -2232591107911385564L;
    private Transferable transferable;
    private DropTargetDetails dropDetails;

    public DragAndDropEvent(Transferable tr, DropTargetDetails details) {
        transferable = tr;
        dropDetails = details;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public DropTargetDetails getDropTargetData() {
        return dropDetails;
    }
}
