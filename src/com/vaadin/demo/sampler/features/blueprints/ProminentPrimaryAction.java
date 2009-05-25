package com.vaadin.demo.sampler.features.blueprints;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.buttons.ButtonLink;
import com.vaadin.demo.sampler.features.buttons.ButtonPush;
import com.vaadin.ui.Button;
import com.vaadin.ui.Link;

@SuppressWarnings("serial")
public class ProminentPrimaryAction extends Feature {

    @Override
    public String getName() {
        return "Prominent primary action";
    }

    @Override
    public String getDescription() {
        return "A primary action is an action that is clearly the"
                + " default, and it should be visually more prominent"
                + " than the secondary actions.<br/>Good candidates"
                + " include <i>Save</i>, <i>Submit</i>, <i>Continue</i>, <i>Next</i>,"
                + " <i>Finish</i> and so on.<br/>Note that 'dangerous' actions"
                + " that can not be undone should not be primary, and that it's"
                + " not always possible to identify a primary action"
                + " - don't force it if it's not obvious.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class),
                new APIResource(Link.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonPush.class, ButtonLink.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {

                new NamedExternalResource("CSS for 'Sign up' button",
                        getThemeBase() + "prominentprimaryaction/styles.css"),

                new NamedExternalResource(
                        "Article: Primary & Secondary Actions in Web Forms (LukeW)",
                        "http://www.lukew.com/resources/articles/psactions.asp"),
                new NamedExternalResource(
                        "Article: Primary & Secondary Actions (UI Pattern Factory)",
                        "http://uipatternfactory.com/p=primary-and-secondary-actions/") };
    }
}
