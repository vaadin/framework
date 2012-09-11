package com.vaadin.ui;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.browserframe.BrowserFrameState;

/**
 * A component displaying an embedded web page. Implemented as a HTML
 * <code>iframe</code> element.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
public class BrowserFrame extends AbstractEmbedded {

    /**
     * Creates a new empty browser frame.
     */
    public BrowserFrame() {

    }

    /**
     * Creates a new empty browser frame with the given caption.
     * 
     * @param caption
     *            The caption for the component
     */
    public BrowserFrame(String caption) {
        setCaption(caption);
    }

    /**
     * Creates a new browser frame with the given caption and content.
     * 
     * @param caption
     *            The caption for the component.
     * @param source
     *            A Resource representing the Web page that should be displayed.
     */
    public BrowserFrame(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    @Override
    protected BrowserFrameState getState() {
        return (BrowserFrameState) super.getState();
    }
}
