package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class NotificationWarning extends Feature {

    @Override
    public String getName() {
        return "Warning notification";
    }

    @Override
    public String getDescription() {
        return "<p>The <i>Warning</i> notification is an implementation of"
                + " the <i>transparent message</i> -pattern, and is meant"
                + " to interrupt the user as little as possible, while"
                + " still drawing the needed attention."
                + "The <i>Warning</i> message fades away after a few moments"
                + " once the user interacts with the application (e.g. moves"
                + " mouse, types)</p><p>Candidates for a"
                + " <i>Warning</i> notification include 'You canceled XYZ',"
                + " 'XYZ deleted', and other situations that the user should"
                + " be made aware of, but are probably intentional.</p>";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationHumanized.class,
                NotificationTray.class, NotificationError.class,
                NotificationCustom.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Monolog Boxes and Transparent Messages",
                "http://humanized.com/weblog/2006/09/11/monolog_boxes_and_transparent_messages/") };
    }

}
