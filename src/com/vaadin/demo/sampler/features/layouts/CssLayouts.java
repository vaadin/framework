package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class CssLayouts extends Feature {

    @Override
    public String getName() {
        return "Css layout";
    }

    @Override
    public String getDescription() {
        // TODO
        return "Most commonly developers usign Vaadin don't want to think "
                + "of the browser environment at all. With the flexible "
                + "layout API found from grid, horizontal and vertical "
                + "layout developers can build almost anything with plain "
                + "Java. But sometimes experienced web developers miss "
                + "flexibility of CSS and HTML. CssLayout is a simple "
                + "layout that puts contained componets into div element. "
                + "It has a simple DOM structure and it leaves all the power "
                + "to CSS designer hands. Having a very narrow feature set"
                + ", CssLayout is also the fastest layout to render in "
                + "Vaadin.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class),
                new APIResource(VerticalLayout.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { ApplicationLayout.class, CustomLayouts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "CSS for the layout", getThemeBase() + "layouts/cssexample.css") };
    }
}
