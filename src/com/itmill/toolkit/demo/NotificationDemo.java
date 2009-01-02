/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * Demonstrates the use of Notifications.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class NotificationDemo extends com.itmill.toolkit.Application {

    // Dropdown select for notification type, using the native dropdown
    NativeSelect type;
    // Textfield for the notification caption
    TextField caption;
    // Textfield for the notification content
    TextField message;

    /**
     * The initialization method that is the only requirement for inheriting the
     * com.itmill.toolkit.service.Application class. It will be automatically
     * called by the framework when a user accesses the application.
     */
    @Override
    public void init() {

        // Create new window for the application and give the window a visible.
        final Window main = new Window("Notification demo");
        // set as main window
        setMainWindow(main);

        // Create the 'type' dropdown select.
        type = new NativeSelect("Notification type");
        // no empty selection allowed
        type.setNullSelectionAllowed(false);
        // we want a different caption than the value
        type.addContainerProperty("caption", String.class, null);
        type.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        type.setItemCaptionPropertyId("caption");
        // add some content (items) using the Container API
        Item i = type.addItem(new Integer(
                Window.Notification.TYPE_HUMANIZED_MESSAGE));
        i.getItemProperty("caption").setValue("Humanized message");
        i = type.addItem(new Integer(Window.Notification.TYPE_WARNING_MESSAGE));
        i.getItemProperty("caption").setValue("Warning message");
        i = type.addItem(new Integer(Window.Notification.TYPE_ERROR_MESSAGE));
        i.getItemProperty("caption").setValue("Error message");
        i = type
                .addItem(new Integer(Window.Notification.TYPE_TRAY_NOTIFICATION));
        i.getItemProperty("caption").setValue("Tray notification");
        // set the initially selected item
        type.setValue(new Integer(Window.Notification.TYPE_HUMANIZED_MESSAGE));
        main.addComponent(type); // add to layout

        // Notification caption
        caption = new TextField("Caption");
        caption.setColumns(20);
        caption.setValue("Brown Fox!");
        main.addComponent(caption);

        // Notification message
        message = new RichTextArea();
        message.setCaption("Message");
        message.setValue("A quick one jumped over the lazy dog.");
        main.addComponent(message); // add to layout

        // Button to show the notification
        final Button b = new Button("Show notification", new ClickListener() {
            // this is an inline ClickListener
            public void buttonClick(ClickEvent event) {
                // show the notification
                getMainWindow().showNotification((String) caption.getValue(),
                        (String) message.getValue(),
                        ((Integer) type.getValue()).intValue());
            }
        });
        main.addComponent(b); // add button to layout
    }
}
