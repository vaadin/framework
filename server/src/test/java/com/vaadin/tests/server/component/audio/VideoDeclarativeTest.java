package com.vaadin.tests.server.component.audio;

import java.io.File;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Video;

public class VideoDeclarativeTest extends DeclarativeTestBase<Video> {

    @Test
    public void testEmptyVideo() {
        String design = "<vaadin-video />";
        Video audio = new Video();
        testRead(design, audio);
        testWrite(design, audio);
    }

    @Test
    public void testVideoMultipleSources() {
        String design = "<vaadin-video muted show-controls='false'>"
                + "some <b>text</b>" //
                + "<source href='http://foo.pl' />"
                + "<source href='https://bar.pl' />" //
                + "<source href='ohai' />" //
                + "<poster href='http://foo.pl/poster' />" //
                + "</vaadin-video>";
        Video video = new Video();
        video.setAltText("some <b>text</b>");
        video.setAutoplay(false);
        video.setMuted(true);
        video.setShowControls(false);
        video.setSources(new ExternalResource("http://foo.pl"),
                new ExternalResource("https://bar.pl"),
                new FileResource(new File("ohai")));
        video.setPoster(new ExternalResource("http://foo.pl/poster"));
        testRead(design, video);
        testWrite(design, video);
    }

}
