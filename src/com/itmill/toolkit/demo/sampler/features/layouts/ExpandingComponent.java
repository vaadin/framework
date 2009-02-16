package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.HorizontalLayout;

public class ExpandingComponent extends Feature {

    @Override
    public String getName() {
        return "Expanding components";
    }

    @Override
    public String getDescription() {
        return "You can <i>expand</i> components to make them"
                + " occupy the space left over by other components.<br/>"
                + " If more than one component is expanded, the <i>ratio</i>"
                + " determines how the leftover space is shared between the"
                + " expanded components.<br/>Mousover each component for a"
                + " description (tooltip).<br/> Also try resizing the window.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(HorizontalLayout.class),

        };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {};
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }
}
