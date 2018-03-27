package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PushWithPreserveOnRefreshTest extends MultiBrowserTest {

    @Test
    public void ensurePushWorksAfterRefresh() {
        openTestURL();
        $(ButtonElement.class).first().click();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("2. Button has been clicked 2 times", getLogRow(0));
        openTestURL();
        Assert.assertEquals("2. Button has been clicked 2 times", getLogRow(0));
        $(ButtonElement.class).first().click();
        Assert.assertEquals("3. Button has been clicked 3 times", getLogRow(0));

    }
}
