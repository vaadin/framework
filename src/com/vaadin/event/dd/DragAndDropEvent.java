/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event.dd;

import java.io.Serializable;

import com.vaadin.event.Transferable;

/**
 * TODO Javadoc
 * 
 * @since 6.3
 * 
 */
public class DragAndDropEvent implements Serializable {
    private static final long serialVersionUID = -2232591107911385564L;
    private Transferable transferable;
    private DropTargetDetails dropTargetDetails;

    public DragAndDropEvent(Transferable transferable,
            DropTargetDetails dropTargetDetails) {
        this.transferable = transferable;
        this.dropTargetDetails = dropTargetDetails;
    }

    public Transferable getTransferable() {
        return transferable;
    }

    public DropTargetDetails getDropTargetDetails() {
        return dropTargetDetails;
    }

}
