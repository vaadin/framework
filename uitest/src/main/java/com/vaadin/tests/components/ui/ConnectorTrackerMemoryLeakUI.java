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

        System.out.println("Vaadin version: "
                + com.vaadin.shared.Version.getFullVersion());

        Button button = new Button(BUTTON_CAPTION);
        button.addClickListener(e -> {
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
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
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            long delta = Runtime.getRuntime().totalMemory() - memory;
            memory = memory + delta;
            System.out.println(" After: " + memory + " (+" + delta + ")");
        });

        layout.addComponents(button, panel);
        setContent(layout);
    }

    private static void updatePanel(CssLayout panel, List<String> items) {
        panel.removeAllComponents();
        items.forEach(i -> panel.addComponent(new Button(i, e -> {
            Window w = new Window();
            UI.getCurrent().addWindow(w);
        })));
    }

}
