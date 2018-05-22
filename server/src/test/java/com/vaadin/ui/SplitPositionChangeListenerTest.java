package com.vaadin.ui;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeListener;

/**
 * Test for {@link SplitPositionChangeListener}
 *
 * @author Vaadin Ltd
 */
public class SplitPositionChangeListenerTest {

    @Test
    public void testSplitPositionListenerIsTriggered() throws Exception {
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        SplitPositionChangeListener splitPositionChangeListener = mock(
                SplitPositionChangeListener.class);
        splitPanel.addSplitPositionChangeListener(splitPositionChangeListener);
        splitPanel.setSplitPosition(50, Unit.PERCENTAGE);
        verify(splitPositionChangeListener)
                .onSplitPositionChanged(any(SplitPositionChangeEvent.class));
    }
}
