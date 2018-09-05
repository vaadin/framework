/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.video.VideoConstants;
import com.vaadin.shared.ui.video.VideoState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

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
 * by the browser depending on which file formats it supports. See
 * <a href="http://en.wikipedia.org/wiki/HTML5_video#Table">wikipedia</a> for a
 * table of formats supported by different browsers.
 *
 * @author Vaadin Ltd
 * @since 6.7.0
 */
public class Video extends AbstractMedia {

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

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        Elements elems = design.getElementsByTag("poster");
        for (Element poster : elems) {
            if (getPoster() == null && poster.hasAttr("href")) {
                setPoster(DesignAttributeHandler.readAttribute("href",
                        poster.attributes(), Resource.class));
            }
            poster.remove();
        }

        // Poster is extracted so AbstractMedia does not include it in alt text
        super.readDesign(design, designContext);
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        if (getPoster() != null) {
            Attributes attr = design.appendElement("poster").attributes();
            DesignAttributeHandler.writeAttribute("href", attr, getPoster(),
                    null, Resource.class, designContext);
        }
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("poster");
        return result;
    }

    @Override
    protected VideoState getState() {
        return (VideoState) super.getState();
    }

    @Override
    protected VideoState getState(boolean markAsDirty) {
        return (VideoState) super.getState(markAsDirty);
    }

}
