/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.featurebrowser;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Demonstrates the use of Notifications.
 * 
 * @author IT Mill Ltd.
 * @see com.vaadin.ui.Window
 */
@SuppressWarnings("serial")
public class NotificationExample extends CustomComponent {

    // Dropdown select for notification type, using the native dropdown
    NativeSelect type;
    // Textfield for the notification caption
    TextField caption;
    // Textfield for the notification content
    TextField message;

    /**
     * Default constructor; We're subclassing CustomComponent, so we need to
     * choose a root component and set it as composition root.
     */
    public NotificationExample() {
        // Main layout
        final VerticalLayout main = new VerticalLayout();
        main.setSizeUndefined();
        main.setSpacing(true);
        main.setMargin(true); // use theme-specific margin
        setCompositionRoot(main);

        // Create the 'type' dropdown select.
        type = new NativeSelect("Notification type");
        main.addComponent(type);
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

        // Notification caption
        caption = new TextField("Caption");
        main.addComponent(caption);
        caption.setColumns(20);
        caption.setValue("Brown Fox!");

        // Notification message
        message = new RichTextArea();
        main.addComponent(message);
        message.setCaption("Message");
        message.setValue("A quick one jumped over the lazy dog.");

        // Button to show the notification
        final Button b = new Button("Show notification", new ClickListener() {
            // this is an inline ClickListener
            public void buttonClick(ClickEvent event) {
                // show the notification
                getWindow().showNotification((String) caption.getValue(),
                        (String) message.getValue(),
                        ((Integer) type.getValue()).intValue());
            }
        });
        main.addComponent(b);
        main.setComponentAlignment(b, Alignment.MIDDLE_RIGHT);
    }
}
