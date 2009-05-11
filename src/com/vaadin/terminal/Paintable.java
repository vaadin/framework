/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;
import java.util.EventObject;

/**
 * Interface implemented by all classes that can be painted. Classes
 * implementing this interface know how to output themselves to a UIDL stream
 * and that way describing to the terminal how it should be displayed in the UI.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Paintable extends java.util.EventListener, Serializable {

    /**
     * <p>
     * Paints the Paintable into a UIDL stream. This method creates the UIDL
     * sequence describing it and outputs it to the given UIDL stream.
     * </p>
     * 
     * <p>
     * It is called when the contents of the component should be painted in
     * response to the component first being shown or having been altered so
     * that its visual representation is changed.
     * </p>
     * 
     * @param target
     *            the target UIDL stream where the component should paint itself
     *            to.
     * @throws PaintException
     *             if the paint operation failed.
     */
    public void paint(PaintTarget target) throws PaintException;

    /**
     * Requests that the paintable should be repainted as soon as possible.
     */
    public void requestRepaint();

    /**
     * Adds an unique id for component that get's transferred to terminal for
     * testing purposes. Keeping identifiers unique throughout the Application
     * instance is on programmers responsibility.
     * 
     * @param id
     *            A short (< 20 chars) alphanumeric id
     */
    public void setDebugId(String id);

    /**
     * Get's currently set debug identifier
     * 
     * @return current debug id, null if not set
     */
    public String getDebugId();

    /**
     * Repaint request event is thrown when the paintable needs to be repainted.
     * This is typically done when the <code>paint</code> method would return
     * dissimilar UIDL from the previous call of the method.
     */
    @SuppressWarnings("serial")
    public class RepaintRequestEvent extends EventObject {

        /**
         * Constructs a new event.
         * 
         * @param source
         *            the paintable needing repaint.
         */
        public RepaintRequestEvent(Paintable source) {
            super(source);
        }

        /**
         * Gets the paintable needing repainting.
         * 
         * @return Paintable for which the <code>paint</code> method will return
         *         dissimilar UIDL from the previous call of the method.
         */
        public Paintable getPaintable() {
            return (Paintable) getSource();
        }
    }

    /**
     * Listens repaint requests. The <code>repaintRequested</code> method is
     * called when the paintable needs to be repainted. This is typically done
     * when the <code>paint</code> method would return dissimilar UIDL from the
     * previous call of the method.
     */
    public interface RepaintRequestListener extends Serializable {

        /**
         * Receives repaint request events.
         * 
         * @param event
         *            the repaint request event specifying the paintable source.
         */
        public void repaintRequested(RepaintRequestEvent event);
    }

    /**
     * Adds repaint request listener. In order to assure that no repaint
     * requests are missed, the new repaint listener should paint the paintable
     * right after adding itself as listener.
     * 
     * @param listener
     *            the listener to be added.
     */
    public void addListener(RepaintRequestListener listener);

    /**
     * Removes repaint request listener.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeListener(RepaintRequestListener listener);

    /**
     * Request sending of repaint events on any further visible changes.
     * Normally the paintable only send up to one repaint request for listeners
     * after paint as the paintable as the paintable assumes that the listeners
     * already know about the repaint need. This method resets the assumtion.
     * Paint implicitly does the assumtion reset functionality implemented by
     * this method.
     * <p>
     * This method is normally used only by the terminals to note paintables
     * about implicit repaints (painting the component without actually invoking
     * paint method).
     * </p>
     */
    public void requestRepaintRequests();
}
