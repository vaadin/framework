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
        String design = "<v-audio />";
        Audio audio = new Audio();
        testRead(design, audio);
        testWrite(design, audio);
    }

    @Test
    public void testAudioMultipleSources() {
        String design = "<v-audio muted='true' show-controls='false'>"
                + "some <b>text</b>" //
                + "<source href='http://foo.pl' />"
                + "<source href='https://bar.pl' />" //
                + "<source href='ohai' />" //
                + "</v-audio>";
        Audio audio = new Audio();
        audio.setAltText("some <b>text</b>");
        audio.setAutoplay(false);
        audio.setMuted(true);
        audio.setShowControls(false);
        audio.setSources(new ExternalResource("http://foo.pl"),
                new ExternalResource("https://bar.pl"), new FileResource(
                        new File("ohai")));
        testRead(design, audio);
        testWrite(design, audio);
    }
}
