/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.client.ui.VVideo;

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
@ClientWidget(VVideo.class)
public class Video extends AbstractMedia {

    private Resource poster;

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
        this.poster = poster;
    }

    /**
     * @return The poster image.
     */
    public Resource getPoster() {
        return poster;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        if (getPoster() != null) {
            target.addAttribute(VVideo.ATTR_POSTER, getPoster());
        }
    }
}
