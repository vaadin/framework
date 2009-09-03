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
        return "Most commonly developers using Vaadin don't want to think "
                + "of the browser environment at all. With the flexible "
                + "layout API found from Grid, Horizontal and Vertical "
                + "layouts, developers can build almost anything with plain "
                + "Java. But sometimes experienced web developers miss the "
                + "flexibility that pure CSS and HTML can offer.<br /><br />"
                + "CssLayout is a simple layout that places its contained "
                + "components into a <code>DIV</code> element. It has a "
                + "simple DOM structure and it leaves all the power to the "
                + "CSS designer's hands. While having a very narrow feature "
                + "set, CssLayout is the fastest layout to render in Vaadin.";
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
