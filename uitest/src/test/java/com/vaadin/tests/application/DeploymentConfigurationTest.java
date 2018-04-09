package com.vaadin.tests.application;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DeploymentConfigurationTest extends SingleBrowserTest {

    @Test
    public void testParameters() {
        openTestURL();
        List<String> texts = new ArrayList<>(Arrays.asList("Init parameters:",
                "widgetset: com.vaadin.v7.Vaadin7WidgetSet",
                "closeIdleSessions: true", "productionMode: false",
                "testParam: 42", "heartbeatInterval: 301",
                "resourceCacheTime: 3601"));

        for (LabelElement label : $(LabelElement.class).all()) {
            assertTrue(label.getText() + " not found",
                    texts.contains(label.getText()));
            texts.remove(label.getText());
        }
        assertTrue(texts.isEmpty());
    }

}
