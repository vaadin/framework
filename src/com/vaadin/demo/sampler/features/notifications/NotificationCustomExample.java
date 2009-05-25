package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class NotificationCustomExample extends VerticalLayout {

    private static final Object CAPTION_PROPERTY = new Object();

    public NotificationCustomExample() {
        setSpacing(true);
        setWidth(null); // layout will grow with content

        final TextField caption = new TextField("Caption", "Message sent");
        caption
                .setDescription("Main info; a short caption-only notification is often most effective.");
        caption.setWidth("200px");
        addComponent(caption);

        final RichTextArea description = new RichTextArea();
        description.setValue("<p>to <i>john.doe@example.com</i></p>");
        description.setCaption("Description");
        description
                .setDescription("Additional information; try to keep it short.");
        addComponent(description);

        HorizontalLayout horiz = new HorizontalLayout();
        horiz.setSpacing(true);
        addComponent(horiz);

        final NativeSelect position = new NativeSelect("Position");
        position.setNullSelectionAllowed(false);
        horiz.addComponent(position);
        initPositionItems(position);

        final NativeSelect style = new NativeSelect("Style");
        style.setNullSelectionAllowed(false);
        horiz.addComponent(style);
        initTypeItems(style);

        final Slider delay = new Slider("Delay (msec), -1 means click to hide");
        delay
                .setDescription("Delay before fading<br/>Pull all the way to the left to get -1, which means forever (click to hide).");
        delay.setWidth("100%"); // 'description' will push width
        delay.setMin(Notification.DELAY_FOREVER);
        delay.setMax(10000);
        addComponent(delay);

        // TODO icon select

        Button show = new Button("Show notification",
                new Button.ClickListener() {
                    // "Inline" click listener; this is where the
                    // notification is actually created and shown.
                    public void buttonClick(ClickEvent event) {
                        // create Notification instance and customize
                        Notification n = new Notification((String) caption
                                .getValue(), (String) description.getValue(),
                                (Integer) style.getValue());
                        n.setPosition((Integer) position.getValue());
                        Double d = (Double) delay.getValue();
                        n.setDelayMsec(d.intValue()); // sec->msec
                        getWindow().showNotification(n);
                    }
                });
        addComponent(show);
        setComponentAlignment(show, Alignment.MIDDLE_RIGHT);

    }

    /*
     * Helper to fill the position select with the various possibilities
     */
    private void initPositionItems(NativeSelect position) {
        position.addContainerProperty(CAPTION_PROPERTY, String.class, null);
        position.setItemCaptionPropertyId(CAPTION_PROPERTY);
        Item i = position.addItem(Notification.POSITION_TOP_LEFT);
        Property c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Top left");
        i = position.addItem(Notification.POSITION_CENTERED_TOP);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Top centered");
        i = position.addItem(Notification.POSITION_TOP_RIGHT);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Top right");
        i = position.addItem(Notification.POSITION_CENTERED);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Centered");
        i = position.addItem(Notification.POSITION_BOTTOM_LEFT);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Bottom left");
        i = position.addItem(Notification.POSITION_CENTERED_BOTTOM);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Bottom, centered");
        i = position.addItem(Notification.POSITION_BOTTOM_RIGHT);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Bottom right");
        position.setValue(Notification.POSITION_CENTERED);
    }

    /*
     * Helper to fill the position select with the various possibilities
     */
    private void initTypeItems(NativeSelect type) {
        type.addContainerProperty(CAPTION_PROPERTY, String.class, null);
        type.setItemCaptionPropertyId(CAPTION_PROPERTY);
        Item i = type.addItem(Notification.TYPE_HUMANIZED_MESSAGE);
        Property c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Humanized");
        i = type.addItem(Notification.TYPE_WARNING_MESSAGE);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Warning");
        i = type.addItem(Notification.TYPE_ERROR_MESSAGE);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Error");
        i = type.addItem(Notification.TYPE_TRAY_NOTIFICATION);
        c = i.getItemProperty(CAPTION_PROPERTY);
        c.setValue("Tray");

        type.setValue(Notification.TYPE_HUMANIZED_MESSAGE);
    }

}
