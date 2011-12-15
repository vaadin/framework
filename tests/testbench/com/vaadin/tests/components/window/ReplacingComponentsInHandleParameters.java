package com.vaadin.tests.components.window;

import java.util.Map;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

public class ReplacingComponentsInHandleParameters extends TestBase {

    @Override
    protected String getDescription() {
        return "Reusing debug IDs when replacing components in handleParameters() causes out of sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8090;
    }

    @Override
    protected void setup() {
        final ClickListener clickListener = new ClickListener() {
            public void buttonClick(ClickEvent event) {
                event.getButton().setCaption("Clicked!");
            }
        };

        final Window main = new Window() {
            @Override
            public void handleParameters(Map<String, String[]> parameters) {
                super.handleParameters(parameters);
                removeAllComponents();
                Button btn = new Button("Testing 2", clickListener);
                btn.setDebugId("TestId");
                addComponent(btn);
            }
        };

        Button btn = new Button("Testing", clickListener);
        btn.setDebugId("TestId");
        main.addComponent(btn);

        setMainWindow(main);
    }
}
