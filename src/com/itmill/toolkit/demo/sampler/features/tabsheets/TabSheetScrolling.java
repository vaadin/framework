package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TabSheet;

public class TabSheetScrolling extends Feature {

    @Override
    public String getDescription() {
        return "A tabsheet component with too many tabs to display"
                + " within the width of the tabsheet. A scrolling"
                + "feature will be added to the tab bar automatically.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TabSheet.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TabSheetIcons.class, TabSheetDisabled.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
