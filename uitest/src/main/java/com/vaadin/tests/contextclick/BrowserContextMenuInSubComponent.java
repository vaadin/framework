package com.vaadin.tests.contextclick;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextArea;

@Widgetset(TestingWidgetSet.NAME)
public class BrowserContextMenuInSubComponent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();
        final TextArea textArea = new TextArea();
        // Make TextArea show regular context menu instead of firing the
        // server-side event.
        BrowserContextMenuExtension.extend(textArea);
        final Button button = new Button("Submit",
                event -> Notification.show(textArea.getValue()));

        layout.addComponent(textArea);
        layout.addComponent(button);

        panel.setContent(layout);

        panel.addContextClickListener(event -> button.click());

        addComponent(panel);
    }

    /**
     * A simple extension for making extended component stop propagation of the
     * context click events, so the browser will handle the context click and
     * show its own context menu.
     */
    public static class BrowserContextMenuExtension extends AbstractExtension {
        private BrowserContextMenuExtension(AbstractComponent c) {
            super(c);
        }

        public static BrowserContextMenuExtension extend(AbstractComponent c) {
            return new BrowserContextMenuExtension(c);
        }
    }

}
