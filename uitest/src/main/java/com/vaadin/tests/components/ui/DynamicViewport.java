package com.vaadin.tests.components.ui;

import com.vaadin.annotations.ViewportGeneratorClass;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.ViewportGenerator;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.ui.DynamicViewport.MyViewportGenerator;
import com.vaadin.ui.Label;

@ViewportGeneratorClass(MyViewportGenerator.class)
public class DynamicViewport extends AbstractReindeerTestUI {

    public static final String VIEWPORT_DISABLE_PARAMETER = "noViewport";

    public static class MyViewportGenerator implements ViewportGenerator {
        @Override
        public String getViewport(VaadinRequest request) {
            if (request.getParameterMap()
                    .containsKey(VIEWPORT_DISABLE_PARAMETER)) {
                return null;
            }
            return request.getHeader("User-Agent");
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        String negation = request.getParameterMap()
                .containsKey(VIEWPORT_DISABLE_PARAMETER) ? "not " : "";
        addComponent(new Label(
                "I should " + negation + "have a dynamic viewport tag"));
    }
}
