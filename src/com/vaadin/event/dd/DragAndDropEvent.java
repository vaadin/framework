package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.event.Transferable;

public abstract class DragAndDropEvent implements Serializable {
    private static final long serialVersionUID = -2232591107911385564L;
    private Transferable transferable;
    private TargetDetails dropDetails;

    public DragAndDropEvent(Transferable tr, TargetDetails details) {
        transferable = tr;
        dropDetails = details;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public TargetDetails getDropTargetData() {
        return dropDetails;
    }
}
