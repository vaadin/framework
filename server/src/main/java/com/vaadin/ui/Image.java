/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.image.ImageServerRpc;
import com.vaadin.shared.ui.image.ImageState;

/**
 * Component for embedding images.
 *
 * @author Vaadin Ltd.
 * @version @VERSION@
 * @since 7.0
 */
@SuppressWarnings("serial")
public class Image extends AbstractEmbedded {

    protected ImageServerRpc rpc = (MouseEventDetails mouseDetails) -> {
        fireEvent(new ClickEvent(Image.this, mouseDetails));
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
    protected ImageState getState() {
        return (ImageState) super.getState();
    }

    @Override
    protected ImageState getState(boolean markAsDirty) {
        return (ImageState) super.getState(markAsDirty);
    }

    /**
     * Add a click listener to the component. The listener is called whenever
     * the user clicks inside the component. Depending on the content the event
     * may be blocked and in that case no event is fired.
     *
     * @see Registration
     *
     * @param listener
     *            The listener to add, not null
     * @return a registration object for removing the listener
     * @since 8.0
     */
    public Registration addClickListener(ClickListener listener) {
        return addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener, ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the component. The listener should earlier
     * have been added using {@link #addClickListener(ClickListener)}.
     *
     * @param listener
     *            The listener to remove
     *
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addClickListener(ClickListener)}.
     */
    @Deprecated
    public void removeClickListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }
}
