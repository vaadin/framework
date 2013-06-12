package com.vaadin.tests.tb3;

import org.junit.Test;

public class LabelModesTest extends MultiBrowserTest {

    @Test
    public void test() throws Exception {
        compareScreen("labelmodes");
    }

    @Override
    protected String getPath() {
        return "/run/com.vaadin.tests.components.label.LabelModes?restartApplication";
    }

    @Override
    protected String getBaseURL() {
        return "http://demo-us1.demo.vaadin.com/tests-7.1.0.beta1";
    }

}
