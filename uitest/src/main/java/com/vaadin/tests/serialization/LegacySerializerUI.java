package com.vaadin.tests.serialization;

import java.util.Map;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.LegacyComponent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class LegacySerializerUI extends AbstractTestUIWithLog {

    public class LegacySerializerComponent extends AbstractComponent
            implements LegacyComponent {

        @Override
        public void changeVariables(Object source,
                Map<String, Object> variables) {
            log("doubleInfinity: " + variables.get("doubleInfinity"));
        }

        @Override
        public void paintContent(PaintTarget target) throws PaintException {
            target.addAttribute("doubleInfinity", Double.POSITIVE_INFINITY);
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new LegacySerializerComponent());
    }
}
