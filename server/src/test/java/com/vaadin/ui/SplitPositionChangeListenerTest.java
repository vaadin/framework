/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitPositionChangeListener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Test
    public void testSplitPositionListenerContainsOldValues() throws Exception {
        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();

        float previousPosition = 50.0f;
        float newPosition = 125.0f;

        AtomicBoolean executed = new AtomicBoolean(false);

        splitPanel.setSplitPosition(previousPosition, Unit.PERCENTAGE);
        splitPanel.addSplitPositionChangeListener(event -> {
            assertFalse(event.isUserOriginated());

            assertTrue(previousPosition == event.getOldSplitPosition());
            assertEquals(Unit.PERCENTAGE, event.getOldSplitPositionUnit());

            assertTrue(newPosition == event.getSplitPosition());
            assertEquals(Unit.PIXELS, event.getSplitPositionUnit());

            executed.set(true);
        });
        splitPanel.setSplitPosition(newPosition, Unit.PIXELS);
        assertTrue(executed.get());
    }
}
