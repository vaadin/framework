package com.vaadin.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;

public class HorizontalSplitPanelTest {

    @Test
    public void primaryStyleName() {
        assertEquals(new HorizontalSplitPanelState().primaryStyleName,
                new HorizontalSplitPanel().getPrimaryStyleName());
    }
}
