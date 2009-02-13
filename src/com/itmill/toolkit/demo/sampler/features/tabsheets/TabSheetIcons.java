package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.TabSheet;

public class TabSheetIcons extends Feature {
    @Override
    public String getName() {
        return "Tabsheet with icons";
    }

    @Override
    public String getDescription() {
        return "Each tab can have an Icon in addition to the caption.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TabSheet.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TabSheetScrolling.class, TabSheetDisabled.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
