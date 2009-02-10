package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TwinColSelect;

public class TwinColumnSelect extends Feature {
    @Override
    public String getName() {
        return "Twin column select (list builder)";
    }

    @Override
    public String getDescription() {
        return "The TwinColumnSelect is a multiple selection component"
                + " that shows two lists side by side. The list on the left"
                + " shows the available items and the list on the right shows"
                + " the selected items. <br>You can select items"
                + " from the list on the left and click on the >> button to move"
                + " them to the list on the right. Items can be moved back by"
                + " selecting them and clicking on the << button.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TwinColSelect.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NativeSelection.class, ListSelectMultiple.class,
                ListSelectSingle.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "Open Source Design Pattern Library; List Builder",
                "http://www.uidesignpatterns.org/content/list-builder") };
    }

}
