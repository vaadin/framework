package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

public class NotificationTray extends Feature {

    @Override
    public String getName() {
        return "Tray notification";
    }

    @Override
    public String getDescription() {
        return "<p>The <i>Tray</i> notification shows up in the lower right corner,"
                + " and is meant to interrupt the user as little as possible"
                + " even if it's shown for a while. "
                + "The <i>Tray</i> message fades away after a few moments"
                + " once the user interacts with the application (e.g. moves"
                + " mouse, types)</p><p>Candidates for a"
                + " <i>Tray</i> notification include 'New message received',"
                + " 'Job XYZ completed' &ndash; generally notifications about events"
                + " that have been delayed, or occur in the background"
                + " (as opposed to being a direct result of the users last action.)</p>";
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
                NotificationCustom.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };
    }

}
