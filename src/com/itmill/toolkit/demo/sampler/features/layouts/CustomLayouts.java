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
        return "With the CustomLayout component, you can write your layout"
                + " as a template in XHTML that provides locations of any contained"
                + " components. The layout template must be included in a theme. This"
                + " separation allows the layout to be designed separately from code,"
                + " for example using WYSIWYG web designer tools."
                + "<br>The client-side engine of IT Mill Toolkit will replace contents"
                + " of the location elements with the components. The components are"
                + " bound to the location elements by the location identifier given to"
                + " addComponent().";
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
                "XHTML for the custom layout", getThemeBase()
                        + "layouts/examplecustomlayout_forviewing.html") };
    }
}
