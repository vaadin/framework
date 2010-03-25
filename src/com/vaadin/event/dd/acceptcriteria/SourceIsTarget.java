/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.event.dd.acceptcriteria;

import com.vaadin.event.Transferable;
import com.vaadin.event.TransferableImpl;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.terminal.gwt.client.ui.dd.VSourceIsTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

/**
 * 
 * A criterion that ensures the drag source is the same as drop target. Eg.
 * {@link Tree} or {@link Table} could support only re-ordering of items, but no
 * {@link Transferable}s coming outside.
 * <p>
 * Note! Class is singleton, use {@link #get()} method to get the instance.
 * 
 * @since 6.3
 * 
 */
@ClientCriterion(VSourceIsTarget.class)
public class SourceIsTarget extends ClientSideCriterion {

    private static final long serialVersionUID = -451399314705532584L;
    private static SourceIsTarget instance = new SourceIsTarget();

    private SourceIsTarget() {
    }

    public boolean accept(DragAndDropEvent dragEvent) {
        if (dragEvent.getTransferable() instanceof TransferableImpl) {
            Component sourceComponent = ((TransferableImpl) dragEvent
                    .getTransferable()).getSourceComponent();
            DropTarget target = dragEvent.getTargetDetails().getTarget();
            return sourceComponent == target;
        }
        return false;
    }

    public static synchronized SourceIsTarget get() {
        return instance;
    }

}