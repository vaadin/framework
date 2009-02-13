package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.form.FormBasic;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationError;
import com.itmill.toolkit.ui.AbstractComponent;

public class Errors extends Feature {

    private static final String desc = "A <i>component error</i> can be set to"
            + " indicate an error - an error indicator icon will appear,"
            + " and the error message will appear as a 'tooltip' when"
            + " mousing over.<br/>"
            + "You can do this on almost any component, but please note"
            + " that from a usability standpoint it's not always the best"
            + " solution.<br/>"
            + "<i>Component error</i> is most useful to indicate what is"
            + " causing the error (e.g an 'email' TextField), so that the user"
            + " can find and correct the problem. <br/>"
            + "On the other hand, it is usually not a good idea to set an error"
            + " on a Button: the user can not click 'Save' differently to"
            + " correct the error.<br/>"
            + "If there is no component causing the error, consider using a"
            + " (styled) Label or a Notification to indicate the error.";

    @Override
    public String getName() {
        return "Error indicator";
    }

    public String getDescription() {
        return desc;
    }

    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(AbstractComponent.class) };
    }

    public Class[] getRelatedFeatures() {
        // TODO link validation sample, form sample
        return new Class[] { Validation.class, FormBasic.class,
                NotificationError.class };
    }

    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
