package com.vaadin.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.shared.ui.splitpanel.VerticalSplitPanelState;

public class VerticalSplitPanelTest {

    @Test
    public void primaryStyleName() {
        assertEquals(new VerticalSplitPanelState().primaryStyleName,
                new VerticalSplitPanel().getPrimaryStyleName());
    }
}
