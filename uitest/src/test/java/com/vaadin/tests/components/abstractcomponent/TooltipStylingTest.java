package com.vaadin.tests.components.abstractcomponent;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import com.vaadin.tests.tb3.SingleBrowserTest;

@RunWith(ParameterizedTB3Runner.class)
public class TooltipStylingTest extends SingleBrowserTest {

    private String theme;

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Parameters
    public static Collection<String> getThemes() {
        return MultiBrowserThemeTest.themesToTest;
    }

    @Test
    public void tooltipStyling() throws IOException {
        openTestURL("theme=" + theme);

        $(LabelElement.class).id("default").showTooltip();

        compareScreen("default");

        $(LabelElement.class).id("html").showTooltip();

        compareScreen("html");
    }
}
