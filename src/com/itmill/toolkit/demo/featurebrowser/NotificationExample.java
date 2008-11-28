/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OrderedLayout;
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
        final OrderedLayout main = new OrderedLayout();
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
        main.setComponentAlignment(b, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
    }
}
