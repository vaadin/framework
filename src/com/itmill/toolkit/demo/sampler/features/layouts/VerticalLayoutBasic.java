package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonPush;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonSwitch;
import com.itmill.toolkit.demo.sampler.features.link.LinkCurrentWindow;
import com.itmill.toolkit.demo.sampler.features.link.LinkNoDecorations;
import com.itmill.toolkit.demo.sampler.features.link.LinkSizedWindow;
import com.itmill.toolkit.ui.VerticalLayout;

public class VerticalLayoutBasic extends Feature {

    @Override
    public String getName() {
        return "Vertical Layout";
    }

    @Override
    public String getDescription() {
        return "Vertical and horizontal layouts are the foremost " +
        		"basic layout of all applications. You can add spacing " +
        		"between the elements, set the alignment of the components " +
        		"inside the cells of the layouts, and set one or more " +
        		"components as expanding so that they fill the available space " +
        		"according to the specified expand ratio.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] {
                new APIResource(VerticalLayout.class)};
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { HorizontalLayoutBasic.class, LayoutSpacing.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
                new NamedExternalResource("Reference Manual: VerticalLayout",
                        "/doc/manual/layout.components.orderedlayout.html"),
                        };
    }
}
