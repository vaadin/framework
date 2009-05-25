package com.vaadin.demo.sampler.features.commons;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.form.FormBasic;
import com.vaadin.demo.sampler.features.notifications.NotificationError;
import com.vaadin.ui.AbstractComponent;

@SuppressWarnings("serial")
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

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(AbstractComponent.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        // TODO link validation sample, form sample
        return new Class[] { Validation.class, FormBasic.class,
                NotificationError.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
