package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class NotificationHumanized extends Feature {

    @Override
    public String getDescription() {
        return "Notifications are lightweight informational messages,"
                + " used to inform the user of various events. The"
                + " <i>Humanized</i> variant is an implementation of"
                + " the <i>transparent message</i> -pattern, and is meant"
                + " to indicate non-critical events while interrupting"
                + " the user as little as possible.<br/>"
                + "The <i>Humanized</i> message quickly fades away once"
                + " the user interacts with the application (e.g. moves"
                + " mouse, types)<br/> Candidates for a"
                + " <i>Humanized</i> notification include 'XYZ saved',"
                + " 'Added XYZ', and other messages that the user can"
                + " safely ignore, once the application is familliar.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationWarning.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };
    }

}
