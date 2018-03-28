package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;

public class HorizontalSplitPanelTest {

    @Test
    public void primaryStyleName() {
        Assert.assertEquals(new HorizontalSplitPanelState().primaryStyleName,
                new HorizontalSplitPanel().getPrimaryStyleName());
    }
}
