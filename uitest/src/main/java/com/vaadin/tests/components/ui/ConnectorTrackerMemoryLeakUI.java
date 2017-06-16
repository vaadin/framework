package com.vaadin.tests.components.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConnectorTrackerMemoryLeakUI extends UI {

    public static final String BUTTON_CAPTION = "Kill!";
    public static final String LABEL_STOPPED = "Still alive!";
    private CssLayout panel = new CssLayout();
    private List<String> items = new ArrayList<>(200);
    private VerticalLayout layout = new VerticalLayout();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        Button button = new Button(BUTTON_CAPTION);
        button.addClickListener(e -> {
            gc();
            long memory = Runtime.getRuntime().totalMemory();
            System.out.println("Before: " + memory);
            // To simulate 200 concurrent session we do this 200 times
            for (int i = 0; i < 200; i++) {

                // Clear items
                items.clear();
                for (int j = 1; j <= 200; j++) {

                    // Add one item and update the panel with those
                    items.add("Item #" + j);
                    updatePanel(panel, items);
                }
            }

            // We made it this far. Good for us.
            Label labelStop = new Label(LABEL_STOPPED);
            layout.addComponent(labelStop);
            gc();
            long delta = Runtime.getRuntime().totalMemory() - memory;
            memory = memory + delta;
            System.out.println(" After: " + memory + " (+" + delta + ")");
        });

        layout.addComponents(button, panel);
        setContent(layout);
    }

    private void gc() {
        // Sometimes the VM needs a couple of "suggestions" to actually
        // perform gc. There is no automated test for this UI so tweak if
        // needed.
        for (int i = 0; i < 3; i++) {
            Runtime.getRuntime().gc();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
        }
    }

    private static void updatePanel(CssLayout panel, List<String> items) {
        panel.removeAllComponents();
        items.forEach(i -> panel.addComponent(new Button(i, e -> {
            Window w = new Window();
            UI.getCurrent().addWindow(w);
        })));
    }

}
