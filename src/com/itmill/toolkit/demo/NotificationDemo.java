package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Select;
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

	NativeSelect type;
	TextField caption;
	TextField message;

	/**
	 * The initialization method that is the only requirement for inheriting the
	 * com.itmill.toolkit.service.Application class. It will be automatically
	 * called by the framework when a user accesses the application.
	 */
	public void init() {

		/*
		 * - Create new window for the application - Give the window a visible
		 * title - Set the window to be the main window of the application
		 */
		Window main = new Window("Notification demo");
		setMainWindow(main);

		/*
		 * Create a 'inline' window within the main window, and set its size.
		 */
		Window conf = new Window("Show Notification");
		conf.setWidth(470);
		conf.setHeight(360);
		main.addWindow(conf);

		// Dropdown select for notification type.
		type = new NativeSelect("Notification type");
		type.addContainerProperty("caption", String.class, null);
		type.setNullSelectionAllowed(false);
		type.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		type.setItemCaptionPropertyId("caption");
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
		type.setValue(new Integer(Window.Notification.TYPE_HUMANIZED_MESSAGE));
		conf.addComponent(type);

		// Notification caption
		caption = new TextField("Caption");
		caption.setValue("Brown Fox!");
		caption.setColumns(20);
		conf.addComponent(caption);
		// Notification message
		message = new RichTextArea();
		message.setCaption("Message");
		message.setValue("A quick one jumped over the lazy dog.");
		conf.addComponent(message);
		// Button to show the notification
		Button b = new Button("Show notification", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				getMainWindow().showNotification((String) caption.getValue(),
						(String) message.getValue(),
						((Integer) type.getValue()).intValue());
			}
		});
		conf.addComponent(b);
	}
}
