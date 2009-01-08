package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.NativeSelect;

public class NativeSelection extends Feature {

    @Override
    public String getDescription() {
        return "A native selection component provides a simple drop-down list for selecting one item.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(NativeSelect.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ListSelectSingle.class, ListSelectMultiple.class,
                TwinColumnSelect.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
