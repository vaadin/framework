package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class NotificationTray extends Feature {

    
    public String getDescription() {
        return "Notifications are lightweight informational messages,"
                + " used to inform the user of various events. The"
                + " <i>Tray</i> variant shows up in the lower left corner,"
                + " and is meant to interrupt the user as little as possible"
                + " even if it's shown for a while. "
                + "The <i>Tray</i> message fades away after a few moments"
                + " once the user interacts with the application (e.g. moves"
                + " mouse, types)<br/> Candidates for a"
                + " <i>Tray</i> notification include 'New message received',"
                + " 'Job XYZ completed' - generally notifications about events"
                + " that have been delayed, or occur in the background"
                + " (as opposed to being a direct result of the users last action.)";
    }

    
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationHumanized.class,
                NotificationWarning.class, NotificationError.class,
                NotificationCustom.class };
    }

    
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };
    }

}
