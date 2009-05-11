package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

public class NotificationHumanized extends Feature {

    @Override
    public String getName() {
        return "Humanized notification";
    }

    @Override
    public String getDescription() {
        return "<p>The <i>Humanized</i> notification is an implementation of"
                + " the <i>transparent message</i> -pattern, and can be used"
                + " to indicate non-critical events while interrupting"
                + " the user as little as possible.<br/>"
                + "The <i>Humanized</i> message quickly fades away once"
                + " the user interacts with the application (i.e. moves"
                + " mouse, types).</p><p>Candidates for a"
                + " <i>Humanized</i> notification include 'XYZ saved',"
                + " 'Added XYZ', and other messages that the user can"
                + " safely ignore, once the application is familliar.</p>";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationTray.class, NotificationWarning.class,
                NotificationError.class, NotificationCustom.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };
    }

}
