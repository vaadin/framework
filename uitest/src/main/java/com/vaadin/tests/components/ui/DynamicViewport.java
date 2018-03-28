package com.vaadin.tests.components.ui;

import com.vaadin.annotations.ViewportGeneratorClass;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.ViewportGenerator;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.ui.DynamicViewport.MyViewportGenerator;
import com.vaadin.ui.Label;

@ViewportGeneratorClass(MyViewportGenerator.class)
public class DynamicViewport extends AbstractReindeerTestUI {
    public static class MyViewportGenerator implements ViewportGenerator {
        @Override
        public String getViewport(VaadinRequest request) {
            String userAgent = request.getHeader("User-Agent");
            System.out.println(userAgent);
            if (userAgent == null || userAgent.contains("Chrome")) {
                return null;
            }
            return userAgent;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("I should have a dynamic viewport tag"));
    }
}
