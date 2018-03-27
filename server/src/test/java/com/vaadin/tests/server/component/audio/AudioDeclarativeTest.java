package com.vaadin.tests.server.component.audio;

import java.io.File;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Audio;

/**
 * Tests specs of declarative support for abstract media and its
 * implementations.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class AudioDeclarativeTest extends DeclarativeTestBase<Audio> {

    @Test
    public void testEmptyAudio() {
        String design = "<vaadin-audio />";
        Audio audio = new Audio();
        testRead(design, audio);
        testWrite(design, audio);
    }

    @Test
    public void testAudioMultipleSources() {
        String design = "<vaadin-audio muted show-controls='false'>"
                + "some <b>text</b>" //
                + "<source href='http://foo.pl' />"
                + "<source href='https://bar.pl' />" //
                + "<source href='ohai' />" //
                + "</vaadin-audio>";
        Audio audio = new Audio();
        audio.setAltText("some <b>text</b>");
        audio.setAutoplay(false);
        audio.setMuted(true);
        audio.setShowControls(false);
        audio.setSources(new ExternalResource("http://foo.pl"),
                new ExternalResource("https://bar.pl"),
                new FileResource(new File("ohai")));
        testRead(design, audio);
        testWrite(design, audio);
    }
}
