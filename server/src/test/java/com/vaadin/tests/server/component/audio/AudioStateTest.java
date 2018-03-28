package com.vaadin.tests.server.component.audio;

import static org.junit.Assert.assertEquals;

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
        assertEquals("Unexpected state class", AudioState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_audioHasCustomPrimaryStyleName() {
        Audio audio = new Audio();
        AudioState state = new AudioState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                audio.getPrimaryStyleName());
    }

    @Test
    public void audioStateHasCustomPrimaryStyleName() {
        AudioState state = new AudioState();
        assertEquals("Unexpected primary style name", "v-audio",
                state.primaryStyleName);
    }

    private static class TestAudio extends Audio {

        @Override
        public AudioState getState() {
            return super.getState();
        }
    }
}
