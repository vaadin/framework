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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.audio.AudioState;
import com.vaadin.ui.Audio;

/**
 * Tests for Audio state.
 * 
 */
public class AudioStateTest {
    @Test
    public void getState_audioHasCustomState() {
        TestAudio audio = new TestAudio();
        AudioState state = audio.getState();
        Assert.assertEquals("Unexpected state class", AudioState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_audioHasCustomPrimaryStyleName() {
        Audio audio = new Audio();
        AudioState state = new AudioState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, audio.getPrimaryStyleName());
    }

    @Test
    public void audioStateHasCustomPrimaryStyleName() {
        AudioState state = new AudioState();
        Assert.assertEquals("Unexpected primary style name", "v-audio",
                state.primaryStyleName);
    }

    private static class TestAudio extends Audio {

        @Override
        public AudioState getState() {
            return super.getState();
        }
    }
}
