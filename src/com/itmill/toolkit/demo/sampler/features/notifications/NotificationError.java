package com.itmill.toolkit.demo.sampler.features.notifications;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Window;

public class NotificationError extends Feature {

    @Override
    public String getDescription() {
        return "Notifications are lightweight informational messages,"
                + " used to inform the user of various events. The"
                + " <i>Error</i> variant is modal, and is to be used for"
                + " messages that must be seen by the user.<br/>"
                + " The <i>Error</i> message must be closed by clicking"
                + " the notification.<br/> Candidates for an"
                + " <i>Error</i> notification include 'Save failed',"
                + " 'Permission denied', and other situations that the"
                + " user must be made aware of.<br/>It's a good idea to"
                + " provide hints about what went wrong, and how the user'"
                + " can proceed to correct the situation.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class),
                new APIResource(Window.Notification.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NotificationHumanized.class,
                NotificationWarning.class, NotificationTray.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
