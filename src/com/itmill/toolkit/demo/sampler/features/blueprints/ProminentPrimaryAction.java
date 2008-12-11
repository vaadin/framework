package com.itmill.toolkit.demo.sampler.features.blueprints;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonLink;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonPush;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Link;

public class ProminentPrimaryAction extends Feature {

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

    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Button.class),
                new APIResource(Link.class) };
    }

    public Class[] getRelatedFeatures() {
        return new Class[] { ButtonPush.class, ButtonLink.class };
    }

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
