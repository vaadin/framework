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
        String design = "<v-video />";
        Video audio = new Video();
        testRead(design, audio);
        testWrite(design, audio);
    }

    @Test
    public void testVideoMultipleSources() {
        String design = "<v-video muted='true' show-controls='false'>"
                + "some <b>text</b>" //
                + "<source href='http://foo.pl' />"
                + "<source href='https://bar.pl' />" //
                + "<source href='ohai' />" //
                + "<poster href='http://foo.pl/poster' />" //
                + "</v-video>";
        Video video = new Video();
        video.setAltText("some <b>text</b>");
        video.setAutoplay(false);
        video.setMuted(true);
        video.setShowControls(false);
        video.setSources(new ExternalResource("http://foo.pl"),
                new ExternalResource("https://bar.pl"), new FileResource(
                        new File("ohai")));
        video.setPoster(new ExternalResource("http://foo.pl/poster"));
        testRead(design, video);
        testWrite(design, video);
    }

}
