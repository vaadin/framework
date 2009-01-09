package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TabSheet;

public class TabSheetDisabled extends Feature {

    @Override
    public String getDescription() {
        return "With a tabsheet component you also have the"
                + " ability to enable and disable as well as"
                + " show and hide specific tabs.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TabSheet.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TabSheetIcons.class, TabSheetScrolling.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
