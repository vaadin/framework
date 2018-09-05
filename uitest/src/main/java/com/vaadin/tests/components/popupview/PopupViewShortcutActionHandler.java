package com.vaadin.tests.components.popupview;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.v7.ui.TextField;

/**
 * Test UI to check availability of shortcut action listener in the popup view
 * oeverlay component.
 *
 * @author Vaadin Ltd
 */
public class PopupViewShortcutActionHandler extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new PopupView(new DemoPoupView()));
    }

    @Override
    protected String getTestDescription() {
        return "Shortcut listener search should be executed in the end "
                + "of request (after legacy UIDL request handling).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14275;
    }

    private class DemoPoupView implements PopupView.Content {

        @Override
        public String getMinimizedValueAsHTML() {
            return "Click Me";
        }

        @Override
        public Component getPopupComponent() {
            TextField field = new TextField("Enter text");
            field.setImmediate(true);
            field.addShortcutListener(new ShortcutListener("SearchAction",
                    ShortcutAction.KeyCode.ENTER, null) {
                private static final long serialVersionUID = 1L;

                @Override
                public void handleAction(Object sender, Object target) {
                    Label label = new Label(
                            "shortcut addedEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    label.addStyleName("shortcut-result");
                    addComponent(label);
                }
            });
            return field;
        }

    }

}
