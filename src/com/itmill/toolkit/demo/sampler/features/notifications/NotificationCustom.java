package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class NotificationCustom extends Feature {

    @Override
    public String getDescription() {
        return "Notifications are lightweight informational messages,"
                + " used to inform the user of various events.<br/>"
                + "The notification can have a caption, a richtext"
                + " description, and an icon. Position and delay can"
                + " also be customized.<br/> Not that more often than"
                + " not, less is more: try to make the messages short"
                + " and to the point.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationHumanized.class,
                NotificationWarning.class, NotificationError.class,
                NotificationTray.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };

    }

}
