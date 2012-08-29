/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.image.ImageServerRpc;
import com.vaadin.shared.ui.image.ImageState;

/**
 * Component for embedding images.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
@SuppressWarnings("serial")
public class Image extends AbstractEmbedded {

    protected ImageServerRpc rpc = new ImageServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Image.this, mouseDetails));
        }
    };

    /**
     * Creates a new empty Image.
     */
    public Image() {
        registerRpc(rpc);
    }

    /**
     * Creates a new empty Image with caption.
     * 
     * @param caption
     */
    public Image(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new Image whose contents is loaded from given resource. The
     * dimensions are assumed if possible. The type is guessed from resource.
     * 
     * @param caption
     * @param source
     *            the Source of the embedded object.
     */
    public Image(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    @Override
    public ImageState getState() {
        return (ImageState) super.getState();
    }

    /**
     * Add a click listener to the component. The listener is called whenever
     * the user clicks inside the component. Depending on the content the event
     * may be blocked and in that case no event is fired.
     * 
     * Use {@link #removeListener(ClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(ClickListener listener) {
        addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
                ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the component. The listener should earlier
     * have been added using {@link #addListener(ClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }
}
