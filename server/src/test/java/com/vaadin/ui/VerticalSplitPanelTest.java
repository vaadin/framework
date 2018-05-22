package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.splitpanel.VerticalSplitPanelState;

public class VerticalSplitPanelTest {

    @Test
    public void primaryStyleName() {
        Assert.assertEquals(new VerticalSplitPanelState().primaryStyleName,
                new VerticalSplitPanel().getPrimaryStyleName());
    }
}
