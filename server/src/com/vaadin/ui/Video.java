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

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.video.VideoConstants;
import com.vaadin.shared.ui.video.VideoState;

/**
 * The Video component translates into an HTML5 &lt;video&gt; element and as
 * such is only supported in browsers that support HTML5 media markup. Browsers
 * that do not support HTML5 display the text or HTML set by calling
 * {@link #setAltText(String)}.
 * 
 * A flash-player fallback can be implemented by setting HTML content allowed (
 * {@link #setHtmlContentAllowed(boolean)} and calling
 * {@link #setAltText(String)} with the flash player markup. An example of flash
 * fallback can be found at the <a href=
 * "https://developer.mozilla.org/En/Using_audio_and_video_in_Firefox#Using_Flash"
 * >Mozilla Developer Network</a>.
 * 
 * Multiple sources can be specified. Which of the sources is used is selected
 * by the browser depending on which file formats it supports. See <a
 * href="http://en.wikipedia.org/wiki/HTML5_video#Table">wikipedia</a> for a
 * table of formats supported by different browsers.
 * 
 * @author Vaadin Ltd
 * @since 6.7.0
 */
public class Video extends AbstractMedia {

    @Override
    protected VideoState getState() {
        return (VideoState) super.getState();
    }

    public Video() {
        this("", null);
    }

    /**
     * @param caption
     *            The caption for this video.
     */
    public Video(String caption) {
        this(caption, null);
    }

    /**
     * @param caption
     *            The caption for this video.
     * @param source
     *            The Resource containing the video to play.
     */
    public Video(String caption, Resource source) {
        setCaption(caption);
        setSource(source);
        setShowControls(true);
    }

    /**
     * Sets the poster image, which is shown in place of the video before the
     * user presses play.
     * 
     * @param poster
     */
    public void setPoster(Resource poster) {
        setResource(VideoConstants.POSTER_RESOURCE, poster);
    }

    /**
     * @return The poster image.
     */
    public Resource getPoster() {
        return getResource(VideoConstants.POSTER_RESOURCE);
    }

}
