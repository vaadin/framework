/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.server.ConnectorResource;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.AbstractMediaState;
import com.vaadin.shared.ui.MediaControl;

/**
 * Abstract base class for the HTML5 media components.
 * 
 * @author Vaadin Ltd
 */
public abstract class AbstractMedia extends AbstractComponent {

    @Override
    protected AbstractMediaState getState() {
        return (AbstractMediaState) super.getState();
    }

    @Override
    protected AbstractMediaState getState(boolean markAsDirty) {
        return (AbstractMediaState) super.getState(markAsDirty);
    }

    /**
     * Sets a single media file as the source of the media component.
     * 
     * @param source
     */
    public void setSource(Resource source) {
        clearSources();

        addSource(source);
    }

    private void clearSources() {
        getState().sources.clear();
        getState().sourceTypes.clear();
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
            List<URLReference> sources = getState().sources;
            sources.add(new ResourceReference(source, this, Integer
                    .toString(sources.size())));
            getState().sourceTypes.add(source.getMIMEType());
        }
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        Matcher matcher = Pattern.compile("(\\d+)(/.*)?").matcher(path);
        if (!matcher.matches()) {
            return super.handleConnectorRequest(request, response, path);
        }

        DownloadStream stream;

        VaadinSession session = getSession();
        session.lock();
        try {
            List<URLReference> sources = getState().sources;

            int sourceIndex = Integer.parseInt(matcher.group(1));

            if (sourceIndex < 0 || sourceIndex >= sources.size()) {
                getLogger().log(Level.WARNING,
                        "Requested source index {0} is out of bounds",
                        sourceIndex);
                return false;
            }

            URLReference reference = sources.get(sourceIndex);
            ConnectorResource resource = (ConnectorResource) ResourceReference
                    .getResource(reference);
            stream = resource.getStream();
        } finally {
            session.unlock();
        }

        stream.writeResponse(request, response);
        return true;
    }

    private Logger getLogger() {
        return Logger.getLogger(AbstractMedia.class.getName());
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
        clearSources();
        for (Resource source : sources) {
            addSource(source);
        }
    }

    /**
     * @return The sources pointed to in this media.
     */
    public List<Resource> getSources() {
        ArrayList<Resource> sources = new ArrayList<Resource>();
        for (URLReference ref : getState(false).sources) {
            sources.add(((ResourceReference) ref).getResource());
        }
        return sources;
    }

    /**
     * Sets whether or not the browser should show native media controls.
     * 
     * @param showControls
     */
    public void setShowControls(boolean showControls) {
        getState().showControls = showControls;
    }

    /**
     * @return true if the browser is to show native media controls.
     */
    public boolean isShowControls() {
        return getState(false).showControls;
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
     * @param altText
     */
    public void setAltText(String altText) {
        getState().altText = altText;
    }

    /**
     * @return The text/html that is displayed when a browser doesn't support
     *         HTML5.
     */
    public String getAltText() {
        return getState(false).altText;
    }

    /**
     * Set whether the alternative text ({@link #setAltText(String)}) is
     * rendered as HTML or not.
     * 
     * @param htmlContentAllowed
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * @return true if the alternative text ({@link #setAltText(String)}) is to
     *         be rendered as HTML.
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }

    /**
     * Sets whether the media is to automatically start playback when enough
     * data has been loaded.
     * 
     * @param autoplay
     */
    public void setAutoplay(boolean autoplay) {
        getState().autoplay = autoplay;
    }

    /**
     * @return true if the media is set to automatically start playback.
     */
    public boolean isAutoplay() {
        return getState(false).autoplay;
    }

    /**
     * Set whether to mute the audio or not.
     * 
     * @param muted
     */
    public void setMuted(boolean muted) {
        getState().muted = muted;
    }

    /**
     * @return true if the audio is muted.
     */
    public boolean isMuted() {
        return getState(false).muted;
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

}
