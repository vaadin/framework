/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;


public interface VAcceptCallback {

    /**
     * This method is called by {@link VDragAndDropManager} if the
     * {@link VDragEvent} is still active. Developer can update for example drag
     * icon or empahsis the target if the target accepts the transferable. If
     * the drag and drop operation ends or the {@link VAbstractDropHandler} has
     * changed before response arrives, the method is never called.
     */
    public void accepted(VDragEvent event);

}
