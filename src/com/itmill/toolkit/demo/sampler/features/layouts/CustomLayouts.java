package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.CustomLayout;

public class CustomLayouts extends Feature {

    @Override
    public String getName() {
        return "Custom layout";
    }

    @Override
    public String getDescription() {
        return "The CustomLayout allows you to make a layout in regular HTML,"
                + " using styles and embedding images to suit your needs."
                + " You can even make the layout using a WYSIWYG editor.<br/>"
                + " Marking an area in the HTML as a named <i>location</i>"
                + " will allow you to replace that area with a component later."
                + "<br/>HTML prototypes can often be quickly converted into a"
                + " working application this way, providing a clear path from"
                + " design to implementation.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(CustomLayout.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { WebLayout.class, ApplicationLayout.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Layout HTML (view source)", getThemeBase()
                        + "layouts/examplecustomlayout.html") };
    }
}
