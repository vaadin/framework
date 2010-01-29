package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;

/**
 * Vaadin Widgets (TODO or Paintables, see {@link HasDropHandler}) that want to
 * receive something via drag and drop implement this interface.
 */
public interface DropHandler {

    /**
     * Called by D'D' manager when drag gets over this drop handler.
     * 
     * @param drag
     */
    public void dragEnter(DragEvent drag);

    /**
     * Called by D'D' manager when drag gets out this drop handler.
     * 
     * @param drag
     */
    public void dragLeave(DragEvent drag);

    /**
     * The actual drop happened on this drop handler.
     * 
     * @param drag
     * @return true if Tranferrable of this drag event needs to be sent to
     *         server, false if drop was finally canceled or no server visit is
     *         needed
     */
    public boolean drop(DragEvent drag);

    /**
     * When drag is over current drag handler.
     * 
     * With drag implementation by {@link DragAndDropManager} will be called
     * when mouse is moved. HTML5 implementations call this continuously even
     * though mouse is not moved.
     * 
     * @param currentDrag
     */
    public void dragOver(DragEvent currentDrag);

    /**
     * Returns the Paintable into which this DragHandler is assosiated
     */
    public Paintable getPaintable();

    /**
     * Returns the application connection to which this {@link DropHandler}
     * belongs to. DragAndDropManager uses this fucction to send Transferable to
     * server side.
     */
    public ApplicationConnection getApplicationConnection();

}
