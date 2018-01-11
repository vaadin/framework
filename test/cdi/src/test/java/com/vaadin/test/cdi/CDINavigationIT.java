package com.vaadin.test.cdi;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;

@RunWith(ParallelRunner.class)
@RunLocally(Browser.PHANTOMJS)
public class CDINavigationIT extends ParallelTest {

    protected static final String BASE_URL = "http://localhost:8080";

    @Override
    public void setup() throws Exception {
        super.setup();

        testBench().resizeViewPortTo(1024, 600);
        getDriver().get(getTestURL());
    }

    protected String getUIPath() {
        return "/";
    }

    protected String getViewSeparator() {
        return "#!";
    }

    @Test
    public void testNavigation() {
        navigateTo("new");
        navigateTo("persisting");
        navigateTo("param/foo");
    }

    @Test
    public void testReloadPage() {
        navigateTo("name/foo");
        navigateTo("name/bar");

        List<String> content = getLogContent();

        getDriver().navigate().refresh();
        Assert.assertTrue(isElementPresent(By.id("name")));
        Assert.assertEquals("Content was lost when reloading", content,
                getLogContent());
    }

    protected String getTestURL() {
        return BASE_URL + getUIPath();
    }

    private List<String> getLogContent() {
        return $(CssLayoutElement.class).$$(LabelElement.class).all().stream()
                .map(LabelElement::getText).collect(Collectors.toList());
    }

    protected void navigateTo(String state) {
        navigateTo(state, getUIPath(), getViewSeparator());
    }

    protected void navigateTo(String state, String uiPath,
            String viewSeparator) {
        $(ButtonElement.class).caption(state).first().click();

        String id = state;
        if (id.contains("/")) {
            id = id.substring(0, id.indexOf("/"));
        }

        Assert.assertTrue(isElementPresent(By.id(id)));
        Assert.assertEquals("Navigation to state '" + state + "' failed",
                BASE_URL + uiPath + viewSeparator + state,
                getDriver().getCurrentUrl());
    }
}
