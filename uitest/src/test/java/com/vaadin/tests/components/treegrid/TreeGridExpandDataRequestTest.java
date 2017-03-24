package com.vaadin.tests.components.treegrid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridExpandDataRequestTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return TreeGridBasicFeatures.class;
    }

    TreeGridElement grid;

    @Before
    public void before() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
        selectMenuPath("Component", "Features", "Set data provider",
                "LoggingDataProvider");
        clearLog();
    }

    private void clearLog() {
        selectMenuPath("Settings", "Clear log");
    }

    @Test
    public void expand_node0_does_not_request_root_nodes() {
        grid.expandWithClick(0);
        Assert.assertFalse("Log should not contain request for root nodes.",
                logContainsText("Root node request: "));
    }

    @Test
    public void expand_node0_after_node1_does_not_request_children_of_node1() {
        grid.expandWithClick(1);
        Assert.assertFalse("Log should not contain request for root nodes.",
                logContainsText("Root node request: "));
        clearLog();
        grid.expandWithClick(0);
        Assert.assertFalse(
                "Log should not contain request for children of '0 | 1'.",
                logContainsText("Children request: 0 | 1"));
        Assert.assertFalse("Log should not contain request for root nodes.",
                logContainsText("Root node request: "));
    }
}
