/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.ui.MediaBaseConnector;
import com.vaadin.terminal.gwt.client.ui.MediaBaseConnector.MediaControl;

/**
 * Abstract base class for the HTML5 media components.
 * 
 * @author Vaadin Ltd
 */
public class AbstractMedia extends AbstractComponent {

    private List<Resource> sources = new ArrayList<Resource>();

    private boolean showControls;

    private String altText;

    private boolean htmlContentAllowed;

    private boolean autoplay;

    private boolean muted;

    /**
     * Sets a single media file as the source of the media component.
     * 
     * @param source
     */
    public void setSource(Resource source) {
        sources.clear();
        addSource(source);
    }

    /**
     * Adds an alternative media file to the sources list. Which of the sources
     * is used is selected by the browser depending on which file formats it
     * supports. See <a
     * href="http://en.wikipedia.org/wiki/HTML5_video#Table">wikipedia</a> for a
     * table of formats supported by different browsers.
     * 
     * @param source
     */
    public void addSource(Resource source) {
        if (source != null) {
            sources.add(source);
            requestRepaint();
        }
    }

    /**
     * Set multiple sources at once. Which of the sources is used is selected by
     * the browser depending on which file formats it supports. See <a
     * href="http://en.wikipedia.org/wiki/HTML5_video#Table">wikipedia</a> for a
     * table of formats supported by different browsers.
     * 
     * @param sources
     */
    public void setSources(Resource... sources) {
        this.sources.addAll(Arrays.asList(sources));
        requestRepaint();
    }

    /**
     * @return The sources pointed to in this media.
     */
    public List<Resource> getSources() {
        return Collections.unmodifiableList(sources);
    }

    /**
     * Sets whether or not the browser should show native media controls.
     * 
     * @param showControls
     */
    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
        requestRepaint();
    }

    /**
     * @return true if the browser is to show native media controls.
     */
    public boolean isShowControls() {
        return showControls;
    }

    /**
     * Sets the alternative text to be displayed if the browser does not support
     * HTML5. This text is rendered as HTML if
     * {@link #setHtmlContentAllowed(boolean)} is set to true. With HTML
     * rendering, this method can also be used to implement fallback to a
     * flash-based player, see the <a href=
     * "https://developer.mozilla.org/En/Using_audio_and_video_in_Firefox#Using_Flash"
     * >Mozilla Developer Network</a> for details.
     * 
     * @param text
     */
    public void setAltText(String text) {
        altText = text;
        requestRepaint();
    }

    /**
     * @return The text/html that is displayed when a browser doesn't support
     *         HTML5.
     */
    public String getAltText() {
        return altText;
    }

    /**
     * Set whether the alternative text ({@link #setAltText(String)}) is
     * rendered as HTML or not.
     * 
     * @param htmlContentAllowed
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
        requestRepaint();
    }

    /**
     * @return true if the alternative text ({@link #setAltText(String)}) is to
     *         be rendered as HTML.
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    /**
     * Sets whether the media is to automatically start playback when enough
     * data has been loaded.
     * 
     * @param autoplay
     */
    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
        requestRepaint();
    }

    /**
     * @return true if the media is set to automatically start playback.
     */
    public boolean isAutoplay() {
        return autoplay;
    }

    /**
     * Set whether to mute the audio or not.
     * 
     * @param muted
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
        requestRepaint();
    }

    /**
     * @return true if the audio is muted.
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Pauses the media.
     */
    public void pause() {
        getRpcProxy(MediaControl.class).pause();
    }

    /**
     * Starts playback of the media.
     */
    public void play() {
        getRpcProxy(MediaControl.class).play();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute(MediaBaseConnector.ATTR_CONTROLS, isShowControls());
        if (getAltText() != null) {
            target.addAttribute(MediaBaseConnector.ATTR_ALT_TEXT, getAltText());
        }
        target.addAttribute(MediaBaseConnector.ATTR_HTML,
                isHtmlContentAllowed());
        target.addAttribute(MediaBaseConnector.ATTR_AUTOPLAY, isAutoplay());
        for (Resource r : getSources()) {
            target.startTag(MediaBaseConnector.TAG_SOURCE);
            target.addAttribute(MediaBaseConnector.ATTR_RESOURCE, r);
            target.addAttribute(MediaBaseConnector.ATTR_RESOURCE_TYPE,
                    r.getMIMEType());
            target.endTag(MediaBaseConnector.TAG_SOURCE);
        }
        target.addAttribute(MediaBaseConnector.ATTR_MUTED, isMuted());
    }
}
