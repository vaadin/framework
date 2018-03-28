package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CustomDeploymentConfTest extends MultiBrowserTest {
    @Test
    public void testCustomDeploymentConf() {
        openTestURL();

        LabelElement cacheTimeLabel = $$(VerticalLayoutElement.class)
                .$$(VerticalLayoutElement.class).$$(LabelElement.class).first();

        LabelElement customParamLabel = $$(VerticalLayoutElement.class)
                .$$(VerticalLayoutElement.class).$$(LabelElement.class).get(1);

        assertEquals("Resource cache time: 3599", cacheTimeLabel.getText());
        assertEquals("Custom config param: customValue",
                customParamLabel.getText());
    }
}
