package com.vaadin.demo.sampler.features.notifications;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class NotificationError extends Feature {

    @Override
    public String getName() {
        return "Error notification";
    }

    @Override
    public String getDescription() {
        return "<p>The <i>Error</i> notification is modal, and is to be used for"
                + " messages that must be seen by the user.<br/>"
                + " The <i>Error</i> message must be closed by clicking"
                + " the notification.</p><p>Candidates for an"
                + " <i>Error</i> notification include 'Save failed',"
                + " 'Permission denied', and other situations that the"
                + " user must be made aware of.<br/>It's a good idea to"
                + " provide hints about what went wrong, and how the user'"
                + " can proceed to correct the situation.</p>";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { NotificationHumanized.class,
                NotificationTray.class, NotificationWarning.class,
                NotificationCustom.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
