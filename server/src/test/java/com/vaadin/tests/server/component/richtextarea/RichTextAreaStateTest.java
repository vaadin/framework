package com.vaadin.tests.server.component.richtextarea;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.textarea.RichTextAreaState;
import com.vaadin.ui.RichTextArea;

/**
 * Tests for RichTextArea State.
 *
 */
public class RichTextAreaStateTest {
    @Test
    public void getState_areaHasCustomState() {
        TestRichTextArea area = new TestRichTextArea();
        RichTextAreaState state = area.getState();
        Assert.assertEquals("Unexpected state class", RichTextAreaState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_areaHasCustomPrimaryStyleName() {
        RichTextArea area = new RichTextArea();
        RichTextAreaState state = new RichTextAreaState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, area.getPrimaryStyleName());
    }

    @Test
    public void areaStateHasCustomPrimaryStyleName() {
        RichTextAreaState state = new RichTextAreaState();
        Assert.assertEquals("Unexpected primary style name", "v-richtextarea",
                state.primaryStyleName);
    }

    private static class TestRichTextArea extends RichTextArea {

        @Override
        public RichTextAreaState getState() {
            return super.getState();
        }
    }
}
